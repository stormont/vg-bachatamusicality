package com.voyagegames.bachatamusicality;

import com.badlogic.gdx.Game;

public class MainGdxGame extends Game {
	
	@Override
	public void create() {
		this.setScreen(new MainScreen());
	}

}
