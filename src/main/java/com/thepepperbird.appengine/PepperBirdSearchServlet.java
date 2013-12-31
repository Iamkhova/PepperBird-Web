package com.thepepperbird.appengine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.http.*;

public class PepperBirdSearchServlet extends HttpServlet
{

 private static final long serialVersionUID = 1L;

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException, ServletException {
      resp.setContentType("text/plain");
      req.getRequestDispatcher("ThePepperBird.jsp").forward(req,resp);
    }
    
        
}

