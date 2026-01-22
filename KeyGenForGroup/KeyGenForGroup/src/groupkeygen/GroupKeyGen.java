package groupkeygen;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;

public class GroupKeyGen {
 
    KeyPairGenerator keyPairGen;
       
    public GroupKeyGen() throws NoSuchAlgorithmException {
      
      keyPairGen =  KeyPairGenerator.getInstance("RSA");
      keyPairGen.initialize(2048);
      
    }
    
    public KeyPair KeyPairGen() {
        return keyPairGen.genKeyPair();
    }
   
    public KeyPairGenerator getKeyPairGen() {
      return keyPairGen;
    }

    public static void main (String args[]) throws NoSuchAlgorithmException, InvalidKeySpecException, Exception {
    
        HashMap<String, PublicKey> CenPubMap = new HashMap<>( );
        HashMap<String, PrivateKey> CenPriMap = new HashMap<>( ); 
        HashMap<String, PublicKey> StePubMap = new HashMap<>( );
        HashMap<String, PrivateKey> StePriMap = new HashMap<>( );
        HashMap<String, PublicKey> MicPubMap = new HashMap<>( );
        HashMap<String, PrivateKey> MicPriMap = new HashMap<>( );
        
        GroupKeyGen gkg=new GroupKeyGen();
        KeyPair kp=gkg.KeyPairGen();
        CenPubMap.put("CENTRE", kp.getPublic());
        CenPriMap.put("CENTRE", kp.getPrivate());
        StePubMap.put("CENTRE", kp.getPublic());
        MicPubMap.put("CENTRE", kp.getPublic());
        
        kp=gkg.KeyPairGen();
        CenPubMap.put("Stephen Smith", kp.getPublic());
        StePubMap.put("Stephen Smith", kp.getPublic());
        StePriMap.put("Stephen Smith", kp.getPrivate());
        
        kp=gkg.KeyPairGen();
        CenPubMap.put("Michael Fox", kp.getPublic());
        MicPubMap.put("Michael Fox", kp.getPublic());
        MicPriMap.put("Michael Fox", kp.getPrivate());
        
        FileOutputStream fout = new FileOutputStream("CentrePub.ser"); 
        ObjectOutputStream oout = new ObjectOutputStream(fout); 
        oout.writeObject(CenPubMap); 
        oout.close();
        fout.close();
        
        fout = new FileOutputStream("CentrePri.ser"); 
        oout = new ObjectOutputStream(fout); 
        oout.writeObject(CenPriMap); 
        oout.close();
        fout.close();
        
        fout = new FileOutputStream("Stephen Smith-pub.ser"); 
        oout = new ObjectOutputStream(fout); 
        oout.writeObject(StePubMap); 
        oout.close();
        fout.close();
        
        fout = new FileOutputStream("Stephen Smith-pri.ser"); 
        oout = new ObjectOutputStream(fout); 
        oout.writeObject(StePriMap); 
        oout.close();
        fout.close();
        
        fout = new FileOutputStream("Michael Fox-pub.ser"); 
        oout = new ObjectOutputStream(fout); 
        oout.writeObject(MicPubMap); 
        oout.close();
        fout.close();
        
        fout = new FileOutputStream("Michael Fox-pri.ser"); 
        oout = new ObjectOutputStream(fout); 
        oout.writeObject(MicPriMap); 
        oout.close();
        fout.close();
    }
}
