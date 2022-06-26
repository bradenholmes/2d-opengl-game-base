package com.baseengine.main.primitive;

public enum Spritesheets
{
	MAIN_SHEET("res/img/sheets/spritesheet.png");
	
	private final String filepath;
	
	Spritesheets(String filepath){
		this.filepath = filepath;
	}
	
	public String getFilepath() {
		return filepath;
	}
}
