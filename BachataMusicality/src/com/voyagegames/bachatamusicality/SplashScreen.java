package com.voyagegames.bachatamusicality;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SplashScreen implements Screen {

	private final MainGdxGame game;
	private final TweenManager tweenManager = new TweenManager();
	private final Stage stage;
	private final OrthographicCamera camera;
	private final Texture logo;
	private final Texture title;

	public SplashScreen(final MainGdxGame game) {
		this.game = game;
		Tween.registerAccessor(Image.class, new ImageAccessor());
		
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1f, h / w);
		stage = new Stage();
		
		logo = new Texture(Gdx.files.internal("data/graphics/logo-basic.png"));
		logo.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		title = new Texture(Gdx.files.internal("data/graphics/title.png"));
		title.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		setupLogo(logo, w, h, 1f, 3.5f);
		setupTitle(logo, w, h, 2f, 3.5f);
	}
	
	private void setupLogo(
			final Texture texture,
			final float width,
			final float height,
			final float fadeInDelay,
			final float fadeOutDelay) {
		final Image img = new Image(new TextureRegion(texture, 0, 0, 256, 256));
		final float x = img.getWidth();
		final float y = img.getHeight();
		
		img.setTouchable(Touchable.disabled);
		img.setOrigin(x * 0.5f, y * 0.5f);
		img.setPosition(width * 0.5f - x * 0.5f, height * 0.75f - y * 0.5f);
		img.setScale(width / x * 0.5f);
		
		final Color c = img.getColor();
		c.set(c.r, c.g, c.b, 0f);
		img.setColor(c);

		Tween.to(img, ImageAccessor.OPACITY, 1f)
			.target(1f)
			.delay(fadeInDelay)
			.ease(aurelienribon.tweenengine.equations.Circ.OUT)
			.start(tweenManager);

		Tween.to(img, ImageAccessor.OPACITY, 0.5f)
			.target(0f)
			.delay(fadeOutDelay)
			.ease(aurelienribon.tweenengine.equations.Circ.OUT)
			.start(tweenManager);
		
		stage.addActor(img);
	}
	
	private void setupTitle(
			final Texture texture,
			final float width,
			final float height,
			final float fadeInDelay,
			final float fadeOutDelay) {
		final Image img = new Image(new TextureRegion(title, 0, 0, 512, 512));
		final float x = img.getWidth();
		final float y = img.getHeight();
		final float textureScale = width / x * 2f;
		
		img.setTouchable(Touchable.disabled);
		img.setPosition(0f, height * 0.1f - y * 0.5f * textureScale);
		img.setScale(textureScale);
		
		final Color c = img.getColor();
		c.set(c.r, c.g, c.b, 0f);
		img.setColor(c);

		Tween.to(img, ImageAccessor.OPACITY, 1f)
			.target(1f)
			.delay(fadeInDelay)
			.ease(aurelienribon.tweenengine.equations.Circ.OUT)
			.start(tweenManager);

		Tween.to(img, ImageAccessor.OPACITY, 0.5f)
			.target(0f)
			.delay(fadeOutDelay)
			.ease(aurelienribon.tweenengine.equations.Circ.OUT)
			.setCallback(new TweenCallback() {

				@Override
				public void onEvent(final int arg0, final BaseTween<?> arg1) {
					game.splashScreenComplete();
				}
				
			})
			.setCallbackTriggers(TweenCallback.END)
			.start(tweenManager);
		
		stage.addActor(img);
	}
	
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		tweenManager.update(delta);
        stage.act(delta);
        stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.setViewport(width, height, true);
	}

	@Override
	public void dispose() {
		stage.dispose();
		logo.dispose();
		title.dispose();
	}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

}
