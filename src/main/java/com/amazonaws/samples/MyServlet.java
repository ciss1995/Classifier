package com.amazonaws.samples;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet implements Servlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//@Override
	//Reload
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// set response headers
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		
		// When the user reload the page, display the index.jsp 
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
			  .append("<html>\r\n")
			  .append("		<head>\r\n")
			  .append("			<TITLE>Heavy Water Project</TITLE>\r\n")
			  .append("		</head>\r\n")
			  .append("		<body>\r\n")
			  .append("			<FORM action=\"/MyServlet-web\" method=\"post\">\r\n")
			  .append("				The processing time may take a minute or two:<br> \r\n")
			  .append("				Please enter your document here:<br> \r\n")
			  .append("				<textarea name=\"ipt\" rows=\"10\" cols=\"50\" wrap=\"soft\" id=\"userInput\">\r\n")
			  .append("				</textarea>\r\n")
			  .append("				<BR>\r\n")
			  .append("				<INPUT type=\"submit\" name=\"Submit\" value=\"Submit\">\r\n")
			  .append("			</form>\r\n")
			  .append("		</body>\r\n")
			  .append("</html>\r\n");
	}
	@Override
	//After submit
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		//Get input from document
		String document = request.getParameter("ipt");
		//Instance of processor
		ProcessDocument pd = new ProcessDocument();
		//Process user input
		pd.setDocument(document);
		//Get result from model
		String output = pd.getDocument();

		//Set up the html respond page
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		//HTML Respond page
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>\r\n")
			  .append("<html>\r\n")
			  .append("		<head>\r\n")
			  .append("			<title>Respond</title>\r\n")
			  .append("		</head>\r\n")
			  .append("		<body>\r\n");
		if (document != null && !document.trim().isEmpty()) {
			writer.append("	Your document is a " + output + ".\r\n");
		} else {
			writer.append("	You did not entered a document! Please reload.\r\n");
		}
		writer.append("		</body>\r\n")
			  .append("</html>\r\n");
	}	
	
}
