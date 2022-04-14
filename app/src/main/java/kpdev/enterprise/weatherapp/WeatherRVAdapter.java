package kpdev.enterprise.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter  extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<WeatherRVModal> weatherRVModalList;

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModal> weatherRVModalList) {
        this.context = context;
        this.weatherRVModalList = weatherRVModalList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter.ViewHolder holder, int position) {
        WeatherRVModal modal = weatherRVModalList.get(position);
        holder.isDay = modal.getIsDay();
        holder.condition = modal.getCondition();
        holder.temperatureTV.setText(modal.getTemperature()+"°c");

        if (holder.isDay == 1) { //เป็นกลางวัน
            Picasso.get().load("https://www.thanomsri.ac.th/v2.2/weather/day/" + holder.condition + ".png").into(holder.conditionTV);
        } else { //เป็นกลางคืน
            // Icon สภาพอากาศ ตอนกลางคืน
            if (holder.condition.equals("Clear")) {
                Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963119007106465863/unknown.png").into(holder.conditionTV);
            }
            else if (holder.condition.equals("Partly cloudy")){
                Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963804499275808788/unknown.png").into(holder.conditionTV);
            }
            else if (holder.condition.equals("Patchy rain possible")){
                Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963833412886614037/unknown.png").into(holder.conditionTV);
            }
            else if (holder.condition.equals("Patchy snow possible")){
                Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963833460487749693/unknown.png").into(holder.conditionTV);
            }
            else if (holder.condition.equals("Patchy sleet possible")){
                Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963834933904809984/nightpatchysleetpossible.png").into(holder.conditionTV);
            }
        }
        if (holder.condition.equals("Cloudy")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963804673276534814/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Overcast")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963804852088102922/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Mist")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963821765321166918/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Patchy freezing drizzle possible")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963838958251417740/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Thundery outbreaks possible")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963839740900171776/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Blowing snow")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963841176576860180/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Blizzard")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963842282459648020/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Fog")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963843408378597386/unknown.png").into(holder.conditionTV);
        }
        else if (holder.condition.equals("Fog")){
            Picasso.get().load("https://cdn.discordapp.com/attachments/950973417216180244/963844259465805855/unknown.png").into(holder.conditionTV);
        }

        //Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionTV);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        if(holder.isDay == 1){
            holder.cardViewBG.setBackgroundResource(R.drawable.daycard_background);
        }
        try{
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        }catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return weatherRVModalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView  temperatureTV , timeTV ;
        private ImageView conditionTV ;
        private RelativeLayout  cardViewBG;
        private String condition;
        private int isDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewBG = itemView.findViewById(R.id.cardViewBG);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionTV = itemView.findViewById(R.id.idIVCondition);

        }
    }
}
