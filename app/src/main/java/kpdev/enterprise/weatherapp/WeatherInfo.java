package kpdev.enterprise.weatherapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class WeatherInfo {

    private String cityName ;
    private String temperature;
    private String city;
    private int isDay;
    private String condition;
    private String conditionText;
    private String feelLike;



    private  String pressSure;
    private String sunsetData;
    private String sunriseData;
    private String rainFall;
    private JSONArray hourArray;



    public WeatherInfo(JSONObject response) {
        try{
            this.temperature = response.getJSONObject("current").getString("temp_c");
            this.city = response.getJSONObject("location").getString("name");
            this.isDay = response.getJSONObject("current").getInt("is_day");

            // Convert Condition
            this.conditionText = response.getJSONObject("current").getJSONObject("condition").getString("text");
            String convertConditionFormatToArray[] = this.conditionText.split("\\ " , -1);
            this.condition = String.join("" , convertConditionFormatToArray).toLowerCase(Locale.ROOT);
            this.feelLike = response.getJSONObject("current").getString("feelslike_c");
            this.pressSure = response.getJSONObject("current").getString("pressure_mb");

            JSONObject forecastObject = response.getJSONObject("forecast");
            JSONObject forecastO = forecastObject.getJSONArray("forecastday").getJSONObject(0);
            this.hourArray = forecastO.getJSONArray("hour");

            this.sunsetData = forecastO.getJSONObject("astro").getString("sunset");
            this.sunriseData = forecastO.getJSONObject("astro").getString("sunrise");
            this.rainFall = this.hourArray.getJSONObject(0).getString("chance_of_rain");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }

    public JSONArray getHourArray() {
        return hourArray;
    }

    public void setHourArray(JSONArray hourArray) {
        this.hourArray = hourArray;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIsDay() {
        return isDay;
    }

    public void setIsDay(int isDay) {
        this.isDay = isDay;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFeelLike() {
        return feelLike;
    }

    public void setFeelLike(String feelLike) {
        this.feelLike = feelLike;
    }

    public String getPressSure() {
        return pressSure;
    }

    public void setPressSure(String pressSure) {
        this.pressSure = pressSure;
    }

    public String getSunsetData() {
        return sunsetData;
    }

    public void setSunsetData(String sunsetData) {
        this.sunsetData = sunsetData;
    }

    public String getSunriseData() {
        return sunriseData;
    }

    public void setSunriseData(String sunriseData) {
        this.sunriseData = sunriseData;
    }

    public String getRainFall() {
        return rainFall;
    }

    public void setRainFall(String rainFall) {
        this.rainFall = rainFall;
    }
}
