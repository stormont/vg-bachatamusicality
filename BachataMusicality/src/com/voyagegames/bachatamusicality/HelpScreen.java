package com.voyagegames.bachatamusicality;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class HelpScreen implements Screen, InputProcessor {

	private final MainGdxGame game;
	private final TweenManager tweenManager = new TweenManager();
	private final Stage stage;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final Texture backgroundTexture;
	private final Sprite background;

	public HelpScreen(final MainGdxGame game) {
		this.game = game;
		Tween.registerAccessor(Image.class, new ImageAccessor());
		
		Gdx.input.setInputProcessor(this);
		Gdx.input.setCatchBackKey(true);
		
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1f, h / w);
		stage = new Stage();

		batch = new SpriteBatch();
		backgroundTexture = Utility.setupTexture("data/graphics/brushed-metal.png");
		background = new Sprite(backgroundTexture);
		background.setSize(w, h);
	}
	
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		tweenManager.update(delta);
        stage.act(delta);

        batch.begin();
        background.draw(batch);
        batch.end();
        
        stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.setViewport(width, height, true);
		background.setSize(width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
		backgroundTexture.dispose();
		batch.dispose();
	}

	@Override
	public boolean keyDown(final int keycode) {
	    if (keycode == Keys.BACK || keycode == Keys.ESCAPE) {
	    	game.helpScreenComplete();
	    }
        
	    return false;
	}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public boolean keyUp(final int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(final char character) {
		return false;
	}

	@Override
	public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
		return false;
	}

	@Override
	public boolean touchDragged(final int screenX, final int screenY,final int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(final int screenX, final int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(final int amount) {
		return false;
	}

}
