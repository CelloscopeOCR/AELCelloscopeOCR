package co.celloscope.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.ArrayList;

public class OCRService extends Service {

	private static final String TAG = OCRService.class.getSimpleName();

	final Messenger mMessenger = new Messenger(new IncomingHandler());

	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	String mValue = "";
	private OCRManager mOcrManager;

	@Override
	public void onCreate() {
		mOcrManager = new OCRManager(OCRService.this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		mOcrManager.initialize(this, mClients);
		Log.i(TAG, "Service bounded");
		return mMessenger.getBinder();
	}

	@Override
	public void onDestroy() {

		if (mOcrManager.baseApi != null) {
			mOcrManager.baseApi.end();
		}
		Log.i(TAG, "Service Destroyed");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (mOcrManager.ocrActivityHandler != null) {
			mOcrManager.ocrActivityHandler.quitSynchronously();
		}
		Log.i(TAG, "Unbind");
		return super.onUnbind(intent);
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ServiceOperations.MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case ServiceOperations.MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case ServiceOperations.MSG_DO_OCR:
				// mValue = ((Bundle) msg.obj).getString("name");
				mValue = Environment.getExternalStorageDirectory() + "/ocr.jpg";
				mOcrManager.ocrActivityHandler.doOCR(mValue);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}