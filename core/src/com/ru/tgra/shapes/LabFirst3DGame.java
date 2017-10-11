package com.ru.tgra.shapes;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.utils.BufferUtils;

public class LabFirst3DGame extends ApplicationAdapter implements InputProcessor {

	private FloatBuffer matrixBuffer;

	private int renderingProgramID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private int positionLoc;
	private int normalLoc;

	private int modelMatrixLoc;
	private int viewMatrixLoc;
	private int projectionMatrixLoc;

	private int colorLoc;
	private Camera cam;
	private Camera orthoCam;
	private int angle;
	private float fov = 90.0f;
	private float velocity = 0.0f;
	private float gravity = -9.8f;
	private ArrayList<Wall> walls;


	//private ModelMatrix modelMatrix;

	@Override
	public void create () {
		
		walls = new ArrayList<Wall>();
		Gdx.input.setInputProcessor(this);

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

		colorLoc				= Gdx.gl.glGetUniformLocation(renderingProgramID, "u_color");

		Gdx.gl.glUseProgram(renderingProgramID);
/*
		float[] mm = new float[16];

		mm[0] = 1.0f; mm[4] = 0.0f; mm[8] = 0.0f; mm[12] = 0.0f;
		mm[1] = 0.0f; mm[5] = 1.0f; mm[9] = 0.0f; mm[13] = 0.0f;
		mm[2] = 0.0f; mm[6] = 0.0f; mm[10] = 1.0f; mm[14] = 0.0f;
		mm[3] = 0.0f; mm[7] = 0.0f; mm[11] = 0.0f; mm[15] = 1.0f;

		modelMatrixBuffer = BufferUtils.newFloatBuffer(16);
		modelMatrixBuffer.put(mm);
		modelMatrixBuffer.rewind();

		Gdx.gl.glUniformMatrix4fv(modelMatrixLoc, 1, false, modelMatrixBuffer);
*/
		//COLOR IS SET HERE
		Gdx.gl.glUniform4f(colorLoc, 0.7f, 0.2f, 0, 1);

		BoxGraphic.create(positionLoc, normalLoc);
		SphereGraphic.create(positionLoc, normalLoc);
		SincGraphic.create(positionLoc);
		CoordFrameGraphic.create(positionLoc);

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		ModelMatrix.main.setShaderMatrix(modelMatrixLoc);

		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		//OrthographicProjection3D(-2, 2, -2, 2, 1, 100);
		cam = new Camera(viewMatrixLoc,projectionMatrixLoc);
		cam.look(new Point3D(-3,4,-3), new Point3D(0,4,0), new Vector3D(0,1,0));
		
		orthoCam = new Camera(viewMatrixLoc,projectionMatrixLoc);
		orthoCam.orthographicProjection(-10, 10, -10, 10,10.0f, 2000.0f);
		
		setupWalls();
		
		
	}
	
	private void handleInput(float deltaTime)
	{
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam.yaw(90.0f * deltaTime);  	
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam.yaw(-90.0f * deltaTime);
		}
//		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
//			cam.pitch(90.0f * deltaTime);
//		}
//		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//			cam.pitch(-90.0f * deltaTime);
//		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			cam.slide(-3.0f * deltaTime,  0,  0);

		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			cam.slide(3.0f * deltaTime,  0, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			cam.slide(0, 0, -3.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			cam.slide(0, 0, 3.0f * deltaTime);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.R)) {
			cam.slide(0, 3.0f * deltaTime, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F)) {
			cam.slide(0, -3.0f * deltaTime, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			cam.roll(-90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.E)) {
			cam.roll(90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.T)) {
			fov -= 30.0f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.G)) {
			fov += 30.0f * deltaTime;
		}
	}
	
	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		handleInput(deltaTime);

		//do all updates to the game
		
		// Walls
		if(cam.eye.x <= -4f){
			cam.eye.x = -4f;
		}
		if(cam.eye.x >= 4f){
			cam.eye.x = 4f;
		}
		if(cam.eye.z <= -4f){
			cam.eye.z = -4f;
		}
		if(cam.eye.z >= 4f){
			cam.eye.z = 4f;
		}
		if(cam.eye.y <= 1){
			cam.eye.y = 1;
			velocity = 0;
		}
		
		for(Wall w: walls){
			
		}
		
		velocity +=gravity * deltaTime;
		cam.eye.y += velocity * deltaTime;
		
		
		
	}
	
	private void display()
	{
		//do all actual drawing and rendering here
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
			Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			cam.perspectiveProjection(fov, 2.0f, 0.1f, 100.0f);
			cam.setShaderMatrices();

			Gdx.gl.glUniform4f(colorLoc, 0.3f, 0.3f, 1.0f, 1.0f);
	
			
			ModelMatrix.main.loadIdentityMatrix();
			
//			int maxlevel = 15;
//			ModelMatrix.main.pushMatrix();
//			for(int level = 0; level < maxlevel;level++){
//				ModelMatrix.main.addTranslation(0.55f, 1.0f, -0.55f);
//				ModelMatrix.main.pushMatrix();
//				for(int i = 0; i < maxlevel-level; i++){
//					ModelMatrix.main.addTranslation(1.1f, 0, 0);
//					ModelMatrix.main.pushMatrix();
//					for(int j = 0; j < maxlevel-level; j++){
//						ModelMatrix.main.addTranslation(0, 0, -1.1f);
//						ModelMatrix.main.pushMatrix();
//
//						if(i % 2 == 0){
//							ModelMatrix.main.addScale(0.2f, 1.0f, 1);
//						}
//						else{
//							ModelMatrix.main.addScale(1, 1, 0.2f);
//
//						}
//						ModelMatrix.main.setShaderMatrix();
//						BoxGraphic.drawSolidCube();
//						ModelMatrix.main.popMatrix();
//
//					}
//					ModelMatrix.main.popMatrix();
//				}
//				ModelMatrix.main.popMatrix();
//			}
//			ModelMatrix.main.popMatrix();
			
			for(Wall w: walls){
				ModelMatrix.main.loadIdentityMatrix();
				Gdx.gl.glUniform4f(colorLoc, w.color.r,  w.color.g,  w.color.b, 1.0f);
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(w.pos.x, w.pos.y, w.pos.z);
				ModelMatrix.main.addScale(w.scale.x, w.scale.y, w.scale.z);
				ModelMatrix.main.setShaderMatrix();
				BoxGraphic.drawSolidCube();
				ModelMatrix.main.popMatrix();
			}
			
	}

	@Override
	public void render () {
		
		//put the code inside the update and display methods, depending on the nature of the code
		update();
		display();

	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void setupWalls(){	
		Color wallColor = new Color(0.3f,1.0f,0.5f);
		Color floorColor = new Color(1.0f,0.3f,0.1f);
		// Walls
		walls.add(new Wall(new Point3D(-5, 0, 0), new Point3D(1.0f, 10.0f, 10.0f), wallColor));
		walls.add(new Wall(new Point3D(5, 0, 0), new Point3D(1.0f, 10.0f, 10.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 0, 5), new Point3D(10.0f, 10.0f, 1.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 0, -5), new Point3D(10.0f, 10.0f, 1.0f),wallColor));
		
		// Floor
		walls.add(new Wall(new Point3D(0, 0, 0), new Point3D(10.0f, 1.0f, 10.0f),floorColor));
		
		walls.add(new Wall(new Point3D(0, 1.0f, 0), new Point3D(2.0f, 2.0f, 2.0f),floorColor));


	}
	
	private void checkCollisionOnWall(Wall wall){
			
	}

}