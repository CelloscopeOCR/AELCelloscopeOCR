package co.celloscope.ocr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import co.celloscope.services.R;

final class OCRHandler extends Handler {

	private static final String TAG = OCRHandler.class.getSimpleName();

	private final Context context;
	private final DecodeThread decodeThread;
	private static State state;

	static final int MSG_DO_OCR = 3;

	private enum State {
		PREVIEW, PREVIEW_PAUSED, CONTINUOUS, CONTINUOUS_PAUSED, SUCCESS, DONE
	}

	OCRHandler(OCRManager ocrHelper, Context context) {
		this.context = context;
		decodeThread = new DecodeThread(ocrHelper, context);
		decodeThread.start();

		state = State.SUCCESS;
		restartOcrPreview();
	}

	private void restartOcrPreview() {

		if (state == State.SUCCESS) {
			state = State.PREVIEW;
		}
	}

	@Override
	public void handleMessage(Message message) {

		switch (message.what) {

		case R.id.ocr_decode_succeeded:
			state = State.SUCCESS;
			if (context instanceof Activity) {
				new AlertDialog.Builder(context)
						.setMessage(message.obj.toString())
						.setPositiveButton("OCR", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// activity.startEmbeddedCropActivity();

							}
						}).setCancelable(false).create().show();
			} else {

				Toast.makeText(context, message.obj.toString(),
						Toast.LENGTH_LONG);
				Log.i(TAG, message.obj.toString());

			}

			break;
		case R.id.ocr_decode_failed:
			state = State.PREVIEW;

			Toast toast = Toast.makeText(context,
					"OCR failed. Please try agai'n.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 0, 0);
			toast.show();
			break;
		}
	}

	void stop() {

		Log.d(TAG, "Setting state to CONTINUOUS_PAUSED.");
		state = State.CONTINUOUS_PAUSED;
		removeMessages(R.id.ocr_continuous_decode);
		removeMessages(R.id.ocr_decode);
		removeMessages(R.id.ocr_continuous_decode_failed);
		removeMessages(R.id.ocr_continuous_decode_succeeded);
	}

	void quitSynchronously() {
		state = State.DONE;
		try {
			decodeThread.join(500L);
		} catch (InterruptedException e) {
			Log.w(TAG, "Caught InterruptedException in quitSyncronously()", e);

		} catch (RuntimeException e) {
			Log.w(TAG, "Caught RuntimeException in quitSyncronously()", e);
		} catch (Exception e) {
			Log.w(TAG, "Caught unknown Exception in quitSynchronously()", e);
		}
		removeMessages(R.id.ocr_continuous_decode);
		removeMessages(R.id.ocr_decode);

	}

	void hardwareShutterButtonClick(String filePath) {

		if (state == State.PREVIEW) {
			ocrDecode(filePath);
		}
	}

	void shutterButtonClick(String filePath) {

		ocrDecode(filePath);
	}

	void doOCR(String filePath) {
		ocrDecode(filePath);
	}

	private void ocrDecode(String filePath) {
		state = State.PREVIEW_PAUSED;
		Message message = decodeThread.getHandler().obtainMessage(
				R.id.ocr_decode, 0, 0, filePath);
		message.sendToTarget();

	}

}
