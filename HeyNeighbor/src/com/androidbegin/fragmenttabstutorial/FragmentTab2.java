package com.androidbegin.fragmenttabstutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class FragmentTab2 extends Fragment {
    
	// Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
	
    // Debugging
    //private static final String TAG = "DeviceListActivity";
    //private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    
	/*
	private static final int REQUEST_ENABLE_BT = 1;
	private Button onBtn;
	private Button offBtn;
	private Button listBtn;
	private Button findBtn;
	private TextView text;
	private BluetoothAdapter myBluetoothAdapter;
	private Set<BluetoothDevice> pairedDevices;
	private ListView myListView;
	private ArrayAdapter<String> BTArrayAdapter;
	*/
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab2, container, false);
        
        InputStream getName = getResources().openRawResource(R.raw.name);
        InputStream getPhone = getResources().openRawResource(R.raw.phone);
        InputStream getEmail = getResources().openRawResource(R.raw.email);
        InputStream getStatus = getResources().openRawResource(R.raw.status);
        
        BufferedReader nameReader = new BufferedReader(new InputStreamReader(getName));
        BufferedReader phoneReader = new BufferedReader(new InputStreamReader(getPhone));
        BufferedReader emailReader = new BufferedReader(new InputStreamReader(getEmail));
        BufferedReader statusReader = new BufferedReader(new InputStreamReader(getStatus));
        
        try {
			DeviceListActivity.name = nameReader.readLine();
			DeviceListActivity.phone = phoneReader.readLine();
			DeviceListActivity.email = emailReader.readLine();
			DeviceListActivity.status = statusReader.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        //setContentView(R.layout.fragmenttab2);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //finish();
            return rootView;
        }
        
        ensureDiscoverable();
        
        //Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
        //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
        
        Button scanButton = (Button) rootView.findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.device_name);

        // Find and set up the ListView for paired devices
        //ListView pairedListView = (ListView) rootView.findViewById(R.id.paired_devices);
        //pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        //pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) rootView.findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            rootView.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mNewDevicesArrayAdapter.add(noDevices);
        }
        
        /* Uncomment for original settings
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) 
        {
        	onBtn.setEnabled(false);
      	  	offBtn.setEnabled(false);
      	  	listBtn.setEnabled(false);
      	  	findBtn.setEnabled(false);
      	  	text.setText("Status: not supported");
      	  	
      	  Toast.makeText(getActivity(),"Your device does not support Bluetooth",
          		 Toast.LENGTH_LONG).show();
        } 
        else 
        {
  	      	text = (TextView) rootView.findViewById(R.id.text);
  	      	onBtn = (Button) rootView.findViewById(R.id.turnOn);
  	      onBtn.setOnClickListener(new OnClickListener() {
  			
  			@Override
  			public void onClick(View v) {
  				// TODO Auto-generated method stub
  				on(v);
  			}
  	      });
  	      
  	    offBtn = (Button) rootView.findViewById(R.id.turnOff);
	      offBtn.setOnClickListener(new OnClickListener() {
	  		
	  		@Override
	  		public void onClick(View v) {
	  			// TODO Auto-generated method stub
	  			off(v);
	  		}
	      });
	      
	      listBtn = (Button) rootView.findViewById(R.id.paired);
	      listBtn.setOnClickListener(new OnClickListener() {
	  		
	  		@Override
	  		public void onClick(View v) {
	  			// TODO Auto-generated method stub
	  			list(v);
	  		}
	      });
	      
	      findBtn = (Button) rootView.findViewById(R.id.search);
	      findBtn.setOnClickListener(new OnClickListener() {
	  		
	  		@Override
	  		public void onClick(View v) {
	  			// TODO Auto-generated method stub
	  			find(v);
	  		}
	      });
	      
	      myListView = (ListView) rootView.findViewById(R.id.listView1);
        }
        
        BTArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
   	 	myListView.setAdapter(BTArrayAdapter);*/
   	 	
        return rootView;
    }
	
	@Override
    public void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.message);
        mConversationView = (ListView) getView().findViewById(R.id.in);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        mOutEditText = (EditText) getView().findViewById(R.id.edit_text_out);
        mOutEditText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) getView().findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) getView().findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if(D) Log.e(TAG, "--- ON DESTROY ---");
        
     // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        getActivity().unregisterReceiver(mReceiver);
    }

    private void ensureDiscoverable() {
        if(D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);
        }
    }

    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };

    private final void setStatus(int resId) {
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setSubtitle(subTitle);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                    mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    setStatus(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    setStatus(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                
                if(readMessage.startsWith("un "))
                {
                	DeviceListActivity.name=readMessage.substring(readMessage.indexOf(" "), readMessage.indexOf("~"));
                	DeviceListActivity.phone=readMessage.substring(readMessage.indexOf("~"), readMessage.indexOf("%"));
                	DeviceListActivity.email=readMessage.substring(readMessage.indexOf("%"), readMessage.indexOf("^"));
                	DeviceListActivity.status=readMessage.substring(readMessage.indexOf("^"), readMessage.indexOf("<"));
                }
                else
                {
                	mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                }
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getActivity(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getActivity(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        /*case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;*/
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    /**
     * This is from deviceListActivity
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        // Indicate scanning in the title
        getActivity().setProgressBarIndeterminateVisibility(true);
        getActivity().setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        getView().findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            
            connectDevice(intent, false);
            
            Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            // Set result and finish this Activity
            //getActivity().setResult(Activity.RESULT_OK, intent);
            //getActivity().finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                getActivity().setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
    
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }*/

    //@Override
    //public boolean onOptionsItemSelected(MenuItem item) {
    //    Intent serverIntent = null;
    //    switch (item.getItemId()) {
    //    /*case R.id.scan:
    //        // Launch the DeviceListActivity to see devices and do scan
    //        serverIntent = new Intent(this, DeviceListActivity.class);
    //        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
    //        return true;*/
    //    case R.id.scan:
    //        // Launch the DeviceListActivity to see devices and do scan
    //        serverIntent = new Intent(getActivity(), DeviceListActivity.class);
    //        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    //       return true;
    //    case R.id.discoverable:
    //        // Ensure this device is discoverable by others
    //        ensureDiscoverable();
    //        return true;
    //    }
    //    return false;
    //}
	
	/*Uncomment for original setting
	public void on(View view){
	      if (!myBluetoothAdapter.isEnabled()) {
	         Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	         getActivity().startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

	         Toast.makeText(getActivity(),"Bluetooth turned on" ,
	        		 Toast.LENGTH_LONG).show();
	         
	         Intent discoverableIntent = new
	        		 Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	        		 discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	        		 getActivity().startActivity(discoverableIntent);
	      }
	      else{
	    	  Toast.makeText(getActivity(),"Bluetooth is already on",
	         		 Toast.LENGTH_LONG).show();
	      }
	   }
	   
	   @Override
	   public void onActivityResult(int requestCode, int resultCode, Intent data) {
		   // TODO Auto-generated method stub
		   if(requestCode == REQUEST_ENABLE_BT){
			   if(myBluetoothAdapter.isEnabled()) {
				   text.setText("Status: Enabled");
			   } else {   
				   text.setText("Status: Disabled");
			   }
		   }
	   }
	   
	   public void list(View view){
		  // get paired devices
	      pairedDevices = myBluetoothAdapter.getBondedDevices();
	      
	      // put it's one to the adapter
	      for(BluetoothDevice device : pairedDevices)
	    	  BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());
	      
	      Toast.makeText(getActivity(),"Show Paired Devices",
	    		  Toast.LENGTH_SHORT).show();
	   }
	   
	   final BroadcastReceiver bReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        // When discovery finds a device
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		             // Get the BluetoothDevice object from the Intent
		        	 BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        	 // add the name and the MAC address of the object to the arrayAdapter
		             BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		             BTArrayAdapter.notifyDataSetChanged();
		        }
		    }
		};
		
	   public void find(View view) {
		   if (myBluetoothAdapter.isDiscovering()) {
			   // the button is pressed when it discovers, so cancel the discovery
			   myBluetoothAdapter.cancelDiscovery();
		   }
		   else {
				BTArrayAdapter.clear();
				myBluetoothAdapter.startDiscovery();
			
				getActivity().registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		   }    
	   }
	   
	   public void off(View view){
		  myBluetoothAdapter.disable();
		  text.setText("Status: Disconnected");
	   }
	   
	   @Override
	   public void onDestroy() {
		   // TODO Auto-generated method stub
		   super.onDestroy();
		   getActivity().unregisterReceiver(bReceiver);
	   }*/
}