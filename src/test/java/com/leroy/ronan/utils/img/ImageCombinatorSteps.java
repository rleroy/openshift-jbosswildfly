package com.leroy.ronan.utils.img;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ImageCombinatorSteps {
	
	private BufferedImage left;
	private BufferedImage right;
	private BufferedImage combined;
	
	@Given("^left image  is \"(.*?)\"$")
	public void left_image_is(String left) throws Throwable {
		this.left = ImageIO.read(getClass().getResource("features/"+left));
	}

	@Given("^right image is \"(.*?)\"$")
	public void right_image_is(String right) throws Throwable {
		this.right = ImageIO.read(getClass().getResource("features/"+right));
	}

	@When("^I combine both images left to right$")
	public void i_combine_both_images_left_to_right() throws Throwable {
		this.combined = ImageCombinator.combineLeftToRight(this.left, this.right);
	}

	@Then("^target image is generated with the right size$")
	public void target_image_is_generated_with_the_right_size() throws Throwable {
		Assert.assertEquals(left.getWidth()+right.getWidth(), combined.getWidth());
		Assert.assertEquals(Math.max(left.getHeight(),right.getHeight()), combined.getHeight());
	}

}
