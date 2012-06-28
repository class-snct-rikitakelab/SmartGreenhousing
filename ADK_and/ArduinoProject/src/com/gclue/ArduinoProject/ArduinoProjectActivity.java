/**
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gclue.ArduinoProject;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.widget.TextView;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class ArduinoProjectActivity extends Activity implements Runnable {

	/**
	 * Logを表示するためのタグ
	 */
	private static final String TAG = "ADK_SAMPLE";

	/**
	 * ADKのアクション名
	 */
	private static final String ACTION_USB_PERMISSION = "com.google.android.DemoKit.action.USB_PERMISSION";

	/**
	 * Usb Manager
	 */
	private UsbManager mUsbManager;

	/**
	 * ペンディングIntent(USBが切断された際に発行)
	 */
	private PendingIntent mPermissionIntent;

	/**
	 * Permissionのリクエストのフラグ
	 */
	private boolean mPermissionRequestPending;

	/**
	 * Usb Accessory
	 */
	private UsbAccessory mAccessory;

	/**
	 * File Descriptor
	 */
	private ParcelFileDescriptor mFileDescriptor;

	/**
	 * Arduinoからのデータ受信用のStream
	 */
	private FileInputStream mInputStream;

	/**
	 * Arduinoへのデータ送信用のStream
	 */
	private FileOutputStream mOutputStream;

	/**
	 * valueを表示するTextView
	 */
	private TextView valueText;

	/**
	 * USBの接続用のBroadcast Receiver
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive");
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbAccessory accessory = UsbManager.getAccessory(intent);
					if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						openAccessory(accessory);
					} else {
						Log.d(TAG, "permission denied for accessory " + accessory);
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

	/**
	 * Activityの起動とともに呼び出される
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		mUsbManager = UsbManager.getInstance(this);
		mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
		registerReceiver(mUsbReceiver, filter);

		if (getLastNonConfigurationInstance() != null) {
			mAccessory = (UsbAccessory) getLastNonConfigurationInstance();
			openAccessory(mAccessory);
		}

		setContentView(R.layout.main);

		// 受信した値を表示するためのTextViewをプログラムに取り込む
		valueText = (TextView) findViewById(R.id.valueText);

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i(TAG, "onRetainNonConfigurationInstance");
		if (mAccessory != null) {
			return mAccessory;
		} else {
			return super.onRetainNonConfigurationInstance();
		}
	}

	/**
	 * アプリの起動直前に呼びだされる(onCreate()->onResume()->起動)
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
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
						mUsbManager.requestPermission(accessory, mPermissionIntent);
						mPermissionRequestPending = true;
					}
				}
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
	}

	/**
	 * アプリが一時停止した場合に呼び出される
	 */
	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause");
		closeAccessory();
	}

	/**
	 * アプリが終了する際に呼び出される
	 */
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		unregisterReceiver(mUsbReceiver);
		super.onDestroy();
	}

	/**
	 * Open Accessoryに接続した際
	 * 
	 * @param accessory
	 */
	private void openAccessory(UsbAccessory accessory) {
		Log.i(TAG, "openAccessory");
		mFileDescriptor = mUsbManager.openAccessory(accessory);
		if (mFileDescriptor != null) {
			mAccessory = accessory;
			FileDescriptor fd = mFileDescriptor.getFileDescriptor();
			mInputStream = new FileInputStream(fd);
			mOutputStream = new FileOutputStream(fd);
			Thread thread = new Thread(null, this, "DemoKit");
			thread.start();
			Log.d(TAG, "accessory opened");
		} else {
			Log.d(TAG, "accessory open fail");
		}
	}

	/**
	 * Open Accessoryから切断された場合
	 * 
	 * @param accessory
	 */
	private void closeAccessory() {

		Log.i(TAG, "closeAccessory");
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

	/**
	 * 接続中に呼ばれ続ける
	 */
	public void run() {
		Log.i(TAG, "run");
		int ret = 0;
		byte[] buffer = new byte[16384];

		while (ret >= 0) {
			try {
				ret = mInputStream.read(buffer);
			} catch (IOException e) {
				break;
			}

			// 取得した値をHandlerに渡し描画
			// run()のでTextViewに値を渡し描画するとエラーで落ちる
			Message valueMsg = new Message();
			valueMsg.obj = "msg[0]:" + buffer[0] 
						+ " msg[1]:" + buffer[1] 
						+ " msg[2]:" + buffer[2];
			mHandler.sendMessage(valueMsg);
		}
	}

	/**
	 * 描画処理はHandlerでおこなう
	 */
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String valueStr = (String) msg.obj;
			valueText.setText(valueStr);
		}
	};
}
