package com.example.osc2016nagoyateam3;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class MainActivity extends Activity {

    private LocationManager mLocationManager;
    private GeoPos mCurrent = null;
    private GeoPos mHinanGeoPos;
	private SensorManager mSensorManager;
	private float mTanmatsuHoui;

	private TextView mTanmatsuTextView;
	private TextView mHinanTextView;
	private ImageView mImageView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO ダミー
        mHinanGeoPos = new GeoPos();
        mHinanGeoPos.lat = 35.1784159; // degree
        mHinanGeoPos.lon = 136.96908159999998;
        mHinanGeoPos.name = "なごやかハウス希望ヶ丘";
   		mHinanGeoPos.address = "愛知県名古屋市千種区希望ヶ丘二丁目3-9";
   		
        // 現在地の方位角
        
        // 避難所の取得
        // 緯度経度, AsyncTask ?
        // 避難所の方位角
        
        // GPS用
        
        // ボタンを押したら現在位置を取得
        Button btn = (Button) findViewById(R.id.btn_calc);
        btn.setOnClickListener(mListener);
        
        // 表示先
        mTanmatsuTextView = (TextView) findViewById(R.id.txt_houi_tanmatsu);
        mHinanTextView = (TextView) findViewById(R.id.txt_houi_hinan);
        mImageView =(ImageView) findViewById(R.id.image_hinan_dir);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		// GPS
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        boolean gpsFlg = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        
        Log.d("GPS Enabled", gpsFlg?"OK":"NG");

        Location l = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mCurrent = new GeoPos();
        mCurrent.lat = l.getLatitude();
        mCurrent.lon = l.getLongitude();
        
        // 端末のほ方位角
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(
        		mHouiListener,
        		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        		SensorManager.SENSOR_DELAY_UI);
   		mSensorManager.registerListener(
        		mHouiListener,
        		mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
        		SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// GPS解除
        mLocationManager.removeUpdates(mLocatonListener);
		// センサー解除
		mSensorManager.unregisterListener(mHouiListener);
	}

    // GPSボタン
	// http://qiita.com/yasumodev/items/5f0f030f0ebfcecdff11
	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			mLocationManager.removeUpdates(mLocatonListener);
			mLocationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, //LocationManager.NETWORK_PROVIDER,
	                1000, // 通知のための最小時間間隔（ミリ秒）
	                10, // 通知のための最小距離間隔（メートル）
	                mLocatonListener);
	        Log.d("gps listner", "calld");
		}
	};

	// GPS
	private LocationListener mLocatonListener = new LocationListener() {
			@Override
            public void onLocationChanged(Location location) {
            	mCurrent = new GeoPos();
            	mCurrent.lat = location.getLatitude();  // degrees
            	mCurrent.lon = location.getLongitude();
//                mLocationManager.removeUpdates(this);
            	Log.d("location", "lat, lon:" + mCurrent.lat + ", " + mCurrent.lon);
            	viewUpdate();
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
	};

	private void viewUpdate() {
		Log.d("viewupdate", "called");
        // TODO
        // 表示更新（暫定）
    	mTanmatsuTextView.setText("端末の方位角：" + mTanmatsuHoui);
    	
        if (null != mCurrent) {
        	final double hinanHoui = Houikaku.getHouikakuBtw2(mCurrent, mHinanGeoPos);
        	mHinanTextView.setText("避難所の方位角:" + hinanHoui);

        	// 画像を回転
        	// http://www.k-sugi.sakura.ne.jp/java/android/2670/
        	// http://m-shige1979.hatenablog.com/entry/2015/02/06/080000
        	mImageView.setScaleType(ScaleType.MATRIX); 

//            //getDrawableメソッドで取り戻したものを、BitmapDrawable形式にキャストする
//            BitmapDrawable bd = (BitmapDrawable)mImageView.getDrawable();
//            //getBitmapメソッドでビットマップファイルを取り出す
//            Bitmap bmp = bd.getBitmap();
            //回転させる
            Matrix matrix = new Matrix();
            
            matrix.postRotate((float)(hinanHoui - mTanmatsuHoui), mImageView.getWidth()/2, mImageView.getHeight()/2);
//            //Bitmap回転させる
//            Bitmap flippedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//            //加工したBitmapを元のImageViewにセットする
//            mImageView.setImageBitmap(flippedBmp);
//            mImageView.setImageDrawable(new BitmapDrawable(flippedBmp));
            
            mImageView.setImageMatrix(matrix);
        }
	}
	
	// 角度
	private SensorEventListener mHouiListener = new SensorEventListener() {
		private float[] mAcMatrix;
		private float[] mMgMatrix;

		@Override
		public void onSensorChanged(SensorEvent event){
			Log.d("houi", "called");
			
			switch(event.sensor.getType()){
				//加速度センサーの値取得
				case Sensor.TYPE_ACCELEROMETER:
					mAcMatrix = event.values.clone();
					break;
					
				//磁気センサーの値取得
				case Sensor.TYPE_MAGNETIC_FIELD:
					mMgMatrix = event.values.clone();
					break;
			}
			
			if(mMgMatrix !=null && mAcMatrix !=null){
				float[] orientation = new float[3];
				float R[] = new float[16];
				float I[] = new float[16];
				
				//加速度センサー、磁気センサーの値を元に、回転行列を計算する
				SensorManager.getRotationMatrix(R, I, mAcMatrix, mMgMatrix);
				
				//デバイスの向きに応じて回転行列を計算する
				SensorManager.getOrientation(R, orientation);
				
				//ラジアンから角度へ変換
				float angle = (float)Math.floor(Math.toDegrees(orientation[0]));
				
				//角度の範囲を0~360度へ調整
				if(angle >=0){
					orientation[0]=angle;
				}else if(angle < 0){
					orientation[0]= 360 + angle;
				}
				
				//得られた角度を渡す
				mTanmatsuHoui = orientation[0];
			}
			viewUpdate();
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
}
