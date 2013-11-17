package com.nudge.reminder;

import java.util.List;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

public interface Remindable extends Parcelable {

	public void schedule(Context context, Reminder reminder,/*List<Long> triggerDates*/long triggerTime, PendingIntent alarmSenderIntent);
	public void unschedule();
	public String getNotificationTitle();
	public String getNotificationContent();
	public int getNotificationResourceDrawableId();
}
