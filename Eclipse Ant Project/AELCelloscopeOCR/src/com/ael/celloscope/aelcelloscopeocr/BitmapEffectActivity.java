package com.ael.celloscope.aelcelloscopeocr;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ael.celloscope.aelcelloscopeocr.mediaeffects.BitmapEffect;
import com.ael.celloscope.aelcelloscopeocr.mediaeffects.VerticalSeekBar;

public class BitmapEffectActivity extends Activity implements
		OnSeekBarChangeListener, OnCheckedChangeListener {
	ImageView imageView;
	View rotateLayout, briConLayout;
	SeekBar rotateSeekbar;
	VerticalSeekBar brightnessSeekbar, contrastSeekbar;
	RadioGroup effectRadioGroup;

	Bitmap targetBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bitmap_effect);

		initViews();

		setControllers();

		// targetBitmap = BitmapEffect.decodeSmallBitmap(
		// new File(this.getCacheDir(), "cropped").getAbsolutePath(), 320,
		// 240);
		targetBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puppy);
		imageView.setImageBitmap(targetBitmap);
	}

	private void initViews() {
		imageView = (ImageView) findViewById(R.id.imgView);
		rotateLayout = (View) findViewById(R.id.rotateLayout);
		briConLayout = (View) findViewById(R.id.briConLayout);
		rotateSeekbar = (SeekBar) findViewById(R.id.rotateSeekbar);
		brightnessSeekbar = (VerticalSeekBar) findViewById(R.id.brightnessSeekbar);
		contrastSeekbar = (VerticalSeekBar) findViewById(R.id.contrastSeekbar);
		effectRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
	}

	private void setControllers() {
		rotateSeekbar.setOnSeekBarChangeListener(this);
		brightnessSeekbar.setOnSeekBarChangeListener(this);
		contrastSeekbar.setOnSeekBarChangeListener(this);
		effectRadioGroup.setOnCheckedChangeListener(this);
	}

	// This method is invoked from activity_bitmap_effect.xml layout
	public void adjustRotation(View v) {
		switch (v.getId()) {
		case R.id.rotateClockwise:
			rotateSeekbar.setProgress(rotateSeekbar.getProgress() + 1);
			break;
		case R.id.rotateCounterClockwise:
			rotateSeekbar.setProgress(rotateSeekbar.getProgress() - 1);
			break;
		}
	}

	int brightness = 0;
	double contrast = 0.0;

	// This method is invoked from activity_bitmap_effect.xml layout
	public void adjustBrightness(View view) {
		switch (view.getId()) {
		case R.id.increaseBrightness:
			brightness += 5;
			break;
		case R.id.decreaseBrightness:
			brightness -= 5;
			break;
		default:
			break;
		}
		tempTargetBitmap = BitmapEffect.doBrightness(targetBitmap, brightness);
		imageView.setImageBitmap(tempTargetBitmap);
	}

	public void adjustContrast(View view) {
		switch (view.getId()) {
		case R.id.increaseContrast:
			contrast += 5.0;
			break;
		case R.id.decreaseContrast:
			contrast -= 5.0;
			break;

		default:
			break;
		}
		tempTargetBitmap = BitmapEffect.doContrast(targetBitmap, contrast);
		imageView.setImageBitmap(tempTargetBitmap);
	}

	Bitmap tempTargetBitmap;
	int rotation = 0;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.rotateSeekbar:
			rotation = progress - 180;
			tempTargetBitmap = BitmapEffect.rotate(targetBitmap, rotation);
			break;
		case R.id.brightnessSeekbar:
			tempTargetBitmap = BitmapEffect.doBrightness(targetBitmap,
					brightness + progress);
			break;
		case R.id.contrastSeekbar:
			tempTargetBitmap = BitmapEffect.doContrast(targetBitmap, contrast
					+ progress);
			break;

		default:
			break;
		}

		imageView.setImageBitmap(tempTargetBitmap);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		/* targetBitmap = tempTargetBitmap; */
		switch (checkedId) {
		case R.id.radio_rotate:
			rotateLayout.setVisibility(View.VISIBLE);
			briConLayout.setVisibility(View.GONE);
			break;
		case R.id.radio_bright_cont:
			rotateLayout.setVisibility(View.GONE);
			briConLayout.setVisibility(View.VISIBLE);
			break;

		case R.id.radio_save:

			break;
		case R.id.radio_reset:
			resetEdit();
			break;
		default:
			break;
		}
	}

	private void resetEdit() {
		brightness = 0;
		contrast = 0.0;
		rotateSeekbar.setProgress(0);
		brightnessSeekbar.setProgress(0);
		contrastSeekbar.setProgress(0);
//		targetBitmap = BitmapEffect.decodeSmallBitmap(
//				new File(this.getCacheDir(), "cropped").getAbsolutePath(), 100,
//				50);
		targetBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puppy);
		imageView.setImageBitmap(targetBitmap);
		if (tempTargetBitmap != null) {
			tempTargetBitmap.recycle();
		}
	}
}
