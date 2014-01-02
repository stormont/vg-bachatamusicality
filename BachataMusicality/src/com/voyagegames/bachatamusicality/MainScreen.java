package com.voyagegames.bachatamusicality;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MainScreen implements Screen {

	private static final int NUMBERS_OFFSET = 6;
	
	private enum InputListenerType {
		INSTRUMENT,
		NUMBER_DISPLAY,
		NUMBER_VOCAL,
	}
	
	private final TweenManager tweenManager = new TweenManager();
	private final Stage stage;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final Texture backgroundTexture;
	private final Sprite background;
	private final Texture[] textures = new Texture[10];
	private final Image[] countActors = new Image[4];
	private final Music[] music = new Music[16];
	private final Sound[] sounds = new Sound[4];

	private int activeMusic;
	private float textureScale;
	private float totalTime;
	private boolean showNumbers;
	private boolean speakNumbers;
	private int lastCount;
	
	BitmapFont font;
	
	public MainScreen() {
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(Image.class, new ImageAccessor());
		
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1f, h / w);
		batch = new SpriteBatch();
		backgroundTexture = setupTexture("data/graphics/brushed-metal.png");
		background = new Sprite(backgroundTexture);
		background.setSize(w, h);
		
		textures[0] = setupTexture("data/graphics/guitar.png");
		textures[1] = setupTexture("data/graphics/bass.png");
		textures[2] = setupTexture("data/graphics/guira.png");
		textures[3] = setupTexture("data/graphics/bongos.png");
		textures[4] = setupTexture("data/graphics/numbers.png");
		textures[5] = setupTexture("data/graphics/voice.png");
		textures[6] = setupTexture("data/graphics/one.png");
		textures[7] = setupTexture("data/graphics/two.png");
		textures[8] = setupTexture("data/graphics/three.png");
		textures[9] = setupTexture("data/graphics/four.png");
		textureScale = w / textures[0].getWidth() * 0.5f;

		final TextureRegion[] regions = new TextureRegion[NUMBERS_OFFSET];
		
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

		sounds[0] = Gdx.audio.newSound(Gdx.files.internal("data/audio/1.wav"));
		sounds[1] = Gdx.audio.newSound(Gdx.files.internal("data/audio/2.wav"));
		sounds[2] = Gdx.audio.newSound(Gdx.files.internal("data/audio/3.wav"));
		sounds[3] = Gdx.audio.newSound(Gdx.files.internal("data/audio/4.wav"));

		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        final float xInc = stage.getWidth() * 0.2f;
        final float yInc = stage.getHeight() * 0.2f;
		
		setupActor(regions[0], xInc * 1.5f, yInc * 3f, 0x01, 0f, InputListenerType.INSTRUMENT);
		setupActor(regions[1], xInc * 3.5f, yInc * 3f, 0x02, 0.1f, InputListenerType.INSTRUMENT);
		setupActor(regions[2], xInc * 1.5f, yInc * 2f, 0x04, 0.2f, InputListenerType.INSTRUMENT);
		setupActor(regions[3], xInc * 3.5f, yInc * 2f, 0x08, 0.3f, InputListenerType.INSTRUMENT);
		setupActor(regions[4], xInc * 1.5f, yInc * 1f, 0, 0.4f, InputListenerType.NUMBER_DISPLAY);
		setupActor(regions[5], xInc * 3.5f, yInc * 1f, 0, 0.5f, InputListenerType.NUMBER_VOCAL);
		
		font = new BitmapFont();

		setupNumberActor(w, h);
		showNumbers = true;
		speakNumbers = false;
	}
	
	@Override
	public void render(final float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		tweenManager.update(delta);
        stage.act(delta);
        updateCount(delta);

        batch.begin();
        background.draw(batch);
        batch.end();
        
        stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.setViewport(width, height, true);
		background.setSize(width, height);
		textureScale = width / textures[0].getWidth() * 0.5f;
		setupNumberActor(width, height);
	}
	
	@Override
	public void show() {
		playMusic();
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
		backgroundTexture.dispose();
		batch.dispose();

		for (final Texture t : textures) {
			if (t == null) continue;
			t.dispose();
		}
		
		for (final Music m : music) {
			if (m == null) continue;
			m.dispose();
		}
		
		for (final Sound s : sounds) {
			if (s == null) continue;
			s.dispose();
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
			final float delay,
			final InputListenerType listenerType) {
		final Image img = new Image(region);
		final float x = img.getWidth();
		final float y = img.getHeight();
		
		img.setTouchable(Touchable.enabled);
		img.setOrigin(x * 0.5f, y * 0.5f);
		img.setPosition(xOffset - x * 0.5f, yOffset - y * 0.5f);
		img.setScale(0f);
		
		switch (listenerType) {
		case INSTRUMENT:
			img.addListener(new ImageInputListener(img, mask));
			break;
		case NUMBER_DISPLAY:
			img.addListener(new NumbersInputListener(img, false));
			break;
		case NUMBER_VOCAL:
			img.addListener(new NumbersInputListener(img, true));
			break;
		}
		
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

	private void setupNumberActor(final float width, final float height) {
		for (int i = 0; i < 4; i++) {
			final TextureRegion region = new TextureRegion(textures[NUMBERS_OFFSET + i], 0, 0, 128, 128);
			countActors[i] = setupNumberActor(region, width, height);
		}
	}

	private Image setupNumberActor(final TextureRegion region, final float width, final float height) {
		final Image img = new Image(region);
		final float x = img.getWidth();
		final float y = img.getHeight();
		
		img.setTouchable(Touchable.disabled);
		img.setOrigin(x * 0.5f, y * 0.5f);
		img.setPosition(width * 0.5f - x * 0.5f, height * 0.8f - y * 0.5f);
		img.setVisible(false);
		img.setScale(0f);
		setupImageTween(img, 0f);
		stage.addActor(img);
		return img;
	}
	
	private void playMusic() {
		music[activeMusic].play();
		totalTime = 100f;
		lastCount = 5;
		hideNumbers();
	}

	private void updateCount(final float delta) {
		if (activeMusic < 1 || activeMusic > 15) return;
        totalTime += delta;
        if (totalTime > music[activeMusic].getPosition()) totalTime = 0f;
        final int count = (int)Math.floor((totalTime % 2f) * 2f);
        
        if (lastCount != count) {
			if (speakNumbers) sounds[count].play();
        	if (showNumbers) {
            	if (lastCount < 4) countActors[lastCount].setVisible(false);
            	final Image img = countActors[count];
            	img.setVisible(true);
        		img.setScale(0f);
    			Tween.to(img, ImageAccessor.SCALE_XY, 0.2f)
    				.target(0.5f * textureScale, 0.5f * textureScale)
    				.ease(Elastic.OUT)
    				.start(tweenManager);
        	}
        }
        
        lastCount = count;
	}
	
	private void hideNumbers() {
		for (final Image img : countActors) img.setVisible(false);
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
				playMusic();
			} else {
				hideNumbers();
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

	class NumbersInputListener extends InputListener {
		
		private final Image image;
		private final boolean vocals;
		
		public NumbersInputListener(final Image image, final boolean vocals) {
			this.image = image;
			this.vocals = vocals;
			
			if (vocals) {
				Tween.to(image, ImageAccessor.TINT, 0.25f)
					.target(0.5f, 0.5f, 0.5f)
					.ease(Cubic.INOUT)
					.start(tweenManager);
			}
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			Tween.to(image, ImageAccessor.SCALE_XY, 0.25f)
				.target(1f * textureScale, 1f * textureScale)
				.ease(Elastic.OUT)
				.start(tweenManager);

			if (vocals) {
				if (speakNumbers) {
					Tween.to(image, ImageAccessor.TINT, 0.25f)
						.target(0.5f, 0.5f, 0.5f)
						.ease(Cubic.INOUT)
						.start(tweenManager);
				} else {
					Tween.to(image, ImageAccessor.TINT, 0.25f)
						.target(1f, 1f, 1f)
						.ease(Cubic.INOUT)
						.start(tweenManager);
				}

				speakNumbers = !speakNumbers;
			} else {
				if (showNumbers) {
					Tween.to(image, ImageAccessor.TINT, 0.25f)
						.target(0.5f, 0.5f, 0.5f)
						.ease(Cubic.INOUT)
						.start(tweenManager);
					
					hideNumbers();
				} else {
					Tween.to(image, ImageAccessor.TINT, 0.25f)
						.target(1f, 1f, 1f)
						.ease(Cubic.INOUT)
						.start(tweenManager);
					countActors[lastCount].setVisible(true);
				}

				showNumbers = !showNumbers;
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
