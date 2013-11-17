package com.nudge.reminder;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service {
	public static String TAG = AlarmService.class.getCanonicalName();
	
	NotificationManager mNM;

	long tripId;
	Binder mBinder = new Binder();
//	Bundle mIntents;
	PendingIntent mNotificationContentIntent;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "In onBind.");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		mNotificationContentIntent = intent.getParcelableExtra(Reminder.INTENT);
		
//		tripId = intent.getLongExtra(Constants.DATA, 0);
		
		Log.d(TAG, "In onStartCommand");
		Log.d(TAG, "" + tripId);
		wakeUpScreen();
		showNotification();
		return START_STICKY;
	}

	
	private void wakeUpScreen() {
		PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//		if(!powerManager.isScreenOn()) {
			WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE, "reminderWake");
			wakeLock.acquire(10000);
		
			
//		}
	}
	
	
	private void showNotification() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
												/*.setSmallIcon(R.drawable.briefcase)*/
												.setContentTitle("Hello")
												.setContentText("Hi")
												.setContentIntent(mNotificationContentIntent);
												
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		mNM.notify(Reminder.NOTIFICATION_ID, notification);
		
		this.stopSelf();
	}

/*	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d(TAG, "In onStartCommand");
		return START_STICKY;
	}*/

}
