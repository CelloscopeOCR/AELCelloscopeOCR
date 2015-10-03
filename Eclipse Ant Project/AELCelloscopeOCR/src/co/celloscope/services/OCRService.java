package co.celloscope.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.util.ArrayList;

import co.celloscope.ocr.OCRManager;

public class OCRService extends Service {

	private static final String TAG = OCRService.class.getSimpleName();

	private final Messenger mServiceMessenger = new Messenger(new Handler() {

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
				String filePath = ((Bundle) msg.obj).getString("name");
				mOcrManager.doOCR(filePath);
				break;
			default:
				super.handleMessage(msg);
			}
		}

	});

	ArrayList<Messenger> mClients = new ArrayList<Messenger>();
	private OCRManager mOcrManager;

	@Override
	public void onCreate() {
		mOcrManager = new OCRManager(OCRService.this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		mOcrManager.initialize(OCRService.this, mClients);
		Log.i(TAG, "Service bounded");
		return mServiceMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		mOcrManager.release();
		Log.i(TAG, "Unbind");
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		mOcrManager.destroy();
		Log.i(TAG, "Service Destroyed");
	}

}