import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.util.*;

public class CookieServlet extends HttpServlet {
  private Map mHitMap = new HashMap();
  
  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    HttpSession session = request.getSession();
    
    String id = session.getId();
    
    int hits = -1;
    
    // Try to retrieve the hits from the map.
    Integer hitsInteger = (Integer)mHitMap.get(id);
    if (hitsInteger != null)
      hits = hitsInteger.intValue();
    
    // Increment and store.
    hits++;
    mHitMap.put(id, new Integer(hits));
    
    String message = "Hits for this session: " + hits + ".";

    response.setContentType("text/plain");
    response.setContentLength(message.length());
    PrintWriter out = response.getWriter();
    out.println(message);
  }
}
