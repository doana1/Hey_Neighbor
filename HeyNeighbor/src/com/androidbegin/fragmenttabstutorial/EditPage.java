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
import android.content.res.AssetManager;

public class EditPage extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.edit_profile, container, false);
        
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
        
        Button button = (Button) rootView.findViewById(R.id.button3);
 	   	button.setOnClickListener(new OnClickListener()
 	   	{
 	            @Override
 	            public void onClick(View v)
 	            {
 	            	String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/textData";
 	            	
 	            	//Save changes to file
 	            	EditText name  = (EditText) rootView.findViewById(R.id.editText1);
 	            	EditText phone  = (EditText) rootView.findViewById(R.id.editText2);
 	            	EditText email  = (EditText) rootView.findViewById(R.id.editText3);
 	            	EditText status  = (EditText) rootView.findViewById(R.id.editText4);
 	            	
 	            	try 
 	            	{	
 	            		FileWriter fw = new FileWriter(fullPath + "/name.txt", false);
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