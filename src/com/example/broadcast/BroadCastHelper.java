package com.example.broadcast;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BroadCastHelper extends BroadcastReceiver {

	
	private Context ct = null;
	
	private BroadCastHelper instance;
	
	public BroadCastHelper(Context c){
		this.ct = c;
		instance = this;
	}
	
	
	public void refisterAction(String action){
		IntentFilter filter = new IntentFilter();
		filter.addAction(action);
		ct.registerReceiver(instance, filter);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
//		String msg = intent.getStringExtra("msg");
//		
//		String action = intent.getAction();
//		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//		
//		Notification no = new Notification(R.drawable.ic_launcher,"",System.currentTimeMillis());
//		Intent it = new Intent(context,MainActivity.class);
//        PendingIntent contentIntent=PendingIntent.getActivity(context,
//                0, it, 0);
//        no.setLatestEventInfo(context, 
//                "msg", msg, contentIntent);
//        nm.notify(0, no);
	}

}
