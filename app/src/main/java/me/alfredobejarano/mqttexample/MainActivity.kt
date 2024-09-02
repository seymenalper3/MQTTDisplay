package me.alfredobejarano.mqttexample

import android.R.anim.fade_in
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
import java.util.LinkedList
import java.util.Queue

const val MQTT_SERVER = "192.168.10.43:1883"
const val MQTT_TOPIC = "oran"
const val MQTT_TOPIC2 = "stoppingTime"
const val MQTT_TOPIC3 = "workingTime"



class MainActivity : AppCompatActivity() {
    private var errorSnackbar: Snackbar? = null
    private var mqttDataSource: MqttDataSource? = null
    private lateinit var binding: ActivityMainBinding

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
    private val messageQueue: Queue<Pair<Int, String>> = LinkedList()

    private fun displayNextMessage() {
        if (messageQueue.isNotEmpty()) {
            val (topicIndex, message) = messageQueue.poll()
            when (topicIndex) {
                1 -> {
                    mqttMessageReceivedTextView1.text = message
                    mqttMessageReceivedTextView1.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, fade_in))
                }
                2 -> {
                    mqttMessageReceivedTextView2.text = message
                    mqttMessageReceivedTextView2.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, fade_in))
                }
                3 -> {
                    mqttMessageReceivedTextView3.text = message
                    mqttMessageReceivedTextView3.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, fade_in))
                }
            }

        }
    }


    private fun onMqttMessageReceived(result: MqttResult.Success, topicIndex: Int) {
        binding.mqttProgressBar.visibility = View.GONE
        result.payload?.run {
            val message = String(this)
            Log.d("MQTT", "Received message: $message for topic index: $topicIndex")
            when (topicIndex) {
                1 -> {
                    if (message.contains("workingRatio")) {
                        mqttMessageReceivedTextView1.text = message
                    }
                }
                2 -> {
                    if (message.contains("totalsecondsStopping")) {
                        mqttMessageReceivedTextView2.text = message
                    }
                }
                3 -> {
                    if (message.contains("totalsecondsWorking")) {
                        mqttMessageReceivedTextView3.text = message
                    }
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