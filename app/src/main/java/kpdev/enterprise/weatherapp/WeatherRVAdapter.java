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
import java.util.Locale;

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
        String convertConditionFormatToArray[] = holder.condition.split("\\ " , -1);
        String convertConditionArrayToString = String.join("" , convertConditionFormatToArray).toLowerCase(Locale.ROOT);

        if (holder.isDay == 1) { //เป็นกลางวัน
            Picasso.get().load("https://www.thanomsri.ac.th/v2.2/weather/day/" + convertConditionArrayToString + ".png").into(holder.conditionTV);
        } else { //เป็นกลางคืน
            // Icon สภาพอากาศ ตอนกลางคืน
            Picasso.get().load("https://www.thanomsri.ac.th/v2.2/weather/night/" + convertConditionArrayToString + ".png").into(holder.conditionTV);
        }
//
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
