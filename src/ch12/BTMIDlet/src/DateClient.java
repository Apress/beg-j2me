import java.io.*;
import javax.microedition.io.*;
import javax.bluetooth.*;

public final class DateClient implements Runnable {
    private Thread mClientThread = null;
    private static DateClient inst = new DateClient();
    private  DateClient() {
    }
    public static DateClient getInstance() {
        return inst;
    }
    private boolean mEndNow = false;
    
    private static final UUID DATING_SERVICE_ID =
        new UUID("BAE0D0C0B0A000955570605040302010", false);
    private String mDateConnect = null;
    private DiscoveryAgent mDiscoveryAgent = null;
    
    private String mPref = null;
    private String mHeight = null;
    private String mContact = null;
    
    public void setMyInfo(String inPref, String inHeight, String inContact) {
        mPref = inPref;
        mHeight = inHeight;
        mContact = inContact;
    }
    public void startClient() {
        if (mClientThread != null)
            return;
        mEndNow = false;
        // start receive thread
        mClientThread = new Thread(this);
        mClientThread.start();
    }
    public void stopClient() {
        mEndNow = true;
        try {
            mClientThread.join();
        } catch (Exception ex) {}
        mClientThread = null;
    }
    
    
    public void run() {
        // this is the BT client portion of the dating service
        try {
            mDiscoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
        } catch (Exception ex) {
        }
        
        StreamConnection conn  = null;
        DataOutputStream dos  = null;
        while( !mEndNow)  {
            try  {
                 mDateConnect = mDiscoveryAgent.selectService(DATING_SERVICE_ID,
                ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                
                if (mDateConnect != null)  {
                    conn = (StreamConnection) Connector.open(mDateConnect);
                    dos = conn.openDataOutputStream();
                    dos.writeUTF(mPref);
                    dos.writeUTF(mHeight);
                    dos.writeUTF(mContact);
                    dos.flush();
                    dos.close();
                }
                
            } catch (Exception ex) {
            }
        }
    }
    
    
}
