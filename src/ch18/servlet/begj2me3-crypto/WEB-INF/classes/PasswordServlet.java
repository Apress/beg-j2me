import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.*;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

public class PasswordServlet extends HttpServlet {
  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    System.out.println("user = " + request.getParameter("user"));
    System.out.println("timestamp = " + request.getParameter("timestamp"));
    System.out.println("random = " + request.getParameter("random"));
    System.out.println("digest = " + request.getParameter("digest"));
    
    // Retrieve the user name.
    String user = request.getParameter("user");
    // Look up the password for this user.
    String password = lookupPassword(user);
    // Pull the timestamp and random number (hex encoded) out
    //   of the request.
    String timestamp = request.getParameter("timestamp");
    String randomNumber = request.getParameter("random");
    
    // Compare the timestamp with the last saved
    //   timestamp for this user. Accept only timestamps
    //   that are greater than the last saved timestamp for this user.
    // [not implemented]
    
    // Gather values for the message digest.
    byte[] userBytes = user.getBytes();
    byte[] timestampBytes = HexCodec.hexToBytes(timestamp);
    byte[] randomBytes = HexCodec.hexToBytes(randomNumber);
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
    
    // Now compare the digest values.
    String message = "";
    String clientDigest = request.getParameter("digest");
    if (isEqual(digestValue, HexCodec.hexToBytes(clientDigest)))
      message = "User " + user + " logged in.";
    else
      message = "Login was unsuccessful.";

    // Send a response to the client.
    response.setContentType("text/plain");
    response.setContentLength(message.length());
    PrintWriter out = response.getWriter();
    out.println(message);
  }
  
  private String lookupPassword(String user) {
    // Here you could do a real lookup based on the user name.
    //   You might look in a text file or a database. Here, I
    //   just use a hardcoded value.
    return "happy8";
  }
  
  private boolean isEqual(byte[] one, byte[] two) {
    if (one.length != two.length) return false;
    for (int i = 0; i < one.length; i++)
      if (one[i] != two[i]) return false;
    return true;
  }
}

