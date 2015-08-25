/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ael.celloscope.aelcelloscopeocr.mediaeffects;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;

import com.ael.celloscope.aelcelloscopeocr.R;

public class MediaEffectsFragment extends Fragment implements
		GLSurfaceView.Renderer {

	private static final String STATE_CURRENT_EFFECT = "current_effect";

	private GLSurfaceView mEffectView;
	private int[] mTextures = new int[2];
	private EffectContext mEffectContext;
	private Effect mEffect;
	private TextureRenderer mTexRenderer = new TextureRenderer();
	private int mImageWidth;
	private int mImageHeight;
	private boolean mInitialized = false;
	private int mCurrentEffect = R.id.radio_reset;
	String sourcePath;
	VerticalSeekBar brightnessSeekbar, contrastSeekbar;

	public void adjustBrightnessContrast(View view) {
		switch (view.getId()) {
		case R.id.increaseBrightness:
			brightnessSeekbar.setProgress(brightnessSeekbar.getProgress() + 1);
			brightnessSeekbar.move();
			break;
		case R.id.decreaseBrightness:
			brightnessSeekbar.setProgress(brightnessSeekbar.getProgress() - 1);
			brightnessSeekbar.move();
			break;
		case R.id.increaseContrast:
			contrastSeekbar.setProgress(contrastSeekbar.getProgress() + 1);
			contrastSeekbar.move();
			break;
		case R.id.decreaseContrast:
			contrastSeekbar.setProgress(contrastSeekbar.getProgress() - 1);
			contrastSeekbar.move();
			break;

		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sourcePath = getArguments().getString("img_path");
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_media_effect, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mEffectView = (GLSurfaceView) view.findViewById(R.id.effectsview);
		mEffectView.setEGLContextClientVersion(2);
		mEffectView.setRenderer(this);
		mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		brightnessSeekbar = ((VerticalSeekBar) view
				.findViewById(R.id.brightnessSeekbar));
		contrastSeekbar = ((VerticalSeekBar) view
				.findViewById(R.id.contrastSeekbar));

		RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
		final SeekBar rotateSeek = (SeekBar) view
				.findViewById(R.id.rotateSeekbar);
		final View brightnessLayout = view
				.findViewById(R.id.brightnessLinearLayout);
		final View contrastLayout = view
				.findViewById(R.id.contrastLinearLayout);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {

				case R.id.radio_rotate:
					brightnessLayout.setVisibility(View.GONE);
					contrastLayout.setVisibility(View.GONE);
					rotateSeek.setVisibility(View.VISIBLE);
					EffectFactory effectFactory = mEffectContext.getFactory();
					if (mEffect != null) {
						mEffect.release();
					}
					mEffect = effectFactory
							.createEffect(EffectFactory.EFFECT_ROTATE);
					mEffect.setParameter("angle", 180);
					applyEffect();
					renderResult();
					break;
				/*case R.id.radio_crop:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					break;*/
				case R.id.radio_bright_cont:
					rotateSeek.setVisibility(View.GONE);
					brightnessLayout.setVisibility(View.VISIBLE);
					contrastLayout.setVisibility(View.VISIBLE);
					break;
				case R.id.radio_undo:
					rotateSeek.setVisibility(View.GONE);
					brightnessLayout.setVisibility(View.GONE);
					contrastLayout.setVisibility(View.GONE);
					break;
				case R.id.radio_redo:
					rotateSeek.setVisibility(View.GONE);
					brightnessLayout.setVisibility(View.GONE);
					contrastLayout.setVisibility(View.GONE);
					break;
				case R.id.radio_reset:
					rotateSeek.setVisibility(View.GONE);
					brightnessLayout.setVisibility(View.GONE);
					contrastLayout.setVisibility(View.GONE);
					break;
				}
			}
		});
		setCurrentEffect(R.id.radio_reset);
		mEffectView.requestRender();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
		// Nothing to do here
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (mTexRenderer != null) {
			mTexRenderer.updateViewSize(width, height);
		}
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (!mInitialized) {
			// Only need to do this once
			mEffectContext = EffectContext.createWithCurrentGlContext();
			mTexRenderer.init();
			loadTextures();
			mInitialized = true;
			Log.v("onDraw1", "=" + mInitialized);
		}
		if (mCurrentEffect != R.id.radio_reset) {
			// if an effect is chosen initialize it and apply it to the texture
			Log.v("onDraw2", "=" + mInitialized);
			mEffectContext = EffectContext.createWithCurrentGlContext();
			mTexRenderer.init();
			loadTextures();
			initEffect();
			applyEffect();
		}
		renderResult();
	}

	private void setCurrentEffect(int effect) {
		mCurrentEffect = effect;
	}

	private void loadTextures() {
		// Generate textures
		GLES20.glGenTextures(2, mTextures, 0);

		// Load input bitmap
		Bitmap bitmap = BitmapFactory.decodeFile(new File(getActivity()
				.getCacheDir(), "cropped").getAbsolutePath());
		mImageWidth = bitmap.getWidth();
		mImageHeight = bitmap.getHeight();
		mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

		// Upload to texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		// Set texture parameters
		GLToolbox.initTexParams();
	}

	private void initEffect() {
		EffectFactory effectFactory = mEffectContext.getFactory();
		if (mEffect != null) {
			mEffect.release();
		}
		// Initialize the correct effect based on the selected menu/action item
		switch (mCurrentEffect) {
		case R.id.radio_rotate:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
			mEffect.setParameter("angle", 180);
			break;
		case R.id.radio_reset:
			break;

		case R.id.radio_bright_cont:
			mEffect = effectFactory
					.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
			mEffect.setParameter("brightness", 2.0f);
			break;

		/*case R.id.contrast:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
			mEffect.setParameter("contrast", 1.4f);
			break;*/
		default:
			mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
			mEffect.setParameter("angle", 0);
			break;
		}
	}

	private void applyEffect() {
		mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
	}

	private void renderResult() {
		if (mCurrentEffect != R.id.radio_reset) {
			// if no effect is chosen, just render the original bitmap
			mTexRenderer.renderTexture(mTextures[1]);
		} else {
			// render the result of applyEffect()
			mTexRenderer.renderTexture(mTextures[0]);
		}
	}

}
