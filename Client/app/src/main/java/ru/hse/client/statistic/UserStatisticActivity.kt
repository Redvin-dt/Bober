package ru.hse.client.statistic

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.google.android.material.progressindicator.LinearProgressIndicator.IndicatorDirection
import com.google.protobuf.value
import com.patrykandpatrick.vico.core.DefaultDimens
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.DefaultAxisValueFormatter
import com.patrykandpatrick.vico.core.axis.formatter.PercentageFormatAxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.layout.HorizontalLayout
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.extension.half
import com.patrykandpatrick.vico.core.marker.Marker
import okhttp3.OkHttpClient
import ru.hse.client.R
import ru.hse.client.databinding.ActivityGroupSelectMenuBinding
import ru.hse.client.databinding.ActivityUserStatisticBinding
import ru.hse.client.utility.DrawerBaseActivity
import ru.hse.server.proto.EntitiesProto
import ru.hse.server.proto.EntitiesProto.GroupTestPercent
import ru.hse.server.proto.EntitiesProto.UserStatistic
import kotlin.collections.ArrayList
import kotlin.math.log


class UserStatisticActivity : DrawerBaseActivity() {
    private val startAxisValueFormatter = DefaultAxisValueFormatter<AxisPosition.Vertical.Start>()
    //private val startAxisValueFormatter = PercentageFormatAxisValueFormatter<AxisPosition.Vertical.Start>()
    private val startAxisItemPlacer = AxisItemPlacer.Vertical.default(MAX_START_AXIS_ITEM_COUNT)
    private val bottomAxisItemPlacer = AxisItemPlacer.Horizontal.default(BOTTOM_AXIS_ITEM_SPACING, BOTTOM_AXIS_ITEM_OFFSET)

    private val okHttpClient = OkHttpClient()
    private lateinit var binding : ActivityUserStatisticBinding

    private val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()
    private var groupNameByValue: MutableMap<Int, String> = HashMap<Int, String>().toMutableMap()
    private var dateByIndex : MutableMap<Int, String> = HashMap<Int, String>().toMutableMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allocateActivityTitle("Statistic")

        val groupChartView = binding.groupChartFrame.chartView
        val dateChartView = binding.dayChartFrame.chartView

        val groupHorizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> {
            value, _ -> if (groupNameByValue.containsKey(value.toInt())) groupNameByValue[value.toInt()].toString() else "error"
        }

        chartEntryModelProducer.setEntries(getGroupStatisticAsEntries())
        with(groupChartView) {
            runInitialAnimation = true
            entryProducer = chartEntryModelProducer
            with(bottomAxis as HorizontalAxis) {
                itemPlacer = bottomAxisItemPlacer
                valueFormatter = groupHorizontalAxisValueFormatter
                label = textComponent {
                    color = Color.BLACK
                    textSizeSp = 25f
                    typeface = Typeface.MONOSPACE
                    ellipsize = TextUtils.TruncateAt.END
                }
                guideline?.color = Color.GRAY
            }

            with(startAxis as VerticalAxis) {
                itemPlacer = startAxisItemPlacer
                valueFormatter = startAxisValueFormatter
                label = textComponent {
                    color = Color.BLACK
                    textSizeSp = 25f
                    typeface = Typeface.MONOSPACE
                    ellipsize = TextUtils.TruncateAt.START
                }
                valueFormatter = AxisValueFormatter { value, _ ->  value.toInt().toString() }
            }
        }


        val dateHorizontalAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> {
            value, _ -> if (dateByIndex.containsKey(value.toInt())) dateByIndex[value.toInt()].toString() else "error"
        }

