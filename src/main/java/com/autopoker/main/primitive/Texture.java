package com.autopoker.main.primitive;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_image_free;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class Texture
{
	protected String filepath;
	protected int imageWidth, imageHeight;
	protected int texId;
	
	public Texture(String filepath) {
		this.filepath = filepath;
		
		texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		ByteBuffer image = stbi_load(filepath, width, height, channels, 0);
		
		if (image != null) {
			this.imageWidth = width.get(0);
			this.imageHeight = height.get(0);
			if (channels.get(0) == 3) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
			} else if (channels.get(0) == 4) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			} else {
				System.out.println("ERROR: Unknown number of channels (" + channels.get(0) + ") in image '" + filepath + "'");
			}
		} else {
			System.out.println("ERROR: Could not load image '" + filepath + "'");
		}
		
		stbi_image_free(image);
	}
	
	public Texture(BufferedImage image) {
		this.filepath = "Generated";
		texId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texId);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
		int[] pixels = new int[imageWidth * imageHeight];
		image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);
		ByteBuffer buffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * 4);
		
		for(int y = 0; y < imageHeight; y++) {
		    for(int x = 0; x < imageWidth; x++) {
		        int pixel = pixels[x + y * imageWidth];
		        buffer.put((byte) ((pixel >> 16) & 0xFF));
		        buffer.put((byte) ((pixel >> 8) & 0xFF));
		        buffer.put((byte) (pixel & 0xFF));
		        buffer.put((byte) ((pixel >> 24) & 0xFF));
		    }
		}
		buffer.flip();
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, imageWidth, imageHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		//stbi_image_free(buffer);
	}
	
	public Sprite createSpriteFromTexture() {
		return new Sprite(this);
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texId);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public int getImageWidth() {
		return imageWidth;
	}
	
	public int getImageHeight(){
		return imageHeight;
	}
	
	public int getId() {
		return texId;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Texture) {
			if (((Texture)o).getFilepath().equals(this.filepath)) {
				return true;
			}
		}
		return false;
	}
}
