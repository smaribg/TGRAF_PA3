	package com.ru.tgra.lab1.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ru.tgra.shapes.LabFirst3DGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Lab1"; // or whatever you like
		config.width = 1620;  //experiment with
		config.height = 810;  //the window size
		config.x = 250;
		config.y = 150;
		new LwjglApplication(new LabFirst3DGame(), config);
	}
}
