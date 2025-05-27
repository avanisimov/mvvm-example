package ru.practicum.mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.components.SingletonComponent
import ru.practicum.mvvm.login.LoginScreen
import ru.practicum.mvvm.news.ui.NewsScreen
import ru.practicum.mvvm.ui.theme.MVVMExampleTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MVVMExampleTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController,
                        startDestination = Screens.News.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screens.Login.route) { LoginScreen(navController) }
                        composable(Screens.News.route) { NewsScreen(navController) }
                    }
                }
            }
        }
    }
}

sealed class Screens(val route: String) {
    data object Login : Screens("login")
    data object News : Screens("news")
}