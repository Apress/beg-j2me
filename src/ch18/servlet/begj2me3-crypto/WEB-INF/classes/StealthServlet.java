import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.*;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.params.KeyParameter;

public class StealthServlet extends HttpServlet { 
  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException { 
    String user = request.getParameter("user");
    
    // Try to find the user's cipher pair.
    HttpSession session = request.getSession();
    StreamCipher inCipher = (StreamCipher)session.getAttribute("inCipher");
    StreamCipher outCipher = (StreamCipher)session.getAttribute("outCipher");
    
    // If the ciphers aren't found, create and initialize a new pair.
    if (inCipher == null && outCipher == null) { 
      // Retrieve the client's keys.
      byte[] inKey = getInKey(user);
      byte[] outKey = getOutKey(user);
      // Create and initialize the ciphers.
      inCipher = new RC4Engine();
      outCipher = new RC4Engine();
      inCipher.init(true, new KeyParameter(inKey));
      outCipher.init(false, new KeyParameter(outKey));
      // Now put them in the session object.
      session.setAttribute("inCipher", inCipher);
      session.setAttribute("outCipher", outCipher);
    } 
    
    // Retrieve the client's message.
    String clientHex = request.getParameter("message");
    byte[] clientCiphertext = HexCodec.hexToBytes(clientHex);
    byte[] clientDecrypted = new byte[clientCiphertext.length];
    inCipher.processBytes(clientCiphertext, 0, clientCiphertext.length,
        clientDecrypted, 0);
    System.out.println("message = " + new String(clientDecrypted));

    // Create the response message.
    String message = "Hello, this is StealthServlet.";

    // Encrypt the message.
    byte[] plaintext = message.getBytes();
    byte[] ciphertext = new byte[plaintext.length];
    outCipher.processBytes(plaintext, 0, plaintext.length, ciphertext, 0);
    char[] hexCiphertext = HexCodec.bytesToHex(ciphertext);

    response.setContentType("text/plain");
    response.setContentLength(hexCiphertext.length);
    PrintWriter out = response.getWriter();
    out.println(hexCiphertext);
  } 
  
  private byte[] getInKey(String user) { 
    return "Outgoing MIDlet key".getBytes();
  } 

private byte[] getOutKey(String user) { 
    return "Incoming MIDlet key".getBytes();
  } 
} 
