package com.ael.celloscope.aelcelloscopeocr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

public class MatrixHelper {

	public static Bitmap setBrightness(Bitmap sourceBitmap, int brightness) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);

		Paint paint = new Paint();
		paint.setColorFilter(getColorMatrixColorFilterForBrightness(brightness));

		canvas.drawBitmap(sourceBitmap, new Matrix(), paint);

		return alteredBitmap;
	}

	public static Bitmap setContrast(Bitmap sourceBitmap, float contrast) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);

		Paint paint = new Paint();
		paint.setColorFilter(getColorMatrixColorFilterForContrast(contrast));

		canvas.drawBitmap(sourceBitmap, new Matrix(), paint);

		return alteredBitmap;
	}

	public static Bitmap rotate(Bitmap sourceBitmap, float angle) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);
		canvas.drawBitmap(
				sourceBitmap,
				getRotateMatrix(angle, sourceBitmap.getWidth() / 2,
						sourceBitmap.getHeight() / 2), new Paint());

		return alteredBitmap;
	}

	public static ColorMatrixColorFilter getColorMatrixColorFilterForBrightness(
			int brightness) {
		ColorMatrix colorMatrix = new ColorMatrix(new float[] { 1, 0, 0, 0,
				brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0,
				0, 0, 1, 0 });
		return new ColorMatrixColorFilter(colorMatrix);
	}

	public static ColorMatrixColorFilter getColorMatrixColorFilterForContrast(
			float contrast) {
		float scale = contrast + 1.f;
		float translate = (-.5f * scale + .5f) * 255.f;
		float[] array = new float[] { scale, 0, 0, 0, translate, 0, scale, 0,
				0, translate, 0, 0, scale, 0, translate, 0, 0, 0, 1, 0 };
		ColorMatrix matrix = new ColorMatrix(array);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		return filter;
	}

	public static Matrix getRotateMatrix(float degrees, float px, float py) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees, px, py);
		return matrix;

	}

}
