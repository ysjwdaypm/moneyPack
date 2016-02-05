package com.example.broadcast;

import android.R.integer;
import android.accessibilityservice.AccessibilityService;  
import android.annotation.TargetApi;  
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Notification;  
import android.app.PendingIntent;  
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;  
import android.os.Handler;  
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.util.Log;  

import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;  
import android.view.accessibility.AccessibilityManager;  
import android.view.accessibility.AccessibilityNodeInfo;  
import android.widget.Toast;  
  
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;  
import java.util.Locale;
import android.os.IBinder;


  
/** 
 * <p>Created by Administrator</p> 
 * <p/> 
 * 抢红包外挂服务 
 */  
public class EnvelopeService extends AccessibilityService {  
  
    static final String TAG = "Jackie";  
  
    /** 
     * 微信的包名 
     */  
    static final String WECHAT_PACKAGENAME = "com.tencent.mm";  
    /** 
     * 红包消息的关键字 
     */  
    static final String ENVELOPE_TEXT_KEY = "[微信红包]";  
  
    Handler handler = new Handler();  
    
    
    public static final String OPEN_MONEY_PACK = "开";
    
    public static final String OPEN_MONEY_PACK_COM = "開";
    
    public static final String CHA_MONEY_PACK = "拆红包";
    
    private static String openMoneyPackType = "拆红包";
    
