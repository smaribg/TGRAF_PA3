package com.ru.tgra.goblin;

public class Coin {

	public Point3D pos;
	public Point3D scale;
	public Point3D rotation;
	public float rotationSpeed;
	public boolean pickedUp;
	public Color color;
	
	public Coin(Point3D pos, Point3D scale,Point3D rotation, float rotationSpeed, Color color){
		this.pos = pos;
		this.scale = scale;
		this.rotation = rotation;
		this.rotationSpeed = rotationSpeed;
		pickedUp = false;
		this.color = color;
	}
}
