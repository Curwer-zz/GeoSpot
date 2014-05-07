package net.erayzx.tab;

import net.erayzx.Login;
import net.erayzx.R;
import net.erayzx.Registered;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class Register_frag extends Fragment {

	/**
	 *  JSON Response node names.
	 **/


	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_FIRSTNAME = "fname";
	private static String KEY_LASTNAME = "lname";
	private static String KEY_USERNAME = "uname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	private static String KEY_ERROR = "error";

	/**
	 * Defining layout items.
	 **/

	EditText inputFirstName;
	EditText inputLastName;
	EditText inputUsername;
	EditText inputEmail;
	EditText inputPassword;
	Button btnRegister;
	TextView registerErrorMsg;
	View reg;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle saveInstanceState) {
		reg = inflater.inflate(R.layout.reg_frag, container, false);

		/**
		 * Defining all layout items
		 **/
		inputFirstName = (EditText)reg.findViewById(R.id.fname);
		inputLastName = (EditText)reg.findViewById(R.id.lname);
		inputUsername = (EditText)reg.findViewById(R.id.uname);
		inputEmail = (EditText)reg.findViewById(R.id.email);
		inputPassword = (EditText)reg.findViewById(R.id.pword);
		btnRegister = (Button)reg.findViewById(R.id.register);
		registerErrorMsg = (TextView)reg.findViewById(R.id.register_error);

		/**
		 * Button which Switches back to the login screen on clicked
		 **/

		Button login = (Button)reg.findViewById(R.id.bktologin);
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Login.class);
				startActivityForResult(myIntent, 0);

			}

		});

		/**
		 * Register Button click event.
		 * A Toast is set to alert when the fields are empty.
		 * Another toast is set to alert Username must be 5 characters.
		 **/

		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (  ( !inputUsername.getText().toString().equals("")) && ( !inputPassword.getText().toString().equals("")) && ( !inputFirstName.getText().toString().equals("")) && ( !inputLastName.getText().toString().equals("")) && ( !inputEmail.getText().toString().equals("")) )
				{
					if ( inputUsername.getText().toString().length() > 4 ){
						NetAsync(view);

					}
					else
					{
						Toast.makeText(getActivity().getApplicationContext(),
								"Username should be minimum 5 characters", Toast.LENGTH_SHORT).show();
					}
				}
				else
				{
					Toast.makeText(getActivity().getApplicationContext(),
							"One or more fields are empty", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return reg;

	}
	private class ProcessRegister extends AsyncTask<String, String, JSONObject> {

		/**
		 * Defining Process dialog
		 **/
		private ProgressDialog pDialog;

		String email,password,fname,lname,uname;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			inputUsername = (EditText)reg.findViewById(R.id.uname);
			inputPassword = (EditText)reg.findViewById(R.id.pword);
			fname = inputFirstName.getText().toString();
			lname = inputLastName.getText().toString();
			email = inputEmail.getText().toString();
			uname= inputUsername.getText().toString();
			password = inputPassword.getText().toString();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Registering ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {


			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.registerUser(fname, lname, email, uname, password);

			return json;


		}
		@Override
		protected void onPostExecute(JSONObject json) {
			/**
			 * Checks for success message.
			 **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					registerErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);

					String red = json.getString(KEY_ERROR);

					if(Integer.parseInt(res) == 1){
						pDialog.setTitle("Getting Data");
						pDialog.setMessage("Loading Info");

						registerErrorMsg.setText("Successfully Registered");


						DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						/**
						 * Removes all the previous data in the SQlite database
						 **/

						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getActivity().getApplicationContext());
						db.addUser(json_user.getString(KEY_FIRSTNAME),json_user.getString(KEY_LASTNAME),json_user.getString(KEY_EMAIL),json_user.getString(KEY_USERNAME),json_user.getString(KEY_UID),json_user.getString(KEY_CREATED_AT));
						/**
						 * Stores registered data in SQlite Database
						 * Launch Registered screen
						 **/

						Intent registered = new Intent(getActivity().getApplicationContext(), Registered.class);

						/**
						 * Close all views before launching Registered screen
						 **/
						registered.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(registered);

					}

					else if (Integer.parseInt(red) ==2){
						pDialog.dismiss();
						registerErrorMsg.setText("User already exists");
					}
					else if (Integer.parseInt(red) ==3){
						pDialog.dismiss();
						registerErrorMsg.setText("Invalid Email id");
					}

				}


				else{
					pDialog.dismiss();

					registerErrorMsg.setText("Error occured in registration");
				}

			} catch (JSONException e) {
				e.printStackTrace();


			}
		}}
	public void NetAsync(View view){
		new ProcessRegister().execute();
	


}

}
