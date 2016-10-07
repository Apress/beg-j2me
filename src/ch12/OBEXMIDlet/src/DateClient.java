import java.io.*;
import javax.microedition.io.*;
import javax.obex.*;

public final class DateClient implements Runnable {
    private Thread mClientThread = null;
    private static DateClient inst = new DateClient();
    private  DateClient() {
    }
    public static DateClient getInstance() {
        return inst;
    }
    private boolean mEndNow = false;
    private String mPref = null;
    private String mHeight = null;
    private String mContact = null;
    
    private static final String url =
    "irdaobex://discover;ias=DatingService";
    
    
    
    
    public void setMyInfo(String inPref, String inHeight,
    String inContact) {
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
            // only on CLDC 1.1
            // mClientThread.interrupt();
            mClientThread.join();
        } catch (Exception ex) {
            System.out.println("in stop client");
            ex.printStackTrace();
        }
        mClientThread = null;
        
    }
    
    public void run() {
        DataOutputStream dos  = null;
        Operation op = null;
        ClientSession ses = null;
        int code = 0;
        HeaderSet resp = null;
        HeaderSet hdrs = null;
        
        while( !mEndNow)  {
            ses = null;
            dos = null;
            op = null;
            try  {
                
                // System.out.println("b4 client gcf open");
                
                ses = (ClientSession) Connector.open(url);
                
                //  System.out.println("after client gcf open");
            } catch (IOException ex) {
                // discovery fails, sleep for a while and try again
                try {
                    Thread.sleep(3000l);
                } catch (Exception e) {}
                continue;
            }
            
            try {
                resp = ses.connect(null);
                code = resp.getResponseCode();
                
                if (code != ResponseCodes.OBEX_HTTP_OK) {
                    throw new IOException("OBEX connect operation failed");
                }
                hdrs = ses.createHeaderSet();
                op = ses.put(hdrs);
                dos = null;
                dos = op.openDataOutputStream();
                
                
                if (dos != null)  {
                    dos.writeUTF(mPref);
                    dos.writeUTF(mHeight);
                    dos.writeUTF(mContact);
                    dos.flush();
                    dos.close();
                    
                    code = op.getResponseCode();
                    //   System.out.println("before os close");
                    if (code != ResponseCodes.OBEX_HTTP_OK) {
                        throw new IOException("OBEX failure after put operations");
                    }
                    //   System.out.println("before op close");
                    
                    op.close();
                    
                }
                
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }  // while
    } // run
    
}
