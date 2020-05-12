package fragement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import adapter.Hotel_adapter;
import app.kevs.biyang.getlist.R;
import model.Hotel_model;

public class Hotel extends Fragment {

    Spinner spinner,spinner1;
    LinearLayout calender1, calender2;
    TextView today1, date,button_grid;

    private int mMonth, mYear, mDay;

    Dialog slideDialog;
    Integer image1[] = {R.drawable.c1, R.drawable.c2, R.drawable.c1};
    String tv1[] = {"Goa Beach", " Beach of Trivandrum", "Goa Beach"};
    String tv2[] = {"3 night 4 days", "5 night 6 days", "5 night 6 days"};
    String tv3[] = {"₹ 1999", "₹ 3999", "₹ 1999"};

    RecyclerView rc4;
    Hotel_adapter hotel_adapter;
    private ArrayList<Hotel_model> hotel_model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hotel, container, false);

        today1 = view.findViewById(R.id.today1);
        button_grid = view.findViewById(R.id.button_grid);

        /*calender open onclick*/
        calender1 =  view.findViewById(R.id.calender1);
        calender1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                today1.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });

        /*calender open onclick*/
        calender2 =  view.findViewById(R.id.calender2);
        date = view.findViewById(R.id.date);
        calender2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
                int coMonth = c.get(Calendar.MONTH);
                int coDay = c.get(Calendar.DAY_OF_MONTH);
            }
        });

        /*spinner code*/
        spinner = view.findViewById(R.id.spinner);
        spinner1 = view.findViewById(R.id.spinner1);

        List<String> list = new ArrayList<String>();
        list.add("2 Adults , 3 children");
        list.add("3 children");
        list.add("2 Adults , 3 children");
        list.add("12 Adults , 3 children");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinnergreen,
                R.id.spinner_text1, list);
        spinner.setAdapter(dataAdapter);

        List<String> list1 = new ArrayList<String>();
        list1.add("2");
        list1.add("3");
        list1.add("4");
        list1.add("5");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(getActivity(), R.layout.item_spinnergreen1,
                R.id.spinner_text2, list1);
        spinner1.setAdapter(dataAdapter1);

        /*recyclerview use*/
        rc4 = view.findViewById(R.id.rc4);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rc4.setLayoutManager(layoutManager);
        rc4.setItemAnimator(new DefaultItemAnimator());

        hotel_model = new ArrayList<>();

        for (int i = 0; i < image1.length; i++) {
            Hotel_model ab = new Hotel_model(image1[i], tv1[i], tv2[i], tv3[i]);
            hotel_model.add(ab);
        }
        hotel_adapter = new Hotel_adapter(getActivity(), hotel_model);
        rc4.setAdapter(hotel_adapter);

        /*slide dialog open*/
        button_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                slideDialog = new Dialog(getActivity(), R.style.CustomDialogAnimation);
                slideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Setting dialogview
                Window window = slideDialog.getWindow();
              //  window.setGravity(Gravity.BOTTOM);

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                slideDialog.setContentView(R.layout.pop_up);

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                slideDialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation;
                layoutParams.copyFrom(slideDialog.getWindow().getAttributes());

                //int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.480);

                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = height;
                layoutParams.gravity = Gravity.BOTTOM;

                slideDialog.getWindow().setAttributes(layoutParams);
                slideDialog.setCancelable(true);
                slideDialog.setCanceledOnTouchOutside(true);
                slideDialog.show();
            }
        });
            return view;
        }
}
