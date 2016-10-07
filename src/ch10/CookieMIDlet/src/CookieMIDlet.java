import java.io.*;

import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class CookieMIDlet
    extends MIDlet
    implements CommandListener, Runnable {
  private Display mDisplay;
  private Form mForm;
  
  private String mSession;
  
  public void startApp() {
    mDisplay = Display.getDisplay(this);
    
    if (mForm == null) {
      mForm = new Form("CookieMIDlet");
  
      mForm.addCommand(new Command("Exit", Command.EXIT, 0));
      mForm.addCommand(new Command("Send", Command.SCREEN, 0));
      mForm.setCommandListener(this);
    }

    mDisplay.setCurrent(mForm);
  }

  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) notifyDestroyed();
    else {
      Form waitForm = new Form("Connecting...");
      mDisplay.setCurrent(waitForm);
      Thread t = new Thread(this);
      t.start();
    }
  }

  public void run() {
    String url = getAppProperty("CookieMIDlet-URL");

    try {
      // Query the server and retrieve the response.
      HttpConnection hc = (HttpConnection)Connector.open(url);
      if (mSession != null)
        hc.setRequestProperty("cookie", mSession);
      InputStream in = hc.openInputStream();
      
      String cookie = hc.getHeaderField("Set-cookie");
      if (cookie != null) {
        int semicolon = cookie.indexOf(';');
        mSession = cookie.substring(0, semicolon);
      }
      int length = (int)hc.getLength();
      byte[] raw = new byte[length];
      in.read(raw);

      String s = new String(raw);
      Alert a = new Alert("Response", s, null, null);
      a.setTimeout(Alert.FOREVER);
      mDisplay.setCurrent(a, mForm);

      in.close();
      hc.close();
    }
    catch (IOException ioe) {
      Alert a = new Alert("Exception", ioe.toString(), null, null);
      a.setTimeout(Alert.FOREVER);
      mDisplay.setCurrent(a, mForm);
    }
  }
}
