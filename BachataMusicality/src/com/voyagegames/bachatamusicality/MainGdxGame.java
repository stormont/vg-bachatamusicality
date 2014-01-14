package com.voyagegames.bachatamusicality;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class MainGdxGame extends Game {

	private final Sound[] sounds = new Sound[12];
	
	private MainScreen mainScreen;
	
	@Override
	public void create() {
		sounds[0] = Gdx.audio.newSound(Gdx.files.internal("data/audio/1.wav"));
		sounds[1] = Gdx.audio.newSound(Gdx.files.internal("data/audio/2.wav"));
		sounds[2] = Gdx.audio.newSound(Gdx.files.internal("data/audio/3.wav"));
		sounds[3] = Gdx.audio.newSound(Gdx.files.internal("data/audio/4.wav"));
		sounds[4] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bongo1.wav"));
		sounds[5] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guira1.wav"));
		sounds[6] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bass1.wav"));
		sounds[7] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guitar1.wav"));
		sounds[8] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bongo2.wav"));
		sounds[9] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guira2.wav"));
		sounds[10] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bass2.wav"));
		sounds[11] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guitar2.wav"));
		
		this.setScreen(new SplashScreen(this));
	}
	
	public void splashScreenComplete() {
		this.setScreen(getMainScreen());
	}
	
	public void helpScreenComplete() {
		this.setScreen(getMainScreen());
	}
	
	public void showHelpScreen() {
		this.setScreen(new HelpScreen(this));
	}
	
	private MainScreen getMainScreen() {
		if (mainScreen != null) return mainScreen;
		mainScreen = new MainScreen(this, sounds);
		return mainScreen;
	}
	
}
