package Contract;

import java.io.Serializable;

/**
 *
 * @author Zhengxu Sun 12233612 
 */
public class CSAuthenticator implements Serializable {

    String PlainUserName;
    String CipherUserName;
    String VerificationString;
    String SessionKey;

    public CSAuthenticator(String PlainUserName, String CipherUserName, String VerificationString, String SessionKey) {
        this.PlainUserName = PlainUserName;
        this.CipherUserName = CipherUserName;
        this.VerificationString = VerificationString;
        this.SessionKey = SessionKey;
    }

    public CSAuthenticator(String PlainUserName, String CipherUserName, String VerificationString) {
        this.PlainUserName = PlainUserName;
        this.CipherUserName = CipherUserName;
        this.VerificationString = VerificationString;
        this.SessionKey = null;
    }

    public String getPlainUserName() {
        return PlainUserName;
    }

    public String getCipherUserName() {
        return CipherUserName;
    }

    public String getVerificationString() {
        return VerificationString;
    }

    public String getSessionKey() {
        return SessionKey;
    }

}
