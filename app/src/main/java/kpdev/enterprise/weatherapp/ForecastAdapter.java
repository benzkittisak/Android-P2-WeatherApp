package kpdev.enterprise.weatherapp;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import kpdev.enterprise.weatherapp.ForecastModal;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private ArrayList<ForecastModal> forecastDay;
    private Context context;

    public ForecastAdapter(Context context , ArrayList<ForecastModal> forecastDay) {
        this.forecastDay = forecastDay;
        this.context = context;
    }

    @NonNull
    @Override
    public ForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_layout , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapter.ViewHolder holder, int position) {
        ForecastModal modal = forecastDay.get(position);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("EEE");
        holder.iconimg = modal.getIcon();
        String convertConditionFormatToArray[] = holder.iconimg.split("\\ " , -1);
        String convertConditionArrayToString = String.join("" , convertConditionFormatToArray).toLowerCase(Locale.ROOT);

        Picasso.get().load("https://www.thanomsri.ac.th/v2.2/weather/day/" + convertConditionArrayToString + ".png").into(holder.imgDay);

        holder.temperatureTV.setText(modal.getTemperature());

        try{
            Date t = input.parse(modal.getDate());
            holder.tvDay.setText(output.format(t).toUpperCase(Locale.ROOT));
        }catch(ParseException e){
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return forecastDay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{

        private TextView tvDay , temperatureTV  ;
        private ImageView imgDay ;
        private String iconimg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            temperatureTV = itemView.findViewById(R.id.tvDayTemp);
            imgDay = itemView.findViewById(R.id.imgDay);
        }

        @Override
        public void onClick(View view) { }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }
    }
}
