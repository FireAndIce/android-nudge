package com.nudge.reminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	public static String TAG = AlarmReceiver.class.getCanonicalName();
	Context mContext;
	NotificationManager mNM;
	Remindable mRemindable;
	
	long tripId;
	Binder mBinder = new Binder();
//	Bundle mIntents;
	PendingIntent mNotificationContentIntent;
	Reminder mReminder;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
//		if(intent.getAction().equals("intent")) {
			
		
		Class className = intent.getClass();
		Bundle bundle = intent.getExtras();
		
//		mNotificationContentIntent = bundle.getParcelable(Reminder.INTENT);
//		mNotificationContentIntent = intent.getParcelableExtra(Reminder.INTENT);
		mRemindable = bundle.getParcelable(Reminder.REMINDABLE);
		mReminder = bundle.getParcelable(Reminder.NUDGE);
		mNotificationContentIntent = mReminder.createNotificationContentIntent(mContext, mReminder.getBackStackIntents());
		Log.d(TAG, mRemindable.toString());
		Log.d(TAG, "" + tripId);
		wakeUpScreen();
		showNotification();
//		}
	}

	private void wakeUpScreen() {
		PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
//		if(!powerManager.isScreenOn()) {
			WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE, "reminderWake");
			wakeLock.acquire(10000);
		
			
//		}
	}
	
	private void showNotification() {
		mNM = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.getApplicationContext())
												.setSmallIcon(mRemindable.getNotificationResourceDrawableId())
												.setContentTitle(mRemindable.getNotificationTitle())
												.setContentText(mRemindable.getNotificationContent())
												.setTicker(mRemindable.getNotificationTitle() + "\n" + mRemindable.getNotificationContent())
												.setContentIntent(mNotificationContentIntent);
												
		Notification notification = mBuilder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		mNM.notify(Reminder.NOTIFICATION_ID, notification);
		
//		this.stopSelf();
	}


}
