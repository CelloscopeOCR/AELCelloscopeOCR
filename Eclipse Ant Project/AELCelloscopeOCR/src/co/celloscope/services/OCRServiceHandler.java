package co.celloscope.services;

import java.util.ArrayList;

import co.celloscope.ocr.OCRManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

public class OCRServiceHandler extends Handler {
	private final ArrayList<Messenger> mClients;
	private final OCRManager mOcrManager;

	public OCRServiceHandler(OCRManager mOcrManager) {
		super();
		this.mOcrManager = mOcrManager;
		this.mClients = new ArrayList<Messenger>();
	}

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

			// Bundle mBundle = new Bundle();
			// mBundle.putString("ocrText", message.obj.toString());
			// for (int i = mClients.size() - 1; i >= 0; i--) {
			// try {
			// mClients.get(i).send(
			// Message.obtain(null, MSG_DO_OCR, mBundle));
			// } catch (RemoteException e) {
			// mClients.remove(i);
			// Log.e(TAG, e.getMessage());
			// } catch (Exception e) {
			// Log.e(TAG, e.getMessage());
			// }
			// }
			break;
		default:
			super.handleMessage(msg);
		}
	}

}
