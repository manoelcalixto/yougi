package org.cejug.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.cejug.business.UserAccountBsn;

public class EmailConfirmationServlet extends HttpServlet {
    @EJB
    private UserAccountBsn userAccountBsn;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String confirmationCode = request.getParameter("code");

        if(confirmationCode == null || confirmationCode.equals("")) {
            PrintWriter out = response.getWriter();
            try {
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Yasmim - Email Confirmation Failure</title>");
                out.println("<link href=\"/cejug/resources/css/default.css\" rel=\"stylesheet\" type=\"text/css\"/>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Email Confirmation Failure</h1>");
                out.println("<p>This email confirmation is invalid. Check if the address on the browser coincides with the address sent by email.</p>");
                String scheme = request.getScheme();
                String serverName = request.getServerName();
                int serverPort = request.getServerPort();
                String contextPath = request.getContextPath();
                out.println("<p><a href=\""+ scheme + "://" + serverName + (serverPort != 80?":"+ serverPort:"") + (contextPath.equals("")?"":contextPath) +"\">Go to Homepage</a>.");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        }
        try {
            userAccountBsn.confirmUser(confirmationCode);
        }
        catch(Exception e) {
            PrintWriter out = response.getWriter();
            try {
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Yasmim - Email Confirmation Failure</title>");
                out.println("<link href=\"/cejug/resources/css/default.css\" rel=\"stylesheet\" type=\"text/css\"/>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Email Confirmation Failure</h1>");
                out.println("<p>This email confirmation is invalid.</p>");
                out.println("<p class=\"alertMessage\">Cause: "+ e.getCause().getMessage() +"</p>");
                out.println("<p>Check if the address on the browser coincides with the address sent my email.</p>");
                String scheme = request.getScheme();
                String serverName = request.getServerName();
                int serverPort = request.getServerPort();
                String contextPath = request.getContextPath();
                out.println("<p><a href=\""+ scheme + "://" + serverName + (serverPort != 80?":"+ serverPort:"") + (contextPath.equals("")?"":contextPath) +"\">Go to website</a>");
                out.println("</body>");
                out.println("</html>");
            } finally {
                out.close();
            }
        }
        response.sendRedirect("login.xhtml");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}