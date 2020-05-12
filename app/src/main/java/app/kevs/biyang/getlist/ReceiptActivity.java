package app.kevs.biyang.getlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import adapter.Recipt_adapter;
import model.Recipt_model;

public class ReceiptActivity extends AppCompatActivity {

    String title[] = {"Mehrangarh Fort", "Sajjangarh Fort", "Sajjangarh Fort"};
    String detaile[] = {"Mehrangarh Fort is located in Jodhpur, the second largest city in Rajasthan state, in Northern India.",
            "Sajjangarh Fort is located in Jodhpur, the second largest city in Rajasthan state, in Northern India.",
            "Sajjangarh Fort is located in Jodhpur, the second largest city in Rajasthan state, in Northern India."};
    String km[] = {"4 KM", "7.2 KM", "7.2 KM"};

    Integer image1[] = {R.drawable.abc1, R.drawable.def1, R.drawable.img2};

    private RecyclerView recyclerView;
    private Recipt_adapter recipt_adapter;
    private ArrayList<Recipt_model> recipt_models;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        /*recycler code here*/
        recyclerView = findViewById(R.id.reciept);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ReceiptActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recipt_models = new ArrayList<>();

        for (int i = 0; i < title.length; i++) {
            Recipt_model ab = new Recipt_model(title[i], detaile[i], km[i], image1[i]);
            recipt_models.add(ab);
        }
        recipt_adapter = new Recipt_adapter(ReceiptActivity.this, recipt_models);
        recyclerView.setAdapter(recipt_adapter);
    }
}
