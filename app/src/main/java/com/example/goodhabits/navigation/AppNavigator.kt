package com.example.goodhabits.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.goodhabits.screens.*
import com.example.goodhabits.viewmodel.*

@Composable
fun AppNavigator(
    habitViewModel: HabitViewModel,
    nutritionViewModel: NutritionViewModel,
    userViewModel: UserViewModel,
    progressViewModel: ProgressViewModel,
    meditationViewModel: MeditationViewModel,
    taskViewModel: TaskViewModel,
    exerciseViewModel: ExerciseViewModel
) {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onSplashFinished = {
                navController.navigate("login") { popUpTo("splash") { inclusive = true } }
            })
        }

        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { userName ->
                    navController.navigate("main/$userName") { popUpTo("login") { inclusive = true } }
                }
            )
        }

        composable("main/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"

            MainScreen(
                habitViewModel = habitViewModel,
                nutritionViewModel = nutritionViewModel,
                progressViewModel = progressViewModel,
                meditationViewModel = meditationViewModel,
                taskViewModel = taskViewModel,
                exerciseViewModel = exerciseViewModel,
                userName = userName,
                onNavigateToLogin = {
                    navController.navigate("login") { popUpTo("main/$userName") { inclusive = true } }
                },
                onNavigateToSettings = { navController.navigate("settings/$userName") } // Pass username
            )
        }

        composable("settings/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "User"
            SettingsScreen(userViewModel = userViewModel, currentUsername = userName) 
        }
    }
}