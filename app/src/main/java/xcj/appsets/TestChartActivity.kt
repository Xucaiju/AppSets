package xcj.appsets

import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import kotlinx.android.synthetic.main.activity_test_chart.*
import xcj.appsets.ui.BaseActivity
import xcj.appsets.viewmodel.TodayAppViewModelTest
import java.util.*

class TestChartActivity : BaseActivity() {
lateinit var todayAppViewModel: TodayAppViewModelTest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_chart)
       /* todayAppViewModel = ViewModelProvider(this).get(TodayAppViewModelTest::class.java)
        todayAppViewModel.todayApp?.observe(this){
           it?.let {
               testEditText.setText(it.toString())
           }
        }
        todayAppViewModel.getTodayAppData()*/


        my_test_chart?.apply {
            webLineWidth = 1f
            webColor = getColor(R.color.colorGray)
            webLineWidthInner = 1f
            webColorInner = getColor(R.color.colorGray)
            webAlpha = 100
            description = null
            isRotationEnabled = false
        }


        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        val mv: MarkerView = MarkerView(this, R.layout.custom_marker_view)
        mv.chartView = my_test_chart
        my_test_chart.marker = mv
        setData()
        my_test_chart.animateXY(1400, 1400, Easing.EaseInOutQuad)
        val xAxis: XAxis = my_test_chart.xAxis
        xAxis.textSize = 16f
        xAxis.yOffset = 0f
        xAxis.xOffset = 0f
        xAxis.valueFormatter = object : ValueFormatter() {
            private val mActivities =
                arrayOf("1", "2", "3", "4", "5")

            override fun getFormattedValue(value: Float): String {
                return mActivities[value.toInt() % mActivities.size]
            }
        }
        xAxis.textColor = Color.WHITE
        xAxis.setDrawLabels(true)

        val yAxis: YAxis = my_test_chart.yAxis
        yAxis.setLabelCount(5, true)
        yAxis.textSize = 9f
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 12f
        yAxis.setDrawLabels(true)

        val l: Legend = my_test_chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 50f
        l.textColor = Color.WHITE



    }
    private fun setData() {
        val mul = 80f
        val min = 20f
        val cnt = 5
        val entries1 = ArrayList<RadarEntry>()
        val entries2 = ArrayList<RadarEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        //  for (i in 0 until cnt) {
        val val1 = (Math.random() * mul).toFloat() + min
        entries1.add(RadarEntry(val1))
        // val val2 = (Math.random() * mul).toFloat() + min
        entries2.add(RadarEntry(10f))
        entries2.add(RadarEntry(8f))
        entries2.add(RadarEntry(6f))
        entries2.add(RadarEntry(4f))
        entries2.add(RadarEntry(2f))
        // }
        val set1 = RadarDataSet(entries1, "Last Week")
        set1.color = Color.rgb(103, 110, 129)
        set1.fillColor = Color.rgb(103, 110, 129)
        set1.setDrawFilled(true)
        set1.fillAlpha = 180
        set1.lineWidth = 2f
        set1.isDrawHighlightCircleEnabled = true
        set1.setDrawHighlightIndicators(false)
        val set2 = RadarDataSet(entries2,"AppSets评分").apply {
            color = Color.rgb(12, 188, 105)
            fillColor = Color.rgb(12, 188, 105)
            setDrawFilled(true)
            lineWidth = 2f
            fillAlpha = 60
            isDrawHighlightCircleEnabled = true
            setDrawHighlightIndicators(true)

        }
        set2.color = Color.rgb(121, 162, 175)
        set2.fillColor = Color.rgb(121, 162, 175)
        set2.setDrawFilled(true)
        set2.fillAlpha = 180
        set2.lineWidth = 2f
        set2.isDrawHighlightCircleEnabled = true
        set2.setDrawHighlightIndicators(false)
        val sets = ArrayList<IRadarDataSet>()
        sets.add(set1)
        sets.add(set2)
        val data = RadarData(sets).apply {
            setValueTextSize(8f)
            setDrawValues(true)
            setValueTextColor(Color.WHITE)
        }
        data.setValueTextSize(8f)
        data.setDrawValues(true)
        data.setValueTextColor(Color.WHITE)
        my_test_chart.data = data
        my_test_chart.invalidate()
    }
}

