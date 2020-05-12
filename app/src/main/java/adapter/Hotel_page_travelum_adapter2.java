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
import model.Hotel_page_travelum_model2;

public class Hotel_page_travelum_adapter2 extends RecyclerView.Adapter<Hotel_page_travelum_adapter2.ViewHolder> {
    Context context;
    ArrayList<Hotel_page_travelum_model2>model2s;

    public Hotel_page_travelum_adapter2(Context context, ArrayList<Hotel_page_travelum_model2> model2s) {
        this.context = context;
        this.model2s = model2s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel_page_travelum2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Hotel_page_travelum_adapter2.ViewHolder holder, int position) {

        holder.image3.setImageResource(model2s.get(position).getImage3());
        holder.tv1.setText(model2s.get(position).getTv1());
        holder.tv2.setText(model2s.get(position).getTv2());
    }

    @Override
    public int getItemCount() {
        return model2s.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image3;
        TextView tv1,tv2;
        public ViewHolder(View itemView) {
            super(itemView);

            image3 = itemView.findViewById(R.id.image3);


            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
        }
    }
}
