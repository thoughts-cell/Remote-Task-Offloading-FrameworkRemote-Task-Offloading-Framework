/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
 
public class SecurityUtil {
       //Generate a random alpha numeric Base64 string to a certain length n 
    public static String RandomAlphaNumericString(int n) { 
        //The character pool to generate a random alpha numeric string 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "0123456789"
                        + "abcdefghijklmnopqrstuvxyz"
                        +"+/"; 
        StringBuilder sb = new StringBuilder(n); 
        for (int i = 0; i < n; i++) { 
            int index = (int)(AlphaNumericString.length()*Math.random()); 
            sb.append(AlphaNumericString.charAt(index)); 
        } 
        return sb.toString(); 
    } 
    //Decrypt the session key from a string to a SecretKey
    public static SecretKey DecryptSessionKey(String CipherSessionKeyString, PrivateKey prikey) {
        //The session key in plain string
        String SessionKeyString=SecurityUtil.asyDecrypt(CipherSessionKeyString, prikey);
        //Convert the session key string to key
        return SecurityUtil.B64StringTokey(SessionKeyString);
    }
    //Encrypt a session key from SecretKey to a string
    public static String EncryptSessionKey(SecretKey SessionKey, PublicKey pubkey) {
        //Convert the session key to a string
         String SessionKeyString=SecurityUtil.keytoB64String(SessionKey);
        //Encypt the session key string 
        return SecurityUtil.asyEncrypt(SessionKeyString, pubkey);
    }
    
    public static String SymEncryptObj(Object obj, SecretKey sessionKey) {
        byte [] ObjectBytes=SecurityUtil.convertObjectToBytes(obj);
        String ObjectString=SecurityUtil.SymEncrypt(ObjectBytes, sessionKey);
        return ObjectString;
    }
    
    public static Object SymDecryptObj(String ObjectString, SecretKey sessionKey) {
        byte[] ObjectBytes=SecurityUtil.SymDecrypt((String)ObjectString, sessionKey);
        //Conver the byte array format into object format
        Object obj=SecurityUtil.convertBytesToObject(ObjectBytes);
        return obj;
    }
    
    //Overloaded method that accepts a custom ClassLoader for dynamic class loading
    public static Object SymDecryptObj(String ObjectString, SecretKey sessionKey, ClassLoader customClassLoader) {
        byte[] ObjectBytes=SecurityUtil.SymDecrypt((String)ObjectString, sessionKey);
        //Convert the byte array format into object format with custom ClassLoader
        Object obj=SecurityUtil.convertBytesToObject(ObjectBytes, customClassLoader);
        return obj;
    }

    //Convert any objects to a byte array
    public static byte[] convertObjectToBytes(Object obj) {
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
        ois.writeObject(obj);
        return boas.toByteArray();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
      throw new RuntimeException();
  }
    //Convert the byte array format of an object to the object format
    public static Object convertBytesToObject(byte[] bytes) {
      InputStream is = new ByteArrayInputStream(bytes);
      try (ObjectInputStream ois = new ObjectInputStream(is)) {
        return ois.readObject();
      } catch (IOException | ClassNotFoundException ioe) {
        ioe.printStackTrace();
      }
      throw new RuntimeException();
  }
  
