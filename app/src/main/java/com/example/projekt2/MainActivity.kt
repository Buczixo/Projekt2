package com.example.projekt2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projekt2.ui.theme.Projekt2Theme
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// Wersja z trzema ekranami i przerzucaniem danych
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Projekt2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Nav()
                }
            }
        }
    }
}



@Composable
fun Nav(){
    val navCont = rememberNavController()
    NavHost(navController = navCont, startDestination = "A"){
        composable(route = "A"){
            GreetingWithInput(onNavigateToScreen2 = {
                navCont.navigate("second_screen/$it")
            })
        }
        composable(
            "second_screen/{param}", arguments = listOf
                (navArgument("param"){
                type = NavType.StringType
            }))
        {
            val param = it.arguments?.getString("param") ?:""
            EkranDrugi(param = param, navCont )
        }
        composable("third_screen"){
            EkranTrzeci(navCont)
        } }
}

@Composable
fun LightSensorBar() {
    val context = LocalContext.current
    var lightIntensity by remember { mutableStateOf(0f) }

    var sensorManager: SensorManager? = null
    var lightSensor: Sensor? = null

    DisposableEffect(context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    lightIntensity = it.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Do nothing for now
            }
        }

        sensorManager?.registerListener(sensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RectangleShape)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RectangleShape)
                    .background(Color.Yellow.copy(alpha = lightIntensity / 10000))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Light Intensity: $lightIntensity", style = MaterialTheme.typography.bodySmall)
    }


@Preview(showBackground = true)
@Composable
fun LightSensorBarPreview() {
    Projekt2Theme {
        LightSensorBar()
    }
}

@Composable
fun AccelerometerBar(direction : String) {
    val context = LocalContext.current
    var rotation by remember { mutableStateOf(0f) }

    var sensorManager: SensorManager? = null
    var gyroscopeSensor: Sensor? = null

    DisposableEffect(context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    if (direction == "X") 
                        rotation = event.values[0]
                    else if (direction == "Y")
                        rotation = event.values[1]
                    else if (direction == "Z")
                        rotation = event.values[2]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Do nothing for now
            }
        }

        sensorManager?.registerListener(
            sensorEventListener,
            gyroscopeSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Color.Blue.copy(
                            alpha = (rotation
                                .roundToInt()
                                .toFloat().absoluteValue / 20)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Rotation " + direction + " to: $rotation", style = MaterialTheme.typography.bodySmall)
    }

@Preview(showBackground = true)
@Composable
fun AccelerometerBarPreview() {
    Projekt2Theme {
        AccelerometerBar("X")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreetingWithInput(onNavigateToScreen2: (String) -> Unit) {
    var textFieldState by remember {
        mutableStateOf(" ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),

        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LightSensorBar()

        Button(onClick = { onNavigateToScreen2(textFieldState) }) {
            Text(text = "Przejdź na drugą stronę światła")
        }

        // Display the text

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Projekt2Theme {
        Nav()
    }
}

@Composable
fun  EkranDrugi(param: String, navController: NavController){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),

        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Witaj na ekranie 2",
            modifier = Modifier.clickable {  },
        )
        AccelerometerBar("X")
        Button(onClick = { navController.navigate("third_screen") }) {
            Text(text = "Idz do ekranu nr. 3")
        }
    }
}

@Composable
fun  EkranTrzeci(navController: NavController){
    val context = LocalContext.current
    Column (
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ){
        Text(
            text = "Ekran trzeci i ostatni"
        )
        Button(onClick = { navController.navigate("A") }) {
            Text(text = "Wróć na ekran główny")
        }
        AccelerometerBar(direction = "Y")

        Button(onClick = { Toast.makeText(context, "A może tościka?", Toast.LENGTH_SHORT).show()}
        ) {
            Text(text = "Toster")
        }
    }
}
