import java.io.*;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class VideoMIDlet
extends MIDlet
implements CommandListener, Runnable {
    private Display mDisplay;
    private Form mMainScreen;
    private Item mVideoItem;
    private VideoControl mVidc;
    private Command mPlayCommand;
    private Player mPlayer = null;
    
    
    public void startApp() {
        mDisplay = Display.getDisplay(this);
        
        if (mMainScreen == null) {
            mMainScreen = new Form("Video MIDlet");
            mMainScreen.addCommand(new Command("Exit", Command.EXIT, 0));
            mPlayCommand = new Command("Play", Command.SCREEN, 0);
            mMainScreen.addCommand(mPlayCommand);
            mMainScreen.setCommandListener(this);
        }
        
        mDisplay.setCurrent(mMainScreen);
    }
    
    public void pauseApp() {}
    
    public void destroyApp(boolean unconditional) {
        if (mPlayer != null)  {
                mPlayer.close();
        }
        
    }
    
    public void commandAction(Command c, Displayable s) {
        if (c.getCommandType() == Command.EXIT) {
            destroyApp(true);
            notifyDestroyed();
        }
        else {
            Form waitForm = new Form("Loading...");
            mDisplay.setCurrent(waitForm);
            Thread t = new Thread(this);
            t.start();
        }
    }
    
    public void run() {
        playFromResource();
    }
    
    
    private void playFromResource() {
        try {
            InputStream in = getClass().getResourceAsStream("/fish.mpg");
            mPlayer = Manager.createPlayer(in, "video/mpeg");
            // player.start();
            
            mPlayer.realize();
            if ((mVidc = (VideoControl) mPlayer.getControl("VideoControl")) != null) {
                mVideoItem = (Item)
                 mVidc.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE, null);
                mMainScreen.append(mVideoItem);
            }
            
            mPlayer.start();
            mMainScreen.removeCommand(mPlayCommand);
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
