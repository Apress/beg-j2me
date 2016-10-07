import java.io.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.pim.*;

public class SeedMIDlet
extends MIDlet     implements CommandListener {
  
  private Form mForm;
  
  private Command mExitCommand;
  
   public SeedMIDlet() {
    try {
	verifyPIMSupport();    
	seed();
    }
//    catch (RecordStoreException rse) {
	catch (Exception ex) {
      mForm = new Form("Exception");
      mForm.append(new StringItem(null, ex.toString()));
      mExitCommand = new Command("Exit", Command.EXIT, 0);
     
      mForm.addCommand(mExitCommand);
      mForm.setCommandListener(this);
      return;
    }
    
    mForm = new Form("Data Seeded");
    mForm.append(new StringItem(null, "PIM data stored."));
      mExitCommand = new Command("Exit", Command.EXIT, 0);
     
      mForm.addCommand(mExitCommand);
      mForm.setCommandListener(this);
  }
  
  public void startApp() {
    Display.getDisplay(this).setCurrent(mForm);
  }
  
  public void pauseApp() {}

  public void destroyApp(boolean flg) {
  }
  public void commandAction(Command c, Displayable s) {
	  if (c == mExitCommand) {
		     destroyApp(true);
                     notifyDestroyed();
	  }
  }
  
  public void verifyPIMSupport() throws IOException {
      String version = "";
      version = System.getProperty("microedition.pim.version");
      if (version != null) {
	if (!version.equals("1.0"))
	 throw new IOException("Package is not version 1.0.");
      }
      else
	 throw new IOException("PIM optional package is not available.");
  }

  private ContactList contList = null;
  private void seed() throws PIMException {
	  try {	  
       PIM pimInst = PIM.getInstance();
       contList = (ContactList) 
                pimInst.openPIMList(PIM.CONTACT_LIST, PIM.READ_WRITE);
	  }
	  catch (PIMException ex) {
		  // contact list is not supported
	  }
       addContact(contList, "Jack", "Goldburg", "2345 High Park Ave", 
           "Orlando", "USA", "32817");
	addContact(contList, "Mary", "Johnson", "777 Lucky Road", 
           "Chelsea", "UK", "KL3 M3X");
	 if (contList != null)
		       contList.close();
	 contList = null;
       
  }
  private void addContact( ContactList list, String firstName, String lastName,
  String street, String city, String country, String postalcode) throws PIMException {
       Contact ct = list.createContact();
       String [] name =  new String[contList.stringArraySize(Contact.NAME)];
       name[Contact.NAME_GIVEN] = firstName;
       name[Contact.NAME_FAMILY] = lastName;
       ct.addStringArray(Contact.NAME,Contact.ATTR_NONE , name);
       String [] addr =  new String[contList.stringArraySize(Contact.ADDR)];
       addr[Contact.ADDR_STREET] = street;
       addr[Contact.ADDR_LOCALITY] = city;
       addr[Contact.ADDR_COUNTRY] = country;
       addr[Contact.ADDR_POSTALCODE] = street;
       ct.addStringArray(Contact.ADDR, Contact.ATTR_NONE , addr);
       ct.commit();
	  
  }
  
}

