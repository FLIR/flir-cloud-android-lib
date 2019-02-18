package com.examples.ffmpeg4android;

import com.examples.ffmpeg4android_demo.R;
import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.ffmpeg4android.ProgressCalculator;
import com.netcompss.loader.LoadJNI;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProgressBarExample extends Activity  {
	
	public ProgressDialog progressBar;
	
	String workFolder = null;
	String demoVideoFolder = null;
	String demoVideoPath = null;
	String vkLogPath = null;
	LoadJNI vk;
	private final int STOP_TRANSCODING_MSG = -1;
	private final int FINISHED_TRANSCODING_MSG = 0;
	private boolean commandValidationFailedFlag = false;
	
	private void runTranscodingUsingLoader() {
		Log.i(Prefs.TAG, "runTranscodingUsingLoader started...");

 		PowerManager powerManager = (PowerManager)ProgressBarExample.this.getSystemService(Activity.POWER_SERVICE);
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK"); 
		Log.d(Prefs.TAG, "Acquire wake lock");
		wakeLock.acquire();

 		EditText commandText = (EditText)findViewById(R.id.CommandText);
 		
 		String commandStr = commandText.getText().toString();


 		///////////// Set Command using code (overriding the UI EditText) /////
 		//String commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -s 320x240 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 /sdcard/videokit/out.mp4";
 		//String[] complexCommand = {"ffmpeg", "-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental","-vcodec", "mpeg4", "-b", "150k", "-s", "320x240","-r", "25", "-ab", "48000", "-ac", "2", "-ar", "22050","/sdcard/videokit/out.mp4"};
 		///////////////////////////////////////////////////////////////////////

		//commandStr = "ffmpeg -y -i /sdcard/videokit/pic2.jpeg /sdcard/videokit/pic2.gif";
		//String[] complexCommand = {"ffmpeg","-y","-ss","0.0","-t","15.0","-loop","1","-framerate","25","-i","/sdcard/videokit/forest3.gif","-i","/sdcard/videokit/in.mp3","-i","/sdcard/videokit/in.mp3","-strict","experimental","-r","25","-b:v","2300000","-ab","64000","-ac","2","-ar","22050","-filter_complex","[1:a]atrim=0:10, aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=mono, asetpts=PTS-STARTPTS [k0]; [1:a]atrim=0:10, aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=mono, asetpts=PTS-STARTPTS [k1]; [k0][k1] concat=n=2:v=0:a=1  [b0];[2:a]aformat=sample_fmts=fltp:sample_rates=44100:channel_layouts=mono, apad [c0];[b0][c0]amerge [mulchl]; [mulchl] pan=stereo|c0=0.2*c0|c1=1.0*c1 [out]","-map","0:v","-map","[out]","-shortest","/sdcard/videokit/output.mp4"};

//		String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/sample.mp4","-strict", "experimental", "-filter_complex",
//				"[0:v]colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131[colorchannelmixed];[colorchannelmixed]eq=1.0:0:1.3:2.4:1.0:1.0:1.0:1.0[color_effect]",
//				"-map", "[color_effect]","-map", "0:a", "-vcodec", "mpeg4","-b", "15496k", "-ab", "48000", "-ac", "2", "-ar", "22050",
//				"/sdcard/videokit/out.mp4"};


		//commandStr =  "ffmpeg -y -ss 0.0 -t 5 -i /sdcard/videokit/testing.mp4 -i /sdcard/videokit/testing.jpg -i /sdcard/videokit/testing.png -strict experimental -r 25 -vcodec mpeg4 -b 2500k -ab 64000 -ac 2 -ar 22050 -filter_complex [1:v]scale=249:-1 [ovr0]; [2:v]scale=616:-1,rotate=-0.19511767:c=none:ow=rotw(-0.19511767):oh=roth(-0.19511767) [ovr1]; [0:v][ovr0] overlay=402.66666:1093.3334 [temp0];[temp0][ovr1] overlay=-20.00001:554.3334 /sdcard/videokit/output.mp4";

//		String[] complexCommand = {"ffmpeg", "-y" ,"-ss", "00:0:2.559","-t","00:0:2.325", "-i", "/sdcard/videokit/testing.mp3","-i","/sdcard/videokit/sample.mp4","-strict","experimental","-filter_complex","[0:a]volume=1.0[a0];[1:a]volume=1.0[a1];[a0][a1]amix=inputs=2:duration=first,pan=stereo|c0<c0+c2|c1<c1+c3[out]",
//		"-map", "1:v", "-map", "[out]", "/sdcard/videokit/out.mp4"};


//		String[] complexCommand = {"ffmpeg","-y","-ss","0.0","-t","13.22","-i","/sdcard/videokit/sample.mp4","-i","/sdcard/videokit/testing.jpg","-i","/sdcard/videokit/testing.png","-strict","experimental","-r","25","-vcodec","mpeg4","-b","2500k","-ab","64000","-ac","2","-ar","22050",
//				"-filter_complex","[1:v]scale=111:-1 [ovr0]; [2:v]scale=208:-1,rotate=-0.21067664:c=none:ow=rotw(-0.21067664):oh=roth(-0.21067664) [ovr1]; [0:v][ovr0] overlay=178.96295:355.55554 [temp0];[temp0][ovr1] overlay=46.22223:75.51852","/sdcard/output.mp4"};

		//String[] complexCommand = {"ffmpeg","-y","-ss","0.0","-t","4.165","-i","/sdcard/videokit/testing.mp4","-loop","1","-i","/sdcard/videokit/testing.png","-ignore_loop","0","-i","/sdcard/videokit/testing.gif","-strict","experimental","-r","25","-b","4500k","-ab","64000","-ac","2","-ar","22050","-filter_complex","[1:v]scale=249:-1 [ovr0]; [2:v]scale=761:-1,rotate=-0.58085257:c=none:ow=rotw(-0.58085257):oh=roth(-0.58085257) [ovr1]; [0:v][ovr0] overlay=402.66666:1093.3334:shortest=1 [temp0];[temp0][ovr1] overlay=-126.00001:238.33331:shortest=1","/sdcard/videokit/output.mp4"};

		//String[] complexCommand = {"ffmpeg","-y","-i","/sdcard/videokit/in.mp3","-af","rubberband=pitch=12","/sdcard/videokit/output.mp3"};

        //String[] complexCommand = {"ffmpeg","-y","-i","/sdcard/videokit/mj.mp4","-vcodec","copy","-af","rubberband=pitch=1.6","/sdcard/videokit/output.mp4"};

		//commandStr =  "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -vcodec libx264 -preset ultrafast -crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 320x240 -aspect 4:3 /sdcard/videokit/out3.mp4";

		//String[] complexCommand = {"ffmpeg", "-y" ,"-i","concat:/sdcard/videokit/in1.mpeg|/sdcard/videokit/in2.mpeg","-codec","copy","/sdcard/videokit/out1.mpeg"};

		//String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/4k_h265.mp4","-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/videokit/out.mp4"};
        //String[] complexCommand = {"ffmpeg","-y" ,"-i", "/sdcard/videokit/4k_h265.mp4","-strict","experimental","-s", "1280x720","-r","30", "-b", "5000k", "/sdcard/videokit/out.mp4"};

		commandStr =  "ffmpeg -y -i /sdcard/videokit/video.mp4 -i /sdcard/videokit/audio.mp3 -strict experimental -map 0:v -map 1:a -c copy -flags +global_header /sdcard/videokit/out.mp4";

		//String[] complexCommand = {"ffmpeg","-y" ,"-ss", "60","-t","10","-i", "/sdcard/videokit/in2.mp3","-ss", "30", "-t", "20", "-i", "/sdcard/videokit/in2.mp3","-filter_complex","[1]adelay=15000|15000[s1];[0][s1]amix=2[sa]","-map", "[sa]", "/sdcard/videokit/amix.mp3"};

		//commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp3 -ar 44100 -ac 2 -ab 64k -f mp3 /sdcard/videokit/out.mp3";

		//commandStr =  "ffmpeg -y -i /sdcard/videokit/in_mp3.mp4 -strict experimental -vn -acodec copy /sdcard/videokit/out.mp3";

		//commandStr =  "ffmpeg -y -i /sdcard/videokit/sample.mp4 -strict experimental -vn -acodec copy -f adts /sdcard/videokit/out.m4a";

        vk = new LoadJNI();
		try {

			// running regular command with validation
			vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, getApplicationContext());

			// running complex command with validation 
			//vk.run(complexCommand, workFolder, getApplicationContext());
			
			// running without command validation
			//vk.run(complexCommand, workFolder, getApplicationContext(), false);

			Log.i(Prefs.TAG, "vk.run finished.");
			// copying vk.log (internal native log) to the videokit folder
			GeneralUtils.copyFileToFolder(vkLogPath, demoVideoFolder);
			
		} catch (CommandValidationException e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
			commandValidationFailedFlag = true;
		
		} catch (Throwable e) {
			Log.e(Prefs.TAG, "vk run exeption.", e);
		}
		finally {
			if (wakeLock.isHeld()) {
				wakeLock.release();
				Log.i(Prefs.TAG, "Wake lock released");
			}
			else{
				Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
			}
		}

		// finished Toast
		String rc = null;
		if (commandValidationFailedFlag) {
			rc = "Command Vaidation Failed";
		}
		else {
			rc = GeneralUtils.getReturnCodeFromLog(vkLogPath);
		}
		final String status = rc;
 		ProgressBarExample.this.runOnUiThread(new Runnable() {
			  public void run() {
				  Toast.makeText(ProgressBarExample.this, status, Toast.LENGTH_LONG).show();
				  if (status.equals("Transcoding Status: Failed")) {
					  Toast.makeText(ProgressBarExample.this, "Check: " + vkLogPath + " for more information.", Toast.LENGTH_LONG).show();
				  }
			  }
			});
	}
	
	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
		  Log.i(Prefs.TAG, "onCreate ffmpeg4android ProgressBarExample");
		  		
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.ffmpeg_demo_client_2);
	      
	      demoVideoFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videokit/";
	  	  demoVideoPath = demoVideoFolder + "in.mp4";
	      
	      Log.i(Prefs.TAG, getString(R.string.app_name) + " version: " + GeneralUtils.getVersionName(getApplicationContext()) );
	      
	      Button invoke =  (Button)findViewById(R.id.invokeButton);
	      invoke.setOnClickListener(new OnClickListener() {
				public void onClick(View v){
					Log.i(Prefs.TAG, "run clicked.");
					runTranscoding();
				}
			});
	      
	      workFolder = getApplicationContext().getFilesDir() + "/";
	      //Log.i(Prefs.TAG, "workFolder: " + workFolder);
	      vkLogPath = workFolder + "vk.log";
	      GeneralUtils.copyLicenseFromAssetsToSDIfNeeded(this, workFolder);
	      GeneralUtils.copyDemoVideoFromAssetsToSDIfNeeded(this, demoVideoFolder);
	      int rc = GeneralUtils.isLicenseValid(getApplicationContext(), workFolder);
	      Log.i(Prefs.TAG, "License check RC: " + rc);
	}
	
	
	
	private Handler handler = new Handler() {
        	@Override
        	public void handleMessage(Message msg) {
        		Log.i(Prefs.TAG, "Handler got message");
        		if (progressBar != null) {
        			progressBar.dismiss();
        			
        			// stopping the transcoding native
        			if (msg.what == STOP_TRANSCODING_MSG) {
        				Log.i(Prefs.TAG, "Got cancel message, calling fexit");
        				vk.fExit(getApplicationContext());
        				
        			
        			}
        		}
        }
    };
	
	public void runTranscoding() {
		  progressBar = new ProgressDialog(ProgressBarExample.this);
		  progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		  progressBar.setTitle("FFmpeg4Android Direct JNI");
		  progressBar.setMessage("Press the cancel button to end the operation");
		  progressBar.setMax(100);
		  progressBar.setProgress(0);
		  
		  progressBar.setCancelable(false);
		  progressBar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		      @Override
		      public void onClick(DialogInterface dialog, int which) {
		    	  handler.sendEmptyMessage(STOP_TRANSCODING_MSG);
		      }
		  });
		  
		  progressBar.show();
	      
	      new Thread() {
	          public void run() {
	        	  Log.d(Prefs.TAG,"Worker started");
	              try {
	                  //sleep(5000);
	            	  runTranscodingUsingLoader();
	                  handler.sendEmptyMessage(FINISHED_TRANSCODING_MSG);

	              } catch(Exception e) {
	                  Log.e("threadmessage",e.getMessage());
	              }
	          }
	      }.start();
	      
	      // Progress update thread
	      new Thread() {
	    	  ProgressCalculator pc = new ProgressCalculator(vkLogPath);
	          public void run() {
	        	  Log.d(Prefs.TAG,"Progress update started");
	        	  int progress = -1;
	        	  try {
	        		  while (true) {
	        			  sleep(300);
	        			  progress = pc.calcProgress();
	        			  if (progress != 0 && progress < 100) {
	        				  progressBar.setProgress(progress);
	        			  }
	        			  else if (progress == 100) {
	        				  Log.i(Prefs.TAG, "==== progress is 100, exiting Progress update thread");
	        				  pc.initCalcParamsForNextInter();
	        				  break;
	        			  }
	        		  }

	        	  } catch(Exception e) {
	        		  Log.e("threadmessage",e.getMessage());
	        	  }
	          }
	      }.start();
	  }

}
