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

		ColorMatrix colorMatrix = new ColorMatrix(new float[] { 1, 0, 0, 0,
				brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0,
				0, 0, 1, 0 });
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

		canvas.drawBitmap(sourceBitmap, new Matrix(), paint);

		return alteredBitmap;
	}

	public static Bitmap setContrast(Bitmap sourceBitmap, float contrast) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);

		ColorMatrix colorMatrix = new ColorMatrix(
				new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0, 0, 0,
						contrast, 0, 0, 0, 0, 0, 1, 0 });
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));

		canvas.drawBitmap(sourceBitmap, new Matrix(), paint);

		return alteredBitmap;
	}

	public static Bitmap rotate(Bitmap sourceBitmap, float angle) {

		Bitmap alteredBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(),
				sourceBitmap.getHeight(), sourceBitmap.getConfig());
		Canvas canvas = new Canvas(alteredBitmap);

		Matrix matrix = new Matrix();
		matrix.setRotate(angle, sourceBitmap.getWidth() / 2,
				sourceBitmap.getHeight() / 2);

		canvas.drawBitmap(sourceBitmap, matrix, new Paint());

		return alteredBitmap;
	}

}
