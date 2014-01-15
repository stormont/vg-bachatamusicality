package com.voyagegames.bachatamusicality;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class MainGdxGame extends Game {

	private final Sound[] sounds = new Sound[12];
	
	private MainScreen mainScreen;
	
	@Override
	public void create() {
		sounds[0] = Gdx.audio.newSound(Gdx.files.internal("data/audio/1.mp3"));
		sounds[1] = Gdx.audio.newSound(Gdx.files.internal("data/audio/2.mp3"));
		sounds[2] = Gdx.audio.newSound(Gdx.files.internal("data/audio/3.mp3"));
		sounds[3] = Gdx.audio.newSound(Gdx.files.internal("data/audio/4.mp3"));
		sounds[4] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bongo1.mp3"));
		sounds[5] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guira1.mp3"));
		sounds[6] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bass1.mp3"));
		sounds[7] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guitar1.mp3"));
		sounds[8] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bongo2.mp3"));
		sounds[9] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guira2.mp3"));
		sounds[10] = Gdx.audio.newSound(Gdx.files.internal("data/audio/bass2.mp3"));
		sounds[11] = Gdx.audio.newSound(Gdx.files.internal("data/audio/guitar2.mp3"));
		
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
