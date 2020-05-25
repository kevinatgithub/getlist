package app.kevs.biyang.getlist.libs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import app.kevs.biyang.getlist.R

object TravelumHelper {

    fun ShowDialog(ctx : Context,
                   layout : Int,
                   height : Double = 0.480,
                   onAssignUIEventHandlers : (dialog : Dialog) -> Unit) : Dialog{
        val slideDialog = Dialog(ctx, R.style.CustomDialogAnimation)
        slideDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Setting dialogview
        // Setting dialogview
        val window: Window = slideDialog.getWindow()!!
        //  window.setGravity(Gravity.BOTTOM);

        //  window.setGravity(Gravity.BOTTOM);
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        slideDialog.setContentView(layout)

        val layoutParams = WindowManager.LayoutParams()
        slideDialog.getWindow()?.getAttributes()?.windowAnimations = R.style.CustomDialogAnimation
        layoutParams.copyFrom(slideDialog.getWindow()?.getAttributes())

        //int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        //int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        val height = (ctx.resources.getDisplayMetrics().heightPixels * height)

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = height.toInt()
        layoutParams.gravity = Gravity.BOTTOM

        slideDialog.getWindow()?.setAttributes(layoutParams)
        slideDialog.setCancelable(true)
        slideDialog.setCanceledOnTouchOutside(true)
        slideDialog.show()

        onAssignUIEventHandlers(slideDialog)
        return slideDialog
    }
}