package week11.st5198.fitsense.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight

@Composable
fun HistoryScreen(nav: NavHostController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var historyList by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(true) {
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .collection("workouts")
                .orderBy("timestamp")
                .addSnapshotListener { value, _ ->
                    val items = value?.documents?.map { it.data!! } ?: emptyList()
                    historyList = items
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
                    popUpTo("history") {
                        inclusive = true
                    }
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No workouts yet.\nStart your first workout!",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(historyList) { workout ->
                    val steps = workout["steps"] as? Long ?: 0
                    val distance = workout["distance"] as? Double ?: 0.0
                    val duration = workout["duration"] as? Long ?: 0L
                    val calories = workout["calories"] as? Double ?: 0.0
                    val speed = workout["speed"] as? Double ?: 0.0
                    val timestamp = workout["timestamp"] as? Long ?: 0L

                    val formattedDate = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.CANADA)
                        .format(Date(timestamp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2E9)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.DirectionsWalk,
                                    contentDescription = "Steps",
                                    tint = Color(0xFF1DB954),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Steps: $steps", fontSize = 20.sp, color = Color(0xFF1DB954))
                            }
                            Text("Distance: %.2f km".format(distance), fontSize = 16.sp, color = Color.DarkGray)
                            Text("Calories: %.2f kcal".format(calories), fontSize = 16.sp, color = Color.DarkGray)
                            Text("Duration: %02d:%02d".format(duration / 60000, (duration / 1000) % 60), fontSize = 16.sp, color = Color.DarkGray)
                            Text("Date: $formattedDate", fontSize = 16.sp, color = Color.DarkGray)
                            Text("Speed: %.2f km/h".format(speed), fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}
