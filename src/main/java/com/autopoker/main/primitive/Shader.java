package com.autopoker.main.primitive;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import com.autopoker.main.utility.ExceptionPrinter;

public class Shader
{
	private int shaderProgramId;
	private boolean beingUsed = false;
	
	private String vertexSource;
	private String fragmentSource;
	
	public Shader(String filepath) {
		try {
			String source = new String(Files.readAllBytes(Paths.get(filepath)));
			String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");
			
			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\r\n", index);
			String firstPattern = source.substring(index, eol).trim();
			
			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\r\n", index);
			String secondPattern = source.substring(index, eol).trim();
			
			if (firstPattern.equals("vertex")) {
				vertexSource = splitString[1];
			}
			else if (firstPattern.equals("fragment")) {
				fragmentSource = splitString[1];
			} else {
				throw new IOException("Unexpected token: " + firstPattern);
			}
			
			if (secondPattern.equals("vertex")) {
				vertexSource = splitString[2];
			}
			else if (secondPattern.equals("fragment")) {
				fragmentSource = splitString[2];
			} else {
				throw new IOException("Unexpected token: " + secondPattern);
			}
			
		} catch (IOException e) {
			ExceptionPrinter.print(e, "loading shader from path " + filepath);
		}
	}
	
	public void compile() {
		
		int vertexId, fragmentId;
		
		vertexId = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexId, vertexSource);
		glCompileShader(vertexId);
		
		int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: defaultShader.glsl \n\tVertex shader compilation failed.");
			System.out.println(glGetShaderInfoLog(vertexId, len));
		}
		
		fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentId, fragmentSource);
		glCompileShader(fragmentId);
		
		success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: defaultShader.glsl \n\tFragment shader compilation failed.");
			System.out.println(glGetShaderInfoLog(fragmentId, len));
		}
		
		shaderProgramId = glCreateProgram();
		glAttachShader(shaderProgramId, vertexId);
		glAttachShader(shaderProgramId, fragmentId);
		glLinkProgram(shaderProgramId);
		
		success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: defaultShader.glsl \n\tShader linking failed.");
			System.out.println(glGetProgramInfoLog(shaderProgramId, len));
		}
	}
	
	public void use() {
		if (!beingUsed) {
			glUseProgram(shaderProgramId);
			beingUsed = true;
		}
	}
	
	public void detach() {
		glUseProgram(0);
		beingUsed = false;
	}
	
	public void uploadMat4f(String varName, Matrix4f mat4) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		mat4.get(matBuffer);
		glUniformMatrix4fv(varLocation, false, matBuffer);
	}
	
	public void uploadMat3f(String varName, Matrix3f mat3) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		mat3.get(matBuffer);
		glUniformMatrix3fv(varLocation, false, matBuffer);
	}
	
	public void uploadVec4f(String varName, Vector4f vec) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}
	
	public void uploadVec3f(String varName, Vector3f vec) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}
	
	public void uploadVec2f(String varName, Vector2f vec) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform2f(varLocation, vec.x, vec.y);
	}
	
	public void uploadFloat(String varName, float f) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform1f(varLocation, f);
	}
	
	public void uploadInt(String varName, int i) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform1i(varLocation, i);
	}
	
	public void uploadTexture(String varName, int slot) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform1i(varLocation, slot);
	}
	
	public void uploadIntArray(String varName, int[] a) {
		int varLocation = glGetUniformLocation(shaderProgramId, varName);
		use();
		glUniform1iv(varLocation, a);
	}
}
