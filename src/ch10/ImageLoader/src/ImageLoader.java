import java.io.*;

import javax.microedition.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class ImageLoader
    extends MIDlet
    implements CommandListener, Runnable {
  private Display mDisplay;
  private Form mForm;
  
  public ImageLoader() {
    mForm = new Form("Connecting...");
    mForm.addCommand(new Command("Exit", Command.EXIT, 0));
    mForm.setCommandListener(this);
  }
  
  public void startApp() {
    if (mDisplay == null) mDisplay = Display.getDisplay(this);
    mDisplay.setCurrent(mForm);
    
    // Do network loading in a separate thread.      
    Thread t = new Thread(this);
    t.start();
  }
  
  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {}
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT)
      notifyDestroyed();
  }
  
  public void run() {
    HttpConnection hc = null;
    DataInputStream in = null;
    
    try {
      String url = getAppProperty("ImageLoader-URL");
      hc = (HttpConnection)Connector.open(url);
      int length = (int)hc.getLength();
      byte[] data = null;
      if (length != -1) {
        data = new byte[length];
        in = new DataInputStream(hc.openInputStream());
        in.readFully(data);
      }
      else {
        // If content length is not given, read in chunks.
        int chunkSize = 512;
        int index = 0;
        int readLength = 0;
        in = new DataInputStream(hc.openInputStream());
        data = new byte[chunkSize];
        do {
          if (data.length < index + chunkSize) {
            byte[] newData = new byte[index + chunkSize];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
          }
          readLength = in.read(data, index, chunkSize);
          index += readLength;
        } while (readLength == chunkSize);
        length = index;
      }
      Image image = Image.createImage(data, 0, length);
      ImageItem imageItem = new ImageItem(null, image, 0, null);
      mForm.append(imageItem);
      mForm.setTitle("Done.");
    }
    catch (IOException ioe) {
      StringItem stringItem = new StringItem(null, ioe.toString());
      mForm.append(stringItem);
      mForm.setTitle("Done.");
    }
    finally {
      try {
        if (in != null) in.close();
        if (hc != null) hc.close();
      }
      catch (IOException ioe) {}
    }
  }
}

