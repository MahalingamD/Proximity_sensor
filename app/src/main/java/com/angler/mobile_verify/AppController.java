package com.angler.mobile_verify;


import android.app.Application;

import com.google.firebase.FirebaseApp;


public class AppController extends Application {

   public static final String TAG = AppController.class.getSimpleName();
   private static AppController myInstance;


   public static synchronized AppController getInstance() {
      return myInstance;
   }

   @Override
   public void onCreate() {
      super.onCreate();
      myInstance = this;
     /* db = Room.databaseBuilder( this, AppDatabase.class, "rotogro_db" )
              .addMigrations( FROM_1_TO_2 ).build();*/

      FirebaseApp.initializeApp( this );

   }


}