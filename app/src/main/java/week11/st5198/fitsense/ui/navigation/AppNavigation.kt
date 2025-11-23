package week11.st5198.fitsense.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import week11.st5198.fitsense.ui.HomeScreen
import week11.st5198.fitsense.ui.LoginScreen
import week11.st5198.fitsense.ui.RegisterScreen
import week11.st5198.fitsense.ui.HistoryScreen
import week11.st5198.fitsense.ui.TrackingScreen

@Composable
fun AppNavigation(nav: NavHostController) {

    val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = nav,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {

        composable("login") { LoginScreen(nav) }
        composable("register") { RegisterScreen(nav) }
        composable("home") { HomeScreen(nav) }
        composable("tracking") { TrackingScreen(nav) }
        composable("history") { HistoryScreen(nav) }
    }
}