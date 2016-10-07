import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;
import java.io.*;

public class FCMIDlet
extends MIDlet
implements CommandListener {
    private static final String kUser = "user";
    private static final String kPassword = "password";
    private FileBasedPreferences mPreferences;
    private Form mForm;
    private TextField mUserField, mPasswordField;
    private Command mExitCommand, mSaveCommand;
    
    public FCMIDlet() {
        try {
            verifyFileConnectionSupport();
            mPreferences = new FileBasedPreferences("preferences");
        }
        catch (IOException ex) {
            mForm = new Form("Exception");
            mForm.append(new StringItem(null, ex.toString()));
            mExitCommand = new Command("Exit", Command.EXIT, 0);
            
            mForm.addCommand(mExitCommand);
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
        mExitCommand =new Command("Exit", Command.EXIT, 0);
        mSaveCommand = new Command("Save", "Save Password", Command.SCREEN, 0);
        mForm.addCommand(mExitCommand);
        mForm.addCommand(mSaveCommand);
        mForm.setCommandListener(this);
    }
    
    public void startApp() {
        Display.getDisplay(this).setCurrent(mForm);
    }
    
    public void pauseApp() {}
    
    public void savePrefs() {
        // Save the user name and password.
        mPreferences.put(kUser, mUserField.getString());
        mPreferences.put(kPassword, mPasswordField.getString());
        mPreferences.save();
    }
    public void destroyApp(boolean flg) {
    }
    public void commandAction(Command c, Displayable s) {
        if (c == mExitCommand)  {
            if (mPreferences == null) {
                destroyApp(true);
                notifyDestroyed();
            }
            else if ( !mPreferences.isSaving()) {
                destroyApp(true);
                notifyDestroyed();
            }
        }
        else if (c == mSaveCommand)
            savePrefs();
    }
    public void verifyFileConnectionSupport() throws IOException {
        String version = "";
        version = System.getProperty("microedition.io.file.FileConnection.version");
        if (version != null) {
            if (!version.equals("1.0"))
                throw new IOException("Package is not version 1.0.");
        }
        else
            throw new IOException("File connection optional package is not available.");
    }
}
