package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.kevs.biyang.getlist.R;
import model.Result_page_travelum_model;

public class Result_page_travelum_adapter extends RecyclerView.Adapter<Result_page_travelum_adapter.ViewHolder> {

    Context context;
    ArrayList<Result_page_travelum_model>result_page_travelum_models;

    public Result_page_travelum_adapter(Context context, ArrayList<Result_page_travelum_model> result_page_travelum_models) {
        this.context = context;
        this.result_page_travelum_models = result_page_travelum_models;
    }

    @NonNull
    @Override
    public Result_page_travelum_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result_home_page,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Result_page_travelum_adapter.ViewHolder holder, int position) {

        holder.image1.setImageResource(result_page_travelum_models.get(position).getImage1());
    }

    @Override
    public int getItemCount() {
        return result_page_travelum_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image1;
        public ViewHolder(View itemView) {
            super(itemView);

            image1 = itemView.findViewById(R.id.image1);
        }
    }
}
