package com.example.broadcast;

public class Language {
	
	public static final String OPEN_VOVOCE = "0";
	
	public static final String GET_MONEY = "1";
	
	public static final String NO_MONEY_PACK_TIP = "2";
	
	public static final String MISSING_PACK = "3";
	
	public static final String SEND_PACK = "4";
	
	public static final String BAD_MONEY_PACK = "5";
	//----------------------------------
	public static final String MODEL_CN = "cn";
	
	public static final String MODEL_EN = "en";
	//-----------------------------------
	
	private static final String OPEN_SUCC_CN = "语音播报开启成功";
	
	private static final String OPEN_SUCC_EN = "open vovice successful";
	
	private static final String MONEY_COM_CN = "收到一个新的红包";
	
	private static final String MONEY_COM_EN = "get a new money package";
	
	private static final String NO_MONEY_PACK_CN = "当前没有可以抢夺的红包";
	
	private static final String NO_MOMEY_PACK_EN = "no money package can be open";
	
	private static final String MONEY_MISS_CN = "奶奶的，一不小心又错过了好几亿呀";
	
	private static final String MONEY_MISS_EN = "miss momey package";
	
	private static final String SEND_MONEY_PACK_CN = "你确定你要给朋友信发红包";
	
	private static final String SEND_MONEY_PACK_EN = "are you sure to send money package to friends";
	
	
	private static final String BAD_MONEY_PACK_CN = "发了一个假的红包数据";
	
	private static final String BAD_MONEY_PACK_EN = "someone send a bad money pack";
	
	private static String lModel = "cn";
	
	//---------------------------------
	public static final String Authority_forbid = "没有使用权限，请联系qq 2784656421";
	
	public static void setLanguageIndex(int index){
		lModel = MODEL_CN;
		if(index == 2){
			lModel = MODEL_EN;
		}
	}
	
	public static String getLModel(){return lModel;}
	
	public static String getLan(String cn,String money){
		if(lModel.equals(MODEL_CN))
			return cn;
		return "get " + money;
	}
	
	/**
	 * 有红包提醒
	 * @param from
	 * @return
	 */
	public static String getNotifity(String from){
		if(lModel.equals(MODEL_CN))
			return from + "发了一个红包";
		return MONEY_COM_EN;
	}
	
	public static String getLanguage(String type,String extra){
		String ret = "";
		switch (type) {
		case OPEN_VOVOCE:
			if(lModel.equals(MODEL_CN))
				ret= OPEN_SUCC_CN;
			else
				ret = OPEN_SUCC_EN;
			break;
			
		case GET_MONEY:
			if(lModel.equals(MODEL_CN))
				ret= MONEY_COM_CN;
			else
				ret = MONEY_COM_EN;
			break;
			
		case NO_MONEY_PACK_TIP:
			if(lModel.equals(MODEL_CN))
				ret= NO_MONEY_PACK_CN;
			else
				ret = NO_MOMEY_PACK_EN;
			break;
			
		case MISSING_PACK:
			if(lModel.equals(MODEL_CN))
				ret= MONEY_MISS_CN;
			else
				ret = MONEY_MISS_EN;
			break;	
			
		case BAD_MONEY_PACK:
			if(lModel.equals(MODEL_CN))
				ret= extra + BAD_MONEY_PACK_CN;
			else
				ret = BAD_MONEY_PACK_EN;
			break;		
			
		case SEND_PACK:
			if(lModel.equals(MODEL_CN))
				ret= SEND_MONEY_PACK_CN;
			else
				ret = SEND_MONEY_PACK_EN;
			break;
			
		default:
			break;
		}
		
		return ret;
	}
}
