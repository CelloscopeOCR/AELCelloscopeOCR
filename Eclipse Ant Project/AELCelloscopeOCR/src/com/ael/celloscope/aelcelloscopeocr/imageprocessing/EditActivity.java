package com.ael.celloscope.aelcelloscopeocr.imageprocessing;



import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.GLToolbox;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.TextureRenderer;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.VerticalSeekBar;

public class EditActivity extends Activity implements GLSurfaceView.Renderer {
	private SeekBar rotateSeek;

	private static final String STATE_CURRENT_EFFECT = "current_effect";

	private GLSurfaceView mEffectView;
	private int[] mTextures = new int[2];
	private EffectContext mEffectContext;
	private Effect mEffect;
	private TextureRenderer mTexRenderer = new TextureRenderer();
	private int mImageWidth;
	private int mImageHeight;
	private boolean mInitialized = false;
	private int mCurrentEffect;

	public void adjustBrightnessContrast(View view) {
		VerticalSeekBar brightnessSeekbar = ((VerticalSeekBar) findViewById(R.id.brightnessSeekbar));
		VerticalSeekBar contrastSeekbar = ((VerticalSeekBar) findViewById(R.id.contrastSeekbar));
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		initViews(savedInstanceState);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {

				case R.id.radio_rotate:
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					rotateSeek.setVisibility(View.VISIBLE);
					EffectFactory effectFactory = mEffectContext.getFactory();
					if (mEffect != null) {
						mEffect.release();
					}
					mEffect = effectFactory
							.createEffect(EffectFactory.EFFECT_ROTATE);
					mEffect.setParameter("angle", 180);
					Log.v("AllVals", "=" + "mTextures[0]=" + mTextures[0]
							+ " mImageWidth=" + mImageWidth + " mImageHeight="
							+ mImageHeight + " mTextures[1]=" + mTextures[1]);
					mEffect.apply(mTextures[0], mImageWidth, mImageHeight,
							mTextures[1]);
					/*
					 * applyEffect(); renderResult();
					 */

					break;
				case R.id.radio_crop:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					break;
				case R.id.radio_bright_cont:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.VISIBLE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.VISIBLE);
					break;
				case R.id.radio_undo:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					break;
				case R.id.radio_redo:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					break;
				case R.id.radio_reset:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					break;
				}
			}
		});
	}

	private void initViews(Bundle savedInstanceState) {
		mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
		mEffectView.setEGLContextClientVersion(2);
		mEffectView.setRenderer(this);
		mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		if (null != savedInstanceState
				&& savedInstanceState.containsKey(STATE_CURRENT_EFFECT)) {
			setCurrentEffect(savedInstanceState.getInt(STATE_CURRENT_EFFECT));
		} else {
			setCurrentEffect(R.id.radio_reset);
		}

		rotateSeek = (SeekBar) findViewById(R.id.rotateSeekbar);
		rotateSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					// TODO
				}

			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_CURRENT_EFFECT, mCurrentEffect);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
		// Nothing to do here
		Log.v("onSurfaceCreated", "onSurfaceCreated");
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if (mTexRenderer != null) {
			mTexRenderer.updateViewSize(width, height);
			Log.v("onSurfaceChanged1", "1onSurfaceChanged");
		}
		Log.v("onSurfaceChanged2", "2onSurfaceChanged");
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		Log.v("onDrawFrame1", "1onDrawFrame");
		if (!mInitialized) {
			// Only need to do this once
			mEffectContext = EffectContext.createWithCurrentGlContext();
			mTexRenderer.init();
			loadTextures();
			mInitialized = true;
			Log.v("onDrawFrame2", "2onDrawFrame");
		}
		if (mCurrentEffect != R.id.radio_reset) {
			// if an effect is chosen initialize it and apply it to the texture
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

		
//		Bitmap bitmap = BitmapFactory.decodeFile(new File(this.getCacheDir(),
//				"cropped").getAbsolutePath());
		
		Bitmap bitmap = BitmapFactory.decodeFile(new File(Environment.getExternalStorageDirectory(),
				"ael_image.jpg").getAbsolutePath());
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

		/*
		 * case R.id.none: break;
		 * 
		 * case R.id.autofix: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX);
		 * mEffect.setParameter("scale", 0.5f); break;
		 * 
		 * case R.id.bw: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_BLACKWHITE);
		 * mEffect.setParameter("black", .1f); mEffect.setParameter("white",
		 * .7f); break;
		 * 
		 * case R.id.brightness: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_BRIGHTNESS);
		 * mEffect.setParameter("brightness", 2.0f); break;
		 * 
		 * case R.id.contrast: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
		 * mEffect.setParameter("contrast", 1.4f); break;
		 * 
		 * case R.id.crossprocess: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_CROSSPROCESS); break;
		 * 
		 * case R.id.documentary: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_DOCUMENTARY); break;
		 * 
		 * case R.id.duotone: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_DUOTONE);
		 * mEffect.setParameter("first_color", Color.YELLOW);
		 * mEffect.setParameter("second_color", Color.DKGRAY); break;
		 * 
		 * case R.id.filllight: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_FILLLIGHT);
		 * mEffect.setParameter("strength", .8f); break;
		 * 
		 * case R.id.fisheye: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_FISHEYE);
		 * mEffect.setParameter("scale", .5f); break;
		 * 
		 * case R.id.flipvert: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
		 * mEffect.setParameter("vertical", true); break;
		 * 
		 * case R.id.fliphor: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
		 * mEffect.setParameter("horizontal", true); break;
		 * 
		 * case R.id.grain: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_GRAIN);
		 * mEffect.setParameter("strength", 1.0f); break;
		 * 
		 * case R.id.grayscale: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_GRAYSCALE); break;
		 * 
		 * case R.id.lomoish: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH); break;
		 * 
		 * case R.id.negative: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE); break;
		 * 
		 * case R.id.posterize: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_POSTERIZE); break;
		 * 
		 * case R.id.rotate: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
		 * mEffect.setParameter("angle", 180); break;
		 * 
		 * case R.id.saturate: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
		 * mEffect.setParameter("scale", .5f); break;
		 * 
		 * case R.id.sepia: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_SEPIA); break;
		 * 
		 * case R.id.sharpen: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN); break;
		 * 
		 * case R.id.temperature: mEffect = effectFactory
		 * .createEffect(EffectFactory.EFFECT_TEMPERATURE);
		 * mEffect.setParameter("scale", .9f); break;
		 * 
		 * case R.id.tint: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_TINT);
		 * mEffect.setParameter("tint", Color.MAGENTA); break;
		 * 
		 * case R.id.vignette: mEffect =
		 * effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE);
		 * mEffect.setParameter("scale", .5f); break;
		 */

		default:
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
