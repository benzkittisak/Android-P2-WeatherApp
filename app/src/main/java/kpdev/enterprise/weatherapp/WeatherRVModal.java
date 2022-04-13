package kpdev.enterprise.weatherapp;

public class WeatherRVModal {
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

    private String time;
    private String temperature;
    private String icon;
    private String condition;
    private int isDay;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public WeatherRVModal(String time, String temperature, String icon , int isDay , String condition) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.isDay = isDay;
        this.condition = condition;

    }
}
