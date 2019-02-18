package udl.eps.testaccelerometre;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class TestAccelerometreActivity extends Activity implements SensorEventListener {

     private SensorManager sensorManager;
     private boolean color = false;
     private TextView view, textViewMiddle, textViewEnd;
     private ScrollView scrollView;
     private long lastUpdate;
     private long lastLightUpdate;
     private String to_add="";
     private float umbral_baix = 15000;
     private float umbral_alt = 30000;
     private float last_light_value = 0;

     /*
     * Bones Practiques que faltarien:
     * 1. Verify sensors before you use them
     * 2. Register/unregister sensor listeners
     * (el registro se tendria que hacer en el on resume
     * pero no hay diferencia alguna al hacerlo en onCreate)
     * */

      @Override
      public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        view = (TextView) findViewById(R.id.textView);
        textViewMiddle = (TextView) findViewById(R.id.textViewMiddle);
        textViewEnd = (TextView) findViewById(R.id.textViewEnd);
        //scrollView = (ScrollView) findViewById(R.id.S)

        view.setBackgroundColor(Color.GREEN);
        textViewMiddle.setBackgroundColor(Color.CYAN);

        textViewEnd.setBackgroundColor(Color.YELLOW);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

      }

      private void setText()
      {
          Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
          String final_string = "";
          final_string += getResources().getString(R.string.shake) + "\n";
          final_string += "Vendor: " + sensor.getVendor() + "\n";
          final_string += "Name: " + sensor.getName() + "\n";
          final_string += "Resolution: " + sensor.getResolution() + "\n";
          final_string += "Power: " + sensor.getPower() + "\n";
          final_string += "MinDelay: " + sensor.getMinDelay() + "\n";
          final_string += "MaxDelay: " + sensor.getMaxDelay() + "\n";

          textViewMiddle.setText(final_string);


          //sensor.get
      }

      /* Buena practia 2 registro en on resume*/
      @Override
      protected void onResume()
      {
          /* Buena practica  1: Verificar sensor*/
          super.onResume();
          registerAccelerometer();
          registerLight();
      }

      private  void registerLight()
      {
          if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null)
          {
              Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
              sensorManager.registerListener(this,
                      sensor,
                      SensorManager.SENSOR_DELAY_NORMAL);
              // register this class as a listener for the accelerometer sensor
              lastLightUpdate = System.currentTimeMillis();

              //Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
              lastLightUpdate = 15000;
              float max = sensor.getMaximumRange();
              String name = sensor.getName();
              to_add +="Nom: " + name + "\n";
              to_add +="Max. rang: " +  max + "lxs" + "\n";
              textViewEnd.setText(to_add);

          }
          else
          {
              textViewEnd.setText(getResources().getString(R.string.no_light));
          }
      }

      private void registerAccelerometer()
      {
          if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
          {
              setText();
              sensorManager.registerListener(this,
                      sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                      SensorManager.SENSOR_DELAY_NORMAL);
              // register this class as a listener for the accelerometer sensor
              lastUpdate = System.currentTimeMillis();
          }
          else
          {
              textViewMiddle.setText(R.string.no_accelerometer);
              Toast.makeText(this, R.string.not_available, Toast.LENGTH_SHORT).show();
          }
      }

      @Override
      public void onSensorChanged(SensorEvent event) {
          if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
          {
              getAccelerometer(event);

          }
          if (event.sensor.getType() == Sensor.TYPE_LIGHT)
          {
              getLight(event);
          }
      }

      private void getLight(SensorEvent event)
      {
          float value = event.values[0];
          long actualTime = System.currentTimeMillis();
          float difference = value - last_light_value;
          System.out.println("Difference: " + difference);

          if(difference > 500 || difference < -500)
          {
              if (actualTime - lastLightUpdate < 200)
              {
                  return;
              }

              last_light_value = value;
              lastUpdate = actualTime;

              to_add += "New value light sensor = " + value + "\n";
              if (value < umbral_baix)
              {
                  to_add += "LOW Intensity \n";
              }
              else if (value > umbral_alt)
              {
                  to_add += "HIGH Intensity \n";
              }
              else
              {
                  to_add += "MEDIUM Intensity \n";
              }

              textViewEnd.setText(to_add);


          }

      }

      private void getAccelerometer(SensorEvent event) {
        float values[] = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
            / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = System.currentTimeMillis();

        //System.out.println("Estoy en la verga: " + accelationSquareRoot);

        // Por defecto esta en 2 a ver si te va :)
        if (accelationSquareRoot >= 1.5)
        {
          if (actualTime - lastUpdate < 200) {
            return;
          }
          lastUpdate = actualTime;

          Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show();
          if (color) {
            view.setBackgroundColor(Color.GREEN);

          } else {
            view.setBackgroundColor(Color.RED);
          }
          color = !color;
        }
      }

      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
         /* if(sensor.getType() == Sensor.TYPE_LIGHT)
          {
              getLight(accuracy);
          }*/
      }

      /* Puede ser onPause o onStop*/
      @Override
      protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
      }

      @Override
      protected void onStop() {

          super.onStop();
          sensorManager.unregisterListener(this);
      }
} 
