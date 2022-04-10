package kpdev.enterprise.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private CoordinatorLayout coordinator_layout;
    private ProgressBar loadingPB;
    private TextView cityNameTV , temperatureTV , conditionTV , feelLikeTV , sunsetTV ,sunriseTV , pressureTV , rainFallTV ;
    private RecyclerView weatherRV , forecaseRV;
    private ImageView backIV , iconIV ;

    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private ArrayList<ForecastModal> forecastModalList;

    private WeatherRVAdapter weatherRVAdapter;
    private ForecastAdapter forecastAdapter;

    public LocationManager locationManager;
    public int PERMISSION_CODE =1;
    public String cityName ;
    private static final int GPS_TIME_INTERVAL = 1000 * 60 * 5; // get gps location every 1 min
    private static final int GPS_DISTANCE = 1000; // set the distance value in meter
    private static final int HANDLER_DELAY = 1000 * 60 * 5;
    private static final int START_HANDLER_DELAY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // เรียกใช้ตัว action bar แบบสร้างเอง
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);



        coordinator_layout = findViewById(R.id.coordinator_layout);
        loadingPB = findViewById(R.id.idLoading);

        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        pressureTV = findViewById(R.id.idTVPressure);
        rainFallTV = findViewById(R.id.idTVRain);

        weatherRV = findViewById(R.id.idRVWeather);
        forecaseRV = findViewById(R.id.idRVForecastFuture);

        backIV = findViewById(R.id.idIVBackground);
        iconIV = findViewById(R.id.idIVIcon);
        feelLikeTV = findViewById(R.id.idTVFeelLike);
        sunsetTV = findViewById(R.id.idTVSunset);
        sunriseTV = findViewById(R.id.idTVSunrise);



        // กำหนนดข้อมูลเริ่มต้นให้กับ weatherArray
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this , weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        // เรียกใช้งาน Adapter ของ พยากรณ์อากาศใน 10 วัน
        forecastModalList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(this , forecastModalList);
        forecaseRV.setAdapter(forecastAdapter);
        // เรีกยใช้งาน GPS ของตัวเครื่อง

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // เช็คว่าให้สิทธิ์เข้าถึง GPS หรือยัง
        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION} , PERMISSION_CODE);
        }


        Intent sIntent = getIntent();
        String cityName = sIntent.getStringExtra("City");

        if(cityName == null){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    requestLocation();
                    handler.postDelayed(this , HANDLER_DELAY);
                }
            } , START_HANDLER_DELAY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this , "Permission granted!!" , Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this , "Please provide the permissions" , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    // รับข้อมูลชื่อของเมือง
    private String getCityName(double longitude, double latitude){
        String cityName = "Bangkok";
        // เรียกใช้งาน Geocoder เพื่อแปลงข้อความเช่นพวกพิกัดต่าง ๆ ให้เป็นสถานที่
                                    // ส่งตัว context กับตัว location เริ่มต้นลงไป
        Geocoder gcd = new Geocoder(getBaseContext() , Locale.getDefault());
        try{
                                        // ให้ gcd ไปรับข้อมูลของสถานที่จาก latitude กับ longitude โดยที่ผลลัพธ์ที่ได้จะเอามาไม่เกิน 20 สถานที่
            List<Address> addresses = gcd.getFromLocation(latitude,longitude , 20);
            // ทำการลูปข้อมูลของสถานที่ออกมาเก็บไว้ในตัวแปร adr
            for (Address adr : addresses){
                    // ถ้าสถานที่มันไม่ว่าง
                if(adr != null){
                        // ให้ตัวแปร city มาเก็บสถานที่ ที่ได้จากตำแหน่งของ latitude กับ longitude
                    String city = adr.getLocality();
                        // ถ้าตัวแปร city มันมีข้อมูล ก็ให้ตัวแปร cityName = city ไปเลย
                    if(city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.d("TAG" , "CITY NOT FOUND Latitude = " + latitude + " Longitude = " + longitude);
//                        Toast.makeText(this , "User City Not Found!!" , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return cityName;
    }


    private void getWeatherInfo(String cityName){
        // เรียกใช้งาน API
        // กำหนด path ของ API
        String url = "http://api.weatherapi.com/v1/forecast.json?key=102deb83cf914ed596273713220804&q="+cityName+"&days=1&aqi=no&alerts=no";

        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // คือข้อมูลที่ตอบกลับมามันเป็น JSON ฉะนั้นเราก็จะขอข้อมูลที่เป็น JSON มาใช้
                                                    // ให้ขอข้อมูลโดยใช้ Method GET โดยขอข้อมูลจาก url และไม่ส่ง parameter อะไรไป
        JsonObjectRequest jsoneObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            // หลังจากที่ข้อมูลตอบกลับมาจาก Server
            public void onResponse(JSONObject response) {
                // ให้ปิดตัวหน้าจอ Loading
                loadingPB.setVisibility(View.GONE);
                // เปิดหน้าจอของแอป
                coordinator_layout.setVisibility(View.VISIBLE);
                // เคลียร์ค่าในตัว Array ที่ใช้แสดงข้อมูลพยากรณ์อากาศรายชั่วโมง
                weatherRVModalArrayList.clear();

                try{
                    // ดึงข้อมูลอุณหภูมิจากข้อมูลที่มันตอบกลับมา
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°c");

                    // ดึงข้อมูลว่าตอนนี้กลางวันหรือกลางคืน
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    // รับข้อมูลสภาพอากาศ
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    // รับข้อมูลไอคอนของสภาพอากาศ
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");

                    // ใช้ตัว Picasso ไปเซ็ต icon
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    String feelLike = response.getJSONObject("current").getString("feelslike_c");
                    feelLikeTV.setText(feelLike + "°c");

                    String pressSure = response.getJSONObject("current").getString("pressure_mb");
                    pressureTV.setText(pressSure + " hPa");

                    // ถ้าเมืองนั้นเป็นกลางวันจะให้เซ็ตพื้นหลังเป็นรูปอะไร ถ้ากลางคืนจะให้เซ็ตเป็นรูปอะไร
                    if(isDay == 1){
                        // กลางวัน
                        Picasso.get().load("https://i3.fpic.cc/file/img-b1/2022/04/08/TX-2409.jpg").into(backIV);
                    } else {
                        // กลางคืน
                        Picasso.get().load("https://i3.fpic.cc/file/img-b1/2022/04/08/amazing-starry-night-sky-with-milky-way-and-fallin-2021-08-29-02-11-57-utc.jpg").into(backIV);
                    }

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObject.getJSONArray("forecastday").getJSONObject(0);

                    String sunsetData = forecastO.getJSONObject("astro").getString("sunset");
                    sunsetTV.setText(sunsetData);

                    String sunriseData = forecastO.getJSONObject("astro").getString("sunrise");
                    sunriseTV.setText("Sunrise : " + sunriseData);

                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    String rainFall = hourArray.getJSONObject(0).getString("chance_of_rain");
                    rainFallTV.setText(rainFall + '%');

                    for(int i =0  ; i<hourArray.length() ; i++){
                        JSONObject hourObject = hourArray.getJSONObject(i);
                        String time = hourObject.getString("time");
                        String temper = hourObject.getString("temp_c");
                        String img = hourObject.getJSONObject("condition").getString("icon");
                        String wind = hourObject.getString("wind_kph");


                        // เพิ่มข้อมูลเข้าไปในตัว Adapter
                        weatherRVModalArrayList.add(new WeatherRVModal(time , temper , img , wind));
                    }

                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            // หลังจากที่ข้อมูลตอบกลับมาจาก Server เหมือนกันแต่พ่วง Error กลับมาด้วย
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this , "Please enter valid city name!" , Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsoneObjectRequest);


    }

    private void getWeatherForecast(String cityName){
        // เรียกใช้งาน API
        // กำหนด path ของ API
        String url = "http://api.weatherapi.com/v1/forecast.json?key=102deb83cf914ed596273713220804&q="+cityName+"&days=10&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // คือข้อมูลที่ตอบกลับมามันเป็น JSON ฉะนั้นเราก็จะขอข้อมูลที่เป็น JSON มาใช้
        JsonObjectRequest jsoneObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                forecastModalList.clear();
                try{

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONArray forecast = forecastObject.getJSONArray("forecastday");


                    for(int i =0  ; i<forecast.length() ; i++){
                        JSONObject dataObject = forecast.getJSONObject(i);
                        JSONArray hourArray = dataObject.getJSONArray("hour");
                        JSONObject logoObject = hourArray.getJSONObject(0);

                        String day = dataObject.getString("date");
                        String maxTemp = dataObject.getJSONObject("day").getString("maxtemp_c");
                        String minTemp = dataObject.getJSONObject("day").getString("mintemp_c");
                        String temperature = minTemp + "°c / " + maxTemp + "°c";
                        String img = logoObject.getJSONObject("condition").getString("icon");

                        // เพิ่มข้อมูลเข้าไปในตัว Adapter
                        forecastModalList.add(new ForecastModal(day , temperature , img ));
                    }

                    forecastAdapter.notifyDataSetChanged();

                } catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this , "Please enter valid city name!" , Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsoneObjectRequest);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        cityName = getCityName(location.getLongitude() , location.getLatitude());
        getWeatherInfo(cityName);
        getWeatherForecast(cityName);
        locationManager.removeUpdates(this);
    }

    private void requestLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        GPS_TIME_INTERVAL, GPS_DISTANCE, this);
            }
        }
    }


    // สร้างเมนู
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_right_menu , menu);

        return true;
    }

    // จัดการกับเมนูเวลามีคนกดเข้ามา
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if (id == R.id.btnAdd){
            Intent searchIntent = new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
        }

        return  super.onOptionsItemSelected(item);
    }

    // หลังจากที่ไปหน้าหาสถานที่มาแล้ว ให้มาทำงานที่นี่ก่อนเลย
    @Override
    protected void onResume(){
        super.onResume();

        Intent sIntent = getIntent();
        String cityName = sIntent.getStringExtra("City");

        if(cityName != null){
            getWeatherInfo(cityName);
            getWeatherForecast(cityName);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}