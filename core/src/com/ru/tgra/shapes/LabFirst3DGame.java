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
	private Camera orthoCam;
	private int angle;
	private float fov = 90.0f;
	private float velocity = 0.0f;
	private float gravity = -9.8f;
	private ArrayList<Wall> walls;
	private ArrayList<Wall> floors;

	private ArrayList<Coin> coins;
	boolean mouse = false;
	float verticalAngle = 0;
	float horizontalAngle = 0;
	


	//private ModelMatrix modelMatrix;

	@Override
	public void create () {
		
		shader = new Shader();
		walls = new ArrayList<Wall>();
		floors = new ArrayList<Wall>();

		coins = new ArrayList<Coin>();
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

		
		orthoCam = new Camera();
		orthoCam.orthographicProjection(-4, 4, -4, 4, 1f, 50);
		
		setupLevel();	
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
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)) {
			cam1.slide(3.0f * deltaTime,  0, 0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.W)) {
			cam1.slide(0, 0, -3.0f * deltaTime);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)) {
			cam1.slide(0, 0, 3.0f * deltaTime);
		}

		if(Gdx.input.isKeyPressed(Input.Keys.R)) {
			cam1.slide(0, 3.0f * deltaTime, 0);
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

		if(cam1.eye.y <= 1){
			cam1.eye.y = 1;
			velocity = 0;
		}
		
		// Collisions
		for(Wall w: walls){
			checkCollisionOnWall(w,cam1.eye);
		}
		
		
		// Gravity
		velocity +=gravity * deltaTime;
		cam1.eye.y += velocity * deltaTime;
		
		velocity +=gravity * deltaTime;
		
		// Diamonds
		for(Coin d : coins){
			d.rotation.y += d.rotationSpeed * deltaTime;
		}

	}
	
	private void display()
	{
		//do all actual drawing and rendering here
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		for(int viewNum = 0; viewNum < 2; viewNum++ ){
			if(viewNum == 0){
				Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				cam1.perspectiveProjection(fov, 2.0f, 0.1f, 100.0f);
				shader.setViewMatrix(cam1.getViewMatrix());
				shader.setProjectionMatrix(cam1.getProjectionMatrix());
				
				// Mouse stuff
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
				p.y = cam1.eye.y;
				cam1.look(cam1.eye, p, new Vector3D(0,1,0));
				
			}else{
				Gdx.gl.glViewport(3*Gdx.graphics.getWidth()/4,2*Gdx.graphics.getHeight()/3, Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()/3);
				orthoCam.look(new Point3D(cam1.eye.x,5.0f,cam1.eye.z),cam1.eye,new Vector3D(0,0,-1));
				shader.setViewMatrix(orthoCam.getViewMatrix());
				shader.setProjectionMatrix(orthoCam.getProjectionMatrix());
				Gdx.gl.glScissor(3*Gdx.graphics.getWidth()/4,2*Gdx.graphics.getHeight()/3, Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()/3);
				Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
			}
			
			
			ModelMatrix.main.loadIdentityMatrix();
			shader.setLightPosition(5.0f, 5.0f, -5.0f, 1.0f);
			shader.setLightDiffuse(1.0f, 1.0f, 0.8f, 1.0f);

			// Walls
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
			
			for(Wall w: floors){
				ModelMatrix.main.loadIdentityMatrix();
				shader.setMaterialDiffuse(w.color.r,  w.color.g,  w.color.b, 1.0f);
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(w.pos.x, w.pos.y, w.pos.z);
				ModelMatrix.main.addScale(w.scale.x, w.scale.y, w.scale.z);
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				BoxGraphic.drawSolidCube();
				ModelMatrix.main.popMatrix();
			}
			
			// Diamonds
			for(Coin d: coins){
				ModelMatrix.main.loadIdentityMatrix();
				shader.setMaterialDiffuse(d.color.r,  d.color.g,  d.color.b, 1.0f);
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(d.pos.x, d.pos.y, d.pos.z);
				ModelMatrix.main.addRotationY(d.rotation.y);
				ModelMatrix.main.addScale(d.scale.x, d.scale.y, d.scale.z);
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				SphereGraphic.drawSolidSphere();
				ModelMatrix.main.popMatrix();
			}
			
			if(viewNum == 1){
				ModelMatrix.main.loadIdentityMatrix();
				shader.setMaterialDiffuse(1.0f, 0, 0, 1.0f);
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(cam1.eye.x,cam1.eye.y,cam1.eye.z);
				ModelMatrix.main.addScale(1, 1, 1);

				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				SphereGraphic.drawSolidSphere();
				ModelMatrix.main.popMatrix();
			}

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
	
	private void setupLevel(){	
		Color wallColor = new Color(0.5f,0.5f,0.5f);
		Color floorColor = new Color(0.63f,0.32f,0.18f);
		Color coinColor = new Color(1.0f,0.8f,0);
		
		// Walls
		walls.add(new Wall(new Point3D(-5, 2.5f, -5.0f), new Point3D(2.0f, 5.0f, 20.0f), wallColor));
		walls.add(new Wall(new Point3D(5, 2.5f, -5.0f), new Point3D(2.0f, 5.0f, 20.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 2.5f, 5), new Point3D(10.0f, 5.0f, 2.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 1, 0), new Point3D(2.0f, 2.0f, 2.0f),wallColor));

		
		// Floor
		floors.add(new Wall(new Point3D(0, 0, -5), new Point3D(10.0f, 1.0f, 20.0f),floorColor));
		floors.add(new Wall(new Point3D(0, 0, -25), new Point3D(10.0f, 1.0f, 20.0f),floorColor));

		
		// Coins
		coins.add(new Coin(new Point3D(3,1,3), new Point3D(0.01f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));
		coins.add(new Coin(new Point3D(-3,1,3), new Point3D(0.01f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));
		coins.add(new Coin(new Point3D(3,1,-3), new Point3D(0.01f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));
		coins.add(new Coin(new Point3D(-3,1,-3), new Point3D(0.01f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));


	}
	
	private void checkCollisionOnWall(Wall wall, Point3D pos){
		float radius = 0.5f;
		float maxX = wall.pos.x+wall.scale.x/2+radius;
		float minX = wall.pos.x-wall.scale.x/2-radius;
		float maxZ = wall.pos.z+wall.scale.z/2+radius;
		float minZ = wall.pos.z-wall.scale.z/2-radius;

		if(pos.x >= minX && pos.x <= maxX 
			&& pos.z >= minZ && pos.z <= maxZ){
			float minDis = 100.0f;
			int d = 0;
			
			if(Math.abs(maxZ-pos.z) < minDis){
				minDis = Math.abs(maxZ-pos.z);
				d = 3;
			}
			
			if(Math.abs(minZ-pos.z) < minDis){
				minDis = Math.abs(minZ-pos.z);
				d = 4;
			}
			if(Math.abs(maxX-pos.x) < minDis){
				minDis = Math.abs(maxX-pos.x);
				d = 1;
			}
			if(Math.abs(minX-pos.x) < minDis){
				minDis = Math.abs(minX-pos.x);
				d = 2;
			}
			
			if(d == 1){
				System.out.println("maxx");

				pos.x = maxX;
			}
			if(d == 2){
				System.out.println("minx");

				pos.x = minX;
			}
			if(d == 3){
				System.out.println("maxz");

				pos.z = maxZ;
			}
			if(d == 4){
				System.out.println("minz");

				pos.z = minZ;
			}

		}
	}
	
	
}