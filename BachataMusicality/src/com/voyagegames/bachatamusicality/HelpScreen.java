package com.voyagegames.bachatamusicality;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class HelpScreen implements Screen {

	enum HelpIndex {
		GUITAR,
		BASS,
		GUIRA,
		BONGO
	}
	
	private final TweenManager tweenManager = new TweenManager();
	private final Stage stage;
	private final OrthographicCamera camera;
	private final SpriteBatch batch;
	private final Texture backgroundTexture;
	private final Sprite background;
	private final Texture[] textures = new Texture[4];
	private final Image[] actors = new Image[textures.length];
	private final BitmapFont font;

	private float textureScale;
	private HelpIndex helpIndex;
	
	public HelpScreen(final MainGdxGame game) {
		Tween.registerAccessor(Image.class, new ImageAccessor());
		
		final float w = Gdx.graphics.getWidth();
		final float h = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1f, h / w);
		stage = new Stage() {
			
	        @Override
	        public boolean keyDown(final int keyCode) {
	            if (keyCode == Keys.BACK || keyCode == Keys.ESCAPE) {
	            	game.helpScreenComplete();
	            }
	            
	            return super.keyDown(keyCode);
	        }
	        
	    };
	    
		Gdx.input.setInputProcessor(stage);

		batch = new SpriteBatch();
		backgroundTexture = Utility.setupTexture("data/graphics/brushed-metal.png");
		background = new Sprite(backgroundTexture);
		background.setSize(w, h);
		font = new BitmapFont(Gdx.files.internal("data/fonts/calibri-bold.fnt"));

		textures[0] = Utility.setupTexture("data/graphics/guitar.png");
		textures[1] = Utility.setupTexture("data/graphics/bass.png");
		textures[2] = Utility.setupTexture("data/graphics/guira.png");
		textures[3] = Utility.setupTexture("data/graphics/bongos.png");
		textureScale = w / textures[0].getWidth() * 0.5f;
		setFontScale();

		final TextureRegion[] regions = new TextureRegion[textures.length];
		
		for (int i = 0; i < regions.length; i++) {
			regions[i] = new TextureRegion(textures[i], 0, 0, 128, 128);
		}
        
        final float xInc = stage.getWidth() * 0.2f;
        final float yInc = stage.getHeight() * 0.2f;
		
		actors[0] = setupActor(regions[0], xInc * 0.65f, yInc * 4f, 0f);
		actors[1] = setupActor(regions[1], xInc * 1.9f, yInc * 4f, 0.1f);
		actors[2] = setupActor(regions[2], xInc * 3.15f, yInc * 4f, 0.2f);
		actors[3] = setupActor(regions[3], xInc * 4.4f, yInc * 4f, 0.3f);
		selectImage(HelpIndex.BASS);
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
        renderText();
        batch.end();
        
        stage.draw();
	}

	@Override
	public void resize(final int width, final int height) {
		stage.setViewport(width, height, true);
		background.setSize(width, height);
		textureScale = width / textures[0].getWidth() * 0.5f;
		setFontScale();
	}

	@Override
	public void dispose() {
		stage.dispose();
		backgroundTexture.dispose();
		batch.dispose();
		font.dispose();

		for (final Texture t : textures) {
			if (t == null) continue;
			t.dispose();
		}
	}

	@Override
	public void show() {}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	private Image setupActor(
			final TextureRegion region,
			final float xOffset,
			final float yOffset,
			final float delay) {
		final Image img = new Image(region);
		final float x = img.getWidth();
		final float y = img.getHeight();
		
		img.setTouchable(Touchable.enabled);
		img.setOrigin(x * 0.5f, y * 0.5f);
		img.setPosition(xOffset - x * 0.5f, yOffset - y * 0.5f);
		img.setScale(0f);
		img.addListener(new ButtonInputListener(img));
		
		setupImageTween(img, delay);
		stage.addActor(img);
		return img;
	}

	private void setupImageTween(final Image img, final float delay) {
		Tween.to(img, ImageAccessor.SCALE_XY, 1f)
			.target(0.5f * textureScale, 0.5f * textureScale)
			.ease(Elastic.OUT)
			.delay(delay)
			.start(tweenManager);
	}
	
	private void selectImage(final HelpIndex index) {
		final Image image = actors[index.ordinal()];
		
		for (final Image img : actors) {
			if (img == image) continue;
			Tween.to(img, ImageAccessor.TINT, 0.25f)
				.target(0.5f, 0.5f, 0.5f)
				.ease(Cubic.INOUT)
				.start(tweenManager);
		}
		
		helpIndex = index;
		Tween.to(image, ImageAccessor.TINT, 0.25f)
			.target(1f, 1f, 1f)
			.ease(Cubic.INOUT)
			.start(tweenManager);
	}
	
	private void setFontScale() {
		font.setScale(textureScale * 0.5f);
	}
	
	private void renderText() {
		final Image actor = actors[helpIndex.ordinal()];
		final float margin = stage.getWidth() * 0.1f;
		final float hm = margin * 0.5f;
		final float w = stage.getWidth() - margin;
		final float sh = stage.getHeight();
		final float h = sh - (actor.getHeight() * actor.getScaleY() * 2.5f);

		switch (helpIndex) {
		case GUITAR:
			font.drawWrapped(batch, "The guitar often plays the melody of the song or often a harmonic line signified by a low first note, followed by three repeated high notes.", hm, h, w);
			break;
		case BASS:
			font.drawWrapped(batch, "The bass typifies bachata music with its synchopated rhythm, striking a long first note, a quick second note, and the third and fourth notes on the final beats of the measure.", hm, h, w);
			break;
		case GUIRA:
			font.drawWrapped(batch, "The güira typically plays every eighth note while the guitar is playing the melody and slows to quarter notes during the chorus.", hm, h, w);
			break;
		case BONGO:
			font.drawWrapped(batch, "The bongos typically play every eighth note and strike the low drum on the fourth beat of the measure.", hm, h, w);
			break;
		}
	}
	
	class ButtonInputListener extends InputListener {
		
		private final Image image;
		
		public ButtonInputListener(final Image image) {
			this.image = image;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			Tween.to(image, ImageAccessor.SCALE_XY, 0.25f)
				.target(1f * textureScale, 1f * textureScale)
				.ease(Elastic.OUT)
				.start(tweenManager);

			int index = 0;
			
			for (int i = 0; i < actors.length; ++i) {
				if (actors[i] != image) continue;
				index = i;
				break;
			}
			
			selectImage(HelpIndex.values()[index]);
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
