package app.kevs.biyang.getlist;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import adapter.Hotel_page_travelum_adapter;
import adapter.Hotel_page_travelum_adapter2;
import model.Hotel_page_travelum_model;
import model.Hotel_page_travelum_model2;

public class Hotel_page_travelum extends AppCompatActivity implements View.OnClickListener {

    LinearLayout Calendedr_date;
    TextView text_date;
    private int mMonth, mYear, mDay;

    LinearLayout btn1, btn2, btn3, btn4;
    TextView one, two, three, four;

    Integer image2[] = {R.drawable.aram, R.drawable.bad_room, R.drawable.bad1, R.drawable.aram};
    Integer image3[] = {R.drawable.b3, R.drawable.b1, R.drawable.b3};

    String tv1[] = {"Udai Kothi", "Upre 1982", "Upre 1982"};
    String tv2[] = {"starting at ₹ 1999", "starting at ₹ 3999", "starting at ₹ 3999"};

    private RecyclerView recyclerView;
    private Hotel_page_travelum_adapter hotel_page_travelum_adapter;
    private ArrayList<Hotel_page_travelum_model> hotel_page_travelum_models;

    private RecyclerView recyclerView1;
    private Hotel_page_travelum_adapter2 hotel_page_travelum_adapter2;
    private ArrayList<Hotel_page_travelum_model2> hotel_page_travelum_models2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_page_travelum);
        /*recycler code*/
        recyclerView = findViewById(R.id.recycle1);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Hotel_page_travelum.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        hotel_page_travelum_models = new ArrayList<>();

        for (int i = 0; i < image2.length; i++) {
            Hotel_page_travelum_model ab = new Hotel_page_travelum_model(image2[i]);
            hotel_page_travelum_models.add(ab);
        }
        hotel_page_travelum_adapter = new Hotel_page_travelum_adapter(Hotel_page_travelum.this, hotel_page_travelum_models);
        recyclerView.setAdapter(hotel_page_travelum_adapter);
        /*recycler code similer hotel*/
        recyclerView1 = findViewById(R.id.rc3);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(Hotel_page_travelum.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView1.setLayoutManager(layoutManager1);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

        hotel_page_travelum_models2 = new ArrayList<>();

        for (int i = 0; i < image3.length; i++) {
            Hotel_page_travelum_model2 ab = new Hotel_page_travelum_model2(image3[i], tv1[i], tv2[i]);
            hotel_page_travelum_models2.add(ab);
        }
        hotel_page_travelum_adapter2 = new Hotel_page_travelum_adapter2(Hotel_page_travelum.this, hotel_page_travelum_models2);
        recyclerView1.setAdapter(hotel_page_travelum_adapter2);

//Calender Code
        Calendedr_date = findViewById(R.id.Calendedr_date);
        text_date = findViewById(R.id.text_date);

        Calendedr_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(Hotel_page_travelum.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                text_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });
        /*simple button onclick and background color change*/
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);

        btn1.setBackgroundColor(Color.parseColor("#21549c"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn1:

                one.setTextColor(Color.parseColor("#ffffff"));
                two.setTextColor(Color.parseColor("#9398a0"));
                three.setTextColor(Color.parseColor("#9398a0"));
                four.setTextColor(Color.parseColor("#9398a0"));

                btn1.setBackgroundColor(Color.parseColor("#21549c"));
                btn2.setBackgroundColor(Color.parseColor("#00000000"));
                btn3.setBackgroundColor(Color.parseColor("#00000000"));
                btn4.setBackgroundColor(Color.parseColor("#00000000"));
                break;

            case R.id.btn2:
                one.setTextColor(Color.parseColor("#9398a0"));
                two.setTextColor(Color.parseColor("#ffffff"));
                three.setTextColor(Color.parseColor("#9398a0"));
                four.setTextColor(Color.parseColor("#9398a0"));

                btn1.setBackgroundColor(Color.parseColor("#00000000"));
                btn2.setBackgroundColor(Color.parseColor("#21549c"));
                btn3.setBackgroundColor(Color.parseColor("#00000000"));
                btn4.setBackgroundColor(Color.parseColor("#00000000"));
                break;

            case R.id.btn3:
                one.setTextColor(Color.parseColor("#9398a0"));
                two.setTextColor(Color.parseColor("#9398a0"));
                three.setTextColor(Color.parseColor("#ffffff"));
                four.setTextColor(Color.parseColor("#9398a0"));

                btn1.setBackgroundColor(Color.parseColor("#00000000"));
                btn2.setBackgroundColor(Color.parseColor("#00000000"));
                btn3.setBackgroundColor(Color.parseColor("#21549c"));
                btn4.setBackgroundColor(Color.parseColor("#00000000"));
                break;

            case R.id.btn4:
                one.setTextColor(Color.parseColor("#9398a0"));
                two.setTextColor(Color.parseColor("#9398a0"));
                three.setTextColor(Color.parseColor("#9398a0"));
                four.setTextColor(Color.parseColor("#ffffff"));

                btn1.setBackgroundColor(Color.parseColor("#00000000"));
                btn2.setBackgroundColor(Color.parseColor("#00000000"));
                btn3.setBackgroundColor(Color.parseColor("#00000000"));
                btn4.setBackgroundColor(Color.parseColor("#21549c"));
                break;
        }
    }
}