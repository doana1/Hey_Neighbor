package com.androidbegin.fragmenttabstutorial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

public class Edit_Page extends FragmentTab1 {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.edit_page, container, false);
        
        //Unsure if still need this
        /**
         * Context context = getActivity();
         */
        SharedPreferences userProfile = getActivity().getPreferences(Context.MODE_PRIVATE);
       

    	/**
    	 * Just to help get started
    	int counter = userProfile.getInt("counter", 0);
    	
    	// Update the TextView
        TextView text = (TextView) rootView.findViewById(R.id.appNumberCount);
        text.setText("This app has been started " + counter + " times.");
        
        // Increment the counter
        SharedPreferences.Editor editor = userProfile.edit();
        editor.putInt("counter", ++counter);
        editor.commit(); // Very important
        */
        
        /**
         * Default Profile Information
         */
        String name = userProfile.getString("name","name");
        String phone = userProfile.getString("phone","123-456-7890");
        String email = userProfile.getString("email","example@example.example");
        String status = userProfile.getString("status","I like my status!");
        
        /**
         * Accessing EditText fields
         * Placing profile data into fields
         */
        EditText editName = (EditText) rootView.findViewById(R.id.editText1);
        editName.setText(name);
        EditText editPhone = (EditText) rootView.findViewById(R.id.editText2);
        editPhone.setText(phone);
        EditText editEmail = (EditText) rootView.findViewById(R.id.editText3);
        editEmail.setText(email);
        EditText editStatus = (EditText) rootView.findViewById(R.id.editText4);
        editStatus.setText(status);

    	
    	
        /**
         * Don't need this anymore
         * Original attempt to use raw files to read/write profile information
        */
        /*
        EditText name  = (EditText) rootView.findViewById(R.id.editText1);
     	EditText phone  = (EditText) rootView.findViewById(R.id.editText2);
     	EditText email  = (EditText) rootView.findViewById(R.id.editText3);
     	EditText status  = (EditText) rootView.findViewById(R.id.editText4);
     	
        //Text file handlers
        InputStream getName = getResources().openRawResource(R.raw.name);
        InputStream getPhone = getResources().openRawResource(R.raw.phone);
        InputStream getEmail = getResources().openRawResource(R.raw.email);
        InputStream getStatus = getResources().openRawResource(R.raw.status);
        
        BufferedReader nameReader = new BufferedReader(new InputStreamReader(getName));
        BufferedReader phoneReader = new BufferedReader(new InputStreamReader(getPhone));
        BufferedReader emailReader = new BufferedReader(new InputStreamReader(getEmail));
        BufferedReader statusReader = new BufferedReader(new InputStreamReader(getStatus));
        
        EditText name  = (EditText) rootView.findViewById(R.id.editText1);
     	EditText phone  = (EditText) rootView.findViewById(R.id.editText2);
     	EditText email  = (EditText) rootView.findViewById(R.id.editText3);
     	EditText status  = (EditText) rootView.findViewById(R.id.editText4);
     	
     	try {
			name.setText(nameReader.readLine());
			phone.setText(phoneReader.readLine());
			email.setText(emailReader.readLine());
			status.setText(statusReader.readLine());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
        
        
        
        
        
        /**
         * All stuff below is for previous edit confirmation
         * User makes changes to text fields for profile data
         * Button confirms the save and is suppose to write to file

     	 * Updates to be Made:
     	 * - Confirmation to save Changes
     	 * - Ability to exit without changes
     	 */
     	Button button = (Button) rootView.findViewById(R.id.button3);
 	   	button.setOnClickListener(new OnClickListener()
 	   	{
 	            @Override
 	            public void onClick(View v)
 	            {
 	            	/**
 	            	 * Accesses userProfile
 	            	 * Retrieves data in EditText fields
 	            	 * Writes new data into userProfile
 	            	 */
 	            	SharedPreferences userProfile = getActivity().getPreferences(Context.MODE_PRIVATE);
 	            	SharedPreferences.Editor editor = userProfile.edit();
 	            	
 	            	EditText editName = (EditText) rootView.findViewById(R.id.editText1);
 	            	EditText editPhone = (EditText) rootView.findViewById(R.id.editText2);
 	            	EditText editEmail = (EditText) rootView.findViewById(R.id.editText3);
 	            	EditText editStatus = (EditText) rootView.findViewById(R.id.editText4);
 	            	
 	            	String newName = (editName.getText().toString());
 	            	String newPhone = (editPhone.getText().toString());
 	            	String newEmail = (editEmail.getText().toString());
 	            	String newStatus = (editStatus.getText().toString());
 	            	
 	            	editor.putString("name", newName);
 	                editor.putString("phone", newPhone);
 	                editor.putString("email", newEmail);
 	                editor.putString("status", newStatus);
 	                
 	                editor.commit(); // Very important
 	            	/*
 	            	String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/textData";
 	            	
 	            	//Save changes to file
 	            	EditText name  = (EditText) rootView.findViewById(R.id.editText1);
 	            	EditText phone  = (EditText) rootView.findViewById(R.id.editText2);
 	            	EditText email  = (EditText) rootView.findViewById(R.id.editText3);
 	            	EditText status  = (EditText) rootView.findViewById(R.id.editText4);
 	            	
 	            	try 
 	            	{	
 	            		FileWriter fw = new FileWriter(fullPath + "/name", false);
 	            		fw.write(name.toString());
 	            		fw.close();
 	            		
 	            		fw = new FileWriter(fullPath + "/phone.txt", false);
 	            		fw.write(phone.toString());
 	            		fw.close();
 	            		
 	            		fw = new FileWriter(fullPath + "/email.txt", false);
 	            		fw.write(email.toString());
 	            		fw.close();
 	            		
 	            		fw = new FileWriter(fullPath + "/status", false);
 	            		fw.write(status.toString());
 	            		fw.close();
 	                }
 	            	catch (IOException e) {
 	            		Log.e(EditPage.class.getName(), "File write failed: " + e.toString());
 	            	}
 	            	*/
 	            	 //Switch back to profile view
 	            	 Fragment fragment = new FragmentTab1();
 	            	 FragmentManager fm = getFragmentManager();
 	            	 FragmentTransaction transaction = fm.beginTransaction();
 	            	 transaction.replace(R.id.fragment_container, fragment);
 	            	 transaction.commit();
 	             } 
 	   });
        
        return rootView;
    }
 
}