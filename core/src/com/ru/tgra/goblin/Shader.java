package com.ru.tgra.goblin;

import java.nio.FloatBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Shader {
	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;
	private int normalLoc;

	private int modelMatrixLoc;
	private int viewMatrixLoc;
	private int projectionMatrixLoc;
	
	private int eyePosLoc; 

	//private int colorLoc;
	private int globalAmbLoc;
	private int lightPosLoc;
	private int lightColorLoc;
	private int light2PosLoc;
	private int light2ColorLoc;
	private int dirLightPosLoc;
	private int dirLightColorLoc;
	private int materialDiffLoc;
	private int materialSpecLoc;
	private int materialShineLoc;
	private int materialEmissionLoc;

	public Shader(){
		String vertexShaderString;
		String fragmentShaderString;

		vertexShaderString = Gdx.files.internal("shaders/simple3D.vert").readString();
		fragmentShaderString =  Gdx.files.internal("shaders/simple3D.frag").readString();

		vertexShaderID = Gdx.gl.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = Gdx.gl.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	
		Gdx.gl.glShaderSource(vertexShaderID, vertexShaderString);
		Gdx.gl.glShaderSource(fragmentShaderID, fragmentShaderString);
	
		Gdx.gl.glCompileShader(vertexShaderID);
		Gdx.gl.glCompileShader(fragmentShaderID);
		System.out.println(Gdx.gl.glGetShaderInfoLog(vertexShaderID));
		System.out.println(Gdx.gl.glGetShaderInfoLog(fragmentShaderID));

		renderingProgramID = Gdx.gl.glCreateProgram();
	
		Gdx.gl.glAttachShader(renderingProgramID, vertexShaderID);
		Gdx.gl.glAttachShader(renderingProgramID, fragmentShaderID);
	
		Gdx.gl.glLinkProgram(renderingProgramID);

		positionLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_position");
		Gdx.gl.glEnableVertexAttribArray(positionLoc);

		normalLoc				= Gdx.gl.glGetAttribLocation(renderingProgramID, "a_normal");
		Gdx.gl.glEnableVertexAttribArray(normalLoc);

		modelMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_modelMatrix");
		viewMatrixLoc			= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_viewMatrix");
		projectionMatrixLoc	= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_projectionMatrix");

		//colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");
		
		eyePosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_eyePosition");

		globalAmbLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_globalAmbient");
		lightPosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPosition");
		lightColorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightColor");
		light2PosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightPosition2");
		light2ColorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_lightColor2");
		dirLightPosLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_dirLightPosition");
		dirLightColorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_dirLightColor");
		materialDiffLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialDiffuse");
		materialSpecLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialSpecular");
		materialShineLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialShininess");
		materialEmissionLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_materialEmission");


		Gdx.gl.glUseProgram(renderingProgramID);
	}
	
//	public void setColor(float r, float g, float b, float a){
//		Gdx.gl.glUniform4f(colorLoc, r, g, b, a);
//	}

	public void setGlobalAmbient(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(globalAmbLoc, r, g, b, a);
	}
	
	public void setLightColor(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(lightColorLoc, r, g, b, a);
	}
	
	public void setLightPosition(float x, float y, float z, float w){
		Gdx.gl.glUniform4f(lightPosLoc, x, y, z, w);
	}
	
	public void setLight2Color(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(light2ColorLoc, r, g, b, a);
	}
	
	public void setLight2Position(float x, float y, float z, float w){
		Gdx.gl.glUniform4f(light2PosLoc, x, y, z, w);
	}
	
	public void setDirLightColor(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(dirLightColorLoc, r, g, b, a);
	}
	
	public void setDirLightPosition(float x, float y, float z, float w){
		Gdx.gl.glUniform4f(dirLightPosLoc, x, y, z, w);
	}

	public void setEyePosition(float x, float y, float z, float w){
		Gdx.gl.glUniform4f(eyePosLoc, x, y, z, w);
	}
	
	public void setMaterialDiffuse(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(materialDiffLoc, r, g, b, a);
	}
	
	public void setMaterialSpecular(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(materialSpecLoc, r, g, b, a);
	}

	public void setMaterialShininess(float shine){
		Gdx.gl.glUniform1f(materialShineLoc, shine);
	}

	public void setMaterialEmission(float r, float g, float b, float a){
		Gdx.gl.glUniform4f(materialEmissionLoc, r, g, b, a);
	}
	
	public int getVertexPointer(){
		return positionLoc;
	}
	
	public int getNormalPointer(){
		return normalLoc;
	}
	
	public void setModelMatrix(FloatBuffer matrix){
		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, matrix);
	}
	public void setViewMatrix(FloatBuffer matrix){
		Gdx.gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, matrix);
	}	
	public void setProjectionMatrix(FloatBuffer matrix){
		Gdx.gl.glUniformMatrix4fv(projectionMatrixLoc, 1, false, matrix);
	}
}