    private String moneyPackFrom = "";
    /**
     * 红包描述
     */
    private String moneyDesc = "";
    //-------------------- check pack- ----------------  
    @Override  
    public void onAccessibilityEvent(AccessibilityEvent event) {  
    	if(!FunctionArg.getAuthority()){
    		FunctionArg.displayToast(Language.Authority_forbid);
    		return;
    	}
    	
        final int eventType = event.getEventType();  
        Log.d(TAG, "事件---->" + event);  
        this.currentWindowName = "";
        this.moneyPackFrom = "";
        this.moneyDesc = "";
        //通知栏事件  
        if (eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {  
        	if(FunctionArg.watchNotification)
        	{
        		List<CharSequence> texts = event.getText();  
                if (!texts.isEmpty()) {  
                    for (CharSequence t : texts) {  
                        String text = String.valueOf(t);  
                        if (text.contains(ENVELOPE_TEXT_KEY)) {  
                        	
                        	FunctionArg.needContinueWatchLaunchUI = false;
         
                            String[] moneyList = text.split(":");
                            moneyPackFrom = moneyList[0];
                            moneyDesc = moneyList[1];
                            Log.d(TAG, "from = " + moneyPackFrom + " , moneyDesc = " + moneyDesc); 
                            openNotification(event);
                            break;  
                        }  
                    }  
                } 
        	}
             
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {  
            if(FunctionArg.watchWindowStatus)
            {
            	openEnvelope(event);  
            }
        }  
    }  
  
    /*@Override 
    protected boolean onKeyEvent(KeyEvent event) { 
        //return super.onKeyEvent(event); 
        return true; 
    }*/  
  
    
//    private static EnvelopeService instance;
    
    @Override
    public void onCreate() {
    	// TODO Auto-generated method stub
    	super.onCreate();
    	Toast.makeText(this, "开启抢红包服务", Toast.LENGTH_SHORT).show();  
    }
    
    @Override  
    public void onInterrupt() {  
        Toast.makeText(this, "中断抢红包服务", Toast.LENGTH_SHORT).show();  
    }  
  
    @Override  
    protected void onServiceConnected() {  
        super.onServiceConnected();  
        Toast.makeText(this, "连接抢红包服务", Toast.LENGTH_SHORT).show();  
    }  
  
    public static void sendNotificationEvent(Context ct) {  
        AccessibilityManager manager = (AccessibilityManager) ct.getSystemService(ACCESSIBILITY_SERVICE);  
        if (!manager.isEnabled()) {  
            return;  
        }  
        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);  
        event.setPackageName(WECHAT_PACKAGENAME);  
        event.setClassName(Notification.class.getName());  
        CharSequence tickerText = ENVELOPE_TEXT_KEY;  
        event.getText().add(tickerText);  
        manager.sendAccessibilityEvent(event);  
    }  
  
    /** 
     * 打开通知栏消息 
     */  
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)  
    private void openNotification(AccessibilityEvent event) {  
        if (event.getParcelableData() == null || !(event.getParcelableData() instanceof Notification)) {  
            return;  
        }  
        
        FunctionArg.recodeCurWindow(FunctionArg.LAUNCHER_UI);
        //以下是精华，将微信的通知栏消息打开  
        Notification notification = (Notification) event.getParcelableData();  
        PendingIntent pendingIntent = notification.contentIntent;  

        try {  
            pendingIntent.send();
             
        } catch (PendingIntent.CanceledException e) {  
            e.printStackTrace();  
        }  
    }  
    
    
    private String currentWindowName = "null";
  
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)  
    private void openEnvelope(AccessibilityEvent event) {  
    	Log.w(TAG, "openEnvelope curWindow:" + event.getClassName());
    	
    	currentWindowName = (String)event.getClassName();
    	
        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(event.getClassName())) {  
            //点中了红包，下一步就是去拆红包  
        	FunctionArg.recodeCurWindow(FunctionArg.MONEY_RECIVER);
            checkKey1();  
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {  
            //拆完红包后看详细的纪录界面  
            //nonething 
        	FunctionArg.recodeCurWindow(FunctionArg.MONEY_DETAIL);
        	if(!FunctionArg.hasShowMoneyDetails)
        	{
        		this.showMoneyPack();
        	}
        	
        } else if ("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {  
            //在聊天界面,去点中红包  
        	FunctionArg.hasShowMoneyDetails = false;
        	FunctionArg.recodeCurWindow(FunctionArg.LAUNCHER_UI);
        	
        	/**
        	 * 有人发了 [微信红包] 文本 想要破坏抢红包工具
        	 */
        	if(!this.moneyPackFrom.equals("") && !this.hasValidMoneyPack())
        	{
//        		this.simulateKeyEvent();
        		FunctionArg.needContinueWatchLaunchUI = true;
        		this.continueWatchMoneyPack();
        		FunctionArg.speak(Language.getLanguage(Language.BAD_MONEY_PACK, this.moneyPackFrom));
        		this.moneyPackFrom = "";
        		return;
        	}
        	
        	
        	//这步需要多测测呀
        	if(FunctionArg.getEnterToLauncherUIType().equals(FunctionArg.FROM_MONEY_DETAIL))
        	{
        		if(this.checkMoneyPack())
        			FunctionArg.speak(Language.getLanguage(Language.NO_MONEY_PACK_TIP,""));
        	}
        	else
        	{
                checkKey2(); 
        	}
        	 
        }
        else if(currentWindowName.equals(FunctionArg.PAY_MONEY_PACK)){
        	if(FunctionArg.firstToPayWindow)
        	{
        		Toast.makeText(this, Language.getLanguage(Language.SEND_PACK,""), Toast.LENGTH_SHORT).show();
        		FunctionArg.speak(Language.getLanguage(Language.SEND_PACK,""));
            	FunctionArg.tryToSendMoneyPack = true;
            	FunctionArg.recodeCurWindow(FunctionArg.PAY_MONEY_PACK);
            	FunctionArg.firstToPayWindow = false;
        	}
        }
        else if(currentWindowName.equals(FunctionArg.SEND_MONEY_PACK))
        {
        	FunctionArg.recodeCurWindow(FunctionArg.SEND_MONEY_PACK);
        	FunctionArg.firstToPayWindow = true;
        }
        else{
        	if(currentWindowName.contains("plugin")){
        		FunctionArg.recodeCurWindow(currentWindowName);
        	}
        }
    }  
    
    
    
    private void showMoneyPack(){
    	FunctionArg.hasShowMoneyDetails = true;
    	/**
    	 * 关闭了语音播报就不需要这个了
    	 */
    	if(!FunctionArg.speedEnabled)return;
    	
    	AccessibilityNodeInfo nodeinfo = this.getRootInActiveWindow();
    	if(nodeinfo != null){
    		String from = "";
//    		List<AccessibilityNodeInfo> list = nodeinfo.findAccessibilityNodeInfosByText("元");
//    		if(list.size() > 0){
//    			AccessibilityNodeInfo parent = list.get(0).getParent();
//    			if(parent != null){
//    				AccessibilityNodeInfo moneyNode = parent.getChild(0);
//    				if(moneyNode != null){
//    					from =  "领取了" + moneyNode.getText();
//    				}
//    			}
//    		}
    		//com.tencent.mm:id/b4a //from
    		//com.tencent.mm:id/b4e //money
    		
    		List<AccessibilityNodeInfo> list = nodeinfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4a");
    		if(list.size() > 0){
    			AccessibilityNodeInfo moneyNode = list.get(0);
    			from =  "抢夺了" + moneyNode.getText();
    			List<AccessibilityNodeInfo> listMoney = nodeinfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b4e");
    			if(listMoney.size() > 0){
    				String money = "" + listMoney.get(0).getText();
    				String ret = from + money + "元";
    				Toast.makeText(this, ret, Toast.LENGTH_SHORT).show();
        			FunctionArg.speak(Language.getLan(ret, money));
    			}
    			else
    			{
    				
    			}
    			
    		}
    		
    		
//    		return;
    		
//    		List<AccessibilityNodeInfo> list = nodeinfo.findAccessibilityNodeInfosByText("的红包");
//    		if(list.size() > 0){
//    			AccessibilityNodeInfo moneyNode = list.get(0);
//    			from =  "抢夺了" + moneyNode.getText();
//    		}
//    		
//    		if(!from.equals("")){
//    			List<AccessibilityNodeInfo> listMoney = nodeinfo.findAccessibilityNodeInfosByText(".");
//    			String money = "";
//    			if(listMoney.size() > 0){
//    				try
//    				{
//    					if(from.contains("."))
//    						money = "" +  listMoney.get(1).getText();
//    					else
//    						money = "" + listMoney.get(0).getText();
//    				}
//    				catch(Exception e){
//    				}
//    				
//    			}
//    			
//    			String ret = from;
//    			if(!money.equals("")){
//    				ret = from + money + "元";
//    			}
//    			
//    			if(!money.contains("元"))
//    			{
//    				Toast.makeText(this, ret, Toast.LENGTH_SHORT).show();
//        			FunctionArg.speak(Language.getLan(ret, money));
//    			}
//    			
//    		}
//    		
    	}
    }
    
    private List<AccessibilityNodeInfo> getButton(AccessibilityNodeInfo node){
    	
    	List<AccessibilityNodeInfo> rect= new ArrayList<AccessibilityNodeInfo>();
    	
    	int childCount = node.getChildCount();
    	for (int i = 0; i < childCount; i++) {
			AccessibilityNodeInfo child = node.getChild(i);
			if(child.getChildCount() > 0){
				rect.addAll(getButton(child));
			}
			else
			{
				if(child.getClassName().equals("android.widget.Button")){
					rect.add(child);
				}
			}
			
		}
    	
    	return rect;
    }
  
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)  
    private void checkKey1() {  
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  
        if (nodeInfo == null) {  
            Log.w(TAG, "rootWindow为空");  
            return;  
        }         
        
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(CHA_MONEY_PACK); 
        if(list.size() == 0){
        	list = this.getButton(nodeInfo);
        }
        
        if(list.size() > 0){
        	for (AccessibilityNodeInfo n : list) {  
                n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
              }  
        }
        else
        {
        	FunctionArg.speak(Language.getLanguage(Language.MISSING_PACK,""));
        }
        
    }  

    
    /**
     * 是否有效的红包(怕有人捣乱)
     * @param list
     * @return
     */
    private Boolean hasValidMoneyPack(){
    	
    	AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  
        
        if (nodeInfo == null) {  
            Log.w(TAG, "checkKey2 rootWindow为空");  
            return false;  
        }
        Boolean has = false;
        //"com.tencent.mm:id/e4"//领取
        //"com.tencent.mm:id/e5"//微信红包
        return  nodeInfo.findAccessibilityNodeInfosByViewId(FunctionArg.GET_MONEY_ID).size() > 0 ||
        		 nodeInfo.findAccessibilityNodeInfosByViewId(FunctionArg.WX_MONEY_ID).size() > 0;
//        List<AccessibilityNodeInfo> list1 = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/e4");//领取
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
//    	if(list.size()  > 0)
//    	{
//    		for(AccessibilityNodeInfo node : list){
//        		 AccessibilityNodeInfo parent = node.getParent();
//        		
//        		 if(parent != null && parent.getChildCount() > 1){
//        			 has = true;
//        			 break;
//        		 }
//        	 }
//    	}
//    	else
//    	{
//    		list = nodeInfo.findAccessibilityNodeInfosByText(ENVELOPE_TEXT_KEY);
//	       	 if(list.size() == 0)return false;
//	       	 
//	       	 for(AccessibilityNodeInfo node : list){
//	       		 String text = "" + node.getText();
//	       		 if(text.equals(ENVELOPE_TEXT_KEY))
//	       		 {
//	       			 has = true;
//	       		 }
//	         }
//    	}
//    	
//    	return has;
    }
    
    private Boolean checkMoneyPack(){
    	return this.hasValidMoneyPack();
//    	 AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  
//         
//         
//         if (nodeInfo == null) {  
//             Log.w(TAG, "checkKey2 rootWindow为空");  
//             return false;  
//         }
//         
//         List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
//         if(list.size() == 0)
//        	 list = nodeInfo.findAccessibilityNodeInfosByText(ENVELOPE_TEXT_KEY);
//         	 
//         return list.size() > 0;
  
    }
    
    /**
     * 点开聊天里的红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
     private void checkKey2() {  
         AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  

         if (nodeInfo == null) {  
             Log.w(TAG, "checkKey2 rootWindow为空");  
             return;  
         }  
         List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(FunctionArg.GET_MONEY_ID);
     
         FunctionArg.tryToSendMoneyPack = false;
         
         if (list.isEmpty()) {  
             list = nodeInfo.findAccessibilityNodeInfosByViewId(FunctionArg.WX_MONEY_ID);
             for (AccessibilityNodeInfo n : list) {  
             	
             	Log.i(TAG, "-->微信红包:" + n);  
                 n.performAction(AccessibilityNodeInfo.ACTION_CLICK); 
                 
                 break; 
             }  
         } else {  
             //最新的红包领起  
             for (int i = list.size() - 1; i >= 0; i--) {  
                 AccessibilityNodeInfo parent = list.get(i).getParent();  
               
                 if(parent != null)
                 {
                 	Log.i(TAG, "-->领取红包:" + parent.hashCode() + "   " + parent); 
                     parent.performAction(AccessibilityNodeInfo.ACTION_CLICK); 
                     if(!this.moneyPackFrom.equals("")){
                     	FunctionArg.speak(Language.getNotifity(moneyPackFrom));
                     	this.moneyPackFrom = "";
                     }
                     
                     break;
                 }
                 else
                 {
                 	Log.i(TAG, "-->红包不存在"); 
                 }
                 
                 
             }  
         }
         
     } 
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void checkKey2() {  
//        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  
//        
//       
//        if (nodeInfo == null) {  
//            Log.w(TAG, "checkKey2 rootWindow为空");  
//            return;  
//        }  
//        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");
//        
//        /**
//         * 自己发的红包
//         */
//        if(list.isEmpty()){
//        	list = nodeInfo.findAccessibilityNodeInfosByText("查看红包");
//        }
//        else
//        {
//        	for(AccessibilityNodeInfo self : nodeInfo.findAccessibilityNodeInfosByText("查看红包")){
//            	if(FunctionArg.tryToSendMoneyPack)
//            	{
//            		list.add(self);
//            	}
//            }
//        }
//        
//        FunctionArg.tryToSendMoneyPack = false;
//        
//        Boolean hasPack = false;
//        
//        if (list.isEmpty()) {  
//            list = nodeInfo.findAccessibilityNodeInfosByText(ENVELOPE_TEXT_KEY);  
//            for (AccessibilityNodeInfo n : list) {  
//            	
//            	Log.i(TAG, "-->微信红包:" + n);  
//                n.performAction(AccessibilityNodeInfo.ACTION_CLICK); 
//                
//                break; 
//            }  
//        } else {  
//            //最新的红包领起  
//            for (int i = list.size() - 1; i >= 0; i--) {  
//                AccessibilityNodeInfo parent = list.get(i).getParent();  
//              
//                if(parent != null)
//                {
//                	Log.i(TAG, "-->领取红包:" + parent.hashCode() + "   " + parent); 
//                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK); 
//                    if(!this.moneyPackFrom.equals("")){
////                    	FunctionArg.speakCN(moneyPackFrom + "发了一个红包");
//                    	FunctionArg.speak(Language.getNotifity(moneyPackFrom));
//                    }
//                    
//                    break;
//                }
//                else
//                {
//                	Log.i(TAG, "-->红包不存在"); 
//                }
//                
//                
//            }  
//        }
//        
//    }  
    
    
    /**
     * 模拟事件
     * @param keyCode
     */
    private void simulateKeyEvent() {

    }
    
    
