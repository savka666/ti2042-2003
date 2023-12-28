import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class MainActivity : AppCompatActivity() {
    private lateinit var mqttManager: MQTTManager
    private lateinit var tvStatusAndHumidity: TextView
    private lateinit var ivGreenIndicator: ImageView
    private lateinit var ivYellowIndicator: ImageView
    private lateinit var ivRedIndicator: ImageView
    private lateinit var btnOff: Button
    private lateinit var btnHumidifier: Button
    private lateinit var btnDehumidifier: Button

    private var humidityValue: Int = 50  // Porcentaje de humedad actual
    private var deviceStatus: Int = 0    // Estado del humidificador (0 es apagado)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar elementos de la interfaz
        tvStatusAndHumidity = findViewById(R.id.tvStatusAndHumidity)
        ivGreenIndicator = findViewById(R.id.ivGreenIndicator)
        ivYellowIndicator = findViewById(R.id.ivYellowIndicator)
        ivRedIndicator = findViewById(R.id.ivRedIndicator)
        btnOff = findViewById(R.id.btnOff)
        btnHumidifier = findViewById(R.id.btnHumidifier)
        btnDehumidifier = findViewById(R.id.btnDehumidifier)

        // Crear instancia de MQTTManager
        mqttManager = MQTTManager("tcp://mqtt.eclipse.org:1883", "AndroidApp")
        mqttManager.connect()

        // Suscripción a temas relevantes
        mqttManager.subscribe("humidity", HumidityCallback())

        // Configurar acciones para los botones
        btnOff.setOnClickListener { mqttManager.publish("device/control", "OFF") }
        btnHumidifier.setOnClickListener { deviceStatus = 1 }
        btnDehumidifier.setOnClickListener { deviceStatus = -1 }

        // Lanzar la operación del dispositivo en algún punto relevante
        GlobalScope.launch(context = Dispatchers.Main) {
            deviceOperation(1000)
        }
    }

    // Callback para manejar mensajes de MQTT sobre la humedad
    private inner class HumidityCallback : MqttCallback {
        override fun connectionLost(cause: Throwable?) {
            // Lógica de manejo de pérdida de conexión
        }

        override fun messageArrived(topic: String?, message: MqttMessage?) {
            // Lógica para manejar nuevos mensajes de humedad
            val humidityMessage = String(message?.payload ?: byteArrayOf())
            // Actualizar la interfaz con el mensaje de humedad
            tvStatusAndHumidity.text = humidityMessage
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            // Lógica de manejo de entrega completa
        }
    }

    private suspend fun deviceOperation(sleepTime: Long) {
        while (true) {
            humidityValue += 5 * deviceStatus
            if (humidityValue > 100) humidityValue = 100
            else if (humidityValue < 0) humidityValue = 0
            val humidityStatus: String = when {
                humidityValue < 15 -> "RED-"
                humidityValue < 30 -> "YELLOW-"
                humidityValue < 65 -> "GREEN"
                humidityValue < 75 -> "YELLOW+"
                else -> "RED+"
            }
            mqttManager.publish("humidity", humidityStatus)
            delay(sleepTime)
        }
    }
}
