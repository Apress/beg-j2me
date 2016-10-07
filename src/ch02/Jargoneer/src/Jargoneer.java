import java.io.*;

import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class Jargoneer extends MIDlet
    implements CommandListener, Runnable {
  private Display mDisplay;

  private Command mExitCommand, mFindCommand, mCancelCommand;

  private TextBox mSubmitBox;
  private Form mProgressForm;
  private StringItem mProgressString;

  public Jargoneer() {
    mExitCommand = new Command("Exit", Command.EXIT, 0);
    mFindCommand = new Command("Find", Command.SCREEN, 0);
    mCancelCommand = new Command("Cancel", Command.CANCEL, 0);
    
    mSubmitBox = new TextBox("Jargoneer", "", 32, 0);
    mSubmitBox.addCommand(mExitCommand);
    mSubmitBox.addCommand(mFindCommand);
    mSubmitBox.setCommandListener(this);
    
    mProgressForm = new Form("Lookup progress");
    mProgressString = new StringItem(null, null);
    mProgressForm.append(mProgressString);
  }

  public void startApp() {
    mDisplay = Display.getDisplay(this);
    
    mDisplay.setCurrent(mSubmitBox);
  }

  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}

  public void commandAction(Command c, Displayable s) {
    if (c == mExitCommand) {
      destroyApp(false);
      notifyDestroyed();
    }
    else if (c == mFindCommand) {
      // Show the progress form.
      mDisplay.setCurrent(mProgressForm);
      // Kick off the thread to do the query.
      Thread t = new Thread(this);
      t.start();
    }
  }

  public void run() {
    String word = mSubmitBox.getString();
    String definition;
    
    try { definition = lookUp(word); }
    catch (IOException ioe) {
      Alert report = new Alert(
          "Sorry",
          "Something went wrong and that " +
          "definition could not be retrieved.",
          null, null);
      report.setTimeout(Alert.FOREVER);
      mDisplay.setCurrent(report, mSubmitBox);
      return;
    }
    
    Alert results = new Alert("Definition", definition,
        null, null);
    results.setTimeout(Alert.FOREVER);
    mDisplay.setCurrent(results, mSubmitBox);
  }
  
  private String lookUp(String word) throws IOException {
    HttpConnection hc = null;
    InputStream in = null;
    String definition = null;
    
    try {
      String baseURL = "http://65.215.221.148:8080/wj2/jargoneer?word=";
      String url = baseURL + word;
      mProgressString.setText("Connecting...");
      hc = (HttpConnection)Connector.open(url);
      hc.setRequestProperty("Connection", "close");
      in = hc.openInputStream();
      
      mProgressString.setText("Reading...");
      int contentLength = (int)hc.getLength();
      if (contentLength == -1) contentLength = 255;
      byte[] raw = new byte[contentLength];
      int length = in.read(raw);

      // Clean up.
      in.close();
      hc.close();

      definition = new String(raw, 0, length);
    }
    finally {
      try {
        if (in != null) in.close();
        if (hc != null) hc.close();
      }
      catch (IOException ignored) {}
    }
    
    return definition;
  }
}