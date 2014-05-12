package net.erayzx.tab;

import net.erayzx.Main;
import net.erayzx.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.erayzx.cam.CameraController;
import net.erayzx.library.DatabaseHandler;
import net.erayzx.library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login_frag extends Fragment {
	
	Button btnLogin;
	Button cam;
	EditText inputEmail;
	EditText inputPassword;
	View login;
	private TextView loginErrorMsg;
	/**
	 * Called when the activity is first created.
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_FIRSTNAME = "fname";
	private static String KEY_LASTNAME = "lname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle saveInstanceState) {
		login = inflater.inflate(R.layout.login_frag, container, false);
		
		inputEmail = (EditText)login.findViewById(R.id.email);
		inputPassword = (EditText)login.findViewById(R.id.pword);
		btnLogin = (Button)login.findViewById(R.id.login);
		cam = (Button)login.findViewById(R.id.testCam);
		loginErrorMsg = (TextView)login.findViewById(R.id.loginErrorMsg);
		
		cam.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent camera = new Intent(getActivity().getApplicationContext(), CameraController.class);
				startActivity(camera);
			}
		});
		
		/**
		 * Login button click event
		 * A Toast is set to alert when the Email and Password field is empty
		 **/
		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {

				if (  ( !inputEmail.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) )
				{
					NetAsync(view);
				}
				else if ( ( !inputEmail.getText().toString().equals("")) )
				{
					Toast.makeText(getActivity().getApplicationContext(),
							"Password field empty", Toast.LENGTH_SHORT).show();
				}
				else if ( ( !inputPassword.getText().toString().equals("")) )
				{
					Toast.makeText(getActivity().getApplicationContext(),
							"Email field empty", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getActivity().getApplicationContext(),
							"Email and Password field are empty", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return login;
	}

	/**
	 * Async Task to get and send data to My Sql database through JSON respone.
	 **/
	private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


		private ProgressDialog pDialog;

		String email,password;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			inputEmail = (EditText)login.findViewById(R.id.email);
			inputPassword = (EditText)login.findViewById(R.id.pword);
			email = inputEmail.getText().toString();
			password = inputPassword.getText().toString();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {

			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.loginUser(email, password);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {

					String res = json.getString(KEY_SUCCESS);

					if(Integer.parseInt(res) == 1){
						pDialog.setMessage("Loading User Space");
						pDialog.setTitle("Getting Data");
						DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");
						/**
						 * Clear all previous data in SQlite database.
						 **/
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getActivity().getApplicationContext());
						db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
						/**
						 *If JSON array details are stored in SQlite it launches the User Panel.
						 **/
						Intent upanel = new Intent(getActivity().getApplicationContext(), Main.class);
						upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(upanel);
						/**
						 * Close Login Screen
						 **/
					}else{

						pDialog.dismiss();
						loginErrorMsg.setText("Incorrect username/password");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	public void NetAsync(View view){
		new ProcessLogin().execute();
	}


}
