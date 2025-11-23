package week11.st5198.fitsense.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import week11.st5198.fitsense.R
import week11.st5198.fitsense.data.AuthRepository

@Composable
fun LoginScreen(nav: NavHostController, repo: AuthRepository = AuthRepository()) {

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(R.drawable.applogo),
            contentDescription = "App Logo",
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "FitSense",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1DB954)
        )

        Text(
            text = "Smart Fitness & Activity Tracker",
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(28.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Gray,
                unfocusedIndicatorColor = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                repo.login(
                    email,
                    pass,
                    onSuccess = {
                        errorMsg = ""
                        nav.navigate("home")
                    },
                    onFailure = {
                        errorMsg = "Account not found. Please create a new one."
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
        ) {
            Text("Login", color = Color.White, fontSize = 18.sp)
        }

        if (errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = errorMsg, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { nav.navigate("register") }) {
            Text("Create Account", color = Color(0xFF1DB954), fontSize = 16.sp)
        }
    }
}
