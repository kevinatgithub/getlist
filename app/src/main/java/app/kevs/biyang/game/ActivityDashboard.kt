package app.kevs.biyang.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column

class ActivityDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_dashboard_activity)
    }

    var COLUMN : Column? = null
    var CARTESIAN : Cartesian = AnyChart.column()
    var DATA : List<DataEntry> = ArrayList<DataEntry>()
    set(value) {
        field = value
        COLUMN = CARTESIAN.column(value)
    }
}