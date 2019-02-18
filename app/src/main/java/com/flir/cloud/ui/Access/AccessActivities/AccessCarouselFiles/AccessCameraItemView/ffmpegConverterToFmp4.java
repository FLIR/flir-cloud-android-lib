package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.netcompss.ffmpeg4android.CommandValidationException;
import com.netcompss.ffmpeg4android.GeneralUtils;
import com.netcompss.ffmpeg4android.Prefs;
import com.netcompss.loader.LoadJNI;

/**
 *  To run this Demo Make sure you have on your device this folder:
 *  /sdcard/videokit,
 *  and you have in this folder a video file called in.mp4
 * @author elih
 *
 */
public class ffmpegConverterToFmp4  {

	String workFolder = null;
	String demoVideoFolder = null;
	private boolean commandValidationFailedFlag = false;
	public static boolean isFinished = false;
	private Context mContext;
	public ffmpegConverterToFmp4(Context aContext) {
		mContext = aContext;

		workFolder = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + "/";

		GeneralUtils.copyLicenseFromAssetsToSDIfNeeded((CameraItemActivity)mContext, workFolder);
	}

	public void startConvert(){
		new TranscdingBackground().execute();
	}

	public class TranscdingBackground extends AsyncTask<String, Integer, Integer>
	{

		ProgressDialog progressDialog;
		String commandStr;

		public TranscdingBackground () {

		}



		@Override
		protected void onPreExecute() {

			//commandStr =  "ffmpeg -y -i /sdcard/videoStream/inRecordLong.mp4 -vn -c:a aac -strict -2 -sample_rate 48000 -ac 1 -f mp4 -frag_duration 10000 -movflags separate_moof+empty_moov+default_base_moof /sdcard/videoStream/outLongFromLambdaApp.mp4";
			commandStr =  "ffmpeg -y -i " + AudioRecorder.FULL_RECORD_FILE_PATH_NAME_IN + " -vn -c:a aac -strict -2 -sample_rate 48000 -ac 1 -f mp4 -frag_duration 10000 -movflags separate_moof+empty_moov+default_base_moof " + AudioRecorder.FULL_RECORD_FILE_PATH_NAME_OUT;
			//commandStr =  "ffmpeg -y -i " + "udp://127.0.0.1:50005" + " -vn -c:a aac -strict -2 -sample_rate 48000 -ac 1 -f mp4 -frag_duration 10000 -movflags separate_moof+empty_moov+default_base_moof " + "udp://127.0.0.1:50005";

			progressDialog = new ProgressDialog(mContext);
			progressDialog.setMessage("FFmpeg4Android Transcoding in progress...");
			progressDialog.show();

		}

		protected Integer doInBackground(String... paths) {
			Log.i(Prefs.TAG, "doInBackground started...");

			/*// delete previous log
			boolean isDeleted = GeneralUtils.deleteFileUtil(workFolder + "/vk.log");
			Log.i(Prefs.TAG, "vk deleted: " + isDeleted);*/


			PowerManager powerManager = (PowerManager)mContext.getSystemService(Activity.POWER_SERVICE);
			WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "VK_LOCK");
			Log.d(Prefs.TAG, "Acquire wake lock");
			wakeLock.acquire();

			///////////// Set Command using code (overriding the UI EditText) /////
			//commandStr = "ffmpeg -y -i /sdcard/videokit/in.mp4 -strict experimental -s 320x240 -r 30 -aspect 3:4 -ab 48000 -ac 2 -ar 22050 -vcodec mpeg4 -b 2097152 /sdcard/videokit/out.mp4";
			//String[] complexCommand = {"ffmpeg", "-y" ,"-i", "/sdcard/videokit/in.mp4","-strict","experimental","-s", "160x120","-r","25", "-vcodec", "mpeg4", "-b", "150k", "-ab","48000", "-ac", "2", "-ar", "22050", "/sdcard/videokit/out.mp4"};
			///////////////////////////////////////////////////////////////////////


			LoadJNI vk = new LoadJNI();
			try {

				vk.run(GeneralUtils.utilConvertToComplex(commandStr), workFolder, mContext);

			} catch (CommandValidationException e) {
					commandValidationFailedFlag = true;

			} catch (Throwable e) {
			}
			finally {
				if (wakeLock.isHeld())
					wakeLock.release();
				else{
					Log.i(Prefs.TAG, "Wake lock is already released, doing nothing");
				}
			}
			Log.i(Prefs.TAG, "doInBackground finished");
			return Integer.valueOf(0);
		}

		protected void onProgressUpdate(Integer... progress) {
		}

		@Override
		protected void onCancelled() {
			Log.i(Prefs.TAG, "onCancelled");
			//progressDialog.dismiss();
			super.onCancelled();
		}


		@Override
		protected void onPostExecute(Integer result) {
			Log.i(Prefs.TAG, "onPostExecute");
			progressDialog.dismiss();
			super.onPostExecute(result);
			Log.d("MNMNISSU", "onPostExecute");
			isFinished = true;
		}

	}


}
