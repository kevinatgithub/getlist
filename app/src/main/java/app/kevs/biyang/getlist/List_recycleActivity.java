package app.kevs.biyang.getlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import adapter.List_adapter;
import model.List_model;

public class List_recycleActivity extends AppCompatActivity {

    private ArrayList<List_model> cryptoListModelClasses;
    private RecyclerView recyclerView;
    private List_adapter bAdapter;


    private String title[] = {"1.Home_page_Activity","2.Result_page_travelum_Activity","3.Hotel_page_travelum_Activity"
    ,"4.Moreroom_detailsActivity","5.Conformation_pageActivity","6.Applied_couponActivity","7.PaymentActivity",
    "8.CouponsActivity","9.ReceiptActivity","10.AccountpageexpandofmyaccountActivity","11.BookingdetailsActivity",
    "12.Open_popup_boxActivity"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recycle);

        recyclerView = findViewById(R.id.List_recycle);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(List_recycleActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        cryptoListModelClasses = new ArrayList<>();

        for (int i = 0; i < title.length; i++) {
            List_model ab = new List_model(title[i]);
            cryptoListModelClasses.add(ab);
        }
        bAdapter = new List_adapter(List_recycleActivity.this, cryptoListModelClasses);
        recyclerView.setAdapter(bAdapter);





    }
}
