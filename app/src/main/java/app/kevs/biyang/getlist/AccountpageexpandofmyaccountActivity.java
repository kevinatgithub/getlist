package app.kevs.biyang.getlist;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class AccountpageexpandofmyaccountActivity extends AppCompatActivity {

    LinearLayout  linearLayout,linearLayout1,linearLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountpageexpandofmyaccount);

        linearLayout = findViewById(R.id.linear);
        linearLayout1 = findViewById(R.id.linear1);
        linearLayout2 = findViewById(R.id.linear2);

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayout.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout1.setVisibility(View.GONE);
            }
        });

        linearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayout.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.VISIBLE);
            }
        });
    }
}
