package app.kevs.biyang.getlist;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import adapter.Result_page_travelum_adapter;
import model.Result_page_travelum_model;

public class Result_page_travelumActivity extends AppCompatActivity {

    Integer image1[] ={R.drawable.img1, R.drawable.img2,R.drawable.img3};

    private RecyclerView recyclerView;
    private Result_page_travelum_adapter result_page_travelum_adapter;
    private ArrayList<Result_page_travelum_model> result_page_travelum_models;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page_travelum);

        /*recycler code use here*/
        recyclerView = findViewById(R.id.rc1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Result_page_travelumActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        result_page_travelum_models = new ArrayList<>();

        for (int i=0;i < image1.length;i++) {
            Result_page_travelum_model ab = new Result_page_travelum_model(image1[i]);
            result_page_travelum_models.add(ab);
        }

        result_page_travelum_adapter = new Result_page_travelum_adapter(Result_page_travelumActivity.this,result_page_travelum_models);
        recyclerView.setAdapter(result_page_travelum_adapter);

    }
}
