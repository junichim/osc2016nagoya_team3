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

        // TODO �_�~�[
        mHinanGeoPos = new GeoPos();
        mHinanGeoPos.lat = 35.1784159; // degree
        mHinanGeoPos.lon = 136.96908159999998;
        mHinanGeoPos.name = "�Ȃ��₩�n�E�X��]���u";
   		mHinanGeoPos.address = "���m�����É��s�����]���u�񒚖�3-9";
   		
        // ���ݒn�̕��ʊp
        
        // ���̎擾
        // �ܓx�o�x, AsyncTask ?
        // ���̕��ʊp
        
        // GPS�p
        
        // �{�^�����������猻�݈ʒu���擾
        Button btn = (Button) findViewById(R.id.btn_calc);
        btn.setOnClickListener(mListener);
        
        // �\����
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
        
        // �[���ٕ̂��ʊp
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

		// GPS����
        mLocationManager.removeUpdates(mLocatonListener);
		// �Z���T�[����
		mSensorManager.unregisterListener(mHouiListener);
	}

    // GPS�{�^��
	// http://qiita.com/yasumodev/items/5f0f030f0ebfcecdff11
	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			mLocationManager.removeUpdates(mLocatonListener);
			mLocationManager.requestLocationUpdates(
	                LocationManager.GPS_PROVIDER, //LocationManager.NETWORK_PROVIDER,
	                1000, // �ʒm�̂��߂̍ŏ����ԊԊu�i�~���b�j
	                10, // �ʒm�̂��߂̍ŏ������Ԋu�i���[�g���j
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
        // �\���X�V�i�b��j
    	mTanmatsuTextView.setText("�[���̕��ʊp�F" + mTanmatsuHoui);
    	
        if (null != mCurrent) {
        	final double hinanHoui = Houikaku.getHouikakuBtw2(mCurrent, mHinanGeoPos);
        	mHinanTextView.setText("���̕��ʊp:" + hinanHoui);

        	// �摜����]
        	// http://www.k-sugi.sakura.ne.jp/java/android/2670/
        	// http://m-shige1979.hatenablog.com/entry/2015/02/06/080000
        	mImageView.setScaleType(ScaleType.MATRIX); 

//            //getDrawable���\�b�h�Ŏ��߂������̂��ABitmapDrawable�`���ɃL���X�g����
//            BitmapDrawable bd = (BitmapDrawable)mImageView.getDrawable();
//            //getBitmap���\�b�h�Ńr�b�g�}�b�v�t�@�C�������o��
//            Bitmap bmp = bd.getBitmap();
            //��]������
            Matrix matrix = new Matrix();
            
            matrix.postRotate((float)(hinanHoui - mTanmatsuHoui), mImageView.getWidth()/2, mImageView.getHeight()/2);
//            //Bitmap��]������
//            Bitmap flippedBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
//            //���H����Bitmap������ImageView�ɃZ�b�g����
//            mImageView.setImageBitmap(flippedBmp);
//            mImageView.setImageDrawable(new BitmapDrawable(flippedBmp));
            
            mImageView.setImageMatrix(matrix);
        }
	}
	
	// �p�x
	private SensorEventListener mHouiListener = new SensorEventListener() {
		private float[] mAcMatrix;
		private float[] mMgMatrix;

		@Override
		public void onSensorChanged(SensorEvent event){
			Log.d("houi", "called");
			
			switch(event.sensor.getType()){
				//�����x�Z���T�[�̒l�擾
				case Sensor.TYPE_ACCELEROMETER:
					mAcMatrix = event.values.clone();
					break;
					
				//���C�Z���T�[�̒l�擾
				case Sensor.TYPE_MAGNETIC_FIELD:
					mMgMatrix = event.values.clone();
					break;
			}
			
			if(mMgMatrix !=null && mAcMatrix !=null){
				float[] orientation = new float[3];
				float R[] = new float[16];
				float I[] = new float[16];
				
				//�����x�Z���T�[�A���C�Z���T�[�̒l�����ɁA��]�s����v�Z����
				SensorManager.getRotationMatrix(R, I, mAcMatrix, mMgMatrix);
				
				//�f�o�C�X�̌����ɉ����ĉ�]�s����v�Z����
				SensorManager.getOrientation(R, orientation);
				
				//���W�A������p�x�֕ϊ�
				float angle = (float)Math.floor(Math.toDegrees(orientation[0]));
				
				//�p�x�͈̔͂�0~360�x�֒���
				if(angle >=0){
					orientation[0]=angle;
				}else if(angle < 0){
					orientation[0]= 360 + angle;
				}
				
				//����ꂽ�p�x��n��
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
