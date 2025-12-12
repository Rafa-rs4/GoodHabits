package com.example.goodhabits.dataHabits

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.goodhabits.dataMeditation.MeditationActivity
import com.example.goodhabits.dataMeditation.MeditationDao
import com.example.goodhabits.dataNutrition.MealPlan
import com.example.goodhabits.dataNutrition.NutritionDao
import com.example.goodhabits.dataTasks.Task
import com.example.goodhabits.dataTasks.TaskDao
import com.example.goodhabits.dataTasks.TaskLog
import com.example.goodhabits.dataTasks.TaskLogDao
import com.example.goodhabits.dataUser.User
import com.example.goodhabits.dataUser.UserDao

@Database(entities = [Habit::class, HabitLog::class, Badge::class, MealPlan::class, User::class, MeditationActivity::class, Task::class, TaskLog::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun badgeDao(): BadgeDao
    abstract fun nutritionDao(): NutritionDao
    abstract fun userDao(): UserDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun meditationDao(): MeditationDao
    abstract fun taskDao(): TaskDao
    abstract fun taskLogDao(): TaskLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "good_habits_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
