package main.java.com.baseengine.main.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{
	private static Camera camera = null;
	
	//Frustum Variables
	private float nearClipping = 0.01f;
	private float farClipping = 250f;
	
	//Vector3's
	private Vector3f position;
	private Vector3f lookAt = new Vector3f(0, 0, 0);
	private Vector3f cameraUp = new Vector3f(0, 1, 0);
	
	
	//Matrices
	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;
	private Matrix4f rendererMatrix;
	private boolean hasMatrixChanged = false;
	
	private Camera() {
		this.position = new Vector3f(0, 0, 500);
		this.lookAt = new Vector3f(0, 0, 0);
		this.cameraUp = new Vector3f(0, 1, 0);
		
		this.projectionMatrix = new Matrix4f();
		this.viewMatrix = new Matrix4f();
		this.rendererMatrix = new Matrix4f();
		
		adjustProjection();
	}
	
	public static Camera get() {
		if (camera == null) {
			camera = new Camera();
		}
		
		return camera;
	}
	
	public void update(float deltaTime) {

	}
	
	public void endFrame() {
		hasMatrixChanged = false;
	}
	
	public void updateWindowSize() {
		adjustProjection();
	}
	
	public void adjustView() {
		viewMatrix.identity();
		viewMatrix.lookAt(position, lookAt, cameraUp);
		
		adjustRendererMatrix();
	}
	
	public void adjustProjection() {
		
		float distX = Window.get().getWidth() / 2;
		float distY = Window.get().getHeight() / 2;
		
		projectionMatrix.identity();
		projectionMatrix.ortho(distX, distX, distY, distY, nearClipping, farClipping);
		adjustRendererMatrix();
	}
	
	public void adjustRendererMatrix() {
		rendererMatrix.set(projectionMatrix);
		rendererMatrix.mul(viewMatrix);
		
		hasMatrixChanged = true;
	}
	
	/*   
	 *  --------- SETTERS --------------
	 */
	public void setPosition(Vector2f position) {
		setPosition(position.x, position.y);
	}
	
	private void setPosition(float x, float y) {
		position.set(x, y, position.z);
		this.lookAt.set(x, y, 0);
		adjustView();
	}
	
	/*   
	 *  --------- GETTERS --------------
	 */
	public Vector3f getPosition() {
		return position;
	}
	
	
	public boolean hasMatrixChanged() {
		return hasMatrixChanged;
	}
	
	public Matrix4f getRendererMatrix() {
		return rendererMatrix;
	}
	
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
}
