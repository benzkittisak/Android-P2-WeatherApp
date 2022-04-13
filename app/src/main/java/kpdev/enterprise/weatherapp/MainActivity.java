package kpdev.enterprise.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView cityNameTV, temperatureTV, conditionTV, feelLikeTV, sunsetTV, sunriseTV, pressureTV, rainFallTV;
    private RecyclerView weatherRV, forecaseRV;
    private ImageView backIV, iconIV;
    private RelativeLayout idRLHome , cardViewBG;
    private LayoutInflater inflater;

    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private ArrayList<ForecastModal> forecastModalList;

    private WeatherRVAdapter weatherRVAdapter;
    private ForecastAdapter forecastAdapter;

    public LocationManager locationManager;
    public LocationListener locationListener;

    public int PERMISSION_CODE = 1;
    private static final int GPS_TIME_INTERVAL =5000;
    private static final int GPS_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // เรียกใช้ตัว action bar แบบสร้างเอง
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

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
        idRLHome = findViewById(R.id.idRLHome);

        // กำหนนดข้อมูลเริ่มต้นให้กับ weatherArray
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        // เรียกใช้งาน Adapter ของ พยากรณ์อากาศใน 10 วัน
        forecastModalList = new ArrayList<>();
        forecastAdapter = new ForecastAdapter(this, forecastModalList);
        forecaseRV.setAdapter(forecastAdapter);
        // เรีกยใช้งาน GPS ของตัวเครื่อง

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // เช็คว่าให้สิทธิ์เข้าถึง GPS หรือยัง
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted!!", Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            } else {
                Toast.makeText(this, "Please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getWeatherInfo(String latitude , String longitude , String cityName) {
        // เรียกใช้งาน API
        // กำหนด path ของ API
        String url ;
        if(cityName == null)
            url = "http://api.weatherapi.com/v1/forecast.json?key=102deb83cf914ed596273713220804&q=" + latitude + "," + longitude + "&days=1&aqi=no&alerts=no";
        else
            url = "http://api.weatherapi.com/v1/forecast.json?key=102deb83cf914ed596273713220804&q=" + cityName + "&days=1&aqi=no&alerts=no";

//        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // คือข้อมูลที่ตอบกลับมามันเป็น JSON ฉะนั้นเราก็จะขอข้อมูลที่เป็น JSON มาใช้
        // ให้ขอข้อมูลโดยใช้ Method GET โดยขอข้อมูลจาก url และไม่ส่ง parameter อะไรไป
        JsonObjectRequest jsoneObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            // หลังจากที่ข้อมูลตอบกลับมาจาก Server
            public void onResponse(JSONObject response) {
//                // ให้ปิดตัวหน้าจอ Loading
//                idPBLoading.setVisibility(View.GONE);
//                // เปิดหน้าจอของแอป
//                idCLLayout.setVisibility(View.VISIBLE);
                // เคลียร์ค่าในตัว Array ที่ใช้แสดงข้อมูลพยากรณ์อากาศรายชั่วโมง
                weatherRVModalArrayList.clear();

                try {
                    // ดึงข้อมูลอุณหภูมิจากข้อมูลที่มันตอบกลับมา
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature + "°c");

                    String city = response.getJSONObject("location").getString("name");
                    cityNameTV.setText(city);
                    // ดึงข้อมูลว่าตอนนี้กลางวันหรือกลางคืน
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    // รับข้อมูลสภาพอากาศ
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");

                    Log.d("IS_DAY"  , " = " + isDay);
                    // ใช้ตัว Picasso ไปเซ็ต icon (โหลดจาก cloud server ของแอปที่ชื่อว่า discord) โดยแบ่งเป็นกลางวันกลางคืน
                    // ถ้าเมืองนั้นเป็นกลางวันจะให้เซ็ตพื้นหลังเป็นรูปอะไร ถ้ากลางคืนจะให้เซ็ตเป็นรูปอะไร
                    if (isDay == 1) {

                        // พื้นหลัง กลางวัน
                        Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963789850983694336/sky-2021-08-30-06-22-08-utc.jpg").into(backIV);

                        // Icon สภาพอากาศ ตอนกลางวัน
                        if (condition.equals("Clear")) {
                            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963098637125185677/unknown.png").into(iconIV);
                        }
                    } else {
                        // พื้นหลัง กลางคืน
                        Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963120111177322496/night-sky-of-swiss-alps-2021-09-02-02-03-40-utc.jpg").into(backIV);
                        idRLHome.setBackgroundResource(R.drawable.nightbackgroundindex);

                        // Icon สภาพอากาศ ตอนกลางคืน
                        if (condition.equals("Clear")) {
                            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963119007106465863/unknown.png").into(iconIV);
                        }
                    }
                    conditionTV.setText(condition);

                    String feelLike = response.getJSONObject("current").getString("feelslike_c");
                    feelLikeTV.setText(feelLike + "°c");

                    String pressSure = response.getJSONObject("current").getString("pressure_mb");
                    pressureTV.setText(pressSure + " hPa");

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObject.getJSONArray("forecastday").getJSONObject(0);

                    String sunsetData = forecastO.getJSONObject("astro").getString("sunset");
                    sunsetTV.setText(sunsetData);

                    String sunriseData = forecastO.getJSONObject("astro").getString("sunrise");
                    sunriseTV.setText("Sunrise : " + sunriseData);

                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    String rainFall = hourArray.getJSONObject(0).getString("chance_of_rain");
                    rainFallTV.setText(rainFall + '%');

                    for (int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObject = hourArray.getJSONObject(i);
                        String time = hourObject.getString("time");
                        String temper = hourObject.getString("temp_c");
                        String img = hourObject.getJSONObject("condition").getString("icon");

                        // เพิ่มข้อมูลเข้าไปในตัว Adapter
                        weatherRVModalArrayList.add(new WeatherRVModal(time, temper, img));
                    }
                    weatherRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // หลังจากที่ข้อมูลตอบกลับมาจาก Server เหมือนกันแต่พ่วง Error กลับมาด้วย
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error can't find yout location !", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsoneObjectRequest);
    }

    private void getWeatherForecast(String latitude , String longitude , String cityName) {
        // เรียกใช้งาน API
        // กำหนด path ของ API
        String url ;
        if(cityName == null)
            url = "http://api.weatherapi.com/v1/forecast.json?key=102deb83cf914ed596273713220804&q=" + latitude + "," + longitude + "&days=10&aqi=no&alerts=no";
        else
            url = "http://api.weatherapi.com/v1/forecast.json?key=102deb83cf914ed596273713220804&q=" + cityName + "&days=10&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // คือข้อมูลที่ตอบกลับมามันเป็น JSON ฉะนั้นเราก็จะขอข้อมูลที่เป็น JSON มาใช้
        JsonObjectRequest jsoneObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                forecastModalList.clear();
                try {

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONArray forecast = forecastObject.getJSONArray("forecastday");


                    for (int i = 0; i < forecast.length(); i++) {
                        JSONObject dataObject = forecast.getJSONObject(i);
                        JSONArray hourArray = dataObject.getJSONArray("hour");
                        JSONObject logoObject = hourArray.getJSONObject(0);

                        String day = dataObject.getString("date");
                        String maxTemp = dataObject.getJSONObject("day").getString("maxtemp_c");
                        String minTemp = dataObject.getJSONObject("day").getString("mintemp_c");
                        String temperature = minTemp + "°c / " + maxTemp + "°c";
                        String img = logoObject.getJSONObject("condition").getString("icon");

                        // เพิ่มข้อมูลเข้าไปในตัว Adapter
                        forecastModalList.add(new ForecastModal(day, temperature, img));
                    }

                    forecastAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error can't find yout location !", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsoneObjectRequest);
    }

    // สร้างเมนู
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_right_menu, menu);

        return true;
    }

    // จัดการกับเมนูเวลามีคนกดเข้ามา
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.btnAdd) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    // หลังจากที่ไปหน้าหาสถานที่มาแล้ว ให้มาทำงานที่นี่ก่อนเลย
    @Override
    protected void onResume() {
        super.onResume();
        Intent sIntent = getIntent();
        String cityName = sIntent.getStringExtra("cityName");
        if (cityName != null ) {
            getWeatherInfo(null,null,cityName);
            getWeatherForecast(null,null,cityName);
        } else {
            getWeatherForCurrentLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void getWeatherForCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                Log.d("GPS", "LATITUDE = " + latitude + ", LONGITUDE = " + longitude );
                getWeatherInfo(latitude , longitude , null);
                getWeatherForecast(latitude , longitude , null);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_TIME_INTERVAL, GPS_DISTANCE, locationListener);
    }
}