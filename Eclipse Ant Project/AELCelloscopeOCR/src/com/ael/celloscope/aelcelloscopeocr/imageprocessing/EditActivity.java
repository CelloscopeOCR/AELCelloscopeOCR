package com.ael.celloscope.aelcelloscopeocr.imageprocessing;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.SurfaceViewRenderer;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.VerticalSeekBar;

public class EditActivity extends Activity {
	private SeekBar rotateSeek;
	private SurfaceViewRenderer mSurfaceViewRenderer;

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
		GLSurfaceView mGLSurfaceView = (GLSurfaceView) this
				.findViewById(R.id.effectsview);
		mSurfaceViewRenderer = new SurfaceViewRenderer(this, mGLSurfaceView);
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
					mSurfaceViewRenderer.ApplyEffect(R.id.rotate,
							rotateSeek.getProgress());
					break;
				case R.id.radio_brightness:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					mSurfaceViewRenderer.ApplyEffect(R.id.brightness);
					break;
				case R.id.radio_contrast:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.VISIBLE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.VISIBLE);
					mSurfaceViewRenderer.ApplyEffect(R.id.contrast);
					break;
				case R.id.radio_grayscale:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					mSurfaceViewRenderer.ApplyEffect(R.id.grayscale);
					break;
				case R.id.radio_bw:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					mSurfaceViewRenderer.ApplyEffect(R.id.bw);
					break;
				case R.id.radio_reset:
					rotateSeek.setVisibility(View.GONE);
					findViewById(R.id.brightnessLinearLayout).setVisibility(
							View.GONE);
					findViewById(R.id.contrastLinearLayout).setVisibility(
							View.GONE);
					mSurfaceViewRenderer.ApplyEffect(R.id.none);
					break;
				}
			}
		});
	}

	private void initViews(Bundle savedInstanceState) {

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
					mSurfaceViewRenderer.ApplyEffect(R.id.rotate,
							rotateSeek.getProgress());
				}

			}
		});
	}

}
