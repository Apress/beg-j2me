import java.io.*;
import java.net.*;

import javax.servlet.http.*;
import javax.servlet.*;

public class JargoneerServlet extends HttpServlet {
  private static final String kURL = "http://www.dict.org/bin/Dict";
  private static final String kParameters =
      "Form=Dict1&Strategy=*&Database=jargon&Query=";

  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    String word = request.getParameter("word");
    
    String message;
    
    if (word == null || word.length() == 0)
      message = "[No word specified.]";
    else {
      try { message = lookUp(word); }
      catch (IOException ioe) {
        message = "[Exception: " + ioe.toString() + "]";
      }
    }
    
    if (message == null || message.length() == 0)
      message = "[No definition found.]";

    response.setContentType("text/plain");
    response.setContentLength(message.length());
    PrintWriter out = response.getWriter();
    out.println(message);
  }

  public String lookUp(String word) throws IOException {
    HttpURLConnection c = null;
    InputStream in = null;
    OutputStream out = null;
    StringBuffer definition = new StringBuffer();

    String postString = kParameters + word;
    
    try {
      URL url = new URL(kURL);
      c = (HttpURLConnection)url.openConnection();
      
      // Set the request method and headers
      c.setRequestMethod("POST");
      c.setDoOutput(true);
      c.setRequestProperty("User-Agent",
          "JargoneerServlet");
      c.setRequestProperty("Content-Language", "en-US");
      c.setRequestProperty("Content-Type",
          "application/x-www-form-urlencoded");
      c.setRequestProperty("Content-Length",
          String.valueOf(postString.length()));

      // Write out the POST parameters.
      out = c.getOutputStream();
      out.write(postString.getBytes());

      in = c.getInputStream();
      
      String line;
      boolean inPre = false;
      while ((line = readLine(in)) != null) {
        int preIndex = line.indexOf("<pre>");
        int slashpreIndex = line.indexOf("</pre>");
        if (preIndex != -1)
          inPre = true;
        else if (slashpreIndex != -1)
          inPre = (preIndex > slashpreIndex);
        else if (inPre == true)
          trimAndAppend(line, definition);
      }
    }
    finally {
      if (in != null) in.close();
      if (out != null) out.close();
      if (c != null) c.disconnect();
    }
    return definition.toString();
  }

  private byte[] mBuffer = new byte[512];
  
  protected String readLine(InputStream in) throws IOException {
    int c, index = 0;
    while ((c = in.read()) != -1 && c != '\n')
      mBuffer[index++] = (byte)c;
    if (c == -1 && index == 0)
      return null;
    return new String(mBuffer, 0, index);
  }

  protected void trimAndAppend(String line, StringBuffer amalgam) {
    boolean leading = true;
    boolean inTag = false;
  
    int c;
    for (int i = 0; i < line.length(); i++) {
      c = line.charAt(i);
      if (c == '<')
        inTag = true;
      else if (c == '>')
        inTag = false;
      else if (c == ' ' && leading == true)
        ;
      else if (inTag == false) {
        amalgam.append((char)c);
        leading = false;
      }
    }
    if (leading == false) amalgam.append(' ');
  }
}
