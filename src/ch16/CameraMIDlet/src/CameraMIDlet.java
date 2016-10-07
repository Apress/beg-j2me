import java.io.*;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class CameraMIDlet
extends MIDlet
implements CommandListener, Runnable {
    private Display mDisplay;
    private Form mMainScreen;
    private Item mVideoItem;
    private VideoControl mVidc; 
    private Command mCaptureCommand;
    Image mQMarkImg = null;
    private ImageItem mCapturedImgItem = null;
    private Player mPlayer = null;
    private boolean mEndNow = false;
    
    public void startApp() {
        mDisplay = Display.getDisplay(this);
        if (mQMarkImg == null)  {
            try {
               mQMarkImg = Image.createImage("/qmark.png");
              } catch (Exception ex) {
                showException(ex);
            }
        }
        if (mMainScreen == null) {
            mMainScreen = new Form("Camera MIDlet");
            mMainScreen.addCommand(new Command("Exit", Command.EXIT, 0));
            mCaptureCommand = new Command("Capture", Command.SCREEN, 0);
            mMainScreen.addCommand(mCaptureCommand);
            mMainScreen.setCommandListener(this);
        }
        
        mDisplay.setCurrent(mMainScreen);
            Thread t = new Thread(this);
            t.start();
            mEndNow = false;
    }
    
    public void pauseApp() {}
    
    public void destroyApp(boolean unconditional) {
        
        if (mPlayer != null)  {
            mPlayer.close();
        }
        mEndNow = true;
        synchronized(this) {
          this.notify();
        }
    }
    
    public void commandAction(Command c, Displayable s) {
        if (c.getCommandType() == Command.EXIT) {
            destroyApp(true);
            notifyDestroyed();
        }
        else {
            // capture
           synchronized(this) {
               this.notify();
           }
        }
    }
    
    public void run() {
        viewCamera();
        while(!mEndNow) {
            synchronized(this) {
                try {
                this.wait();
                } catch (InterruptedException ex) {
                    // ignore
                }
               if (!mEndNow)
                try {
                byte[] rawImg = mVidc.getSnapshot(null);
                Image image = Image.createImage(rawImg, 0, rawImg.length);
                mCapturedImgItem.setImage(image);
                } catch (MediaException ex) {
                   continue;
                }

            }
            
        }
    }
    
    
    private void viewCamera() {
        try {
            //  InputStream in = getClass().getResourceAsStream("/fish.mpg");
            mPlayer = Manager.createPlayer("capture://video");
            // player.start();
            
            mPlayer.realize();
            if ((mVidc = (VideoControl) mPlayer.getControl("VideoControl")) != null) {
                mVideoItem = (Item)
                mVidc.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
                int prefLayout = Item.LAYOUT_2 | Item.LAYOUT_LEFT | 
                    Item.LAYOUT_NEWLINE_AFTER;
                mVideoItem.setLayout(prefLayout);
                mCapturedImgItem = 
                new ImageItem("", mQMarkImg, prefLayout, "");
               StringItem lab = new StringItem("Captured image:", "");
               lab.setLayout(prefLayout);
               
                mMainScreen.append(mVideoItem);
                mMainScreen.append(lab);
                mMainScreen.append(mCapturedImgItem);
            }
            
            mPlayer.start();
            mDisplay.setCurrent(mMainScreen);
        }
        catch (Exception e) {
            showException(e);
            return;
        }
    }
    
    private void showException(Exception e) {
        Alert a = new Alert("Exception", e.toString(), null, null);
        a.setTimeout(Alert.FOREVER);
        mDisplay.setCurrent(a, mMainScreen);
        
    }
    
    
    
}