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

	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_UNREGISTER_CLIENT = 2;
	static final int MSG_DO_OCR = 3;

	final Messenger mMessenger = new Messenger(new IncomingHandler());

	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	String mValue = "";
	private OCRHelper mOcrHelper;

	@Override
	public void onCreate() {
		mOcrHelper = new OCRHelper(OCRService.this);
		Log.i(TAG, "Created");
	}

	@Override
	public void onDestroy() {

		if (mOcrHelper.baseApi != null) {
			mOcrHelper.baseApi.end();
		}
		Log.i(TAG, "Service Destroyed");
	}

	@Override
	public IBinder onBind(Intent intent) {
		mOcrHelper.initializeOCREngine();
		mOcrHelper.ocrActivityHandler = new OCRHandler(mOcrHelper, this,
				mClients);
		Log.i(TAG, "Service bounded");
		return mMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (mOcrHelper.ocrActivityHandler != null) {
			mOcrHelper.ocrActivityHandler.quitSynchronously();
		}
		Log.i(TAG, "Unbind");
		return super.onUnbind(intent);
	}

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTER_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_DO_OCR:
				// mValue = ((Bundle) msg.obj).getString("name");
				mValue = Environment.getExternalStorageDirectory() + "/ocr.jpg";
				mOcrHelper.ocrActivityHandler.doOCR(mValue);
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}