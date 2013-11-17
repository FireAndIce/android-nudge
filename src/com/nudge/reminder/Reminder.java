package com.nudge.reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;



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

/**
 * This class helps you to create reminders, which when triggered will display notifications.
 * @author Harshal Kshatriya
 *
 */
public class Reminder implements Parcelable {
	
	public static final int NOTIFICATION_ID = 001;
	public static final String INTENT = "intent";
	public static final String NOTIFICATION_TITLE = "title";
	public static final String NOTIFICATION_CONTENT = "content";
	public static final String REMINDABLE = "remindable";
	public static final String NUDGE = "remind";
	public static final String ALARMSENDERS_KEY = "alarmsenders_key";
//	private Context mContext;
	private int mReminderType;
	private String eventDate;
	private List<Long> triggerTimes; 
	private List<Intent> backStackIntents = new ArrayList<Intent> ();
	private HashMap<PendingIntent, Long> alarmSenders = new HashMap<PendingIntent, Long> ();

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
		Bundle bundle = in.readBundle();
		alarmSenders = (HashMap<PendingIntent, Long>) bundle.getSerializable(ALARMSENDERS_KEY);
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

	HashMap<PendingIntent, Long> getAlarmSenders() {
		return alarmSenders;
	}

	void setAlarmSender(HashMap<PendingIntent, Long> alarmSenders) {
		this.alarmSenders = alarmSenders;
	}


	public List<Intent> getBackStackIntents() {
		return backStackIntents;
	}

/**
 * The method adds intents to the backstack.
 * @param backStackIntents List of intents that should start the needed activities on notification click, even after the user has quit the application.
 */
	public void setBackStackIntents(List<Intent> backStackIntents) {
		this.backStackIntents = backStackIntents;
	}

/** 
 * Creates an alarm which when triggered will be broadcasted to <b>alarmReceiver</b> . 
 * @param context 
 * @param remindable The object for which you want to display notification.
 * @param bundle the extras bundle for the alarmSender. The value is <b>null</b> if nothing is to be sent. 
 * @param flags if needed by the alarmSender.
 * @return alarmSender
 */
	public void createAlarmSender(Context context, Remindable remindable, Bundle bundle, int[] flags, List<Long> triggerTimes) {
//		PendingIntent pd = this.createNotificationContentIntent(context, this.getBackStackIntents());
//		bundle.putParcelable(Reminder.INTENT, pd);
		if(bundle == null)
			bundle = new Bundle();
		
		bundle.putParcelable(Reminder.REMINDABLE, remindable);
		bundle.putParcelable(Reminder.NUDGE, this);
		Intent intent = createIntent(context, AlarmReceiver.class, bundle, flags);
		
		Iterator<Long> timeIterator = triggerTimes.iterator();
		while(timeIterator.hasNext()) {
		PendingIntent alarmSender = PendingIntent.getBroadcast(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmSenders.put(alarmSender, timeIterator.next());
		}
//		return alarmSender;
	}


/**
 * 
 * @param context
 * @param activityName Activity to be added to the backstack.
 * @param bundle extras for the intent that will start the activity.
 * @param flags if needed.
 */
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

	//Creates an intent.
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

	
/**
 * Schedules the alarm using the <b>alarmSender</b>
 * @param context
 * @param triggerTime The realtime at which the alarm should go off.
 * @param alarmSender It will send the alarm. It can be created using {@link #createAlarmSender(Context, Remindable, Bundle, int[])} 
 */
	public void schedule(Context context/*, Reminder reminder,*//*List<Long> triggerDates*//*long triggerTime, PendingIntent alarmSender*/) {
		// TODO Auto-generated method stub
		long firstTime;
		int i = 0; 
		if(alarmSenders != null) {
			Iterator<PendingIntent> alarmSenderIterator = alarmSenders.keySet().iterator();
			Iterator<Long> triggerTimeIterator = alarmSenders.values().iterator();
			while(alarmSenderIterator.hasNext() && triggerTimeIterator.hasNext()) {
				scheduleAlarm(context, alarmSenderIterator.next(), triggerTimeIterator.next());
			}
		}

	}

	private void scheduleAlarm(Context context, PendingIntent alarmSender, Long triggerTime) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, /*System.currentTimeMillis() + 30000*/triggerTime, alarmSender);
	}
	

	
//	@Override
	public void unschedule(Context context) {
		// TODO Auto-generated method stub
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		Iterator<PendingIntent> iterator = alarmSenders.keySet().iterator();
		while(iterator.hasNext())
			alarmManager.cancel(iterator.next());
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
		Bundle bundle = new Bundle();
		bundle.putSerializable(ALARMSENDERS_KEY, alarmSenders);
		out.writeBundle(bundle);

	}


}
