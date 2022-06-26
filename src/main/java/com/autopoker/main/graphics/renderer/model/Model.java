package com.autopoker.main.graphics.renderer.model;

import java.util.UUID;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import com.autopoker.main.primitive.Sprite;
import com.autopoker.main.primitive.Texture;
import com.autopoker.main.utility.ExceptionPrinter;

public class Model
{
	private UUID uuid;
	private Texture texture;
	private Sprite sprite;
	
	private Vector2f position;
	private Vector2f size;
	private int drawDepth;
	private float rotation; //IN RADIANS
	private boolean mirroredHorizontal;
	private boolean mirroredVertical;
	private Vector4f color;
	

	protected boolean isDirty;
	
	public Model(Texture texture, Vector2f position, Vector2f size) {
		this.uuid = UUID.randomUUID();
		this.texture = texture;
		this.sprite = null;
		
		this.position = new Vector2f(position);
		this.size = new Vector2f(size);
		this.drawDepth = 0;
		this.rotation = 0;
		this.mirroredHorizontal = false;
		this.mirroredVertical = false;
		
		this.color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
		setDirty();
	}
	
	public void update(float dt) {
		
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public Vector2f getSize() {
		return size;
	}
	
	public int getDrawDepth() {
		return drawDepth;
	}
	
	public float getRotation() {
		return rotation;
	}
	
	public boolean getMirroredHorizontal() {
		return mirroredHorizontal;
	}
	
	public boolean getMirroredVertical() {
		return mirroredVertical;
	}
	
	public Vector4f getColor() {
		return this.color;
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setSprite(Sprite sprite) {
		if (this.sprite == sprite) {
			return;
		}
		if (this.getTexture() != sprite.getTexture()) {
			ExceptionPrinter.print(new Exception("new sprite is not from this flat model's set texture."), "setting singular sprite");
		} else {
			this.sprite = sprite;
		}
	}
	
	public void setColor(Vector4f color){
		this.color.set(color.x, color.y, color.z, color.w);
		setDirty();
	}
	
	public void setPosition(Vector2f pos) {
		position.set(pos);
		setDirty();
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y);
		setDirty();
	}
	
	public void setPositionX(float x) {
		position.set(x, position.y);
		setDirty();
	}
	
	public void setPositionY(float y) {
		position.set(position.x, y);
		setDirty();
	}
	

	
	public void setSize(Vector2i s) {
		size.set(s);
		setDirty();
	}
	
	public void setDrawDepth(int drawDepth) {
		this.drawDepth = drawDepth;
		setDirty();
	}
	
	public void setRotationDeg(float deg) {
		rotation = (float) Math.toRadians(deg);
		setDirty();
	}
	
	public void setRotationRad(float rad) {
		rotation = rad;
		setDirty();
	}
	
	public void setMirroredHorizontal(boolean tf) {
		mirroredHorizontal = tf;
		setDirty();
	}
	
	public void setMirroredVertical(boolean tf) {
		mirroredVertical = tf;
		setDirty();
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public void setDirty() {
		isDirty = true;
	}
	
	public void setClean() {
		isDirty = false;
	}
}
