package org.soho_style.ADKSample01;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class ADKSample01Activity extends Activity implements Runnable {
	private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";
	private static final int MESSAGE_SWITCH = 1;
	private static final int MESSAGE_TEMPERATURE = 2;
	private static final int MESSAGE_LIGHT = 3;
	private static final int MESSAGE_JOY = 4;
	private boolean mPermissionRequestPending;
	private PendingIntent mPermissionIntent;
	UsbManager mUsbManager;
	ParcelFileDescriptor mFileDescriptor;
	FileInputStream mInputStream;
	FileOutputStream mOutputStream;
	UsbAccessory mAccessory;
	TextView tv1, tv2, tv3, tv4;
	EditText et;

	private int composeInt(byte hi, byte lo) {
		int val = (int) hi & 0xff;
		val *= 256;
		val += (int) lo & 0xff;
		return val;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);
		tv1 = (TextView) findViewById(R.id.textView1);
		tv2 = (TextView) findViewById(R.id.textView2);
		tv3 = (TextView) findViewById(R.id.textView3);
		tv4 = (TextView) findViewById(R.id.textView4);
		et = (EditText) findViewById(R.id.editText1);
		et.setTextSize(8);
//		  try {
//	           Runtime.getRuntime().exec("logcat -c");
//	       } catch(Exception e) {
//	           // 例外処理
//	       }
	}
	@Override
	public void onResume() {
		super.onResume();

		Intent intent = getIntent();
		if (mInputStream != null && mOutputStream != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			} else {
				synchronized (mUsbReceiver) {
					if (!mPermissionRequestPending) {
						mUsbManager.requestPermission(accessory,
								mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d("TAG", "mAccessory is null");
		}
	}
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d("AAA", action);
			showLog();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d("TAG", "permission denied for accessory "
								+ accessory);
					}
					mPermissionRequestPending = false;
				}
			} else if (UsbManager.ACTION_USB_ACCESSORY_DETACHED.equals(action)) {
				UsbAccessory accessory = UsbManager.getAccessory(intent);
				if (accessory != null && accessory.equals(mAccessory)) {
					closeAccessory();
				}
			}
		}
	};
	@Override
	public void onPause() {
		super.onPause();
		closeAccessory();
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}
	private void openAccessory(UsbAccessory accessory) {
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "DemoKit");
			thread.start();
		} else {
			Log.d("TAG", "accessory open fail");
		}
	}
	private void closeAccessory() {
		try {
			if (mFileDescriptor != null) {
				mFileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			mFileDescriptor = null;
			mAccessory = null;
		}
	}
	public void run() {
		int ret = 0;
		byte[] buffer = new byte[16384];
		int i;
		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}
			i = 0;
			while (i < ret) {
				int len = ret - i;
				switch (buffer[i]) {
				case 0x1:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_SWITCH);
						m.obj = new String("スイッチ=" + buffer[i + 1] + ":" +  buffer[i + 2]);
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				case 0x4:
					if (len >= 3) {
						Message m = Message.obtain(mHandler,
								MESSAGE_TEMPERATURE);
						double tempval= (double) composeInt(buffer[i+1],buffer[i+2]) * 100 /1023; 
						m.obj = new String("湿度=" + String.format("%.2f", tempval) + "%");
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				case 0x5:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_LIGHT);
						m.obj = new String("明るさ=" + buffer[i + 1] + "!" + buffer[i + 2]);
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				case 0x6:
					if (len >= 3) {
						Message m = Message.obtain(mHandler, MESSAGE_JOY);
						m.obj = new String("ジョイスティック=" + buffer[i + 1] + ":" +  buffer[i + 2]);
						mHandler.sendMessage(m);
					}
					i += 3;
					break;
				default:
					i = len;
					break;
				}
			}

		}
	}
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/*case MESSAGE_SWITCH:
				tv2.setText(msg.obj.toString());
				break;*/
			case MESSAGE_TEMPERATURE:
				tv1.setText(msg.obj.toString());
				break;/*
			case MESSAGE_LIGHT:
				tv3.setText(msg.obj.toString());
				break;
			case MESSAGE_JOY:
				tv4.setText(msg.obj.toString());
				break;*/
			}

		}
	};
	private void showLog() {
		 try {
	           ArrayList<String> commandLine = new ArrayList<String>();
	           commandLine.add( "logcat");
	           commandLine.add( "-d");
	           Process process = Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));
	           BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
	           String line;
	           while ( (line = bufferedReader.readLine()) != null) {
	        	   et.append(line);
	        	   et.append("\n");
	           }
	       } catch ( IOException e) {
	           // 例外処理
	       }
	}
}