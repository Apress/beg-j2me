import java.io.*;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.media.*;

public class AudioMIDlet
    extends MIDlet
    implements CommandListener, Runnable {
  private Display mDisplay;
  private List mMainScreen;
  
  public void startApp() {
    mDisplay = Display.getDisplay(this);
    
    if (mMainScreen == null) {
      mMainScreen = new List("AudioMIDlet", List.IMPLICIT);
  
      mMainScreen.append("Via HTTP", null);
      mMainScreen.append("From resource", null);
      mMainScreen.addCommand(new Command("Exit", Command.EXIT, 0));
      mMainScreen.addCommand(new Command("Play", Command.SCREEN, 0));
      mMainScreen.setCommandListener(this);
    }

    mDisplay.setCurrent(mMainScreen);
  }

  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) notifyDestroyed();
    else {
      Form waitForm = new Form("Loading...");
      mDisplay.setCurrent(waitForm);
      Thread t = new Thread(this);
      t.start();
    }
  }

  public void run() {
    String selection = mMainScreen.getString(
        mMainScreen.getSelectedIndex());
    boolean viaHttp = selection.equals("Via HTTP");

    if (viaHttp)
      playViaHttp();
    else
      playFromResource();
  }
  
  private void playViaHttp() {
    try {
      String url = getAppProperty("AudioMIDlet-URL");
      Player player = Manager.createPlayer(url);
      player.start();
    }
    catch (Exception e) {
      showException(e);
      return;
    }
    mDisplay.setCurrent(mMainScreen);
  }
  
  private void playFromResource() {
    try {
      InputStream in = getClass().getResourceAsStream("/relax.wav");
      Player player = Manager.createPlayer(in, "audio/x-wav");
      player.start();
    }
    catch (Exception e) {
      showException(e);
      return;
    }
    mDisplay.setCurrent(mMainScreen);
  }
  
  private void showException(Exception e) {
    Alert a = new Alert("Exception", e.toString(), null, null);
    a.setTimeout(Alert.FOREVER);
    mDisplay.setCurrent(a, mMainScreen);
  }
}
