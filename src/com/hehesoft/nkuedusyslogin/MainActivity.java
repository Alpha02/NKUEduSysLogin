package com.hehesoft.nkuedusyslogin;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.hehesoft.nkuedusyslogin.R;

public class MainActivity extends Activity {

	Bitmap valicodeBitmap;
	ImageView valicodeView;
	WebLogger myLogger;
	EditText EdText_user;
	EditText EdText_password;
	EditText EdText_valicode;
	ProgressDialog prgDlg;
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(msg.what==0x123){
				valicodeView.setImageBitmap(valicodeBitmap);
			}	
			if(msg.what==0x124){
					prgDlg.setMax(100);
					prgDlg.setTitle("«Î…‘µ»");
					prgDlg.setMessage(myLogger.progress_str);
					prgDlg.setIndeterminate(false);
					prgDlg.setCancelable(false);
					prgDlg.setProgress(myLogger.progress_int);
					prgDlg.show();
			}	
			if(msg.what==0x125){
				prgDlg.dismiss();
			}			
			if(msg.what==0x126){
				Toast toast=Toast.makeText(MainActivity.this,myLogger.toast_str, Toast.LENGTH_SHORT);
				toast.show();
			}
				
		}
		
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		valicodeView=(ImageView)findViewById(R.id.imageView1);
		EdText_user=(EditText)findViewById(R.id.editText_user);
		EdText_password=(EditText)findViewById(R.id.editText_password);
		EdText_valicode=(EditText)findViewById(R.id.editText_valicode);
		prgDlg=new ProgressDialog(MainActivity.this);
		myLogger=new WebLogger();
		myLogger.myActivity=this;
		new Thread(){
			public void run(){
				try{
					myLogger.init();

				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}.start();
		EdText_user.addTextChangedListener(new TextWatcher(){
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) { 
                 
            } 
 
            @Override 
            public void onTextChanged(CharSequence s, int start, int before, 
                    int count) { 
            } 
			public void afterTextChanged(Editable s){
				if (myLogger.logined){
					myLogger.logined=false;
					new Thread(){
						public void run(){
							myLogger.init();
						}
					}.start();
				}
			}
			
		} );
		Button bn_getScore=(Button)findViewById(R.id.button_getScore);
		Button bn_evaluate=(Button)findViewById(R.id.button_evaluateTeacher);
		bn_getScore.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				new Thread(){
					public void run(){
						try{
							updateLoginInfos();
							if(myLogger.getScore()){
					        Bundle data=new Bundle();
						        data.putCharSequence("res_page", myLogger.res_page);
						        Intent intent=new Intent(MainActivity.this, ShowScoreActivity.class);
						        intent.putExtras(data);
						        startActivity(intent);	
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}.start();
			}
			
		});	
		bn_evaluate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				new Thread(){
					public void run(){
						try{
							updateLoginInfos();
							myLogger.evaluateTeacher();
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
				}.start();
			}
			
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void updateLoginInfos(){
		myLogger.setUser(EdText_user.getText().toString());
		myLogger.setPassword(EdText_password.getText().toString());
		myLogger.setValicode(EdText_valicode.getText().toString());
	}
	
	
}
