import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import java.io.*;

public class FileBasedPreferences implements Runnable {
    private String mDirectoryName;
    private static final String fileURLRoot = "file:///";
    private static final String fileExt = ".pfs";
    private Hashtable mHashtable;
    private boolean mSaving = false;
    private String mFileRoot = null;
    
    public FileBasedPreferences(String dirName)
    throws IOException {
        mDirectoryName = dirName;
        mHashtable = new Hashtable();
        Enumeration em = FileSystemRegistry.listRoots();
        // take the first root available for simplicity
        if (em.hasMoreElements())
           mFileRoot = fileURLRoot + em.nextElement();
        if (mFileRoot != null)
           load();
        else
           throw new IOException("No file system available");
    }
    
    public String get(String key) {
        return (String)mHashtable.get(key);
    }
    
    public void put(String key, String value) {
        if (value == null) value = "";
        mHashtable.put(key, value);
    }
    
    private void load() throws IOException {
        FileConnection fc = null;
        DataInputStream dis = null;
        
        StringBuffer fileURL = new StringBuffer(mFileRoot);
        fileURL.append(mDirectoryName);
        fileURL.append(fileExt);
        try {
            fc = (FileConnection) Connector.open(
            fileURL.toString(), Connector.READ);
            if (fc == null)
                return;
            if (!fc.exists())  {
                return;
            }
            
            dis = fc.openDataInputStream();
            
            String curInput;
            while ( (curInput = dis.readUTF()) != null )  {
                int index = curInput.indexOf('|');
                String name = curInput.substring(0, index);
                String value = curInput.substring(index + 1);             
                put(name, value);
            }
        }  catch (Exception ex) {
            // end of file detected the hard way
        }
        finally {
            if (dis != null) dis.close();
            if (fc != null) fc.close();
        }
    }
    
    public void save() {
        Thread t = new Thread(this);
        t.start();
    }
    public void run() {
        mSaving = true;
        try {
            savePref();
        } catch (IOException ex) {
        }
        mSaving = false;
    }
    public boolean isSaving() {
        return mSaving;
    }
    public void savePref() throws IOException {
        FileConnection fc = null;
        DataOutputStream dos = null;
        try {
            // if exists already, first delete file, a little clumsy.
            StringBuffer fileURL = new StringBuffer(mFileRoot);
            fileURL.append(mDirectoryName);
            fileURL.append(fileExt);
            fc = (FileConnection) Connector.open(  fileURL.toString(), Connector.READ_WRITE);
            if (fc.exists()) {
                fc.delete();
                fc.close();
                fc = (FileConnection) Connector.open( fileURL.toString(), Connector.READ_WRITE);
            }
            fc.create();
            dos = new DataOutputStream(fc.openOutputStream());
            
            // Now save the preferences records.
            Enumeration keys = mHashtable.keys();
            while (keys.hasMoreElements()) {
                String key = (String)keys.nextElement();
                String value = get(key);
                String pref = key + "|" + value;
                dos.writeUTF(pref);
            }
            
            
        }
        finally {
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            if (fc != null) fc.close();
            
        }
    }
}

