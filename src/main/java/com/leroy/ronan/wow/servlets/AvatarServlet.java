package com.leroy.ronan.wow.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
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
		String uri = request.getRequestURI();
		String[] params = uri.replace(".png", "").split("/");
		AvatarGenerator generator = new AvatarGenerator();
		try {
			BufferedImage img = generator.buildImage(params[2], params[3], params[4]);
			OutputStream out = response.getOutputStream();
			ImageIO.write(img, "png", out);
			out.close();
		} catch (URISyntaxException e) {
			log(e.getMessage(), e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
