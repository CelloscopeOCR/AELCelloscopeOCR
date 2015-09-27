package com.ael.celloscope.aelcelloscopeocr.ocr;



import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


//import com.soundcloud.android.crop.Crop;
//import com.soundcloud.android.crop.Crop.Position;

public class AELCropActivity extends Activity {
	private static final String TAG = AELCropActivity.class.getSimpleName();
	private static final int CAMERA_REQUEST = 0xffff55;
	Uri sourceUri;
	boolean cropDone = false;
	String imageDestination;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageDestination = "file://"
				+ Environment.getExternalStorageDirectory() + "/ael_image.jpg";
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!cropDone) {
			sourceUri = Uri.parse("file://"
					+ Environment.getExternalStorageDirectory()
					+ "/ael_image.jpg");
			Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, sourceUri);
			startActivityForResult(photoIntent, CAMERA_REQUEST);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			cropDone = true;
			Uri destination = Uri.parse("file://"
					+ Environment.getExternalStorageDirectory()
					+ "/ael_image_cropped.jpg");
//			Crop.of(sourceUri, destination).withMICRAspect()
//					.at(Position.BOTTOM).start(this);
		}
//		else if (requestCode == Crop.REQUEST_CROP) {
//			Log.v("handle", "CROP__");
//			handleCrop(resultCode, data);
//
//		}
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == RESULT_OK) {
			Log.v(TAG, "CROPPING DONE");

		} 
		
//		else if (resultCode == Crop.RESULT_ERROR) {
//			Toast.makeText(this, Crop.getError(result).getMessage(),
//					Toast.LENGTH_SHORT).show();
//		}
	}
}
