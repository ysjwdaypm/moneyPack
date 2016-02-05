package com.example.broadcast;

import java.io.DataOutputStream;
import java.io.IOException;

import com.example.broadcast.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	public int MY_DATA_CHECK_CODE = 1234;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 startBtn = (Button) findViewById(R.id.start);    
	        startBtn.setOnClickListener(new View.OnClickListener() {    
	            @Override    
	            public void onClick(View v) {    
	                try {    
	                    //打开系统设置中辅助功能    
	                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);    
	                    startActivity(intent);    
	                    Toast.makeText(MainActivity.this, "找到抢红包，然后开启服务即可", Toast.LENGTH_LONG).show();    
	                } catch (Exception e) {    
	                    e.printStackTrace();    
	                }    
	            }    
	        });    
	        
	        eft = (ToggleButton)findViewById(R.id.tbtn);
	        eft.setChecked(false);
	        eft.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
//					EnvelopeService.needSpeck = eft.isChecked();
					FunctionArg.speedEnabled = eft.isChecked();
					if(eft.isChecked()){
						Intent checkIntent = new Intent();  
						checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);  
						startActivityForResult(checkIntent, MY_DATA_CHECK_CODE); 
						
					}
					
				}
			});
	        
	     findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				FunctionArg.needContinueWatchLaunchUI = false;
				if(!FunctionArg.needContinueWatchLaunchUI){
					FunctionArg.displayToast("关闭强行检测红包");
				}
			}
		});
	        
	        /**
	         * 
	         */
	        FunctionArg.checkAuthority("admin",this);
	        
//	        FunctionArg.initSpeeh(this);
	        
	}
	
	
	 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MY_DATA_CHECK_CODE) {
			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
				FunctionArg.initSpeeh(this);
			}
			else
			{
				 Intent installIntent = new Intent();  
				 installIntent.setAction(  
				 TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);  
				 startActivity(installIntent);  
			}
		}
	}
	
	
//	BroadCastHelper helper = null;
	
	private Button startBtn;    
	
	private ToggleButton eft;
	
	private long lastBackTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) 
		 {
			 long now = System.currentTimeMillis();
			 if(lastBackTime == 0){
				 lastBackTime = now;
				 FunctionArg.displayToast("再按一次退出");
			 }
			 else
			 {
				 if((now - lastBackTime) < 500){
					 this.finish();
					 System.exit(0);
				 }
				 else
				 {
					 FunctionArg.displayToast("" + (now - lastBackTime));
				 }
				 lastBackTime = now;
			 }
		 }
		return false;
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
//		unregisterReceiver(helper);
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
