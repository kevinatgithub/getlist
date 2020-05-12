package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.kevs.biyang.getlist.AccountpageexpandofmyaccountActivity;
import app.kevs.biyang.getlist.Applied_couponActivity;
import app.kevs.biyang.getlist.BookingdetailsActivity;
import app.kevs.biyang.getlist.Conformation_pageActivity;
import app.kevs.biyang.getlist.CouponsActivity;
import app.kevs.biyang.getlist.Home_pageActivity;
import app.kevs.biyang.getlist.Hotel_page_travelum;
import app.kevs.biyang.getlist.Moreroom_detailsActivity;
import app.kevs.biyang.getlist.Open_popup_boxActivity;
import app.kevs.biyang.getlist.PaymentActivity;
import app.kevs.biyang.getlist.ReceiptActivity;
import app.kevs.biyang.getlist.Result_page_travelumActivity;

import java.util.ArrayList;

import app.kevs.biyang.getlist.R;
import model.List_model;

public class List_adapter extends RecyclerView.Adapter<List_adapter.ViewHolder> {
    Context context;
    ArrayList<List_model>list_models;

    public List_adapter(Context context, ArrayList<List_model> list_models) {
        this.context = context;
        this.list_models = list_models;
    }

    @NonNull
    @Override
    public List_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listrecycle,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull List_adapter.ViewHolder holder, final int position) {

        holder.text_list.setText(list_models.get(position).getText_list());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    Intent i = new Intent(context, Home_pageActivity.class);
                    context.startActivity(i); }

                else if (position == 1) {
                    Intent i = new Intent(context, Result_page_travelumActivity.class);
                    context.startActivity(i); }

                else if (position == 2) {
                    Intent i = new Intent(context, Hotel_page_travelum.class);
                    context.startActivity(i); }
                else if (position == 3) {
                    Intent i = new Intent(context, Moreroom_detailsActivity.class);
                    context.startActivity(i); }

                else if (position == 4) {
                    Intent i = new Intent(context, Conformation_pageActivity.class);
                    context.startActivity(i); }

                else if (position == 5) {
                    Intent i = new Intent(context, Applied_couponActivity.class);
                    context.startActivity(i); }

                else if (position == 6) {
                    Intent i = new Intent(context, PaymentActivity.class);
                    context.startActivity(i); }

                else if (position == 7) {
                    Intent i = new Intent(context, CouponsActivity.class);
                    context.startActivity(i); }
                else if (position == 8) {
                    Intent i = new Intent(context, ReceiptActivity.class);
                    context.startActivity(i); }
                else if (position == 9) {
                    Intent i = new Intent(context, AccountpageexpandofmyaccountActivity.class);
                    context.startActivity(i); }
                else if (position == 10) {
                    Intent i = new Intent(context, BookingdetailsActivity.class);
                    context.startActivity(i); }
                else if (position == 11) {
                    Intent i = new Intent(context, Open_popup_boxActivity.class);
                    context.startActivity(i); }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView text_list;
        public ViewHolder(View itemView) {
            super(itemView);

            text_list = itemView.findViewById(R.id.text_list);
        }
    }
}
