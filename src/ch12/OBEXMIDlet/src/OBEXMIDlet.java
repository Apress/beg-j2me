import java.io.*;
import javax.microedition.midlet.*;
import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.obex.*;

import java.io.IOException;

public class OBEXMIDlet
extends MIDlet
implements CommandListener, Runnable {    
    private DateClient mClient = null;
    private Thread mServer = null;
    private OperationHandler mOpHandler = null;
    
    private Command mExitCommand  = new Command("Exit", Command.EXIT, 2);
    private Display mDisplay = null;
    private StringItem mDateContact = null;
    private Form mForm = null;
    
    
    private boolean mEndNow = false;
    private String mLastContact = "";


    
    private String myPref = null;
    private String myHeight = null;
    private String myContact = null;
    
    private String seekPref = null;
    private String seekHeight = null;
    private String seekContact = null;
    
    private SessionNotifier mServerNotifier;
    private static final String url =
        "irdaobex://localhost;ias=DatingService";
    private Connection mConnection = null;

    
    public OBEXMIDlet() {
        mClient = DateClient.getInstance();
        myPref = getAppProperty("OBEXMIDlet-mypref");
        myHeight = getAppProperty("OBEXMIDlet-myheight");
        myContact = getAppProperty("OBEXMIDlet-mycontact");
        seekPref = getAppProperty("OBEXMIDlet-seekpref");
        seekHeight = getAppProperty("OBEXMIDlet-seekheight");
        
    }
    
    public void commandAction(Command c, Displayable d) {
        if (c == mExitCommand) {            
            destroyApp(true);
            notifyDestroyed();
        }
    }
    
    
    protected void destroyApp(boolean param) {
        mEndNow = true;
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
            mForm = new Form("OBEXMIDlet");
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
        mOpHandler = new OperationHandler(this);
    }
    private void startServer() {
        if (mServer != null)
            return;
        // start server thread
        mServer = new Thread(this);
        mServer.start();
        
    }
    
    public void run() {
        try {
            //      System.out.println("b4 svr gcf open...");           
            mServerNotifier = (SessionNotifier) Connector.open(url);
            //      System.out.println("after svr gcf open...");
        } catch (Exception e) {
            System.err.println("Can't initialize OBEX server: " + e);
        }
        
        while (!mEndNow) {
            mConnection = null;
            
            try {
                mConnection = mServerNotifier.acceptAndOpen(mOpHandler);
            } catch (IOException e) {
                continue;
            }
            
            
            //          System.out.println(" got a connection!");
            try {
                // bad bad API design, need to synchronize server thread
                synchronized(this) {
                    this.wait();
                }
                
                //       System.out.println("svr: before conn close");
                mConnection.close();
            } catch (Exception ex) {
                // log exception
            }
            
        }  // of while
        try {
            mServerNotifier.close();
        } catch (Exception ex) {
            System.out.println("trying to close session...exception");
            ex.printStackTrace();
        }
    }
    
    
    private void processRequest(DataInputStream dis) {
        String pref = null;
        String height = null;
        String contact = null;
        try {
            pref = dis.readUTF();
            height = dis.readUTF( );
            contact = dis.readUTF();
            
            // System.out.println("got a connect from " + pref + "," + height + "," +contact);
            dis.close();
        } catch (IOException e) {
            System.out.println("in process request exception");
            e.printStackTrace();
        } 

        if (! mLastContact.equals(contact)) {
            mLastContact = contact;
            if (pref.equals(seekPref)  && height.equals(seekHeight))
                mDisplay.callSerially(new ShowCandidate(contact));
        }
    }
    
    class OperationHandler extends ServerRequestHandler {
        DataInputStream dis = null;
        Object syncObject = null;
        public OperationHandler( Object inSync) {
            syncObject = inSync;
        }
        public int onPut(Operation op) {
            dis = null;
            try {
                dis = op.openDataInputStream();
            } catch (Exception ex) {
                // okay for CREATE_EMPTY op
            }
            if (dis != null)   // not a  CREATE_EMPTY op
            {
                processRequest(dis);
                try {
                    dis.close();
                    op.close();
                } catch (Exception ex) {
                }
                dis = null;
                synchronized(syncObject) {
                    syncObject.notify();
                }
            }
            return ResponseCodes.OBEX_HTTP_OK;
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
