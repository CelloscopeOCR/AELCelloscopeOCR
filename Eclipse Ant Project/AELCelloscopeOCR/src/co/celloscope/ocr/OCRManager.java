package co.celloscope.ocr;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import com.googlecode.tesseract.android.TessBaseAPI;

public final class OCRManager {

	private static final String TAG = OCRManager.class.getSimpleName();
	final String testFilePath = Environment.getExternalStorageDirectory()
			+ "/ocr.jpg";
	private final Context context;
	private OCRHandler ocrActivityHandler;
	TessBaseAPI baseApi;

	TessBaseAPI getBaseApi() {
		return baseApi;
	}

	public OCRManager(Context context) {
		this.context = context;
	}

	public void initialize() {
		initializeOCREngine();
		ocrActivityHandler = new OCRHandler(this, this.context);
	}

	public void release() {
		if (ocrActivityHandler != null) {
			ocrActivityHandler.quitSynchronously();
		}
	}

	public void destroy() {

		if (baseApi != null) {
			baseApi.end();
		}
	}

	public void doOCR(String filePath) {
		ocrActivityHandler.doOCR(filePath);
	}

	Handler getcaptureActivityHandler() {
		return ocrActivityHandler;
	}

	void initializeOCREngine() {

		boolean doNewInit = (baseApi == null);
		if (doNewInit) {

			File storageDirectory = getStorageDirectory();
			if (storageDirectory != null) {

				if (ocrActivityHandler != null) {
					ocrActivityHandler.quitSynchronously();
				}

				baseApi = new TessBaseAPI();
				new OcrInitAsyncTask(this, context, baseApi)
						.execute(storageDirectory.toString());
			}
			// startEmbeddedCropActivity();
		} else {
			resumeOCR();
		}
	}

	private File getStorageDirectory() {

		String state = null;
		try {
			state = Environment.getExternalStorageState();
		} catch (RuntimeException e) {
			Log.e(TAG, "Is the SD card visible?", e);
		}

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			try {
				return context.getExternalFilesDir(Environment.MEDIA_MOUNTED);
			} catch (NullPointerException e) {

				Log.e(TAG, "External storage is unavailable");
				return null;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return null;
			}

		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {

			Log.e(TAG, "External storage is read-only");
			return null;
		} else {
			Log.e(TAG, "External storage is unavailable");
			return null;
		}
	}

	void resumeOCR() {
		Log.d(TAG, "resumeOCR()");

		if (baseApi != null) {
			baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
			baseApi.setVariable(
					TessBaseAPI.VAR_CHAR_WHITELIST,
					"[]!?@#$%&*()<>_-+=/.,:;'\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
		}

	}

	void stopHandler() {
		if (ocrActivityHandler != null) {
			ocrActivityHandler.stop();
		}
	}

}
