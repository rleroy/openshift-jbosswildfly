package com.leroy.ronan.wow.servlets;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leroy.ronan.wow.avatar.AvatarGenerator;

@WebServlet("/avatar/*")
public class AvatarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public AvatarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String content = request.getRequestURI() + "\n";
		
		AvatarGenerator generator = new AvatarGenerator();
		try {
			generator.buildImage("eu", "Sargeras", "Pamynx");
			content += "Success !";
		} catch (URISyntaxException e) {
			content += "Failure : " + e.getMessage();
		}
		
		response.getWriter().append(content);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
