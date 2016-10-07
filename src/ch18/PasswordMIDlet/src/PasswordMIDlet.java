import java.io.*;
import java.util.Random;

import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class PasswordMIDlet
    extends MIDlet
    implements CommandListener, Runnable { 
  private Display mDisplay;
  private Form mForm;
  private TextField mUserField, mPasswordField;
  private Random mRandom;
  
  public void startApp() { 
    mDisplay = Display.getDisplay(this);
    mRandom = new Random(System.currentTimeMillis());
    
    if (mForm == null) { 
      mForm = new Form("Login");
      mUserField = new TextField("Name", "jonathan", 32, 0);
      mPasswordField = new TextField("Password", "happy8", 32, 0);
      mForm.append(mUserField);
      mForm.append(mPasswordField);
      
      mForm.addCommand(new Command("Exit", Command.EXIT, 0));
      mForm.addCommand(new Command("Login", Command.SCREEN, 0));
      mForm.setCommandListener(this);
    } 

    mDisplay.setCurrent(mForm);
  } 
  
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
    // Gather the values we’ll need.
    long timestamp = System.currentTimeMillis();
    long randomNumber = mRandom.nextLong();
    String user = mUserField.getString();
    byte[] userBytes = user.getBytes();
    byte[] timestampBytes = getBytes(timestamp);
    byte[] randomBytes = getBytes(randomNumber);
    String password = mPasswordField.getString();
    byte[] passwordBytes = password.getBytes();
    
    // Create the message digest.
    Digest digest = new SHA1Digest();
    // Calculate the digest value.
    digest.update(userBytes, 0, userBytes.length);
    digest.update(timestampBytes, 0, timestampBytes.length);
    digest.update(randomBytes, 0, randomBytes.length);
    digest.update(passwordBytes, 0, passwordBytes.length);
    byte[] digestValue = new byte[digest.getDigestSize()];
    digest.doFinal(digestValue, 0);

    // Create the GET URL. The hex encoded message digest value is
    //   included as a parameter.
    URLBuilder ub = new URLBuilder(getAppProperty("PasswordMIDlet-URL"));
    ub.addParameter("user", user);
    ub.addParameter("timestamp",
        new String(HexCodec.bytesToHex(timestampBytes)));
    ub.addParameter("random",
        new String(HexCodec.bytesToHex(randomBytes)));
    ub.addParameter("digest",
        new String(HexCodec.bytesToHex(digestValue)));
    String url = ub.toString();

    try { 
      // Query the server and retrieve the response.
      HttpConnection hc = (HttpConnection)Connector.open(url);
      InputStream in = hc.openInputStream();
      
      int length = (int)hc.getLength();
      byte[] raw = new byte[length];
      in.read(raw);
      String response = new String(raw);
      Alert a = new Alert("Response", response, null, null);
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
  
  private byte[] getBytes(long x) { 
    byte[] bytes = new byte[8];
    for (int i = 0; i < 8; i++)
      bytes[i] = (byte)(x >> ((7 - i) * 8));
    return bytes;
  } 
  
  public void pauseApp() { } 

  public void destroyApp(boolean unconditional) { } 
} 

