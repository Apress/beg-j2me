
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;

import java.io.IOException;

public class MMSMIDlet
extends MIDlet
implements CommandListener, MessageListener,Runnable {
    private MMSSender mSender = null;
    private Thread mReceiver = null;
    private Command mExitCommand  = new Command("Exit", Command.EXIT, 2);
    private Command mRedCommand = new Command("Send Red", Command.SCREEN, 1);
    private Command mBlueCommand = new Command("Send Blue", Command.SCREEN, 1);
    protected static final String RED_IMAGE = "/red.png";
    protected static final String BLUE_IMAGE = "/blue.png";
    protected static final String DEFAULT_IMAGE = "/wait.png";
    
    private Display mDisplay = null;
    protected ImageItem mColorSquare = null;
    protected Image mInitialImage = null;
    private String mAppID = "MMSMIDlet";
    private TextField mNumberEntry= null;
    private Form mForm = null;
    private  Integer mMonitor = new Integer(0);
    
    public MMSMIDlet() {
        mSender = MMSSender.getInstance();
        
    }
    
    public void commandAction(javax.microedition.lcdui.Command c,
    javax.microedition.lcdui.Displayable d) {
        if (c == mExitCommand) {
            if (!mSender.isSending()) {
                destroyApp(true);
                notifyDestroyed();
            }
        } else if (c == mRedCommand) {
            String dest = mNumberEntry.getString();
            if (dest.length() > 0)
                mSender.sendMsg(dest, mAppID, RED_IMAGE);
            
        } else if (c == mBlueCommand) {
            String dest = mNumberEntry.getString();
            if (dest.length() > 0)                
                mSender.sendMsg(dest, mAppID, BLUE_IMAGE);
        }
        
    }
    
    
    protected void destroyApp(boolean param) {
        mEndNow = true;
        try {
            conn.close();
        } catch (IOException ex) {
            System.out.println("destroyApp caught: ");
            ex.printStackTrace();
 
        }
    }
    
    protected void pauseApp() {
        mEndNow = true;
        try {
            conn.setMessageListener(null);
            conn.close();
        } catch (IOException ex) {
            System.out.println("pauseApp caught: ");
            ex.printStackTrace();
 
        }
    }
    
    protected void startApp()
    throws javax.microedition.midlet.MIDletStateChangeException {
        if (mForm == null) {
            mForm = new Form(mAppID);
            mNumberEntry = new TextField("Connect to:",
            null, 256, TextField.PHONENUMBER);
            try {
                mInitialImage = Image.createImage(DEFAULT_IMAGE);
                
            } catch (Exception ex) {
            System.out.println("startApp caught: ");
            ex.printStackTrace();
 
            }
            
            mColorSquare = new ImageItem(null, mInitialImage,ImageItem.
            LAYOUT_DEFAULT, "waiting for image");
            mForm.append(mNumberEntry);
            mForm.append(mColorSquare);
            mForm.addCommand(mExitCommand);
            mForm.addCommand(mRedCommand);
            mForm.addCommand(mBlueCommand);
            
            mForm.setCommandListener(this);
            
        }
        Display.getDisplay(this).setCurrent(mForm);
        
        try {
            conn = (MessageConnection) Connector.open("mms://:" + mAppID);
            conn.setMessageListener(this);
            
        }  catch (Exception e) {
            System.out.println("startApp caught: ");
            e.printStackTrace();
        }
        if (conn != null) {
            
            startReceive();
        }
    }
    private boolean mEndNow = false;
    private void startReceive() {
        
        mEndNow = false;
        // start receive thread
        mReceiver = new Thread(this);
        mReceiver.start();
    }
    

    protected MessageConnection conn = null;
    
    protected int mMsgAvail = 0;
    public void run() {
        Message msg  = null;
        String msgReceived = null;
        Image receivedImage = null;
        
        mMsgAvail = 0;
        
        while (!mEndNow) {
            synchronized(mMonitor) {  // enter monitor
                if (mMsgAvail <= 0)
                    try {
                        System.out.println("going into monitor");
                        mMonitor.wait();
                        
                    } catch (InterruptedException ex) {
                    }
                System.out.println("coming out of monitor");
                        
                mMsgAvail--;
            }
            try {
                msg = conn.receive();
                if (msg instanceof MultipartMessage) {
                    MultipartMessage mpm = (MultipartMessage)msg;
                    
                    MessagePart[] parts = mpm.getMessageParts();
                    if (parts != null) {
                        for (int i = 0; i < parts.length; i++) {
                            MessagePart mp = parts[i];
                            byte[] ba = mp.getContent();
                            receivedImage = Image.createImage(ba, 0, ba.length);
                            Display.getDisplay(this).callSerially(
                            new SetImage(receivedImage));
                        } //of for
                    }
                }
                
                
            } catch (IOException e) {
                System.out.println("Receive thread caught: ");
                e.printStackTrace();
            }
            
        } // of while
    }
    private  void getMessage() {
        synchronized(mMonitor) {
        System.out.println("** got a message **");
        mMsgAvail++;
        mMonitor.notify();
        }
    }
    public void notifyIncomingMessage(MessageConnection msgConn) {
        if (msgConn == conn)
            getMessage();
        
        
    }
    
    class SetImage implements Runnable {
        private Image img = null;
        public SetImage(Image inImg) {
            img = inImg;
        }
        public void run() {
            mColorSquare.setImage(img);
        }
    }
    
}
