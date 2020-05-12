package app.kevs.biyang.getlist;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Open_popup_boxActivity extends AppCompatActivity {

    TextView button_grid;

    Dialog slideDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_popup_box);

        /*onclick open dialogbox google and fb*/
        button_grid = findViewById(R.id.button_grid);

        button_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                slideDialog = new Dialog(Open_popup_boxActivity.this, R.style.CustomDialogAnimation);
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
    }
}



