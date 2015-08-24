package com.leroy.ronan.wow.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

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
     
	private static AvatarGenerator generator = new AvatarGenerator();
	
    public AvatarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		long now = System.currentTimeMillis();
		response.addHeader("Cache-Control", "max-age=" + TimeUnit.SECONDS.convert(24, TimeUnit.HOURS));
		response.addHeader("Cache-Control", "must-revalidate");
		response.setDateHeader("Last-Modified", now);
		response.setDateHeader("Expires", now + TimeUnit.MILLISECONDS.convert(24, TimeUnit.HOURS));

		String uri = request.getRequestURI();
		String[] params = URLDecoder.decode(uri, "UTF-8").replace(".png", "").split("/");
		try {
			BufferedImage img = generator.buildImage(params[2], params[3], params[4].split("-"));
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
