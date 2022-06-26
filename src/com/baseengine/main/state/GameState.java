package com.baseengine.main.state;



public class GameState extends State
{
	
	private boolean resLoaded = false;
	
	public GameState() {

	}
	
	public void init() {
		loadResources();
	}
	
	private void loadResources() {

		resLoaded = true;
	}
	
	public void loadState() {
		
	}
	
	public void update(float deltaTime) {
		if (resLoaded) {
			
		}
	}
	
	public void unloadState() {
		
	}
	

}
