package com.leroy.ronan.wow.servlets;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.leroy.ronan.utils.cache.CacheResponse;
import com.leroy.ronan.wow.avatar.AvatarGenerator;

@WebServlet("/avatar/*")
public class AvatarServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
     
	private static AvatarGenerator generator = new AvatarGenerator(System.getenv("OPENSHIFT_DATA_DIR")+"apifiles/img/");
	
    public AvatarServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String uri = request.getRequestURI();
		String[] params = URLDecoder.decode(uri, "UTF-8").replace(".png", "").split("/");
		CacheResponse<BufferedImage> img = generator.get(params[2], params[3], params[4].split("-"));

		long now = System.currentTimeMillis();
		response.addHeader("Cache-Control", "max-age=" + TimeUnit.SECONDS.convert(img.getTimeToLive(), TimeUnit.MILLISECONDS));
		response.addHeader("Cache-Control", "must-revalidate");
		response.setDateHeader("Last-Modified", img.getCreated());
		response.setDateHeader("Expires", now + TimeUnit.MILLISECONDS.convert(img.getTimeToLive(), TimeUnit.MILLISECONDS));
		
		if (img.getContent() != null) {
	        OutputStream out = response.getOutputStream();
	        ImageIO.write(img.getContent(), "png", out);
	        out.close();
		} else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
