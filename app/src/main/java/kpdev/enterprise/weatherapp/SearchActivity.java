package kpdev.enterprise.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView ;
    ListView listView;
    ArrayList<Location> list = new ArrayList<>();
    JSONArray data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listview);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!list.isEmpty())
                    list.clear();
                getJSON(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!list.isEmpty())
                    list.clear();
                getJSON(newText);
                return false;
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void getJSON(String query) {
        new AsyncTask<Void , Void , Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.weatherapi.com/v1/search.json?key=102deb83cf914ed596273713220804&q=" + query);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp;

                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONArray(json.toString());

                }catch(Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void Void){
                if(data != null) {
                    try{
                        for (int i=0 ; i<data.length() ; i++) {
                            JSONObject obj = data.getJSONObject(i);
                            Location location = new Location();
                            location.setLon(obj.getDouble("lon"));
                            location.setLat(obj.getDouble("lat"));
                            location.setName(obj.getString("name"));
                            location.setRegion(obj.getString("region"));
                            location.setCountry(obj.getString("country"));

                            list.add(location);
                        }

                        SearchLocationAdapter adapter = new SearchLocationAdapter(SearchActivity.this , R.layout.search_listview_item , list);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Location location = (Location) adapter.getItem(i);

                                Toast.makeText(SearchActivity.this , location.getName() , Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent (SearchActivity.this,MainActivity.class);
                                intent.putExtra("cityName",location.getName());
                                startActivity(intent);
                                finish();
                            }
                        });
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }
}