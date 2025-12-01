package week11.st5198.fitsense.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HistoryScreen(nav: NavHostController) {

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var historyList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var selectedWorkout by remember { mutableStateOf<Map<String, Any>?>(null) }

    LaunchedEffect(true) {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("workouts")
                .orderBy("timestamp")
                .addSnapshotListener { value, _ ->
                    val items = value?.documents?.map { it.data!! } ?: emptyList()
                    historyList = items.reversed()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1DB954), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            IconButton(onClick = {
                nav.navigate("home") {
                    popUpTo("history") { inclusive = true }
                }
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Text(
                "Workout History",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.height(20.dp))

        if (historyList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No workouts yet.\nStart your first workout!",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(historyList) { workout ->

                val steps = workout["steps"] as? Long ?: 0
                val distance = workout["distance"] as? Double ?: 0.0
                val duration = workout["duration"] as? Long ?: 0L
                val calories = workout["calories"] as? Double ?: 0.0
                val speed = workout["speed"] as? Double ?: 0.0
                val pauseCount = workout["pauseCount"] as? Long ?: 0
                val timestamp = workout["timestamp"] as? Long ?: 0L

                val formattedDate = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.CANADA)
                    .format(Date(timestamp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable { selectedWorkout = workout },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE7F5ED)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.30f)
                                .background(Color(0xFFDFF3E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.DirectionsWalk,
                                contentDescription = "Workout",
                                tint = Color(0xFF1DB954),
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Text(
                                "Steps: $steps",
                                fontSize = 20.sp,
                                color = Color(0xFF1DB954),
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Distance: %.2f km".format(distance), fontSize = 16.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(formattedDate, fontSize = 15.sp)
                            }

                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFCCE7FF), RoundedCornerShape(6.dp))
                                    .padding(4.dp)
                            ) {
                                Text(
                                    "Duration: %02d:%02d".format(duration / 60000, (duration / 1000) % 60),
                                    color = Color(0xFF007BFF),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedWorkout != null) {

        val w = selectedWorkout!!
        val steps = w["steps"] as? Long ?: 0
        val distance = w["distance"] as? Double ?: 0.0
        val calories = w["calories"] as? Double ?: 0.0
        val duration = w["duration"] as? Long ?: 0L
        val speed = w["speed"] as? Double ?: 0.0
        val pauseCount = w["pauseCount"] as? Long ?: 0
        val timestamp = w["timestamp"] as? Long ?: 0L
        val formattedDate = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.CANADA)
            .format(Date(timestamp))

        AlertDialog(
            onDismissRequest = { selectedWorkout = null },
            confirmButton = {},
            icon = {
                IconButton(onClick = { selectedWorkout = null }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            },
            title = { Text("Workout Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Steps: $steps")
                    Text("Distance: %.2f km".format(distance))
                    Text("Calories: %.2f kcal".format(calories))
                    Text("Duration: %02d:%02d".format(duration / 60000, (duration / 1000) % 60))
                    Text("Speed: %.2f km/h".format(speed))
                    Text("Pauses: $pauseCount")
                    Text("Date: $formattedDate")
                }
            }
        )
    }
}