//    private Boolean inOutChat(){
//    	if(!this.currentWindowName.equals(FunctionArg.LAUNCHER_UI))return false;
//    	 AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();  
// 
//         if (nodeInfo == null) {  
//             Log.w(TAG, "checkKey2 rootWindow为空");  
//             return false;  
//         }  
//    	
//        Boolean ret = false;
//        ret = nodeInfo.findAccessibilityNodeInfosByText("发现").size() > 0 
//        		&&  nodeInfo.findAccessibilityNodeInfosByText("我").size() > 0
//        		&&  nodeInfo.findAccessibilityNodeInfosByText("通讯录").size() > 0
//        		&& nodeInfo.findAccessibilityNodeInfosByText("微信").size() > 0;
//    	return ret;
//    }
    
    private Boolean doingWatchMoneyPack = false;
    
    /**
     * 持续关注红包
     */
    private void continueWatchMoneyPack(){
    	if(doingWatchMoneyPack)
    	{
    		FunctionArg.speakCN("正在强制检测红包，无需要重复");
    		return;
    	}
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				doingWatchMoneyPack = true;
				// TODO Auto-generated method stub
				while(FunctionArg.needContinueWatchLaunchUI == true && 
						currentWindowName.equals(FunctionArg.LAUNCHER_UI) &&
						!hasValidMoneyPack())
				{
					try 
					{
						Thread.sleep(500);
						Log.i(TAG, " 检测是否有红包"); 
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
				
				if(FunctionArg.needContinueWatchLaunchUI &&
						hasValidMoneyPack()){
					checkKey2(); 
				}
				
				doingWatchMoneyPack = false;
			}
		}).start();
    	
    	/**
    	 * $adb shell input keyevent 4            //模拟返回键（BACK）

$adb shell input keyevent 82          //模拟菜单键（MENU）

$adb shell input keyevent 3            //模拟主页键（HOME）
    	 */
//    	this.adbInstallTheAPP();
    }
    
    private void adbInstallTheAPP(){  
	    //adb push core code  
	    String command = "shell input keyevent 4";  
	    Process process = null;  
	    DataOutputStream os = null;  
	    try {  
	        process = Runtime.getRuntime().exec("shell input keyevent 4");// the phone must be root,it can exctue the adb command  
	        os = new DataOutputStream(process.getOutputStream());  
	        FunctionArg.displayToast(os.toString());
	    } catch (Exception e) {  
	            e.printStackTrace();  
	            FunctionArg.displayToast(e.toString());
	        }  
	} 
}  

