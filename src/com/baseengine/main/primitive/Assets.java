package com.baseengine.main.primitive;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Assets
{
	private static Map<String, Shader> shaders = new HashMap<>();
	private static Map<String, Texture> textures = new HashMap<>();
	private static Map<String, Spritesheet> spritesheets = new HashMap<>();
	
	public static Shader getShader(String resourcePath) {
		File file = new File(resourcePath);
		if (shaders.containsKey(file.getAbsolutePath())) {
			return shaders.get(file.getAbsolutePath());
		} else {
			Shader shader = new Shader(resourcePath);
			shader.compile();
			Assets.shaders.put(file.getAbsolutePath(), shader);
			return shader;
		}
	}
	
	public static Spritesheet getSpriteCollection(Spritesheets sheet) {
		File file = new File(sheet.getFilepath());
		if (!Assets.spritesheets.containsKey(file.getAbsolutePath())) {
			
			Spritesheet newCollection = null;
			Texture texture = (Texture) Assets.getTexture(sheet);
			newCollection = new Spritesheet(texture);

			
			newCollection.populateArray();
			Assets.spritesheets.put(file.getAbsolutePath(), newCollection);
			return newCollection;
			
		} else {
			return Assets.spritesheets.getOrDefault(file.getAbsolutePath(), null);
		}
	}
	
	private static Texture getTexture(Spritesheets sheet) {
		File file = new File(sheet.getFilepath());
		if (textures.containsKey(file.getAbsolutePath())) {
			return textures.get(file.getAbsolutePath());
		} else {
			Texture t = new Texture(sheet.getFilepath());
			Assets.textures.put(file.getAbsolutePath(), t);
			return t;
		}
	}
	
}
