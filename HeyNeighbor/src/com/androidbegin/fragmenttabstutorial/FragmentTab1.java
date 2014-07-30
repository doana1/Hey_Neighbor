package com.androidbegin.fragmenttabstutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.AssetManager;

public class FragmentTab1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab1, container, false);

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
        
        Button button = (Button) rootView.findViewById(R.id.button1);
        	   button.setOnClickListener(new OnClickListener()
        	   {
        	             @Override
        	             public void onClick(View v)
        	             {
        	            	 Fragment fragment = new EditPage();

        	            	 FragmentManager fm = getFragmentManager();
        	            	 FragmentTransaction transaction = fm.beginTransaction();
        	            	 transaction.replace(R.id.fragment_container, fragment);
        	            	 transaction.commit();
        	             } 
        	   }); 
        
        return rootView;
    }
 
}