package me.alfredobejarano.mqttexample

import android.R.anim.fade_in
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import me.alfredobejarano.mqttexample.databinding.ActivityMainBinding
import me.alfredobejarano.mqttexample.datasource.MqttDataSource
import me.alfredobejarano.mqttexample.model.MqttResult
import org.json.JSONObject
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.*

const val MQTT_SERVER = "192.168.10.43:1883"
const val MQTT_TOPIC = "oran"
const val MQTT_TOPIC2 = "stoppingTime"
const val MQTT_TOPIC3 = "workingTime"

class MainActivity : AppCompatActivity() {
    private var errorSnackbar: Snackbar? = null
    private var mqttDataSource: MqttDataSource? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var chart: LineChart
    private lateinit var lineData: LineData
    private lateinit var oranDataSet: LineDataSet
    private lateinit var stoppingTimeDataSet: LineDataSet
    private lateinit var workingTimeDataSet: LineDataSet

    private lateinit var mqttMessageReceivedTextView1: TextView
    private lateinit var mqttMessageReceivedTextView2: TextView
    private lateinit var mqttMessageReceivedTextView3: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        mqttMessageReceivedTextView1 = findViewById(R.id.mqttMessageReceivedTextView)
        mqttMessageReceivedTextView2 = findViewById(R.id.mqttMessageReceivedTextView2)
        mqttMessageReceivedTextView3 = findViewById(R.id.mqttMessageReceivedTextView3)

        // Initialize the chart
        chart = findViewById(R.id.chart)

        // Create empty data sets
        oranDataSet = LineDataSet(ArrayList(), "Oran")
        oranDataSet.color = ColorTemplate.getHoloBlue()
        oranDataSet.setCircleColor(ColorTemplate.getHoloBlue())
        oranDataSet.lineWidth = 2f
        oranDataSet.circleRadius = 3f
        oranDataSet.setDrawCircleHole(false)
        oranDataSet.valueTextSize = 9f
        oranDataSet.setDrawFilled(true)

        stoppingTimeDataSet = LineDataSet(ArrayList(), "Stopping Time")
        stoppingTimeDataSet.color = Color.YELLOW
        stoppingTimeDataSet.setCircleColor(Color.YELLOW)
        stoppingTimeDataSet.lineWidth = 2f
        stoppingTimeDataSet.circleRadius = 3f
        stoppingTimeDataSet.setDrawCircleHole(false)
        stoppingTimeDataSet.valueTextSize = 9f
        stoppingTimeDataSet.setDrawFilled(true)

        workingTimeDataSet = LineDataSet(ArrayList(), "Working Time")
        workingTimeDataSet.color = Color.GREEN
        workingTimeDataSet.setCircleColor(Color.GREEN)
        workingTimeDataSet.lineWidth = 2f
        workingTimeDataSet.circleRadius = 3f
        workingTimeDataSet.setDrawCircleHole(false)
        workingTimeDataSet.valueTextSize = 9f
        workingTimeDataSet.setDrawFilled(true)

        // Combine data sets
        lineData = LineData(oranDataSet, stoppingTimeDataSet, workingTimeDataSet)

        // Set data to the chart
        chart.data = lineData

        // Customize chart
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.description.isEnabled = false

        val legend = chart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 11f
        legend.textColor = Color.BLACK
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.yOffset = 11f

        errorSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_SHORT)

        mqttDataSource = MqttDataSource(MQTT_SERVER)
        mqttDataSource?.connect(::subscribeToTopics, ::onMqttError)
    }

    private fun onMqttError(result: MqttResult.Failure) {
        binding.mqttProgressBar.visibility = View.GONE
        errorSnackbar?.setText(result.exception?.localizedMessage ?: "")?.show()
    }

    private fun subscribeToTopics() = runOnUiThread {
        mqttDataSource?.subscribeToTopic(MQTT_TOPIC)?.observe(this) { result ->
            when (result) {
                is MqttResult.Failure -> onMqttError(result)
                is MqttResult.Success -> onMqttMessageReceived(result, 1)
                MqttResult.Waiting -> binding.mqttProgressBar.visibility = View.VISIBLE
            }
        }

        mqttDataSource?.subscribeToTopic(MQTT_TOPIC2)?.observe(this) { result ->
            when (result) {
                is MqttResult.Failure -> onMqttError(result)
                is MqttResult.Success -> onMqttMessageReceived(result, 2)
                MqttResult.Waiting -> binding.mqttProgressBar.visibility = View.VISIBLE
            }
        }

        mqttDataSource?.subscribeToTopic(MQTT_TOPIC3)?.observe(this) { result ->
            when (result) {
                is MqttResult.Failure -> onMqttError(result)
                is MqttResult.Success -> onMqttMessageReceived(result, 3)
                MqttResult.Waiting -> binding.mqttProgressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun onMqttMessageReceived(result: MqttResult.Success, topicIndex: Int) {
        binding.mqttProgressBar.visibility = View.GONE
        result.payload?.run {
            val message = String(this)
            Log.d("MQTT", "Received message: $message for topic index: $topicIndex")

            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val jsonObject = JSONObject(message)
                    val value = when (topicIndex) {
                        1 -> jsonObject.getString("workingRatio").toFloat()
                        2 -> jsonObject.getString("totalsecondsStopping").toFloat()
                        3 -> jsonObject.getString("totalsecondsWorking").toFloat()
                        else -> return@launch
                    }

                    withContext(Dispatchers.Main) {
                        val entry = Entry(lineData.entryCount.toFloat(), value)
                        when (topicIndex) {
                            1 -> oranDataSet.addEntry(entry)
                            2 -> stoppingTimeDataSet.addEntry(entry)
                            3 -> workingTimeDataSet.addEntry(entry)
                        }
                        lineData.notifyDataChanged()
                        chart.notifyDataSetChanged()
                        chart.setVisibleXRangeMaximum(20f)
                        chart.moveViewToX(lineData.entryCount.toFloat())
                    }
                } catch (e: Exception) {
                    Log.e("MQTT", "Error parsing message: ${e.localizedMessage}")
                }
            }
        }
    }

    override fun onDestroy() {
        mqttDataSource?.disconnect()
        mqttDataSource = null
        errorSnackbar = null
        super.onDestroy()
    }
}
