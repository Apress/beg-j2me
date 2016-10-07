
import javax.microedition.io.*;
import javax.wireless.messaging.*;

import java.io.IOException;

public class Sender implements Runnable {
    private static Sender inst = new Sender();
    private  Sender() {
    }
    public static Sender getInstance() {
        return inst;
    }
    private String mReceiver = null;
    private String mPort = null;
    
    private String msgString = null;
    private boolean mSending = false;
    public void sendMsg(String rcvr, String port, String msgText) {
        if (mSending) return;
        mReceiver = rcvr;
        mPort = port;
        msgString = msgText;
        Thread th = new Thread(this);
        th.start();
    }
    public boolean isSending() {
        return mSending;
    }
    // Send the color  message
    public void run() {
        mSending = true;
        try {
            sendSMS();
        } catch (Exception ex) {
            System.out.println("run() caught: ");
            ex.printStackTrace();
        }
        mSending = false;
    }
    
    private void sendSMS() {
        
        String address = "sms://" + mReceiver + ":" + mPort;
        
        MessageConnection conn = null;
        try {
            /** Open the message connection. */
            conn = (MessageConnection) Connector.open(address);
            TextMessage txtmessage = (TextMessage) conn.newMessage(
            MessageConnection.TEXT_MESSAGE);
            txtmessage.setAddress(address);
            txtmessage.setPayloadText(msgString);
            conn.send(txtmessage);
        } catch (Throwable t) {
            System.out.println("Send caught: ");
            t.printStackTrace();
        }
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
