
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;

import java.io.IOException;

public class SMSMIDlet
extends MIDlet
implements CommandListener, Runnable {
    private Sender mSender = null;
    private Thread mReceiver = null;
    private Command mExitCommand  = new Command("Exit", Command.EXIT, 2);
    private Command mRedCommand = new Command("Send Red", Command.SCREEN, 1);
    private Command mBlueCommand = new Command("Send Blue", Command.SCREEN, 1);
    
    private Display mDisplay = null;
    protected ImageItem mColorSquare = null;
    protected Image [] mImages = new Image[2];
    protected Image waitImage = null;
    private String mPort = "1234";
    private TextField mNumberEntry= null;
    private Form mForm = null;
    
    private String mSenderAddress = null;
    public SMSMIDlet() {
        mSender = Sender.getInstance();
        
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
                mSender.sendMsg(dest, mPort, "red");
            
        } else if (c == mBlueCommand) {
            String dest = mNumberEntry.getString();
            if (dest.length() > 0)
                
                mSender.sendMsg(dest, mPort, "blue");
        }
        
    }
    
    
    protected void destroyApp(boolean param) {
        try {
            mEndNow = true;
            conn.close();
        } catch (IOException ex) {
            System.out.println("destroyApp caught: ");
            ex.printStackTrace();
            
        }
        
    }
    
    protected void pauseApp() {
        
    }
    
    protected void startApp() {
        if (mForm == null) {
            mForm = new Form("SMSMIDlet");
            mNumberEntry = new TextField("Connect to:",
            null, 256, TextField.PHONENUMBER);
            try {
                mImages[0] = Image.createImage("/red.png");
                mImages[1] = Image.createImage("/blue.png");
                waitImage = Image.createImage("/wait.png");
            } catch (Exception ex) {
             System.out.println("startApp caught: ");
            ex.printStackTrace();
            }
            
            mColorSquare = new ImageItem(null, waitImage,ImageItem.
            LAYOUT_DEFAULT, "colored square");
            mForm.append(mNumberEntry);
            mForm.append(mColorSquare);
            mForm.addCommand(mExitCommand);
            mForm.addCommand(mRedCommand);
            mForm.addCommand(mBlueCommand);
            
            mForm.setCommandListener(this);
        }
        Display.getDisplay(this).setCurrent(mForm);
        startReceive();
        
    }
    private void startReceive() {
        if (mReceiver != null)
            return;
        // start receive thread
        mReceiver = new Thread(this);
        mReceiver.start();
    }
    private boolean mEndNow = false;
    private MessageConnection conn = null;
    public void run() {
        Message msg  = null;
        String msgReceived = null;
        conn = null;
        mEndNow = false;
        /** Check for sms connection. */
        try {
            conn = (MessageConnection) Connector.open("sms://:" + mPort);
            msg = conn.receive();
            while ((msg != null) && (!mEndNow)) {
                
                if (msg instanceof TextMessage) {
                    
                    msgReceived = ((TextMessage)msg).getPayloadText();
                    if (msgReceived.equals("red")) {
                        Display.getDisplay(this).callSerially(new SetRed());
                    } else if (msgReceived.equals("blue")) {
                        Display.getDisplay(this).callSerially(new SetBlue());
                    }
                }
                
                msg = conn.receive();
            }
        } catch (IOException e) {
            // normal exit when connection is closed
        }
        
        
    }
    class SetRed implements Runnable {
        Display disp = null;
        
        public void run() {
            mColorSquare.setImage(mImages[0]);
        }
    }
    class SetBlue implements Runnable {
        public void run() {
            mColorSquare.setImage(mImages[1]);
        }
    }
}
