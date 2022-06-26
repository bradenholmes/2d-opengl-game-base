package com.baseengine.main.primitive;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joml.Vector2f;

import com.baseengine.main.utility.ExceptionPrinter;


class AtlasSection {
	
	int startingCoordX, startingCoordY;
	int spriteSizeX, spriteSizeY;
	int numSpritesX, numSpritesY;
	int numSprites;
	
	public AtlasSection(String startingCoordX, String startingCoordY, String spriteSizeX, String spriteSizeY, String numSpritesX, String numSpritesY) {
		this.startingCoordX = Integer.valueOf(startingCoordX);
		this.startingCoordY = Integer.valueOf(startingCoordY);
		this.spriteSizeX = Integer.valueOf(spriteSizeX);
		this.spriteSizeY = Integer.valueOf(spriteSizeY);
		this.numSpritesX = Integer.valueOf(numSpritesX);
		this.numSpritesY = Integer.valueOf(numSpritesY);
		this.numSprites = this.numSpritesX * this.numSpritesY;
	}
}

public class Spritesheet
{
	private Sprite[] sprites;
	
	private Texture texture;
	
	private AtlasSection[] sections;
	
	public Spritesheet(Texture texture) {
		this.texture = texture;
		
		readConfigFile();

	}
	
	public Sprite getSprite(int index) {
		return sprites[index];
	}
	
	public Sprite getSprite(int section, int index) {
		int offset = 0;
		for (int i = 0; i < section; i++) {
			offset += sections[i].numSprites;
		}
		
		return sprites[offset + index];
	}
	
	public Sprite getSprite(int section, int indexX, int indexY) {
		int offset = 0;
		for (int i = 0; i < section; i++) {
			offset += sections[i].numSprites;
		}
		
		int index = (indexY * sections[section].numSpritesX) + indexX;
		
		return sprites[offset + index];
	}
	

	
	public void populateArray() {
		
		int texWidth = texture.getImageWidth();
		int texHeight = texture.getImageHeight();
		
		int currentX = 0;
		int currentY = 0;
		
		if (!isConfigurationValid()) {
			System.err.println("ERROR: Configuration for sprite atlas '" + texture.filepath + "' is invalid!");
			return;
		}
		
		//Instantiate Array
		int count = 0;
		for (int n = 0; n < sections.length; n++) {
			count += sections[n].numSprites;
		}
		sprites = new Sprite[count];
		
		int spriteIndex = 0;
		for (int n = 0; n < sections.length; n++) {
			AtlasSection sect = sections[n];
			currentY = sect.startingCoordY;
			
			for (int j = 0; j < sect.numSpritesY; j++) {
				float topY = (currentY) / (float)texHeight;
				float bottomY = (currentY + sect.spriteSizeY) / (float)texHeight;
				
				currentX = sect.startingCoordX;
				for (int i = 0; i < sect.numSpritesX; i++) {
					float rightX = (currentX + sect.spriteSizeX) / (float)texWidth;
					float leftX = currentX /(float)texWidth;
					
					
					Vector2f[] texCoords =  {
							new Vector2f(rightX, bottomY),
							new Vector2f(rightX, topY),
							new Vector2f(leftX, topY),
							new Vector2f(leftX, bottomY)
					};
					
					Sprite sprite = new Sprite(texture, texCoords);
					sprites[spriteIndex] = sprite;
					
					currentX += sect.spriteSizeX;
					spriteIndex++;
				}
				
				currentY += sect.spriteSizeY;
			}
		}
		
	}
	
	public int getSpriteWidth(int section) {
		if (section < 0 || section >= sections.length) {
			return -1;
		}
		return sections[section].spriteSizeX;
	}
	
	public int getSpriteHeight(int section) {
		if (section < 0 || section >= sections.length) {
			return -1;
		}
		return sections[section].spriteSizeY;
	}
	
	private void readConfigFile() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(StringUtils.substringBefore(texture.getFilepath(), ".png") + "_config.txt"));
			String line;
			
			
			//Initialize array
			List<String> lines = reader.lines().collect(Collectors.toList());
			sections = new AtlasSection[lines.size()];
			 
			
			//Read line by line and insert into array
			int index = 0;
			for (int i = 0; i < lines.size(); i++) {
				line = lines.get(i);
				String[] args = line.split(",");
				
				AtlasSection sect = new AtlasSection(args[0], args[1], args[2], args[3], args[4], args[5]);
				sections[index] = sect;
				index++;
			}
			reader.close();
		} catch (IOException e) {
			ExceptionPrinter.print(e, "reading config file " + StringUtils.substringBefore(texture.getFilepath(), ".png") + "_config.txt");
		}
	}
	
	private boolean isConfigurationValid() {
		int texWidth = texture.getImageWidth();
		int texHeight = texture.getImageHeight();
		
		for (int i = 0; i < sections.length; i++) {
			AtlasSection sect = sections[i];
			//Start coord x out of bounds
			if (sect.startingCoordX < 0 || sect.startingCoordX >= texWidth) {
				return false;
			}
			//Start coord y out of bounds
			if (sect.startingCoordY < 0 || sect.startingCoordY >= texHeight) {
				return false;
			}
			//Sprite size x, y and numSprites x,y is negative
			if (sect.spriteSizeX < 0 || sect.spriteSizeY < 0 || sect.numSpritesX < 0 || sect.spriteSizeY < 0) {
				return false;
			}
			//Max X is out of bounds
			if (sect.startingCoordX + (sect.numSpritesX * sect.spriteSizeX) > texWidth) {
				return false;
			}
			//Max Y is out of bounds
			if (sect.startingCoordY + (sect.numSpritesY * sect.spriteSizeY) > texHeight) {
				return false;
			}
		}
		
		
		return true;
	}

	public Texture getTexture(){
		return texture;
	}
}
