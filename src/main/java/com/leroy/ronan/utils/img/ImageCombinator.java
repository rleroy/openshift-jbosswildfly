package com.leroy.ronan.utils.img;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageCombinator {

	public static BufferedImage combineLeftToRight(BufferedImage image1, BufferedImage image2) {
		BufferedImage res;
		if (image1 == null || image2 == null){
			res = null;
		} else {
			int width = image1.getWidth() + image2.getWidth();
			int height = Math.max(image1.getHeight(), image2.getHeight());
		    res = new BufferedImage(width, height, image1.getType());

		    Graphics g = res.getGraphics();
		    g.drawImage(image1, 0, 0, null);
		    g.drawImage(image2, image1.getWidth(), 0, null);
		}
        return res;
	}
}
