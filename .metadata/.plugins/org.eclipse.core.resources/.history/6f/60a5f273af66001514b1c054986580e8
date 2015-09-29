package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ael.celloscope.aelcelloscopeocr.R;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

final class OcrRecognizeAsyncTask extends AsyncTask<Void, Void, Boolean> {

	private CaptureActivity activity;
	private TessBaseAPI baseApi;
	// private byte[] data;
	// private int width;
	// private int height;
	String textResult;
	private ProgressDialog indeterminateDialog;

	OcrRecognizeAsyncTask(CaptureActivity activity, byte[] data, int width,
			int height) {
		this.activity = activity;
		this.baseApi = activity.getBaseApi();
		// this.data = data;
		// this.width = width;
		// this.height = height;

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

		/*
		 * File path = Environment
		 * .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM); File
		 * file = new File(path, "/ocr.jpg"); Bitmap imutableBitmap =
		 * BitmapFactory .decodeFile(file.getAbsolutePath()); Bitmap bitmap =
		 * imutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
		 */

		// Uri sourceUri = Uri.parse("file://"
		// + Environment.getExternalStorageDirectory() + "/ocr.jpg");
		// Bitmap bitmap = null;

		try {
			// try {
			// bitmap = MediaStore.Images.Media.getBitmap(
			// this.activity.getContentResolver(), sourceUri);
			//
			// } catch (FileNotFoundException e) {
			//
			// e.printStackTrace();
			// } catch (IOException e) {
			//
			// e.printStackTrace();
			// }
			baseApi.setImage(ReadFile.readBitmap(BitmapEffect
					.decodeSmallBitmap(
							Environment.getExternalStorageDirectory()
									+ "/ocr.jpg", 1200, 1200 / 7)));
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