    //Overloaded method with custom ClassLoader for dynamic class loading
    public static Object convertBytesToObject(byte[] bytes, ClassLoader classLoader) {
      InputStream is = new ByteArrayInputStream(bytes);
      try {
        // Create a custom ObjectInputStream that uses the provided ClassLoader
        ObjectInputStream ois = new ObjectInputStream(is) {
            @Override
            protected Class<?> resolveClass(java.io.ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                String className = desc.getName();
                try {
                    System.out.println("CustomClassLoader attempting to load: " + className);
                    Class<?> clazz = Class.forName(className, false, classLoader);
                    System.out.println("CustomClassLoader successfully loaded: " + className);
                    return clazz;
                } catch (ClassNotFoundException e) {
                    System.out.println("CustomClassLoader failed for " + className + ", using system classloader");
                    return super.resolveClass(desc);
                }
            }
        };
        Object result = ois.readObject();
        ois.close();
        return result;
      } catch (IOException | ClassNotFoundException ioe) {
        ioe.printStackTrace();
      }
      throw new RuntimeException();
  }
    //The method to generate a session key
    public static SecretKey SecretKeyGen() {
        try {
            KeyGenerator KeyGen=KeyGenerator.getInstance("AES");
            KeyGen.init(128);
            return KeyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Exception in ICentre() : "+ex.getMessage()+"\n");
        }
        throw new RuntimeException();
    }
    //The metyhod to read in public or private keys from a seriailzaed key container (.ser file) 
    public static HashMap ReadinKeys(String Keyfile) {
        FileInputStream pfin = null;
        try {
            pfin = new FileInputStream(Keyfile);
            ObjectInputStream obin= new ObjectInputStream(pfin);
            HashMap keys = (HashMap)obin.readObject();
            obin.close();
            pfin.close();
            return keys;
        } catch (FileNotFoundException ex) {
            System.out.println("Exception in ReadinKeys(): "+ex.getMessage()+"\n");
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Exception in readinKeys(): "+ex.getMessage()+"\n");
        } finally {
            try {
                if (pfin != null) {
                    pfin.close();
                }
            } catch (IOException ex) {
                System.out.println("Exception in readinKeys(): "+ex.getMessage()+"\n");
            }
        }
        throw new RuntimeException();
    }
    //Symmetrical encryption; the result is Base64 string
    public  static String SymEncrypt(byte[] message, Key sk) {
        String ctext=new String();
        try {
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, sk);
            ctext=Base64.getEncoder().encodeToString(aesCipher.doFinal(message));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in SymEncrypt(): "+ex.getMessage());
        }
        return ctext;
    }
    //Symmetrical decryption; the result is byte array
    public static byte[] SymDecrypt(String message, Key sk) {
        byte[] ptext = null;
        try {
            byte [] msgbytes  = Base64.getDecoder().decode(message);
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, sk);
            ptext=aesCipher.doFinal(msgbytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in SymDecrypt(): "+ex.getMessage());
        }
        return ptext;
    }
    //Unsymmetrical encryption; the result is Base64 string
    public static String asyEncrypt(String message, Key pk) {
        String etext=new String();
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte [] cipherData = cipher.doFinal(message.getBytes("UTF-8"));
           etext=Base64.getEncoder().encodeToString(cipherData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                InvalidKeyException | UnsupportedEncodingException | 
                IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in asyEncrypt(): "+ex.getMessage());
        }
        return etext;
        
    }
    //unsymmetrical decryption; the result is normal string
    public static String asyDecrypt(String message, Key prik) {
        String ptext= "";
        try {
            byte [] msgbytes=Base64.getDecoder().decode(message);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, prik, cipher.getParameters());
            ptext=new String(cipher.doFinal(msgbytes));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                InvalidKeyException | InvalidAlgorithmParameterException | 
                IllegalBlockSizeException | BadPaddingException ex) {
            System.out.println("Exception in asyDecrypt(): "+ex.getMessage());
        }
        return ptext;
    } 
    //Convert a Base64 string to a symmetrical (session) key
    public static SecretKey B64StringTokey(String kString)  {
        byte[] bytekey  = Base64.getDecoder().decode(kString);
        return new SecretKeySpec(bytekey , 0, bytekey.length, "AES");
    }
    //Convert a symmetrical (session) key to a Base64 string to 
    public static String keytoB64String(SecretKey sKey) {
        return Base64.getEncoder().encodeToString(sKey.getEncoded());
    }
    //Convert  a public key to a Base64 string
    public static String pubKeytoB64String(PublicKey pKey) {
        return Base64.getEncoder().encodeToString(pKey.getEncoded());
    }
    //Convert  a private key to a Base64 string
    public static String priKeytoB64String(PrivateKey priKey) {
        return Base64.getEncoder().encodeToString(priKey.getEncoded());
    }
}

//Custom ObjectInputStream that uses a specified ClassLoader
class CustomClassLoaderObjectInputStream extends ObjectInputStream {
    private ClassLoader classLoader;
    
    public CustomClassLoaderObjectInputStream(InputStream in, ClassLoader classLoader) throws IOException {
        super(in);
        this.classLoader = classLoader;
    }
    
    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String className = desc.getName();
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException ex) {
            System.out.println("Warning: Could not load class " + className + " with custom classloader, trying system classloader");
            try {
                return super.resolveClass(desc);
            } catch (ClassNotFoundException ex2) {
                System.out.println("Error: Class " + className + " not found in any classloader");
                throw ex2;
            }
        }
    }
}
