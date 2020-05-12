package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.kevs.biyang.getlist.R;
import model.Hotel_model;

public class Hotel_adapter extends RecyclerView.Adapter<Hotel_adapter.ViewHolder> {

    Context context;
    ArrayList<Hotel_model>hotel_models;

    public Hotel_adapter(Context context, ArrayList<Hotel_model> hotel_models) {
        this.context = context;
        this.hotel_models = hotel_models;
    }

    @NonNull
    @Override
    public Hotel_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Hotel_adapter.ViewHolder holder, int position) {

        holder.image1.setImageResource(hotel_models.get(position).getImage1());

        holder.tv1.setText(hotel_models.get(position).getTv1());
        holder.tv2.setText(hotel_models.get(position).getTv2());
        holder.tv3.setText(hotel_models.get(position).getTv3());

    }

    @Override
    public int getItemCount() {
        return hotel_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image1;
        TextView tv1,tv2,tv3;
        public ViewHolder(View itemView) {
            super(itemView);

            image1 = itemView.findViewById(R.id.image1);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
            tv3 = itemView.findViewById(R.id.tv3);
        }
    }
}
