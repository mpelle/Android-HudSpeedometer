package speeddemo.s;

import java.math.BigDecimal;
import java.math.RoundingMode;

import interfaces.Constants;
import interfaces.GPScallback;
import managers.GPSManager;
import Settings.AppSettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity implements GPScallback {  //Start Main activity
        private GPSManager gpsManager = null;
        private double speed = 0;
        private int measurement_index = Constants.INDEX_KM;
        private AbsoluteSizeSpan sizeSpanLarge = null;
        private AbsoluteSizeSpan sizeSpanSmall = null;
    	WakeLock wakeLock;
    	String Font = "digital-7.ttf"; //Initialize font
    @Override
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();               //Create window and make the conten fullscreen
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    
        setContentView(R.layout.main);                                  
        
        gpsManager = new GPSManager();                                     //Initialize a Gps manager
        gpsManager.startListening(getApplicationContext());
        gpsManager.setGPSCallback(this);
        ((TextView)findViewById(R.id.info_message)).setText(getString(R.string.info));
        measurement_index = AppSettings.getMeasureUnit(this);
                                                                           //Prevent Auto-Dim
                 PowerManager powerManager =
                (PowerManager)getSystemService(Context.POWER_SERVICE);
               wakeLock =
                powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"Full Wake Lock");
    }
    
         @Override
         protected void onResume () {
          // TODO Auto-generated method stub
          super.onResume();
          wakeLock.acquire();
         }

         @Override
         protected void onPause() {
          // TODO Auto-generated method stub
          super.onPause();
          wakeLock.release();
         }

          @Override
		public void onGPSUpdate(Location location)       //When the gps gets a new value save the location and get the speed
        {
                location.getLatitude();
                location.getLongitude();
                speed = location.getSpeed();
                
                String speedString = "" + roundDecimal(convertSpeed(speed),2);    //Round the decimal 
                String unitString = measurementUnitString(measurement_index);     //Get the string containing the unit of measure the user wants
                
                setSpeedText(R.id.info_message,speedString + " " + unitString);   //Display speed to the user with the requested unit of measure
        }

        @Override
        protected void onDestroy() {      //stop gps when app is closed
                gpsManager.stopListening();
                gpsManager.setGPSCallback(null);
                
                gpsManager = null;
                
                super.onDestroy();
        }
                
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {              //Create an options menu in the app
                MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {       //Add the items to the menu
                boolean result = true;
        
                switch(item.getItemId())
                {
                        case R.id.menu_about:            //Choice for the about dialog
                        { 
                                displayAboutDialog();
                                
                                break;
                        }
                        case R.id.unit_km:            //Choice for KM/H
                        {
                                measurement_index = 0;
                                
                                AppSettings.setMeasureUnit(this, 0);
                                
                                break;
                        }
                        case R.id.unit_miles:        //Choice for MPH
                        {
                                measurement_index = 1;
                                
                                AppSettings.setMeasureUnit(this, 1);
                                
                                break;
                        }
                        case R.id.HUD_ON:                   //Choice for Hud mode ON
                        {
                        	Font = "digital-7m.ttf";
                        	
                        	break;
                        }
                        case R.id.HUD_OFF:               //Choice for Hud mode Off
                        {
                        	Font = "digital-7.ttf";
                        	break;
                        }
                        default:
                        {
                                result = super.onOptionsItemSelected(item);
                                
                                break;
                        }
                }
                
                return result;
        }

        private double convertSpeed(double speed)    //Math for speed calculation based on GPS data
        {
                return ((speed * Constants.HOUR_MULTIPLIER) * Constants.UNIT_MULTIPLIERS[measurement_index]);
                
        }
      
        
        private String measurementUnitString(int unitIndex)      //The Strings for the selected unit of measurement
        {
                String string = "";
                
                switch(unitIndex)
                {
                        case Constants.INDEX_KM:       string = "km/h";        break;
                        case Constants.INDEX_MILES:    string = "mph";         break;
                }
                
                return string;             //Return the selected string back to the program to perform calculation
        }
        
        

      
        
        private double roundDecimal(double value, final int decimalPlace)     //Method to Round the decimal for the displayed speed
        {
                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
                value = bd.intValue();
                return value;
        }
        
        private void setSpeedText(int textid,String text)            //Method to span the speed text on the text view To display speed to user
        {
                Spannable span = new SpannableString(text);
                int firstPos = text.indexOf(32);
                span.setSpan(sizeSpanLarge, 0, firstPos,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                span.setSpan(sizeSpanSmall, firstPos , text.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                TextView tv = ((TextView)findViewById(textid));
                Typeface font = Typeface.createFromAsset(getAssets(), Font );
                tv.setTypeface(font); 
                tv.setTextSize (125);
                tv.setText(span);
                
              
        }

        	
        	
     
        private void displayAboutDialog()                        //About Dialog
        {
                final LayoutInflater inflator = LayoutInflater.from(this);
                final View settingsview = inflator.inflate(R.layout.about, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name));
                builder.setView(settingsview);
                                
                builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                
                        }
                });
                         builder.create().show();
        }
}
	
