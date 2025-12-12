package com.example.goodhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.goodhabits.dataHabits.AppDatabase
import com.example.goodhabits.dataHabits.HabitRepository
import com.example.goodhabits.dataMeditation.MeditationRepository
import com.example.goodhabits.dataMusic.MusicRepository
import com.example.goodhabits.dataNutrition.NutritionRepository
import com.example.goodhabits.dataTasks.TaskRepository
import com.example.goodhabits.dataUser.UserRepository
import com.example.goodhabits.navigation.AppNavigator
import com.example.goodhabits.ui.theme.GoodHabitsTheme
import com.example.goodhabits.viewmodel.*
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences(packageName + "_osmdroid", MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = packageName

        val database = AppDatabase.getDatabase(this)
        val habitRepository = HabitRepository(database.habitDao(), database.habitLogDao())
        val nutritionRepository = NutritionRepository(database.nutritionDao())
        val userRepository = UserRepository(database.userDao())
        val meditationRepository = MeditationRepository(database.meditationDao())
        val taskRepository = TaskRepository(database.taskDao(), database.taskLogDao())
        val musicRepository = MusicRepository()

        val habitViewModel: HabitViewModel by lazy { ViewModelProvider(this, HabitViewModelFactory(habitRepository))[HabitViewModel::class.java] }
        val nutritionViewModel: NutritionViewModel by lazy { ViewModelProvider(this, NutritionViewModelFactory(nutritionRepository))[NutritionViewModel::class.java] }
        val userViewModel: UserViewModel by lazy { ViewModelProvider(this, UserViewModelFactory(userRepository))[UserViewModel::class.java] }
        val progressViewModel: ProgressViewModel by lazy { ViewModelProvider(this, ProgressViewModelFactory(habitRepository))[ProgressViewModel::class.java] }
        val meditationViewModel: MeditationViewModel by lazy { ViewModelProvider(this, MeditationViewModelFactory(meditationRepository))[MeditationViewModel::class.java] }
        val taskViewModel: TaskViewModel by lazy { ViewModelProvider(this, TaskViewModelFactory(taskRepository))[TaskViewModel::class.java] }
        val exerciseViewModel: ExerciseViewModel by lazy { ViewModelProvider(this, ExerciseViewModelFactory(musicRepository))[ExerciseViewModel::class.java] }

        setContent {
            GoodHabitsTheme {
                 AppNavigator(
                    habitViewModel = habitViewModel,
                    nutritionViewModel = nutritionViewModel,
                    userViewModel = userViewModel,
                    progressViewModel = progressViewModel,
                    meditationViewModel = meditationViewModel,
                    taskViewModel = taskViewModel,
                    exerciseViewModel = exerciseViewModel
                )
            }
        }
    }
}