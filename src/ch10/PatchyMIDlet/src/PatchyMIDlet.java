import java.io.*;

import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.pki.*;

public class PatchyMIDlet
    extends MIDlet
    implements CommandListener, Runnable {
  private Display mDisplay;
  private Form mForm;
  
  private ServerSocketConnection mServerSocketConnection;
  private boolean mTrucking = true;
  
  public void startApp() {
    mDisplay = Display.getDisplay(this);
    
    if (mForm == null) {
      mForm = new Form("PatchyMIDlet");
  
      mForm.addCommand(new Command("Exit", Command.EXIT, 0));
      mForm.setCommandListener(this);
    }
    
    Thread t = new Thread(this);
    t.start();
    
    mDisplay.setCurrent(mForm);
  }

  public void pauseApp() {}

  public void destroyApp(boolean unconditional) { shutdown(); }
  
  private void log(String text) { log(null, text); }
  
  private void log(String label, String text) {
    StringItem si = new StringItem(label, text);
    si.setLayout(Item.LAYOUT_NEWLINE_AFTER);
    mForm.append(si);
  }
  
  private void shutdown() {
    mTrucking = false;
    try { mServerSocketConnection.close(); }
    catch (IOException ioe) {}
  }
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) {
      shutdown();
      notifyDestroyed();
    }
  }

  public void run() {
    try {
      mServerSocketConnection = (ServerSocketConnection)
          Connector.open("socket://:80");
      log("Startup complete.");
      SocketConnection sc = null;
      while (mTrucking) {
        sc = (SocketConnection)
          mServerSocketConnection.acceptAndOpen();
        log("client: ", sc.getAddress());
        // Strictly speaking, each client connection
        // should be handled in its own thread. For
        // simplicity, this implementation handles
        // client connections inline.
        Reader in = new InputStreamReader(
            sc.openInputStream());
        String line;
        while ((line = readLine(in)) != null) ;
        // Ignoring the request, send a response.
        PrintStream out = new PrintStream(sc.openOutputStream());
        out.print("HTTP/1.1 200 OK\r\n\r\n");
        out.print(getMessage());
        out.close();
        in.close();
        sc.close();
      }
    }
    catch (Exception e) {
      log("exception: ", e.toString());
    }
  }
  
  private String readLine(Reader in) throws IOException {
    // This is not efficient.
    StringBuffer line = new StringBuffer();
    int i;
    while ((i = in.read()) != -1) {
      char c = (char)i;
      if (c == '\n') break;
      if (c == '\r') ;
      else line.append(c);
    }
    if (line.length() == 0) return null;
    return line.toString();
  }
  
  private java.util.Random mRandom = new java.util.Random();
  
  private String getMessage() {
    int i = Math.abs(mRandom.nextInt()) % 5;
    String s = null;
    switch (i) {
      case 0: s = "Above all the others we'll fly"; break;
      case 1: s = "There is no reason to hide"; break;
      case 2: s = "I dreamed about Ray Charles last night"; break;
      case 3: s = "Someone keeps moving my chair"; break;
      case 4: s = "Joseph's face was black as night"; break;
      default: break;
    }
    return s;
  }
}
