package com.nudge.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import android.support.v4.app.TaskStackBuilder;
import android.view.WindowManager;

public class Reminder implements Parcelable {
	public static final int NOTIFICATION_ID = 001;
	public static final String INTENT = "intent";
	public static final String NOTIFICATION_TITLE = "title";
	public static final String NOTIFICATION_CONTENT = "content";
	public static final String REMINDABLE = "remindable";
	public static final String NUDGE = "remind";
//	private Context mContext;
	private int mReminderType;
	private String eventDate;
	private List<Long> triggerTimes; 
	private List<Intent> backStackIntents = new ArrayList<Intent> ();
	private PendingIntent alarmSender;

	public Reminder() {


	}

	
	public static final Parcelable.Creator<Reminder> CREATOR
				= new Parcelable.Creator<Reminder>() {

					@Override
					public Reminder createFromParcel(Parcel in) {
						// TODO Auto-generated method stub
						return new Reminder(in);
					}

					@Override
					public Reminder[] newArray(int size) {
						// TODO Auto-generated method stub
						return new Reminder[size];
					}
				};
				
 
	private Reminder(Parcel in)  {
		mReminderType = in.readInt(); 
		eventDate = in.readString();
		triggerTimes = in.readArrayList(Long.class.getClassLoader());
		backStackIntents = in.readArrayList(Intent.class.getClassLoader());
		alarmSender = in.readParcelable(PendingIntent.class.getClassLoader());
	}
				
	public void setReminderType(int reminderType) {
		mReminderType = reminderType;
	}

	public int getReminderType() {
		return mReminderType;
	}




	public List<Long> getTriggerTimes() {
		return triggerTimes;
	}

	public void setTriggerTimes(List<Long> triggerTimes) {
		this.triggerTimes = triggerTimes;
	}

	public String getEventDate() {
		return eventDate;
	}

	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}

	public PendingIntent getAlarmSender() {
		return alarmSender;
	}

	public void setAlarmSender(PendingIntent alarmSender) {
		this.alarmSender = alarmSender;
	}


	public List<Intent> getBackStackIntents() {
		return backStackIntents;
	}

	public void setBackStackIntents(List<Intent> backStackIntents) {
		this.backStackIntents = backStackIntents;
	}


	public PendingIntent createAlarmSender(Context context, Remindable remindable, Class<?> serviceName, Bundle bundle, int[] flags) {
//		PendingIntent pd = this.createNotificationContentIntent(context, this.getBackStackIntents());
//		bundle.putParcelable(Reminder.INTENT, pd);
		bundle.putParcelable(Reminder.REMINDABLE, remindable);
		bundle.putParcelable(Reminder.NUDGE, this);
		Intent intent = createIntent(context, serviceName, bundle, flags);
		
		
		PendingIntent alarmSender = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		this.setAlarmSender(alarmSender);
		return alarmSender;
	}

	public void createActivityOnBackStack(Context context, Class<?> activityName, Bundle bundle, int[] flags) {

		Intent intent = createIntent(context, activityName, bundle, flags);
		backStackIntents.add(intent);
	}

	PendingIntent createNotificationContentIntent(Context context, List<Intent> intents) {


		TaskStackBuilder tsb = TaskStackBuilder.create(context);
		Iterator<Intent> iterator = intents.iterator();
		while(iterator.hasNext()) {
			tsb.addNextIntent(iterator.next());
		}
		PendingIntent pendingIntent = tsb.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_CANCEL_CURRENT);

		return pendingIntent;

	}
	
	private Intent createIntent(Context context, Class<?> className, Bundle bundle, int[] flags) {
		Intent intent = new Intent(context, className);
		if(bundle != null)
			intent.putExtras(bundle);
		if(flags != null) {
		for(int i = 0 ; i < flags.length ; i++) {
			intent.addFlags(flags[i]);
		}
		}
		return intent;
	}

	
//	@Override
	public void schedule(Context context, /*Reminder reminder,*//*List<Long> triggerDates*/long triggerTime, PendingIntent alarmSenderIntent) {
		// TODO Auto-generated method stub
		long firstTime;
		int i = 0; 
//		setReminder(reminder);
		
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, /*System.currentTimeMillis() + 30000*/triggerTime, alarmSenderIntent);

	}

	
//	@Override
	public void unschedule() {
		// TODO Auto-generated method stub

	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeInt(mReminderType);
		out.writeString(eventDate.toString());
		out.writeList(triggerTimes);
		out.writeList(backStackIntents);
		out.writeParcelable(alarmSender, 0);

	}


}
