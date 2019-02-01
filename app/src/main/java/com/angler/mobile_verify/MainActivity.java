package com.angler.mobile_verify;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


   TextView ProximitySensor, data;
   SensorManager mySensorManager;
   Sensor myProximitySensor;

   public static Camera cam = null;

   private Camera mCamera;
   private Camera.Parameters parameters;
   private CameraManager camManager;

   @Override
   protected void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_main );

      ProximitySensor = ( TextView ) findViewById( R.id.proximitySensor );
      data = ( TextView ) findViewById( R.id.data );
      mySensorManager = ( SensorManager ) getSystemService( Context.SENSOR_SERVICE );
      myProximitySensor = mySensorManager.getDefaultSensor( Sensor.TYPE_PROXIMITY );
      if( myProximitySensor == null ) {
         ProximitySensor.setText( "No Proximity Sensor!" );
      } else {
         mySensorManager.registerListener( proximitySensorEventListener, myProximitySensor,
                 SensorManager.SENSOR_DELAY_NORMAL );
      }
   }

   SensorEventListener proximitySensorEventListener = new SensorEventListener() {
      @Override
      public void onAccuracyChanged( Sensor sensor, int accuracy ) {
         // TODO Auto-generated method stub
      }

      @Override
      public void onSensorChanged( SensorEvent event ) {
         // TODO Auto-generated method stub
         if( event.sensor.getType() == Sensor.TYPE_PROXIMITY ) {
            if( event.values[ 0 ] == 0 ) {
               data.setText( "Near" );
               turnFlashlightOn();
            } else {
               data.setText( "Away" );
               turnFlashlightOff();
            }
         }

         onChanged( event );
      }
   };


   public void onChanged( SensorEvent event ) {
      WindowManager.LayoutParams params = this.getWindow().getAttributes();
      if( event.sensor.getType() == Sensor.TYPE_PROXIMITY ) {

         if( event.values[ 0 ] == 0 ) {
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = 0;
            getWindow().setAttributes( params );
         } else {
            params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
            params.screenBrightness = -1f;
            getWindow().setAttributes( params );
         }
      }
   }


   private void turnFlashlightOn() {
      if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
         try {
            camManager = ( CameraManager ) this.getSystemService( Context.CAMERA_SERVICE );
            String cameraId = null; // Usually front camera is at 0 position.
            if( camManager != null ) {
               cameraId = camManager.getCameraIdList()[ 0 ];
               camManager.setTorchMode( cameraId, true );
            }
         } catch( CameraAccessException e ) {
            Log.e( "error", e.toString() );
         }
      } else {
         mCamera = Camera.open();
         parameters = mCamera.getParameters();
         parameters.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH );
         mCamera.setParameters( parameters );
         mCamera.startPreview();
      }
   }


   private void turnFlashlightOff() {
      if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
         try {
            String cameraId;
            camManager = ( CameraManager ) this.getSystemService( Context.CAMERA_SERVICE );
            if( camManager != null ) {
               cameraId = camManager.getCameraIdList()[ 0 ]; // Usually front camera is at 0 position.
               camManager.setTorchMode( cameraId, false );
            }
         } catch( CameraAccessException e ) {
            e.printStackTrace();
         }
      } else {
         mCamera = Camera.open();
         parameters = mCamera.getParameters();
         parameters.setFlashMode( Camera.Parameters.FLASH_MODE_OFF );
         mCamera.setParameters( parameters );
         mCamera.stopPreview();
      }
   }

}
