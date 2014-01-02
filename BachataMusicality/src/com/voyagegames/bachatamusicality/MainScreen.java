package com.voyagegames.bachatamusicality;

import java.util.ArrayList;
import java.util.List;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class MainScreen implements Screen {

	private static final int NUMBERS_OFFSET = 6;
	private static final int KEY_OFFSET = SoundType.UNKNOWN.ordinal() - SoundType.BONGO.ordinal();
	private static final float MUSIC_LENGTH = 15.97f * 0.5f;
	
	private enum InputListenerType {
		INSTRUMENT,
		NUMBER,
		VOCAL,
	}
	
	private enum SoundType {
		ONE,
		TWO,
		THREE,
		FOUR,
		BONGO,
		GUIRA,
		BASS,
		GUITAR,
		UNKNOWN,
	}
	
	private final TweenManager tweenManager = new TweenManager();
	private final Stage stage;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final Texture backgroundTexture;
	private final Sprite background;
	private final Texture[] textures = new Texture[10];
	private final Image[] countActors = new Image[4];
	private final List<SoundType> mutedSounds = new ArrayList<SoundType>();
	private final List<SoundType> unmutedSounds = new ArrayList<SoundType>();
	private final Sound[] sounds = new Sound[12];
	private final Long[] soundIds = new Long[sounds.length];
	
	private float textureScale;
	private float totalTime;
	private boolean soundsReady;
	private boolean firstKey;
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
		
		unmutedSounds.add(SoundType.BONGO);
		unmutedSounds.add(SoundType.BASS);
		unmutedSounds.add(SoundType.GUITAR);
		unmutedSounds.add(SoundType.GUIRA);
		
		stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        
        final float xInc = stage.getWidth() * 0.2f;
        final float yInc = stage.getHeight() * 0.2f;
		
		setupActor(regions[0], xInc * 1.5f, yInc * 3f, SoundType.GUITAR, 0f, InputListenerType.INSTRUMENT);
		setupActor(regions[1], xInc * 3.5f, yInc * 3f, SoundType.BASS, 0.1f, InputListenerType.INSTRUMENT);
		setupActor(regions[2], xInc * 1.5f, yInc * 2f, SoundType.GUIRA, 0.2f, InputListenerType.INSTRUMENT);
		setupActor(regions[3], xInc * 3.5f, yInc * 2f, SoundType.BONGO, 0.3f, InputListenerType.INSTRUMENT);
		setupActor(regions[4], xInc * 1.5f, yInc * 1f, SoundType.UNKNOWN, 0.4f, InputListenerType.NUMBER);
		setupActor(regions[5], xInc * 3.5f, yInc * 1f, SoundType.UNKNOWN, 0.5f, InputListenerType.VOCAL);
		
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
	public void resume() {
		playMusic();
	}

	@Override
	public void hide() {
		for (final Sound s : sounds) s.stop();
		hideNumbers();
	}

	@Override
	public void pause() {
		for (final Sound s : sounds) s.stop();
		hideNumbers();
	}

	@Override
	public void dispose() {
		stage.dispose();
		backgroundTexture.dispose();
		batch.dispose();

		for (final Texture t : textures) {
			if (t == null) continue;
			t.dispose();
		}
		
		for (final Sound s : sounds) {
			if (s == null) continue;
			s.dispose();
		}
	}

	private void playMusic() {
    	for (int i = SoundType.BONGO.ordinal(); i < SoundType.UNKNOWN.ordinal(); i++) {
			sounds[i].stop();
			sounds[i + KEY_OFFSET].stop();
		}
    	
		long id = sounds[sounds.length - 1].loop(0f);
		
		if (id == -1) {
			Gdx.app.log("MainScreen", "Sounds still loading");
			Timer.schedule(new Task() {
				
			    @Override
			    public void run() {
					playMusic();
			    }
			    
			}, 1f);
			return;
		}
		
		soundIds[sounds.length - 1] = id;
		soundIds[sounds.length - 1 - KEY_OFFSET] = sounds[sounds.length - 1 - KEY_OFFSET].loop(0f);
		
		for (int i = SoundType.BONGO.ordinal(); i < SoundType.UNKNOWN.ordinal() - 1; i++) {
			soundIds[i] = sounds[i].loop(0f);
			soundIds[i + KEY_OFFSET] = sounds[i + KEY_OFFSET].loop(0f);
		}
		
		soundsReady = true;
		totalTime = 0f;
		lastCount = -1;
		hideNumbers();
		switchKey();
	}
	
	private void switchKey() {
		firstKey = !firstKey;
		
		for (int i = SoundType.BONGO.ordinal(); i < SoundType.UNKNOWN.ordinal(); i++) {
			if (firstKey) {
				sounds[i].setVolume(soundIds[i], 1f);
				sounds[i + KEY_OFFSET].setVolume(soundIds[i + KEY_OFFSET], 0f);
			} else {
				sounds[i].setVolume(soundIds[i], 0f);
				sounds[i + KEY_OFFSET].setVolume(soundIds[i + KEY_OFFSET], 1f);
			}
		}
		
		for (final SoundType st : mutedSounds) {
			final int ord = st.ordinal();
			sounds[ord].setVolume(soundIds[ord], 0f);
			sounds[ord + KEY_OFFSET].setVolume(soundIds[ord + KEY_OFFSET], 0f);
		}
	}
	
	private Image ghostImage;
	
	private void updateCount(final float delta) {
		if (!soundsReady) return;
        totalTime += delta;
        
        if (totalTime >= MUSIC_LENGTH) {
        	totalTime -= MUSIC_LENGTH;
    		switchKey();
        }
        
        final int count = (int)Math.floor((totalTime % 2f) * 2f);
        
        if (lastCount != count) {
        	hideNumbers();
			if (speakNumbers) sounds[count].play();
        	if (showNumbers && unmutedSounds.size() > 0) {
            	final Image img = countActors[count];
            	img.setVisible(true);
        		img.setScale(0f);
    			Tween.to(img, ImageAccessor.SCALE_XY, 0.1f)
    				.target(0.5f * textureScale, 0.5f * textureScale)
    				.ease(Elastic.OUT)
    				.start(tweenManager);
    			
    			if (lastCount == -1) {
    				ghostImage = img;
    			} else {
    				if (ghostImage != null) {
    					ghostImage.setVisible(false);
    					ghostImage = null;
    				}
    			}
        	}
        }
        
        lastCount = count;
	}
	
	private void hideNumbers() {
		for (final Image img : countActors) img.setVisible(false);
	}

	private Texture setupTexture(final String resource) {
		final Texture t = new Texture(Gdx.files.internal(resource));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return t;
	}

	private void setupActor(
			final TextureRegion region,
			final float xOffset,
			final float yOffset,
			final SoundType soundType,
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
			img.addListener(new ImageInputListener(img, soundType));
			break;
		case NUMBER:
			img.addListener(new NumbersInputListener(img, false));
			break;
		case VOCAL:
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
		img.setPosition(width * 0.5f - x * 0.5f, height * 0.85f - y * 0.5f);
		img.setVisible(false);
		img.setScale(0f);
		setupImageTween(img, 0f);
		stage.addActor(img);
		return img;
	}
	
	class ImageInputListener extends InputListener {
		
		private final Image image;
		private final SoundType soundType;
		
		public ImageInputListener(final Image image, final SoundType soundType) {
			this.image = image;
			this.soundType = soundType;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			Tween.to(image, ImageAccessor.SCALE_XY, 0.25f)
				.target(1f * textureScale, 1f * textureScale)
				.ease(Elastic.OUT)
				.start(tweenManager);

			if (unmutedSounds.contains(soundType)) {
				unmutedSounds.remove(soundType);
				mutedSounds.add(soundType);
				
				final int ord = soundType.ordinal();
				sounds[ord].setVolume(soundIds[ord], 0f);
				sounds[ord + KEY_OFFSET].setVolume(soundIds[ord + KEY_OFFSET], 0f);
						
				Tween.to(image, ImageAccessor.TINT, 0.25f)
					.target(0.5f, 0.5f, 0.5f)
					.ease(Cubic.INOUT)
					.start(tweenManager);
			} else {
				mutedSounds.remove(soundType);
				unmutedSounds.add(soundType);
				
				final int ord = soundType.ordinal();
				
				if (firstKey) {
					sounds[ord].setVolume(soundIds[ord], 1f);
				} else {
					sounds[ord + KEY_OFFSET].setVolume(soundIds[ord + KEY_OFFSET], 1f);
				}
						
				Tween.to(image, ImageAccessor.TINT, 0.25f)
					.target(1f, 1f, 1f)
					.ease(Cubic.INOUT)
					.start(tweenManager);
			}
			
			if (unmutedSounds.size() == 0) hideNumbers();
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
