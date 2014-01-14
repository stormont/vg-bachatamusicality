package com.voyagegames.bachatamusicality;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

public class Utility {

	public static Texture setupTexture(final String resource) {
		final Texture t = new Texture(Gdx.files.internal(resource));
		t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return t;
	}

}
