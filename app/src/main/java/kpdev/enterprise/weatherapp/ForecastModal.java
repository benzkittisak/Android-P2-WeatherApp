package kpdev.enterprise.weatherapp;

public class ForecastModal {
  private String date;
  private String temperature;
  private String icon;



  public ForecastModal(String date, String temperature, String icon) {
    this.date = date;
    this.temperature = temperature;
    this.icon = icon;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
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
}
