package com.androidbegin.fragmenttabstutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentTab1 extends Fragment {
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab1, container, false);
        
        /**
    	 * Gets a Handle to a SharedPreference
    	 * MODE_Private = Other applications can't access it
    	 */
    	Context context = getActivity();
    	SharedPreferences userProfile = getActivity().getPreferences(Context.MODE_PRIVATE);
    	
    	int counter = userProfile.getInt("counter", 0);
    	
    	/**
    	 * Counter was only used to test SharedPreferences
    	 */
    	// Update the TextView
        TextView text = (TextView) rootView.findViewById(R.id.appNumberCount);
        text.setText("This app has been started " + counter + " times.");
        
        // Increment the counter
        SharedPreferences.Editor editor = userProfile.edit();
        editor.putInt("counter", ++counter);
        editor.commit(); // Very important
        
        /**
         * Accesses userProfile for user information
         * Accesses TextViews on Profile Screen
         * Enters userProfile saved data into the TextViews
         */
        String name = userProfile.getString("name","name");
        String phone = userProfile.getString("phone","123-456-7890");
        String email = userProfile.getString("email","example@example.example");
        String status = userProfile.getString("status","I like my status!");
    	
        TextView textName = (TextView) rootView.findViewById(R.id.userName);
        TextView textPhone = (TextView) rootView.findViewById(R.id.userPhone);
        TextView textEmail = (TextView) rootView.findViewById(R.id.userEmail);
        TextView textStatus = (TextView) rootView.findViewById(R.id.userStatus);
        
        textName.setText(name);
        textPhone.setText(phone);
        textEmail.setText(email);
        textStatus.setText(status);
        

        
        
        
    	/*
        //Text file handlers
        InputStream getName = getResources().openRawResource(R.raw.name);
        InputStream getPhone = getResources().openRawResource(R.raw.phone);
        InputStream getEmail = getResources().openRawResource(R.raw.email);
        InputStream getStatus = getResources().openRawResource(R.raw.status);
        
        BufferedReader nameReader = new BufferedReader(new InputStreamReader(getName));
        BufferedReader phoneReader = new BufferedReader(new InputStreamReader(getPhone));
        BufferedReader emailReader = new BufferedReader(new InputStreamReader(getEmail));
        BufferedReader statusReader = new BufferedReader(new InputStreamReader(getStatus));

        //Load user set variables
        TextView userName = (TextView) rootView.findViewById(R.id.textView2);
        TextView userPhone = (TextView) rootView.findViewById(R.id.textView3);
        TextView userEmail = (TextView) rootView.findViewById(R.id.textView4);
        TextView userStatus = (TextView) rootView.findViewById(R.id.textView5);
        try {
        	userName.setText(nameReader.readLine());
        	userPhone.setText(phoneReader.readLine());
        	userEmail.setText(emailReader.readLine());
        	userStatus.setText("\"" + statusReader.readLine() + "\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
        
        //Edit Profile Page
        //Change: Can't use raw files for profile
        //Reason: All data in the res folders have no write permissions, only read
        Button editPage = (Button) rootView.findViewById(R.id.editPage);
        	   editPage.setOnClickListener(new OnClickListener()
        	   {
        	             @Override
        	             public void onClick(View v)
        	             {
        	            	 Fragment fragment = new Edit_Page();

        	            	 FragmentManager fm = getFragmentManager();
        	            	 FragmentTransaction transaction = fm.beginTransaction();
        	            	 transaction.replace(R.id.fragment_container, fragment);
        	            	 transaction.commit();
        	             } 
        	   }); 
        
        return rootView;
    }
 
}