import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.pim.*;
import java.io.*;
import java.util.*;
import javax.microedition.lcdui.List;

public class PIMMIDlet
extends MIDlet
implements CommandListener {
    private ContactList contList = null;
    private Enumeration contacts = null;
    
    private Form mForm;
    private List mNameList;
    private Command mExitCommand;
    public PIMMIDlet() {
        try {
            verifyPIMSupport();
            PIM pimInst = PIM.getInstance();
            contList = (ContactList)
            pimInst.openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY);
            contacts =  contList.items();
        }
        catch (Exception ex) {
            mForm = new Form("Exception");
            mForm.append(new StringItem(null, ex.toString()));
            mExitCommand = new Command("Exit", Command.EXIT, 0);
            mForm.addCommand(mExitCommand);
            mForm.setCommandListener(this);
            return;
        }

        if (contacts == null)
            return;       
        mNameList =  new List("List of contacts", List.EXCLUSIVE);
        while (contacts.hasMoreElements())  {
            Contact tCont = (Contact) contacts.nextElement();
            int [] flds = tCont.getFields();
            String [] nameValues = tCont.getStringArray( Contact.NAME, 0);
            String firstName = nameValues[Contact.NAME_GIVEN];
            String lastName = nameValues[Contact.NAME_FAMILY];
            mNameList.append(lastName + ", " + firstName, null);
        }
        
        
        mExitCommand =new Command("Exit", Command.EXIT, 0);
        mNameList.addCommand(mExitCommand);
        mNameList.setCommandListener(this);
    }
    
    public void startApp() {
        Display.getDisplay(this).setCurrent(mNameList);
    }
    
    public void pauseApp() {}
    public void destroyApp(boolean flg) {
    }
    public void commandAction(Command c, Displayable s) {
        if (c == mExitCommand)  {
            destroyApp(true);
            notifyDestroyed();
        }
    }
    public void verifyPIMSupport() throws IOException {
        String version = null;
        version = System.getProperty("microedition.pim.version");
        if (version != null) {
            if (!version.equals("1.0"))
                throw new IOException("Package is not version 1.0.");
        }
        else
            throw new IOException("PIM optional package is not available.");
    }
}
