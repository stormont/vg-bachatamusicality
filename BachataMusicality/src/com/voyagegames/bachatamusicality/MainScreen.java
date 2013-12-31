package com.voyagegames.bachatamusicality;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainScreen implements Screen {

	private final TweenManager tweenManager = new TweenManager();

	private OrthographicCamera camera;
	private int activeMusic;
	private Stage stage;
	private float textureScale;
	
	final Texture[] textures = new Texture[4];
	final Music[] music = new Music[16];
	
	public MainScreen() {
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Image.class, new ImageAccessor());
		
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1f, h / w);
		
		textures[0] = setupTexture("data/graphics/guitar.png");
		textures[1] = setupTexture("data/graphics/bass.png");
		textures[2] = setupTexture("data/graphics/guira.png");
		textures[3] = setupTexture("data/graphics/bongos.png");
		textureScale = w / textures[0].getWidth() * 0.5f;

		final TextureRegion[] regions = new TextureRegion[4];
		
		for (int i = 0; i < regions.length; i++) {
			regions[i] = new TextureRegion(textures[i], 0, 0, 128, 128);
		}
		
		music[1] = setupMusic("data/audio/guitar-mono.ogg");
		music[2] = setupMusic("data/audio/bass-mono.ogg");
		music[3] = setupMusic("data/audio/bass-guitar-mono.ogg");
		music[4] = setupMusic("data/audio/guira-mono.ogg");
		music[5] = setupMusic("data/audio/guira-guitar-mono.ogg");
		music[6] = setupMusic("data/audio/guira-bass-mono.ogg");
		music[7] = setupMusic("data/audio/guira-bass-guitar-mono.ogg");
		music[8] = setupMusic("data/audio/bongo-mono.ogg");
		music[9] = setupMusic("data/audio/bongo-guitar-mono.ogg");
		music[10] = setupMusic("data/audio/bongo-bass-mono.ogg");
		music[11] = setupMusic("data/audio/bongo-bass-guitar-mono.ogg");
		music[12] = setupMusic("data/audio/bongo-guira-mono.ogg");
		music[13] = setupMusic("data/audio/bongo-guira-guitar-mono.ogg");
		music[14] = setupMusic("data/audio/bongo-guira-bass-mono.ogg");
		music[15] = setupMusic("data/audio/bongo-guira-bass-guitar-mono.ogg");
		activeMusic = 15;

		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        final float xInc = stage.getWidth() * 0.2f;
        final float yInc = stage.getHeight() * 0.2f;
		
		setupActor(regions[0], xInc * 1.5f, yInc * 2.5f, 0x01, 0f);
		setupActor(regions[1], xInc * 3.5f, yInc * 2.5f, 0x02, 0.1f);
		setupActor(regions[2], xInc * 1.5f, yInc * 1f, 0x04, 0.2f);
		setupActor(regions[3], xInc * 3.5f, yInc * 1f, 0x08, 0.3f);
	}
	
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		tweenManager.update(delta);
        stage.act(delta);

        stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.setViewport(width, height, true);
		textureScale = width / textures[0].getWidth() * 0.5f;
	}

	@Override
	public void show() {
		music[activeMusic].play();
	}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		stage.dispose();

		for (final Texture t : textures) {
			if (t == null) continue;
			t.dispose();
		}
		
		for (final Music m : music) {
			if (m == null) continue;
			m.dispose();
		}
	}
	
	private Texture setupTexture(final String resource) {
		final Texture t = new Texture(Gdx.files.internal(resource));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return t;
	}
	
	private Music setupMusic(final String resource) {
		final Music m = Gdx.audio.newMusic(Gdx.files.internal(resource));
		m.setLooping(true);
		return m;
	}
	
	private void setupActor(
			final TextureRegion region,
			final float xOffset,
			final float yOffset,
			final int mask,
			final float delay) {
		final Image img = new Image(region);
		final float x = img.getWidth();
		final float y = img.getHeight();
		
		img.setTouchable(Touchable.enabled);
		img.setOrigin(x * 0.5f, y * 0.5f);
		img.setPosition(xOffset - x * 0.5f, yOffset - y * 0.5f);
		img.setScale(0f);
		img.addListener(new ImageInputListener(img, mask));
		
		setupImageTween(img, delay);
		stage.addActor(img);
	}

	private void setupImageTween(final Image img, final float delay) {
		Tween.to(img, ImageAccessor.SCALE_XY, 1f)
			.target(0.5f * textureScale, 0.5f * textureScale)
			.ease(Elastic.OUT)
			.delay(delay)
			.start(tweenManager);
	}
	
	class ImageInputListener extends InputListener {
		
		private final Image image;
		private final int mask;
		private final int disable;
		
		public ImageInputListener(final Image image, final int mask) {
			this.image = image;
			this.mask = mask;
			this.disable = 0x0F - mask;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			Tween.to(image, ImageAccessor.SCALE_XY, 0.25f)
				.target(1f * textureScale, 1f * textureScale)
				.ease(Elastic.OUT)
				.start(tweenManager);

			if (activeMusic > 0 && activeMusic < 16) {
				music[activeMusic].stop();
			}
			
			if ((activeMusic & mask) != 0) {
				activeMusic &= disable;

				Tween.to(image, ImageAccessor.TINT, 0.25f)
					.target(0.5f, 0.5f, 0.5f)
					.ease(Cubic.INOUT)
					.start(tweenManager);
			} else {
				activeMusic |= mask;

				Tween.to(image, ImageAccessor.TINT, 0.25f)
					.target(1f, 1f, 1f)
					.ease(Cubic.INOUT)
					.start(tweenManager);
			}
			
			if (activeMusic > 0 && activeMusic < 16) {
				music[activeMusic].play();
			}
			
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			Tween.to(image, ImageAccessor.SCALE_XY, 0.25f)
				.target(0.5f * textureScale, 0.5f * textureScale)
				.ease(Elastic.OUT)
				.start(tweenManager);

			super.touchUp(event, x, y, pointer, button);
		}
	
	}

}
