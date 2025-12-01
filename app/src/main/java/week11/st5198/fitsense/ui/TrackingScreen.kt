package week11.st5198.fitsense.ui

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TrackingScreen(nav: NavHostController) {
    val context = LocalContext.current
    val activity = context as ComponentActivity

    var steps by remember { mutableStateOf(0) }
    var lastStepCount by remember { mutableStateOf(0) }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var stepDetectorSensor: Sensor? = null

    var isTracking by remember { mutableStateOf(true) }
    var duration by remember { mutableStateOf(0L) }
    var distance by remember { mutableStateOf(0f) }
    var calories by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(0f) }

    var pauseCount by remember { mutableStateOf(0) }
    var wasMoving by remember { mutableStateOf(false) }

    var screenOff by remember { mutableStateOf(false) }
    var showEndWorkoutOptions by remember { mutableStateOf(false) }

    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    val permission = rememberPermissionState(permission = Manifest.permission.ACTIVITY_RECOGNITION)
    LaunchedEffect(Unit) { permission.launchPermissionRequest() }

    val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let {
                if (it.sensor.type == Sensor.TYPE_STEP_DETECTOR && permission.status.isGranted && isTracking) {
                    steps += 1
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    LaunchedEffect(isTracking, permission.status.isGranted) {
        if (isTracking && permission.status.isGranted) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            if (stepDetectorSensor != null) {
                sensorManager.registerListener(
                    stepListener,
                    stepDetectorSensor,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            } else {
                Toast.makeText(context, "No Step Detector Sensor found!", Toast.LENGTH_SHORT).show()
            }
        } else {
            sensorManager.unregisterListener(stepListener)
        }
    }

    LaunchedEffect(isTracking) {
        while (isTracking) {
            delay(1000)
            val moving = steps > lastStepCount

            if (wasMoving && !moving) {
                pauseCount += 1
            }
            wasMoving = moving

            if (moving) {
                duration += 1000
                distance = steps * 0.0008f
                calories = steps * 0.04f
                speed = if (duration > 0) (distance / (duration / 3600000f)) else 0f
            }

            lastStepCount = steps
        }
    }

    fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    if (!screenOff) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1DB954), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(
                    "Live Workout",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(2.dp, Color(0xFFFFA726), RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF3E0)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = "Steps",
                        tint = Color(0xFFFFA726),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "$steps",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFA726)
                    )
                    Text(
                        text = "Steps",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFFA726)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = "Duration", tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Duration", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            formatDuration(duration),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = "Distance", tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Distance", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            String.format("%.2f km", distance),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsRun, contentDescription = "Speed", tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Speed", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            String.format("%.2f km/h", speed),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = "Calories", tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("Calories", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            String.format("%.2f kcal", calories),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Workout Status", tint = Color.Black)
                        Spacer(Modifier.width(4.dp))
                        Text("Workout Detail", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                    Spacer(Modifier.height(4.dp))
                    val paused = !isTracking || steps == lastStepCount
                    Text(
                        text = if (paused) "Paused: Yes" else "Paused: No",
                        color = if (paused) Color.Red else Color(0xFF1DB954),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Pauses: $pauseCount", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            if (!showEndWorkoutOptions) {
                Button(
                    onClick = { showEndWorkoutOptions = true; isTracking = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Text("End Workout", color = Color.White, fontSize = 20.sp)
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            if (userId != null) {
                                val workout = hashMapOf(
                                    "steps" to steps,
                                    "duration" to duration,
                                    "distance" to distance,
                                    "calories" to calories,
                                    "speed" to speed,
                                    "pauseCount" to pauseCount,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                db.collection("users").document(userId).collection("workouts").add(workout)
                            }
                            steps = 0
                            lastStepCount = 0
                            duration = 0
                            distance = 0f
                            calories = 0f
                            speed = 0f
                            pauseCount = 0
                            wasMoving = false
                            isTracking = false
                            showEndWorkoutOptions = false
                            nav.navigate("history")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                    ) {
                        Text("Save Workout", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            steps = 0
                            lastStepCount = 0
                            duration = 0
                            distance = 0f
                            calories = 0f
                            speed = 0f
                            pauseCount = 0
                            wasMoving = false
                            isTracking = false
                            showEndWorkoutOptions = false
                            nav.navigate("home") { popUpTo("tracking") { inclusive = true } }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.Red)
                    ) {
                        Text("Discard", color = Color.Red)
                    }
                }
            }
        }
    }
}
