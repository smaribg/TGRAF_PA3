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

	Shader shader;
	private Camera cam1;
	private Camera cam2;
	private int angle;
	private float fov = 90.0f;
	private float velocity = 0.0f;
	private float gravity = -9.8f;
	private ArrayList<Wall> walls;
	boolean mouse = false;
	float verticalAngle = 0;
	float horizontalAngle = 0;
	


	//private ModelMatrix modelMatrix;

	@Override
	public void create () {
		
		shader = new Shader();
		walls = new ArrayList<Wall>();
		Gdx.input.setInputProcessor(this);

		
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

		BoxGraphic.create(shader.getVertexPointer(), shader.getNormalPointer());
		SphereGraphic.create(shader.getVertexPointer(), shader.getNormalPointer());
		SincGraphic.create(shader.getVertexPointer());
		CoordFrameGraphic.create(shader.getVertexPointer());

		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		ModelMatrix.main = new ModelMatrix();
		ModelMatrix.main.loadIdentityMatrix();
		//ModelMatrix.main.setShaderMatrix(modelMatrixLoc);
		shader.setModelMatrix(ModelMatrix.main.getMatrix());
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		//OrthographicProjection3D(-2, 2, -2, 2, 1, 100);
		cam1 = new Camera();
		cam1.look(new Point3D(-3,4,-3), new Point3D(0,4,0), new Vector3D(0,1,0));
		cam1.look(new Point3D(-3,4,-3), new Point3D(0,4,0), new Vector3D(0,1,0));

		
		cam2 = new Camera();
		cam2.look(new Point3D(-3,4,-3), new Point3D(0,4,0), new Vector3D(0,1,0));
		
		setupWalls();	
	}
	
	private void handleInput(float deltaTime)
	{
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			cam1.yaw(90.0f * deltaTime);  	
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			cam1.yaw(-90.0f * deltaTime);
		}
//		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
//			cam.pitch(90.0f * deltaTime);
//		}
//		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
//			cam.pitch(-90.0f * deltaTime);
//		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)) {
			cam1.slide(-3.0f * deltaTime,  0,  0);
			cam2.slide(-3.0f * deltaTime,  0,  0);

		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			cam1.slide(3.0f * deltaTime,  0, 0);
			cam2.slide(3.0f * deltaTime,  0, 0);

		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			cam1.slide(0, 0, -3.0f * deltaTime);
			cam2.slide(0, 0, -3.0f * deltaTime);

		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			cam1.slide(0, 0, 3.0f * deltaTime);
			cam2.slide(0, 0, 3.0f * deltaTime);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.R)) {
			cam1.slide(0, 3.0f * deltaTime, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F)) {
			cam1.slide(0, -3.0f * deltaTime, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
			cam1.roll(-90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.E)) {
			cam1.roll(90.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.T)) {
			fov -= 30.0f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.G)) {
			fov += 30.0f * deltaTime;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.V)) {
			mouse = !mouse;
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			System.exit(0);
		}
	}
	
	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		handleInput(deltaTime);

		//do all updates to the game
		
		// Walls
		if(cam1.eye.x <= -4f){
			cam1.eye.x = -4f;
		}
		if(cam1.eye.x >= 4f){
			cam1.eye.x = 4f;
		}
		if(cam1.eye.z <= -4f){
			cam1.eye.z = -4f;
		}
		if(cam1.eye.z >= 4f){
			cam1.eye.z = 4f;
		}
		if(cam1.eye.y <= 1){
			cam1.eye.y = 1;
			velocity = 0;
		}
		
		// Collisions
		for(Wall w: walls){
			checkCollisionOnWall(w);
		}
		
		
		// Gravity
		velocity +=gravity * deltaTime;
		cam1.eye.y += velocity * deltaTime;
		
		velocity +=gravity * deltaTime;
		cam2.eye.y += velocity * deltaTime;
		

	}
	
	private void display()
	{
		//do all actual drawing and rendering here
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam1.perspectiveProjection(fov, 2.0f, 0.1f, 100.0f);
		shader.setViewMatrix(cam1.getViewMatrix());
		shader.setProjectionMatrix(cam1.getProjectionMatrix());
		if(mouse){
			
			// Mouse
			float deltaTime = Gdx.graphics.getDeltaTime();
			int xpos, ypos;
			xpos = Gdx.input.getX();
			ypos = Gdx.input.getY();
			Gdx.input.setCursorPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
			Gdx.input.setCursorCatched(true);
			horizontalAngle += 0.05f * deltaTime * (float)(Gdx.graphics.getWidth()/2 - xpos );
			verticalAngle   += 0.05f * deltaTime * (float)( Gdx.graphics.getHeight()/2 - ypos );
			Vector3D direction = new Vector3D(
					(float)(Math.cos(verticalAngle) * Math.sin(horizontalAngle)),
					(float)Math.sin(verticalAngle),
					(float)(Math.cos(verticalAngle) * Math.cos(horizontalAngle))
					);
			
			Vector3D right = new Vector3D(
					(float)Math.sin(horizontalAngle - 3.14f/2.0f),
					0,
					(float)Math.cos(horizontalAngle - 3.14f/2.0f)
					);
			Vector3D up = right.cross(direction);
			Point3D p = new Point3D();
			p.set(cam1.eye.x, cam1.eye.y, cam1.eye.z);
			p.add(direction);
			cam1.look(cam1.eye, p, up);
			
		}


		
		ModelMatrix.main.loadIdentityMatrix();
		
		shader.setLightPosition(3.0f, 3.0f, 0.0f, 1.0f);
		shader.setLightDiffuse(1.0f, 1.0f, 1.0f, 1.0f);
			
			
		for(Wall w: walls){
			ModelMatrix.main.loadIdentityMatrix();
			shader.setMaterialDiffuse(w.color.r,  w.color.g,  w.color.b, 1.0f);
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(w.pos.x, w.pos.y, w.pos.z);
			ModelMatrix.main.addScale(w.scale.x, w.scale.y, w.scale.z);
			shader.setModelMatrix(ModelMatrix.main.getMatrix());
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
		Color wallColor = new Color(0.7f,0.13f,0.13f);
		Color floorColor = new Color(0.93f,0.9f,0.66f);
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