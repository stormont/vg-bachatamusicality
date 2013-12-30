package com.voyagegames.bachatamusicality;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MainScreen implements Screen {

	private final TweenManager tweenManager = new TweenManager();

	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture texture;
	private Sprite bass;
	private Sprite bongos;
	private Sprite guira;
	private Sprite secondGuitar;
	private Sprite firstGuitar;
	private Sprite voice;
	
	public MainScreen() {
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1f, h / w);
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/greendot.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		final TextureRegion region = new TextureRegion(texture, 0, 0, 128, 128);
		
		bass = setupSprite(region, -1f, 0f);
		bongos = setupSprite(region, 1f, 0f);
		guira = setupSprite(region, -1f, 2f);
		secondGuitar = setupSprite(region, 1f, 2f);
		firstGuitar = setupSprite(region, -1f, 4f);
		voice = setupSprite(region, 1f, 4f);
		
		setupSpriteTween(bass, 0f);
		setupSpriteTween(bongos, 0.1f);
		setupSpriteTween(guira, 0.2f);
		setupSpriteTween(secondGuitar, 0.3f);
		setupSpriteTween(firstGuitar, 0.4f);
		setupSpriteTween(voice, 0.5f);
	}
	
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		tweenManager.update(delta);
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		bass.draw(batch);
		bongos.draw(batch);
		guira.draw(batch);
		secondGuitar.draw(batch);
		firstGuitar.draw(batch);
		voice.draw(batch);
		batch.end();
	}

	@Override
	public void resize(final int width, final int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

	private Sprite setupSprite(final TextureRegion region, final float xOffset, final float yOffset) {
		final Sprite s = new Sprite(region);
		
		final float scaledWidth = 0.15f;
		final float scaledHeight = 0.15f * s.getHeight() / s.getWidth();
		
		s.setSize(scaledWidth, scaledHeight);
		s.setOrigin(scaledWidth * 0.5f, scaledHeight * 0.5f);
		s.setPosition(xOffset * scaledWidth * (2f - xOffset * 0.5f), -scaledHeight * 0.5f * (yOffset * 2f));
		s.setScale(0f);
		
		return s;
	}
	
	private void setupSpriteTween(final Sprite s, final float delay) {
		Tween.to(s, SpriteAccessor.SCALE_XY, 1.5f)
			.target(1f, 1f)
			.ease(Elastic.OUT)
			.delay(delay)
			.start(tweenManager);
	}

}
