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
	private float fov = 90.0f;
	private float velocity = 0.0f;
	private float gravity = -1.0f;
	private ArrayList<Wall> walls;
	private ArrayList<Wall> floors;
	private Wall elevator;
	float eleMin = 0.4f;
	float eleMax = 2.0f;
	boolean eleUp = true;
	
	private Point3D lastMousePos;
	private float mouseSensitivity = 5.0f;

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
		cam1.look(new Point3D(0,1,2), new Point3D(0,1,-2), new Vector3D(0,1,0));

		
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
			mouseSensitivity += 5.0f * deltaTime;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.F)) {
			mouseSensitivity -= 5.0f * deltaTime;
		}
		
		if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			System.exit(0);
		}
	}
	
	private void update()
	{
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		System.out.println(velocity);
		handleInput(deltaTime);
		
		// Collisions
		for(Wall w: walls){
			checkCollisionOnWall(w,cam1.eye);
		}
		checkCollisionOnWall(elevator,cam1.eye);
		for(Coin c: coins){
			checkCollisionOnCoin(c,cam1.eye);
		}
		
		
				
		// Diamonds
		for(Coin d : coins){
			d.rotation.y += d.rotationSpeed * deltaTime;
		}
		
		if(eleUp){
			if(elevator.pos.y <= eleMax){
				elevator.pos.y += 0.5f*deltaTime;
			}
			else{
				eleUp = false;
			}
		}else{
			if(elevator.pos.y >= eleMin){
				elevator.pos.y -= 0.5f*deltaTime;
			}
			else{
				eleUp = true;
			}
		}
		
		gravity = -9.8f;
		for(Wall floor : floors){
			if(checkCollisionOnFloor(floor,cam1.eye)){
				gravity = 0;
				velocity = 0;
			}
		}
		
		// Gravity
		velocity +=gravity * deltaTime;
		cam1.eye.y += velocity * deltaTime;
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
				shader.setEyePosition(cam1.eye.x, cam1.eye.y, cam1.eye.z, 1.0f);
				mouseInput();

				
			}else{
				Gdx.gl.glViewport(3*Gdx.graphics.getWidth()/4,2*Gdx.graphics.getHeight()/3, Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()/3);
				orthoCam.look(new Point3D(cam1.eye.x,10.0f,cam1.eye.z),cam1.eye,new Vector3D(0,0,-1));
				shader.setViewMatrix(orthoCam.getViewMatrix());
				shader.setProjectionMatrix(orthoCam.getProjectionMatrix());
				
				Gdx.gl.glScissor(3*Gdx.graphics.getWidth()/4,2*Gdx.graphics.getHeight()/3, Gdx.graphics.getWidth()/4,Gdx.graphics.getHeight()/3);
				Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
				Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
			}
			
			
			ModelMatrix.main.loadIdentityMatrix();
			shader.setLightPosition(0.0f, 8.0f, 3.0f, 1.0f);
			shader.setLightColor(0.4f, 0.4f, 0.4f, 1.0f);
			shader.setGlobalAmbient(0.3f, 0.25f, 0.25f, 1.0f);

			// Walls
			for(Wall w: walls){
				ModelMatrix.main.loadIdentityMatrix();
				shader.setMaterialDiffuse(w.color.r,  w.color.g,  w.color.b, 1.0f);
				shader.setMaterialShininess(50.0f);
				shader.setMaterialEmission(0.0f, 0.0f, 0.0f, 1.0f);
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
				shader.setMaterialShininess(50.0f);
				shader.setMaterialEmission(0.0f, 0.0f, 0.0f, 1.0f);
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(w.pos.x, w.pos.y, w.pos.z);
				ModelMatrix.main.addScale(w.scale.x, w.scale.y, w.scale.z);
				shader.setModelMatrix(ModelMatrix.main.getMatrix());
				BoxGraphic.drawSolidCube();
				ModelMatrix.main.popMatrix();
			}

			// Diamonds
			for(Coin c: coins){
				if(!c.pickedUp){
					ModelMatrix.main.loadIdentityMatrix();
					shader.setMaterialDiffuse(c.color.r,  c.color.g,  c.color.b, 1.0f);
					shader.setMaterialShininess(5.0f);
					shader.setMaterialEmission(0.3f, 0.3f, 0.3f, 1.0f);
					ModelMatrix.main.pushMatrix();
					ModelMatrix.main.addTranslation(c.pos.x, c.pos.y, c.pos.z);
					ModelMatrix.main.addRotationY(c.rotation.y);
					ModelMatrix.main.addScale(c.scale.x, c.scale.y, c.scale.z);
					shader.setModelMatrix(ModelMatrix.main.getMatrix());
					SphereGraphic.drawSolidSphere();
					ModelMatrix.main.popMatrix();
				}
			}
			
			ModelMatrix.main.loadIdentityMatrix();
			shader.setMaterialDiffuse(elevator.color.r,  elevator.color.g,  elevator.color.b, 1.0f);
			shader.setMaterialShininess(50.0f);
			shader.setMaterialEmission(0.0f, 0.0f, 0.0f, 1.0f);
			ModelMatrix.main.pushMatrix();
			ModelMatrix.main.addTranslation(elevator.pos.x, elevator.pos.y, elevator.pos.z);
			ModelMatrix.main.addScale(elevator.scale.x, elevator.scale.y, elevator.scale.z);
			shader.setModelMatrix(ModelMatrix.main.getMatrix());
			BoxGraphic.drawSolidCube();
			ModelMatrix.main.popMatrix();

			if(viewNum == 1){
				ModelMatrix.main.loadIdentityMatrix();
				shader.setMaterialDiffuse(1.0f, 0, 0, 1.0f);
				shader.setMaterialShininess(50.0f);
				shader.setMaterialEmission(0.0f, 0.0f, 0.0f, 1.0f);
				ModelMatrix.main.pushMatrix();
				ModelMatrix.main.addTranslation(cam1.eye.x,cam1.eye.y,cam1.eye.z);
				ModelMatrix.main.addScale(0.5f, 0.2f, 0.5f);

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
		walls.add(new Wall(new Point3D(-5, 2.5f, -15.0f), new Point3D(2.0f, 5.0f, 40.0f), wallColor));
		walls.add(new Wall(new Point3D(5, 2.5f, -15.0f), new Point3D(2.0f, 5.0f, 40.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 2.5f, 5), new Point3D(12.0f, 5.0f, 2.0f),wallColor));
		
		
		
		// Door1
		walls.add(new Wall(new Point3D(2.5f, 2.5f, 0), new Point3D(4.0f, 5.0f, 1.0f),wallColor));
		walls.add(new Wall(new Point3D(-2.5f, 2.5f, 0), new Point3D(4.0f, 5.0f, 1.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 4.0f, 0), new Point3D(1.0f, 2.0f, 1.0f),wallColor));

		
		// Door2
		walls.add(new Wall(new Point3D(2.5f, 2.5f, -10.0f), new Point3D(4.0f, 5.0f, 1.0f),wallColor));
		walls.add(new Wall(new Point3D(-2.5f, 2.5f, -10.0f), new Point3D(4.0f, 5.0f, 1.0f),wallColor));
		walls.add(new Wall(new Point3D(0, 4.0f, -10.0f), new Point3D(1.0f, 2.0f, 1.0f),wallColor));

		
		// Floor
		floors.add(new Wall(new Point3D(0, 0, -5), new Point3D(10.0f, 1.0f, 20.0f),floorColor));
		floors.add(new Wall(new Point3D(0, 0, -25), new Point3D(10.0f, 1.0f, 20.0f),floorColor));
		floors.add(new Wall(new Point3D(0, 1.0f, -25), new Point3D(8.0f, 2.0f, 20.0f),floorColor));


		
		// Coins
		coins.add(new Coin(new Point3D(3,1,3), new Point3D(0.03f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));
		coins.add(new Coin(new Point3D(-3,1,3), new Point3D(0.03f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));
		coins.add(new Coin(new Point3D(3,1,-3), new Point3D(0.03f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));
		coins.add(new Coin(new Point3D(-3,1,-3), new Point3D(0.03f,0.2f,0.2f),new Point3D(0,0,0),150.0f,coinColor));


		//Elevator
		elevator = new Wall(new Point3D(0, 0.4f, -14.5f), new Point3D(1.0f, 0.2f, 1.0f),wallColor);
		floors.add(elevator);
	}
	
	private void checkCollisionOnWall(Wall wall, Point3D pos){
		float radius = 0.25f;
		float maxX = wall.pos.x+wall.scale.x/2+radius;
		float minX = wall.pos.x-wall.scale.x/2-radius;
		float maxZ = wall.pos.z+wall.scale.z/2+radius;
		float minZ = wall.pos.z-wall.scale.z/2-radius;
		float maxY = wall.pos.y+wall.scale.y/2+radius;
		float minY = wall.pos.y-wall.scale.y/2-radius;

		if(pos.x >= minX && pos.x <= maxX 
			&& pos.z >= minZ && pos.z <= maxZ
			&& pos.y >= minY && pos.y <= maxY){
			float minDis = Integer.MAX_VALUE;
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
			if(Math.abs(maxZ-pos.z) < minDis){
				minDis = Math.abs(maxZ-pos.z);
				d = 3;
			}
			
			if(Math.abs(minZ-pos.z) < minDis){
				minDis = Math.abs(minZ-pos.z);
				d = 4;
			}
			if(Math.abs(maxY-pos.y) < minDis){
				minDis = Math.abs(maxY-pos.y);
				d = 5;
			}
			if(Math.abs(minY-pos.y) < minDis){
				minDis = Math.abs(minY-pos.y);
				d = 6;
			}
			
			if(d == 1){

				pos.x = maxX;
			}
			if(d == 2){

				pos.x = minX;
			}
			if(d == 3){

				pos.z = maxZ;
			}
			if(d == 4){

				pos.z = minZ;
			}
			if(d == 5){

				pos.y = maxY;
			}
			if(d == 6){

				pos.y = minY;
			}

		}
	}
	
	private boolean checkCollisionOnFloor(Wall wall, Point3D pos){
		float radius = 0.25f;
		float yradius = 0.5f;
		float maxX = wall.pos.x+wall.scale.x/2+radius;
		float minX = wall.pos.x-wall.scale.x/2-radius;
		float maxZ = wall.pos.z+wall.scale.z/2+radius;
		float minZ = wall.pos.z-wall.scale.z/2-radius;
		float maxY = wall.pos.y+wall.scale.y/2+yradius;
		float minY = wall.pos.y-wall.scale.y/2-yradius;

		if(pos.x >= minX && pos.x <= maxX 
			&& pos.z >= minZ && pos.z <= maxZ
			&& pos.y >= minY && pos.y <= maxY){
			float minDis = Integer.MAX_VALUE;
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
			if(Math.abs(maxZ-pos.z) < minDis){
				minDis = Math.abs(maxZ-pos.z);
				d = 3;
			}
			
			if(Math.abs(minZ-pos.z) < minDis){
				minDis = Math.abs(minZ-pos.z);
				d = 4;
			}
			if(Math.abs(maxY-pos.y) < minDis){
				minDis = Math.abs(maxY-pos.y);
				d = 5;
			}
			if(Math.abs(minY-pos.y) < minDis){
				minDis = Math.abs(minY-pos.y);
				d = 6;
			}
			
			if(d == 1){

				pos.x = maxX;
			}
			if(d == 2){

				pos.x = minX;
			}
			if(d == 3){

				pos.z = maxZ;
			}
			if(d == 4){

				pos.z = minZ;
			}
			if(d == 5){
				pos.y = maxY;
			}
			if(d == 6){
				
				pos.y = minY;
			}
			
			return true;
		}
		return false;
	}
	
	private void checkCollisionOnCoin(Coin coin, Point3D pos){
		float radius = 0.25f;
		float maxX = coin.pos.x+coin.scale.x/2+radius;
		float minX = coin.pos.x-coin.scale.x/2-radius;
		float maxZ = coin.pos.z+coin.scale.z/2+radius;
		float minZ = coin.pos.z-coin.scale.z/2-radius;
		float maxY = coin.pos.y+coin.scale.y/2+radius;
		float minY = coin.pos.y-coin.scale.y/2-radius;

		if(pos.x >= minX && pos.x <= maxX 
			&& pos.z >= minZ && pos.z <= maxZ
			&& pos.y >= minY && pos.y <= maxY){
			coin.pickedUp = true;
		}
	}
	
	private void mouseInput(){
		if (lastMousePos == null) {
			lastMousePos = new Point3D(Gdx.input.getX(), Gdx.input.getY(),1);
			return;
		}
		
		Gdx.input.setCursorCatched(true);
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		Point3D currMousePos = new Point3D(Gdx.input.getX(), Gdx.input.getY(),1);
		
		float deltaMouse = currMousePos.x-lastMousePos.x;
		lastMousePos = currMousePos;
		
		if(deltaMouse < 0) {
			cam1.yaw(mouseSensitivity * deltaTime * Math.abs(deltaMouse));
		}
		if(deltaMouse > 0) {
			cam1.yaw(-mouseSensitivity * deltaTime * Math.abs(deltaMouse));
		}
	}
	
}