package co.celloscope.services;

import java.util.ArrayList;

import co.celloscope.ocr.OCRManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ServiceHandler extends Handler {

	private static String TAG = ServiceHandler.class.getSimpleName();
	private final ArrayList<Messenger> mClients;
	private final OCRManager mOcrManager;

	public ServiceHandler(OCRManager mOcrManager) {
		super();
		this.mOcrManager = mOcrManager;
		this.mClients = new ArrayList<Messenger>();
	}

	@Override
	public void handleMessage(Message msg) {

		Bundle bundle = new Bundle();
		switch (msg.what) {
		case ServiceOperations.MSG_REGISTER_CLIENT:
			mClients.add(msg.replyTo);
			bundle.putString("text",
					"Registered client " + msg.replyTo.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_REGISTER_CLIENT, bundle));
			break;
		case ServiceOperations.MSG_UNREGISTER_CLIENT:
			bundle.putString("text",
					"Unregistered client " + msg.replyTo.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_UNREGISTER_CLIENT, bundle));
			mClients.remove(msg.replyTo);
			break;
		case ServiceOperations.MSG_DO_OCR:
			String filePath = ((Bundle) msg.obj).getString("name");
			mOcrManager.doOCR(filePath);
			bundle.putString("text",
					"OCR request sent to " + mOcrManager.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_DO_OCR, bundle));
			break;
		case ServiceOperations.MSG_OCR_RESULT:
			bundle.putString("text", "OCR text " + msg.obj.toString());
			this.sendMessageToClients(Message.obtain(null,
					ServiceOperations.MSG_OCR_RESULT, bundle));
			break;
		default:
			super.handleMessage(msg);
		}
	}

	private void sendMessageToClients(Message message) {

		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				mClients.get(i).send(message);
			} catch (RemoteException e) {
				mClients.remove(i);
				Log.e(TAG, e.getMessage());
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
}
