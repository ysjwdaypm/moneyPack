package com.example.broadcast;

import java.util.Iterator;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MService  extends AccessibilityService{

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		if (event.getPackageName().equals(getPackageName())) {
            return;
        }
      
        System.out.println("pkgname " + event.getPackageName());
        System.out.println("classname " + event.getClassName());
        System.out.println("action      : " + event.getAction());
        List<CharSequence> text = event.getText();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.size(); i++) {
            builder.append(text.get(i));
            System.out.println("t " + text.get(i));
        }
//        speech.speak(builder.toString(), TextToSpeech.QUEUE_FLUSH, null);
 
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            System.out.println(" null ");
            return;
        }
        System.out.println("count " + source.getChildCount());
        Bundle extras = source.getExtras();
        Iterator<String> iterator = extras.keySet().iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            Object obj = extras.get(next);
            System.out.println(" . " + next + " . " + obj);
        }
	}

	
	public void register(Context ct){
		Intent in = new Intent(ct,MService.class);
		in.setAction("android.accessibilityservice.AccessibilityService");
//		filter.addAction("android.accessibilityservice.AccessibilityService");
		ct.startService(in);
	}
	
	@Override
	protected void onServiceConnected() {
		// TODO Auto-generated method stub
		super.onServiceConnected();
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
	    info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
	    info.notificationTimeout = 100;
	    info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
	    setServiceInfo(info);
	    
	    System.out.println("-----------onServiceConnected");
	}
	
	
	
	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		System.out.println("-----------interrupt");
	}

}
