package kpdev.enterprise.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameTV , temperatureTV , conditionTV;
    private RecyclerView weatherRV;
    private TextInputEditText cityEdt;
    private ImageView backIV , iconIV , searchIV;

    private ArrayList<WeatherRVModal> weatherRVModalArrayList;
    private WeatherRVAdapter weatherRVAdapter;

    public LocationManager locationManager;
    public int PERMISSION_CODE =1;
    public Criteria criteria;
    public String cityName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        homeRL = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.idLoading);
        cityNameTV = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBackground);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);

        // กำหนนดข้อมูลเริ่มต้นให้กับ weatherArray
        weatherRVModalArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this , weatherRVModalArrayList);
        weatherRV.setAdapter(weatherRVAdapter);

        // เรีกยใช้งาน GPS ของตัวเครื่อง

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // เช็คว่าให้สิทธิ์เข้าถึง GPS หรือยัง
        if(ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this , Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION , Manifest.permission.ACCESS_COARSE_LOCATION} , PERMISSION_CODE);

        }
        // ถ้าให้สิทธิ์แล้ว
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCityName(location.getLongitude() , location.getLatitude());
        getWeatherInfo(cityName);



        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String city = cityEdt.getText().toString().trim();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this , "Please enter city name" , Toast.LENGTH_SHORT).show();
                } else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
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
        String cityName = "Not found!";
        Geocoder gcd = new Geocoder(getBaseContext() , Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitude , 10);
            for (Address adr : addresses){
                if(adr != null){
                    String city = adr.getLocality();
                    if(city != null && !city.equals("")) {
                        cityName = city;
                    } else {
                        Log.e("TAG" , "CITY NOT FOUND");
                        Toast.makeText(this , "User City Not Found!!" , Toast.LENGTH_SHORT).show();
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
        JsonObjectRequest jsoneObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);

                weatherRVModalArrayList.clear();

                try{
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    if(isDay == 1){
                        // in morning
                        Picasso.get().load("https://i3.fpic.cc/file/img-b1/2022/04/08/TX-2409.jpg").into(backIV);
                    } else {
                        Picasso.get().load("https://i3.fpic.cc/file/img-b1/2022/04/08/amazing-starry-night-sky-with-milky-way-and-fallin-2021-08-29-02-11-57-utc.jpg").into(backIV);
                    }

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for(int i =0  ; i<hourArray.length() ; i ++){
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
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this , "Please enter valid city name!" , Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsoneObjectRequest);


    }
}