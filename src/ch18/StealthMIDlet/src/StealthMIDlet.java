import java.io.*;

import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
 
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;

public class StealthMIDlet
    extends MIDlet 
    implements CommandListener, Runnable { 
  private Display mDisplay;
  private TextBox mTextBox;
  
  private String mSession;
  private StreamCipher mOutCipher, mInCipher;
  
  public StealthMIDlet() { 
    mOutCipher = new RC4Engine();
    mInCipher = new RC4Engine();
  } 
    
  public void startApp() { 
    if (mSession == null) { 
      // Load the keys from resource files.
      byte[] inKey = getInKey();
      byte[] outKey = getOutKey();
      
      // Initialize the ciphers.
      mOutCipher.init(true, new KeyParameter(outKey));
      mInCipher.init(false, new KeyParameter(inKey));
    } 

    mDisplay = Display.getDisplay(this);
    
    if (mTextBox == null) { 
      mTextBox = new TextBox("StealthMIDlet",
          "The eagle has landed", 256, 0);
  
      mTextBox.addCommand(new Command("Exit", Command.EXIT, 0));
      mTextBox.addCommand(new Command("Send", Command.SCREEN, 0));
      mTextBox.setCommandListener(this);
    } 

    mDisplay.setCurrent(mTextBox);
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
    // Encrypt our message.
    byte[] plaintext = mTextBox.getString().getBytes();
    byte[] ciphertext = new byte[plaintext.length];
    mOutCipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
    char[] hexCiphertext = HexCodec.bytesToHex(ciphertext);

    // Create the GET URL. Our user name and the encrypted, hex
    //   encoded message are included as parameters. The user name
    //   and base URL are retrieved as application properties. 
    String baseURL = getAppProperty("StealthMIDlet-URL");
    URLBuilder ub = new URLBuilder(baseURL);
    ub.addParameter("user", getAppProperty("StealthMIDlet.user"));
    ub.addParameter("message", new String(hexCiphertext));
    String url = ub.toString();

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
      ciphertext = new byte[length];
      in.read(ciphertext);
      in.close();
      hc.close();
    } 
    catch (IOException ioe) { 
      Alert a = new Alert("Exception", ioe.toString(), null, null);
      a.setTimeout(Alert.FOREVER);
      mDisplay.setCurrent(a, mTextBox);
    } 

 
    // Decrypt the server response.
    String hex = new String(ciphertext);
    byte[] dehexed = HexCodec.hexToBytes(hex.toCharArray());
    byte[] deciphered = new byte[dehexed.length];
    mInCipher.processBytes(dehexed, 0, dehexed.length, deciphered, 0);

    String decipheredString = new String(deciphered);
    Alert a = new Alert("Response", decipheredString, null, null);
    a.setTimeout(Alert.FOREVER);
    mDisplay.setCurrent(a, mTextBox);
  } 

  // Normally you would probably read keys from resource files
  //   in the MIDlet suite JAR, using the getResourceAsStream()
  //   method in Class. Here I just use hardcoded values that match
  //   the hardcoded values in StealthServlet.  
  private byte[] getInKey() { 
    return "Incoming MIDlet key".getBytes();
  } 
  
  private byte[] getOutKey() { 
    return "Outgoing MIDlet key".getBytes();
  } 
  
  public void pauseApp() { } 

  public void destroyApp(boolean unconditional) { } 
} 
