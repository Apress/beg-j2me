import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;
import java.io.*;


public class MMSSender implements Runnable {
    private static MMSSender inst = new MMSSender();
    /** Creates a new instance of Sender */
    private  MMSSender() {
    }
    public static MMSSender getInstance() {
        return inst;
    }
    private String mReceiver = null;
    private String mAppID = null;
    
    private String mImageToSend = null;
    private boolean mSending = false;
    public void sendMsg(String rcvr, String appid, String img) {
        if (mSending) return;
        mReceiver = rcvr;
        mAppID= appid;
        mImageToSend = img;
        Thread th = new Thread(this);
        th.start();
    }
    public boolean isSending() {
        return   mSending;
    }
    // Send the color  message
    public void run() {
        mSending = true;
        try {
            sendMMS();
        } catch (Exception ex) {
            System.out.println("run() caught: ");
            ex.printStackTrace();
            
        }
        mSending = false;
    }
    
    public void sendMMS() {
        
        String address = "mms://" + mReceiver + ":" + mAppID;
        
        MessageConnection conn = null;
        try {
            /** Open the message connection. */
            conn = (MessageConnection) Connector.open(address);
            MultipartMessage mpMessage = (MultipartMessage) conn.newMessage(
            MessageConnection.MULTIPART_MESSAGE);
            
            mpMessage.setSubject("MMSMIDlet Image");
            InputStream is = getClass().getResourceAsStream(mImageToSend);
            byte[] bImage = new byte[is.available()];
            is.read(bImage);
            mpMessage.addMessagePart(new MessagePart(bImage, 0, bImage.length,
            "image/png", "id1",
            "location", null));
            conn.send(mpMessage);
        } catch (Throwable t) {
            System.out.println("Send caught: ");
            t.printStackTrace();
        }  finally {
            
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException ioe) {
                    System.out.println("Closing connection caught: ");
                    ioe.printStackTrace();
                }
            }
        }
    }
    
    
}
