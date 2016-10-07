import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;

public class RecordMIDlet
    extends MIDlet
    implements CommandListener {
  private static final String kUser = "user";
  private static final String kPassword = "password";
  
  private Preferences mPreferences;
  private Form mForm;
  private TextField mUserField, mPasswordField;
  
  public RecordMIDlet() {
    try {
      mPreferences = new Preferences("preferences");
    }
    catch (RecordStoreException rse) {
      mForm = new Form("Exception");
      mForm.append(new StringItem(null, rse.toString()));
      mForm.addCommand(new Command("Exit", Command.EXIT, 0));
      mForm.setCommandListener(this);
      return;
    }

    mForm = new Form("Login");
    mUserField = new TextField("Name",
        mPreferences.get(kUser), 32, 0);
    mPasswordField = new TextField("Password",
        mPreferences.get(kPassword), 32, 0);
    mForm.append(mUserField);
    mForm.append(mPasswordField);
    
    mForm.addCommand(new Command("Exit", Command.EXIT, 0));
    mForm.setCommandListener(this);
  }
  
  public void startApp() {
    Display.getDisplay(this).setCurrent(mForm);
  }
  
  public void pauseApp() {}

  public void destroyApp(boolean unconditional) {
    // Save the user name and password.
    mPreferences.put(kUser, mUserField.getString());
    mPreferences.put(kPassword, mPasswordField.getString());
    try { mPreferences.save(); }
    catch (RecordStoreException rse) {}
  }

  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) {
      destroyApp(true);
      notifyDestroyed();
    }
  }
}
