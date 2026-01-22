package computeserver;

import Contract.CFile;
import Contract.CSAuthenticator;
import Security.SecurityUtil;
import Contract.Task;
import java.io.*;
import java.io.File;
import java.net.*;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import javax.crypto.SecretKey;
import java.net.URLClassLoader;

public class ComputeServer {



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
         System.out.println("-------------------------------------");
        // Start a new thread to handle incoming client connections
 


        new Thread(() -> {
            try {
                int serverPort = 6789;
                ServerSocket listenSocket = new ServerSocket(serverPort);
                 while (true) {
                    Socket clientSocket = listenSocket.accept();
                    SecretKey sessionKey = SecurityUtil.SecretKeyGen();
                    HashMap pubMap = new HashMap();
                    HashMap priHashMap = new HashMap();

                    Connection c = new Connection(clientSocket,pubMap,priHashMap,sessionKey);
                }
            } catch (IOException e) {
                System.out.println("Listen socket:" + e.getMessage());
            }
        }).start();
        System.out.println("The server is listening on port 6789 for object transfer...");
        System.out.println("-----------------------------------");
    }
}

class DynamicClassLoaderObjectInputStream extends ObjectInputStream {
    private URLClassLoader classLoader;
    
    public DynamicClassLoaderObjectInputStream(InputStream in, URLClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }
    
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        try {
            return Class.forName(desc.getName(), false, classLoader);
        } catch (ClassNotFoundException ex) {
            return super.resolveClass(desc);
        }
    }
}

class Connection extends Thread {

    Socket skt;
    ObjectInputStream in;
    ObjectOutputStream out;
    HashMap pubMap, priHashMap;
    URLClassLoader dynamicClassLoader;
    SecretKey sessionKey;

    // Constructor for the Connection class

    public Connection(Socket aClientSocket, HashMap pubMap, HashMap priHashMap, SecretKey sessionKey) {
        this.pubMap = pubMap;
        this.priHashMap = priHashMap;
        this.sessionKey = sessionKey;

        try {
            // Create URLClassLoader for dynamically uploaded classes
            File classesDir = new File("build" + File.separator + "classes");
            URL[] urls = {classesDir.toURI().toURL()};
            this.dynamicClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

            skt = aClientSocket;
            out = new ObjectOutputStream(skt.getOutputStream());
            in = new DynamicClassLoaderObjectInputStream(skt.getInputStream(), dynamicClassLoader);

            // Start a new thread for each client connection
            this.start();

        } catch (IOException e) {
            System.out.println("Connection: " + e.getMessage());
        }
    }
    // Run method for the Connection thread

    public void run() {

        while (true) {
            try {
                // Read the object sent by the client
                Object receivedObject = in.readObject();
                if (receivedObject instanceof CSAuthenticator) {
                    // Read received object

                    CSAuthenticator auth = (CSAuthenticator) receivedObject;
                    String userName = auth.getPlainUserName();
                    String cipherUserName = auth.getCipherUserName();
                    System.out.println(userName);

                    HashMap priHashMap = SecurityUtil.ReadinKeys("Centrepri.ser");

                    HashMap pubMap = SecurityUtil.ReadinKeys(  "Centrepub.ser");

                 PublicKey clientPublicKey = (PublicKey)pubMap.get(userName);
                    PrivateKey cenPrivateKey = (PrivateKey)priHashMap.get("CENTRE");
                    
                    String decUserName = SecurityUtil.asyDecrypt(cipherUserName, clientPublicKey);
                    System.out.println(decUserName);

                    //The server decrypt the verification string by its private key
               
                    String decVerificationString = SecurityUtil.asyDecrypt(auth.getVerificationString(), cenPrivateKey);

                    if (userName.equals(decUserName)) {
                        // The server creates the session key
                        String sessionKeyString = SecurityUtil.keytoB64String(sessionKey);
                        String encSessionKey = SecurityUtil.EncryptSessionKey(sessionKey, (PublicKey) pubMap.get(userName));
                        
                        // Create CSAuthenticator for session key exchange ,ENCRYPT centre with its private key
                        String serverCipherUserName = SecurityUtil.asyEncrypt("CENTRE", cenPrivateKey);
                  
                        String serverVerificationString = SecurityUtil.SymEncryptObj(decVerificationString, sessionKey);
                        CSAuthenticator csa = new CSAuthenticator("CENTRE",
                                serverCipherUserName,
                                serverVerificationString,
                                encSessionKey);
                        System.out.println("The mutual authentication of user " + userName + " is progressing! \n");
                        System.out.println("The verification string in cipher text: " + auth.getVerificationString() + "\n");
                        System.out.println("The verification string in plain text: " + decVerificationString + "\n");
                        System.out.println("The session key in plain text: " + sessionKeyString + "\n");
                        System.out.println("The session key in cipher text: " + encSessionKey + "\n");
                        // Write to socket with secured object
                        out.writeObject(csa);
                        System.out.println("-----------------------------------");


                    } else {
                        System.out.println("Authentication unsuccessfully, closing the connection \n");
                    }
                } else {
                    //decript the object with session key
                    Object objri = SecurityUtil.SymDecryptObj((String) receivedObject, sessionKey, dynamicClassLoader);


                    // If the received object is a CFile, save it to a file
                    if (objri instanceof CFile) {
                        CFile cfile =   (CFile) objri;
                        String packageDir = "build" + File.separator + "classes" + File.separator + "Contract";
                        File packageDirFile = new File(packageDir);
                        packageDirFile.mkdirs(); // Ensure directory exists
                        
                        String filePath = packageDir + File.separator + cfile.getFname();
                        FileOutputStream fo = new FileOutputStream(filePath);
                        BufferedOutputStream bos = new BufferedOutputStream(fo);
                        bos.write(cfile.getFbyte(), 0, cfile.getFbyte().length);
                        bos.close();
                        System.out.println("The class file of " + cfile.getFname() + " has been uploaded to: " + filePath);
                        
                        // Refresh the URLClassLoader to include newly uploaded classes
                        try {
                            dynamicClassLoader.close();
                        } catch (Exception e) {
                            System.out.println("Warning closing old classloader: " + e.getMessage());
                        }
                        File classesDir = new File("build" + File.separator + "classes");
                        
                        // Verify class file exists
                        File contractDir = new File(classesDir, "Contract");
                        File[] classFiles = contractDir.listFiles((d, n) -> n.endsWith(".class"));
                        if (classFiles != null) {
                            System.out.println("Classes found in " + contractDir.getAbsolutePath() + ": " + classFiles.length);
                            for (File cf : classFiles) {
                                System.out.println("  - " + cf.getName());
                            }
                        }
                        
                        URL[] urls = {classesDir.toURI().toURL()};
                        dynamicClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
                        System.out.println("ClassLoader refreshed with URL: " + classesDir.getAbsolutePath());

                    } else {
                              // If the received object is a Task, execute the task
                        Task task = (Task) objri;
                        System.out.println("Performing a client task of " + task.getClass());
                        task.executeTask();
                        Object result = task.getResult();
                        out.writeObject(SecurityUtil.SymEncryptObj(result, sessionKey));
                        out.flush();
                    }


                }
            } catch (ClassNotFoundException | IOException e) {

                e.printStackTrace();
            } //finally {
//                try {
//                    // Close streams and the client socket
//                   // in.close();
//                    //clientSocket.close();
            //             }
        }
    }
}



