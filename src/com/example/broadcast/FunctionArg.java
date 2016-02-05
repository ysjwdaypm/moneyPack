package com.example.broadcast;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class FunctionArg {

	
	public static final String MONEY_RECIVER = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    
	public static final String MONEY_DETAIL = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    
	public static final String LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI";
    
	public static final String SEND_MONEY_PACK = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI";
    
	public static final String PAY_MONEY_PACK = "com.tencent.mm.plugin.wallet.pay.ui.WalletPayUI";
    
	//------------------------------------------------
	public static final String TAG = "Jackie"; 
	//--------------------------------------
	
	public static final String FROM_MONEY_DETAIL = "1";
	public static final String FROM_CHAT_ITEM	 = "2";
	
	/**
	 * 进入聊天界面方式
	 * 1 ： 从红包详情
	 * 2 ： 从聊天条目
	 */
	private static String enterToLauncherUIType = "";
	
	public static String getEnterToLauncherUIType(){return enterToLauncherUIType;}
	
	/**
	 * 是否监测界面变化
	 */
	public static Boolean watchWindowStatus = true;
	
	/**
	 * 是否监通知栏变化
	 */
	public static Boolean watchNotification = true;
	
	/**
	 * 是否尝试发送红包
	 * 此条件成立的时候才要检测自己发出去的红包（也就是抢自己的）
	 */
	public static Boolean tryToSendMoneyPack = false;
	
	
	/**
	 * 是否第一次进入支付界面
	 */
	public static Boolean firstToPayWindow = true;
	
	/**
	 * 是否开启语音播报
	 */
	public static Boolean speedEnabled = true;
	
	
	/**
	 * 是否已经查看了红包信息
	 */
	public static Boolean hasShowMoneyDetails = false;
	
	public static Boolean needContinueWatchLaunchUI = false;
	//------------------------------------
	public static TextToSpeech mTextToSpeech = null;
	
	public static final String GET_MONEY_ID = "com.tencent.mm:id/e4";
	
	public static final String WX_MONEY_ID = "com.tencent.mm:id/e5";
	
	/**
	 * 本机是否有语音
	 */
	private static Boolean hasLanguage = false;
	
	private static Locale[] languag;
    public static int languagIndex = 0;
    
    public static Activity context = null;
    public static void initSpeeh(Activity ct)
    {

    	context = ct;
    	languag = new Locale[3];
    	languag[0] = Locale.CHINA;
    	languag[1] = Locale.CHINESE;
    	languag[2] = Locale.ENGLISH;
    	languagIndex = 0;
    	 //实例并初始化TTS对象
    	try{
    		Toast.makeText(context, "开始初始化语音", Toast.LENGTH_SHORT).show();
    		mTextToSpeech = new TextToSpeech(ct, new TextToSpeech.OnInitListener() {
    			
    			@Override
    			public void onInit(int status) {
    				// TODO Auto-generated method stub
    				if(status == TextToSpeech.SUCCESS){
    					setSpeechLanguage();
    				}
    				else
    				{
    					Toast.makeText(context, "初始化语音失败", Toast.LENGTH_SHORT).show();
    				}
    			}
    		});  
    	}
    	catch(Exception e){
    		Toast.makeText(context, "初始化语音异常", Toast.LENGTH_SHORT).show();
    		
    		Log.d(TAG, "error---->" + e.toString());  
    		if(ct == null){
    			Log.d(TAG, "ct == null---->");
    		}
    	}
    	finally{
//    		Toast.makeText(context, "初始化语音已经执行", Toast.LENGTH_SHORT).show();
    	}
    }
    
    private static void setSpeechLanguage(){
    	hasLanguage = false;
    	int supported = mTextToSpeech.setLanguage(languag[languagIndex]);
    	if((supported != TextToSpeech.LANG_AVAILABLE)&&(supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)){
    		StringBuffer buff = new StringBuffer();
    		buff.append("languagIndex").append(languagIndex).append("len").append(languag.length);
    		displayToast("不支持当前语言！" + buff.toString());
    		languagIndex++;
    		if(languagIndex < languag.length){
    			setSpeechLanguage();
    		}
    		
    	}
    	else
    	{
    		hasLanguage = true;
    		displayToast("语音播报开启成功");
    		Language.setLanguageIndex(languagIndex);
    		speak(Language.getLanguage(Language.OPEN_VOVOCE,""));
    	}
    	
    }
	
	public static void speak(String info){
		Log.d(TAG, "speack :" + info);
    	if(!speedEnabled)return;
    	if(hasLanguage && mTextToSpeech != null){
    		mTextToSpeech.speak(info, TextToSpeech.QUEUE_FLUSH, null);
    		Log.w(TAG, "speack successfull");
    	}
    	else
    	{
    		if(!hasLanguage){
    			Log.w(TAG, "no language");
    		}
    		
    		if(mTextToSpeech == null){
    			Log.w(TAG, "no mTextToSpeech");
    		}
    	}
	}
	
	
	public static void speakCN(String Info){
		if(Language.getLModel().equals(Language.MODEL_CN)){
			speak(Info);
		}
	}
	//------------------------------
	public static void displayToast(String info){
    	if(context != null)
    		Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }
	//-----------------------------------
	private static String lastWindowName = "";
	
	public static void recodeCurWindow(String windowName){
		/**
		 * 上一个记录的界在收到红包界面（这里可要拆红包 或是红包已经抢完）和红包详情界面（到了这里你当然是抢到红包了）
		 * 还有发红包 支付红包
		 */
		if(windowName.equals(LAUNCHER_UI)){
			if(lastWindowName.equals(MONEY_DETAIL) || lastWindowName.equals(MONEY_RECIVER)){
				enterToLauncherUIType = FROM_MONEY_DETAIL;
			}
			else{
				if(lastWindowName.equals(LAUNCHER_UI))
					enterToLauncherUIType = FROM_CHAT_ITEM;
				else if(!lastWindowName.equals(""))
					enterToLauncherUIType = FROM_MONEY_DETAIL;
			}
			
			if(tryToSendMoneyPack){
				enterToLauncherUIType = FROM_CHAT_ITEM;
			}
		}
		
		lastWindowName = windowName;
//		Boolean sendMoneyPack = lastWindowName.equals(SEND_MONEY_PACK) || lastWindowName.equals(PAY_MONEY_PACK);
		
//		if(lastWindowName.equals(LAUNCHER_UI) || tryToSendMoneyPack){
//			enterToLauncherUIType = FROM_CHAT_ITEM;
//		}
//		else
//		{
//			enterToLauncherUIType = FROM_MONEY_DETAIL;
//		}
		
		Log.w(TAG, "current enterToLauncherUIType = " + enterToLauncherUIType);
	}
	
	
	
	private static Boolean _hasAuthority = false;
	
	public static Boolean getAuthority(){return _hasAuthority;}
	
	/**
	 * 检测权限
	 */
	public static void checkAuthority(String accessId,Context ct){
		_hasAuthority = true;
		 //now 
//		_hasAuthority = false;
//		
//		new HTTP().send("http://money.maple.com/checkAuthority.php?accessId="+accessId,new HTTP.Delegate() {
//			
//			@Override
//			public void execute(String ret) {
//				// TODO Auto-generated method stub
//				JSONParser json = new JSONParser(ret);
//				if(json.getStringByKey("ret") == "1"){
//					_hasAuthority = true;
//				}
//			}
//		});
//		String telphoneNumber = getPhoneNumber(ct);
//		_hasAuthority = validTelelPhoneNumber.contains(telphoneNumber);
	}
	
	
	private static String validTelelPhoneNumber = "15316167160|15316789113|13667008725|13916403296|15070269998|";
	
	private static String getPhoneNumber(Context ct){
		TelephonyManager tm = (TelephonyManager)ct.getSystemService(Context.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();  
		  
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());  
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());  
        sb.append("\nLine1Number = " + tm.getLine1Number());  
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());  
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());  
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());  
        sb.append("\nNetworkType = " + tm.getNetworkType());  
        sb.append("\nPhoneType = " + tm.getPhoneType());  
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());  
        sb.append("\nSimOperator = " + tm.getSimOperator());  
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());  
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());  
        sb.append("\nSimState = " + tm.getSimState());  
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());  
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());  
        String ret = sb.toString();
		return tm.getLine1Number();
	}
}