        chartEntryModelProducer.setEntries(getDateStatisticAsEntries())
        with(dateChartView) {
            runInitialAnimation = true
            entryProducer = chartEntryModelProducer
            with(bottomAxis as HorizontalAxis) {
                itemPlacer = bottomAxisItemPlacer
                valueFormatter = dateHorizontalAxisValueFormatter
                label = textComponent {
                    color = Color.BLACK
                    textSizeSp = 25f
                    typeface = Typeface.MONOSPACE
                    ellipsize = TextUtils.TruncateAt.END
                }
                guideline?.color = Color.GRAY
            }

            with(startAxis as VerticalAxis) {
                itemPlacer = startAxisItemPlacer
                valueFormatter = startAxisValueFormatter
                label = textComponent {
                    color = Color.BLACK
                    textSizeSp = 25f
                    typeface = Typeface.MONOSPACE
                    ellipsize = TextUtils.TruncateAt.START
                }
                valueFormatter = AxisValueFormatter { value, _ ->  value.toInt().toString() }
            }
        }
    }

    private fun getDateStatisticAsEntries() : List<FloatEntry> {
        dateByIndex = HashMap<Int, String>().toMutableMap()
        val userStatistic: UserStatistic? = getTestByDateStatistic(this@UserStatisticActivity, okHttpClient);

        val dateTestCountList = userStatistic?.dateTestCountList?.dateTestCountList?.toMutableList()
        if (dateTestCountList == null) {
            Log.e("UserStatisticActivity", "failed to get statistics")
            return listOf()
        }

        dateTestCountList.add(EntitiesProto.DateTestCount.newBuilder().setDate("11-01-2024").setTestCount(5).build())

        val entries : MutableList<FloatEntry> = ArrayList<FloatEntry>().toMutableList()
        for ((index, test) in dateTestCountList.withIndex()) {
            dateByIndex[index] = test.date
            entries.add(FloatEntry(index.toFloat(), test.testCount.toFloat()))
        }

        return entries
    }

    private fun getGroupStatisticAsEntries() : List<FloatEntry> {
        groupNameByValue = HashMap<Int, String>().toMutableMap()
        val userStatistic: UserStatistic? = getTestByGroupStatistic(this@UserStatisticActivity, okHttpClient);

        val groupTestPercentList = userStatistic?.groupTestPercentList?.groupTestPercentList?.toMutableList()
        if (groupTestPercentList == null) {
            Log.e("UserStatisticActivity", "failed to get statistics")
            return listOf()
        }

        Log.i("UserStatistic", "try to add test") // TODO: remove
        groupTestPercentList.add(0, GroupTestPercent.newBuilder().setGroupName("hui").setTestPercent(50).build()) // TODO: remove
        groupTestPercentList.add(0, GroupTestPercent.newBuilder().setGroupName("hui1").setTestPercent(50).build()) // TODO: remove
        groupTestPercentList.add(0, GroupTestPercent.newBuilder().setGroupName("hui2").setTestPercent(25).build()) // TODO: remove
        groupTestPercentList.add(0, GroupTestPercent.newBuilder().setGroupName("hui3").setTestPercent(100).build()) // TODO: remove
        groupTestPercentList.add(0, GroupTestPercent.newBuilder().setGroupName("hui4").setTestPercent(75).build()) // TODO: remove

        for (i in 1..10) {
            groupTestPercentList.add(GroupTestPercent.newBuilder().setGroupName("pizda$i").setTestPercent((i * 10).toLong()).build())
        }

        Log.i("UserStatistic", "start set entities")
        val entries : MutableList<FloatEntry> = ArrayList<FloatEntry>().toMutableList()
        for ((index, testPercent) in groupTestPercentList.withIndex()) {
            groupNameByValue[index] = testPercent.groupName
            entries.add(FloatEntry(index.toFloat(), testPercent.testPercent.toFloat()))
        }

        Log.i("UserStatistic", "return entities")

        return entries;
    }

    companion object {
        private const val MAX_START_AXIS_ITEM_COUNT = 5
        private const val BOTTOM_AXIS_ITEM_SPACING = 1
        private const val BOTTOM_AXIS_ITEM_OFFSET = 5
        private const val MAX_BOTTOM_AXIS_ITEM_COUNT = 5
    }

}
