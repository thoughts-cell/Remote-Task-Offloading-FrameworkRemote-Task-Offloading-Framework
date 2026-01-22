package Contract;

import java.io.Serializable;
/*This is the container to hold the class file of a compute-task that 
is to be transferred to the server*/
public class CFile implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    //The file name
    String fname;
    //The byte array hoding the class file 
    byte [] fbyte;

    public CFile(String fname, byte[] fbyte) {
        this.fname = fname;
        this.fbyte = fbyte;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public byte[] getFbyte() {
        return fbyte;
    }

    public void setFbyte(byte[] fbyte) {
        this.fbyte = fbyte;
    }
   
}
