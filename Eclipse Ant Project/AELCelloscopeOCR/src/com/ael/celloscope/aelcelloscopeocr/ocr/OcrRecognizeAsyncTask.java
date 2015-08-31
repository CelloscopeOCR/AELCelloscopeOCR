package com.ael.celloscope.aelcelloscopeocr.ocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Class to send OCR requests to the OCR engine in a separate thread, send a
 * success/failure message, and dismiss the indeterminate progress dialog box.
 * Used for non-continuous mode OCR only.
 */
final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private CaptureActivity activity;
	private TessBaseAPI baseApi;
	private byte[] data;
	private int width;
	private int height;
	String textResult;
	private ProgressDialog indeterminateDialog;

	OcrRecognizeAsyncTask(CaptureActivity activity, byte[] data, int width,
			int height) {
		this.activity = activity;
		this.baseApi = activity.getBaseApi();
		this.data = data;
		this.width = width;
		this.height = height;

	}

	@Override
	protected void onPreExecute() {
		this.displayProgressDialog();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		//
		// Bitmap bitmap = activity.getCameraManager()
		// .buildLuminanceSource(data, width, height)
		// .renderCroppedGreyscaleBitmap();

		File path = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File file = new File(path, "/test.jpg");
		Bitmap imutableBitmap = BitmapFactory
				.decodeFile(file.getAbsolutePath());
		Bitmap bitmap = imutableBitmap.copy(Bitmap.Config.ARGB_8888, true);

		// Uri sourceUri = Uri.parse("file://"
		// + Environment.getExternalStorageDirectory()
		// + "/ael_image_cropped.jpg");
		// Bitmap bitmap;

		try {
			// bitmap = MediaStore.Images.Media.getBitmap(
			// this.activity.getContentResolver(), sourceUri);
			baseApi.setImage(ReadFile.readBitmap(bitmap));
			textResult = baseApi.getUTF8Text();

			if (textResult != null && !textResult.equals("")) {
				return true;
			}

		} catch (RuntimeException e) {
			Log.e("OcrRecognizeAsyncTask",
					"Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
			e.printStackTrace();
			try {
				baseApi.clear();
				activity.stopHandler();
			} catch (NullPointerException e1) {
				// Continue
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		Handler handler = activity.getcaptureActivityHandler();
		if (handler != null) {

			if (result) {
				Message message = Message.obtain(handler,
						R.id.ocr_decode_succeeded, textResult);
				message.sendToTarget();
			} else {
				Message message = Message.obtain(handler,
						R.id.ocr_decode_failed, textResult);
				message.sendToTarget();
			}

			indeterminateDialog.dismiss();
		}
		if (baseApi != null) {
			baseApi.clear();
		}
	}

	void displayProgressDialog() {

		indeterminateDialog = new ProgressDialog(activity);
		indeterminateDialog.setTitle("Please wait");
		indeterminateDialog.setMessage("Performing OCR " + "...");
		indeterminateDialog.setCancelable(false);
		indeterminateDialog.show();
	}
}
