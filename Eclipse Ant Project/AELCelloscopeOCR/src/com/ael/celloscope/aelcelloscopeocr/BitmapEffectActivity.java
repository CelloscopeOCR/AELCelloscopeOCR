package com.ael.celloscope.aelcelloscopeocr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ael.celloscope.aelcelloscopeocr.mediaeffects.VerticalSeekBar;
import co.celloscope.services.R;

public class BitmapEffectActivity extends Activity implements
		OnSeekBarChangeListener, OnCheckedChangeListener {
	ImageView imageView;
	View rotateLayout, briConLayout;
	SeekBar rotateSeekbar;
	VerticalSeekBar brightnessSeekbar, contrastSeekbar;
	RadioGroup effectRadioGroup;
	public BitmapEffectActivityHandler bitmapEffectActivityHandler;
	// private static final String TAG = BitmapEffectActivity.class
	// .getSimpleName();

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

	@Override
	protected void onResume() {
		super.onResume();
		bitmapEffectActivityHandler = new BitmapEffectActivityHandler(this);
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

	Bitmap tempTargetBitmap;
	private float degrees = 0f;

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		switch (seekBar.getId()) {
		case R.id.rotateSeekbar:
			degrees = progress - 180;

			this.imageView.setImageBitmap(MatrixHelper.rotate(
					this.targetBitmap, degrees));
			// bitmapEffectActivityHandler.rotate(degrees);
			break;
		case R.id.brightnessSeekbar:
			int brightness = progress;
			this.imageView.setImageBitmap(MatrixHelper.setBrightness(
					this.targetBitmap, brightness));
			// bitmapEffectActivityHandler.setBrightness(brightness);
			break;
		case R.id.contrastSeekbar:
			float contrast = progress / 100;

			this.imageView.setImageBitmap(MatrixHelper.setContrast(
					this.targetBitmap, contrast));
			// bitmapEffectActivityHandler.setContrast(contrast);
			break;

		default:
			break;
		}
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
		rotateSeekbar.setProgress(0);
		brightnessSeekbar.setProgress(0);
		contrastSeekbar.setProgress(0);
		targetBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.puppy);
		imageView.setImageBitmap(targetBitmap);
		if (tempTargetBitmap != null) {
			tempTargetBitmap.recycle();
		}
	}

	private ImageView dialer;
	private int dialerHeight, dialerWidth;
	private static Matrix matrix;

	@SuppressWarnings("unused")
	private class MyOnTouchListener implements OnTouchListener {

		private double startAngle;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				startAngle = getAngle(event.getX(), event.getY());
				break;

			case MotionEvent.ACTION_MOVE:
				double currentAngle = getAngle(event.getX(), event.getY());
				rotateDialer((float) (startAngle - currentAngle));
				startAngle = currentAngle;
				break;

			case MotionEvent.ACTION_UP:

				break;
			}

			return true;
		}
	}

	private double getAngle(double xTouch, double yTouch) {
		double x = xTouch - (dialerWidth / 2d);
		double y = dialerHeight - yTouch - (dialerHeight / 2d);

		switch (getQuadrant(x, y)) {
		case 1:
			return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		case 2:
			return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		case 3:
			return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
		case 4:
			return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
		default:
			return 0;
		}
	}

	private static int getQuadrant(double x, double y) {
		if (x >= 0) {
			return y >= 0 ? 1 : 4;
		} else {
			return y >= 0 ? 2 : 3;
		}
	}

	private void rotateDialer(float degrees) {
		matrix.postRotate(degrees);

		dialer.setImageMatrix(matrix);
	}
}
