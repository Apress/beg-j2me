public class URLBuilder { 
  private StringBuffer mBuffer;
  private boolean mHasParameters;
  
  public URLBuilder(String base) { 
    mBuffer = new StringBuffer(base);
    mHasParameters = false;
  } 
  
  public void addParameter(String name, String value) { 
    // Append a separator.
    if (mHasParameters == false) { 
      mBuffer.append('?');
      mHasParameters = true;
    } 
    else
      mBuffer.append('&');
    // Now tack on the name and value pair. These should
    //   really be URL encoded (see java.net.URLEncoder in
    //   J2SE) but this class appends the name and value
    //   as is, for simplicity. Names or values with spaces
    //   or other special characters will not work correctly.
    mBuffer.append(name);
    mBuffer.append('=');
    mBuffer.append(value);
  } 
  
  public String toString() { 
    return mBuffer.toString();
  } 
} 

