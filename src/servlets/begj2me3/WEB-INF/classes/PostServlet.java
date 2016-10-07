import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

public class PostServlet extends HttpServlet {
  public void doPost(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    String name = request.getParameter("name");
    
    String message = "Received name: '" + name + "'";
    response.setContentType("text/plain");
    response.setContentLength(message.length());
    PrintWriter out = response.getWriter();
    out.println(message);
  }
}
