package com.leroy.ronan.utils.img;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.apache.commons.lang3.mutable.MutableInt;

public class ImageCombinator {

	public static BufferedImage combineLeftToRight(BufferedImage...images) {
		int width = Arrays.stream(images).mapToInt(img -> img.getWidth()).sum();
		int height = Arrays.stream(images).mapToInt(img -> img.getHeight()).max().orElse(0);
		
	    BufferedImage result = new BufferedImage(width, height, images[0].getType());
	    Graphics g = result.getGraphics();

	    MutableInt x = new MutableInt(0);
	    Arrays.stream(images)
	    	.forEach(bimg -> {
	    		g.drawImage(bimg, x.getValue(), 0, null); 
	    		x.add(bimg.getWidth());
	    	});
        
        return result;
	}
}
