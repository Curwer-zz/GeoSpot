package net.erayzx.cam;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.erayzx.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraController extends Activity {

	ImageView imVCature_pic;
	Button btnCapture;
	private String date;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_surface_holder);
		initializeControls();
	}

	private void initializeControls() {
		imVCature_pic=(ImageView)findViewById(R.id.imVCature_pic);
		btnCapture=(Button)findViewById(R.id.btnCapture);
		btnCapture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/* create an instance of intent
				 * pass action android.media.action.IMAGE_CAPTURE 
				 * as argument to launch camera
				 */
				getDate();
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				/*create instance of File with name img.jpg*/
				File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Pictures" + File.separator + "GeoSpot");
				boolean success = true;
				if (!file.exists()) {
					success = file.mkdir();
				}
				if (success) {
					file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Pictures" + File.separator + "GeoSpot" + File.separator + date + ".jpg");

				} else {
					Toast.makeText(CameraController.this, "Failed to create picture directory", Toast.LENGTH_LONG).show();
				}
				/*put uri as extra in intent object*/
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				/*start activity for result pass intent as argument and request code */
				startActivityForResult(intent, 1);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//if request code is same we pass as argument in startActivityForResult
		if(requestCode==1){
			galleryAddPic();
			//create instance of File with same name we created before to get image from storage
			File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Pictures" + File.separator + "GeoSpot" + File.separator + date + ".jpg");
			//Crop the captured image using an other intent
			try {
				/*the user's device may not support cropping*/
				cropCapturedImage(Uri.fromFile(file));
			}
			catch(ActivityNotFoundException aNFE){
				//display an error message if user device doesn't support
				String errorMessage = "Sorry - your device doesn't support the crop action!";
				Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
				toast.show();
			}
		}
		if(requestCode==2){
			//Create an instance of bundle and get the returned data
			Bundle extras = data.getExtras();
			//get the cropped bitmap from extras
			Bitmap thePic = extras.getParcelable("data");
			//set image bitmap to image view
			imVCature_pic.setImageBitmap(thePic);
		}
	}
	//create helping method cropCapturedImage(Uri picUri)
	public void cropCapturedImage(Uri picUri){
		//call the standard crop action intent 
		Intent cropIntent = new Intent("com.android.camera.action.CROP");
		//indicate image type and Uri of image
		cropIntent.setDataAndType(picUri, "image/*");
		//set crop properties
		cropIntent.putExtra("crop", "true");
		//indicate aspect of desired crop
		cropIntent.putExtra("aspectX", 1);
		cropIntent.putExtra("aspectY", 1);
		//indicate output X and Y
		cropIntent.putExtra("outputX", 256);
		cropIntent.putExtra("outputY", 256);
		//retrieve data on return
		cropIntent.putExtra("return-data", true);
		//start the activity - we handle returning in onActivityResult
		startActivityForResult(cropIntent, 2);
	}

	public String getDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		date = dateFormat.format(new Date());
		return date;
	}
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(Environment.getExternalStorageDirectory()+ File.separator + "Pictures");
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}
} 