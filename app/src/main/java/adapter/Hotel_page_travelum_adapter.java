package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.kevs.biyang.game.R;
import model.Hotel_page_travelum_model;

public class Hotel_page_travelum_adapter extends RecyclerView.Adapter<Hotel_page_travelum_adapter.ViewHolder> {

    Context context;
    ArrayList<Hotel_page_travelum_model> modelArrayList;

    public Hotel_page_travelum_adapter(Context context, ArrayList<Hotel_page_travelum_model> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel_page_travelum,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.image2.setImageResource(modelArrayList.get(position).getImage2());
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image2;
        public ViewHolder(View itemView) {
            super(itemView);

            image2 = itemView.findViewById(R.id.image2);
        }
    }
}
