//package co.celloscope.ocrservicehost;
package com.ael.celloscope.aelcelloscopeocr.ocr;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

import java.util.ArrayList;

public class OCRService extends Service {
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
		Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {

		if (mOcrHelper.baseApi != null) {
			mOcrHelper.baseApi.end();
		}
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public IBinder onBind(Intent intent) {
		mOcrHelper.initializeOCREngine();
		mOcrHelper.ocrActivityHandler = new OCRActivityHandler(mOcrHelper, this);

		Toast.makeText(this, "Service bounded", Toast.LENGTH_SHORT).show();
		return mMessenger.getBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		if (mOcrHelper.ocrActivityHandler != null) {
			mOcrHelper.ocrActivityHandler.quitSynchronously();
		}
		Toast.makeText(this, "Unbind", Toast.LENGTH_SHORT).show();
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
				// mValue = (new StringBuilder(mValue)).reverse().toString();
				// Bundle mBundle = new Bundle();
				// mBundle.putString("ocrText", mValue);
				// for (int i = mClients.size() - 1; i >= 0; i--) {
				// try {
				// mClients.get(i).send(Message.obtain(null,
				// MSG_DO_OCR, mBundle));
				// } catch (RemoteException e) {
				// mClients.remove(i);
				// Toast.makeText(OCRService.this, e.getMessage(),
				// Toast.LENGTH_LONG).show();
				// } catch (Exception e) {
				// Toast.makeText(OCRService.this, e.getMessage(),
				// Toast.LENGTH_LONG).show();
				// }
				// }
				mOcrHelper.ocrActivityHandler.shutterButtonClick();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}