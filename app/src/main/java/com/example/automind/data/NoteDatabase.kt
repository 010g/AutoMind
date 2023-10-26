        package com.example.automind.data

        import android.content.Context
        import androidx.room.Database
        import androidx.room.Room
        import androidx.room.RoomDatabase

        @Database(entities = [Note::class, Setting::class], version = 1, exportSchema = false)
        abstract class AppDatabase : RoomDatabase() {
            abstract fun noteDao(): NoteDao
            abstract fun settingsDao(): SettingsDao

            companion object {
                private var INSTANCE: AppDatabase? = null

                fun getDatabase(context: Context): AppDatabase {
                    val tempInstance = INSTANCE
                    if (tempInstance != null) {
                        return tempInstance
                    }
                    synchronized(this) {
                        val instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "app_database"
                        ).build()
                        INSTANCE = instance
                        return instance
                    }
                }
            }
        }