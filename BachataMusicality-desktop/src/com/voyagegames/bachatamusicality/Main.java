package com.voyagegames.bachatamusicality;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "BachataMusicality";
		cfg.useGL20 = false;
		cfg.width = 270;
		cfg.height = 480;
		
		new LwjglApplication(new MainGdxGame(), cfg);
	}
}
