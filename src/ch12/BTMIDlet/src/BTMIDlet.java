import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.bluetooth.*;

import java.io.IOException;

public class BTMIDlet
extends MIDlet
implements CommandListener, Runnable {
    private DateClient mClient = null;
    private Thread mServer = null;
    
    private Command mExitCommand  = new Command("Exit", Command.EXIT, 2);
    
    private Display mDisplay = null;
    private StringItem mDateContact = null;
    private Form mForm = null;
    private boolean mEndNow = false;
    
    private String mLastContact = "";
    private LocalDevice mLocalBT;
    private StreamConnectionNotifier mServerNotifier;
    private static final UUID DATING_SERVICE_ID =
    new UUID("BAE0D0C0B0A000955570605040302010", false);
    
    
    private String myPref = null;
    private String myHeight = null;
    private String myContact = null;
    
    private String seekPref = null;
    private String seekHeight = null;
    private String seekContact = null;
    
    public BTMIDlet() {
        mClient = DateClient.getInstance();
        myPref = getAppProperty("BTMIDlet-mypref");
        myHeight = getAppProperty("BTMIDlet-myheight");
        myContact = getAppProperty("BTMIDlet-mycontact");
        seekPref = getAppProperty("BTMIDlet-seekpref");
        seekHeight = getAppProperty("BTMIDlet-seekheight");
        
    }
    
    public void commandAction(javax.microedition.lcdui.Command c,
    javax.microedition.lcdui.Displayable d) {
        if (c == mExitCommand) {
            destroyApp(true);
            notifyDestroyed();
            
        }
    }
    
    
    protected void destroyApp(boolean param) {
        mEndNow = true;
        
        // finilize notifier work
        if (mServerNotifier != null) {
            try {
                mServerNotifier.close();
            } catch (IOException e) {} // ignore
        }
        
        // wait for acceptor thread is done
        try {
            mServer.join();
        } catch (InterruptedException e) {} // ignore
        try {
            mClient.stopClient();
        } catch (Exception e) {} // ignore
        
        
    }
    
    protected void pauseApp() {
        
    }
    
    protected void startApp() {
        if (mForm == null) {
            mForm = new Form("BTMIDlet");
            mDateContact = new StringItem("Potential date found at:",null);
            mForm.append(mDateContact);
            mForm.addCommand(mExitCommand);
            mForm.setCommandListener(this);
        }
        mDisplay = Display.getDisplay(this);
        mDisplay.setCurrent(mForm);
        mEndNow = false;
        startServer();
        mClient.setMyInfo(myPref, myHeight, myContact);
        mClient.startClient();
    }
    private void startServer() {
        if (mServer != null)
            return;
        // start receive thread
        mServer = new Thread(this);
        mServer.start();
        
    }
    
    public void run() {
        
        
        
        
        try {
            // get local BT manager
            mLocalBT = LocalDevice.getLocalDevice();
            
            // set we are discoverable
            mLocalBT.setDiscoverable(DiscoveryAgent.GIAC);
            String url = "btspp://localhost:" + DATING_SERVICE_ID.toString() +
            ";name=Dating Service;authorize=false";
            
            // create notifier now
            mServerNotifier = (StreamConnectionNotifier) Connector.open(
            url.toString());
            //System.out.println(" got notifier ");
            
        } catch (Exception e) {
            System.err.println("Can't initialize bluetooth: " + e);
        }
        StreamConnection conn = null;
        while (!mEndNow) {
            conn = null;
            
            try {
                conn = mServerNotifier.acceptAndOpen();
            } catch (IOException e) {
                
                continue;
            }
            
            
            //        System.out.println(" got a connection!");
            if (conn != null)
                processRequest(conn);
        }
        
    }
    
    
    private void processRequest(StreamConnection conn) {
        DataInputStream dis = null;
        String pref = null;
        String height = null;
        String contact = null;
        try {
            dis = conn.openDataInputStream();
            
            pref = dis.readUTF();
            height = dis.readUTF( );
            contact = dis.readUTF();
            
            //  System.out.println("got a connect from " + pref + "," + height + "," +contact);
            
            
            dis.close();
            conn.close();
            
        } catch (IOException e) {} // ignore
        if (!mLastContact.equals(contact)) {
            mLastContact = contact;
            if (pref.equals(seekPref)  && height.equals(seekHeight))
                mDisplay.callSerially(new ShowCandidate(contact));
        }
    }
    class ShowCandidate implements Runnable {
        Display disp = null;
        String contact = null;
        public ShowCandidate(String cont) {
            contact = cont;
        }
        public void run() {
            mDateContact.setText(contact );
        }
    }
    
}
