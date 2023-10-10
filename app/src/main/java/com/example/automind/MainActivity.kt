package com.example.automind

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.automind.data.AppDatabase
import com.example.automind.data.NoteDao
import com.example.automind.data.Repository
import com.example.automind.data.SettingsDao
import com.example.automind.databinding.ActivityMainBinding
import com.example.automind.ui.record.RecordViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private lateinit var recordViewModel: RecordViewModel

    lateinit var db: RoomDatabase
    lateinit var noteDao: NoteDao
    lateinit var settingsDao: SettingsDao
    lateinit var repository: Repository


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hide the status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recordViewModel = ViewModelProvider(this).get(RecordViewModel::class.java)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white)


        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_hub, R.id.navigation_record, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Set the initial title
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = "AutoMind"

        // Set the title when the destination changes
        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
            when (destination.id) {
                R.id.navigation_hub -> {
                    toolbarTitle.text = "Hub"
                    binding.navView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.primary))
                }
                R.id.navigation_record -> {
                    recordViewModel.hasOriginal = false
                    recordViewModel.hasMarkdown = false
                    recordViewModel.hasSummary = false
                    recordViewModel.hasList = false
                    recordViewModel.originalText.postValue("")
                    recordViewModel.summaryText.postValue("")
                    recordViewModel.listText.postValue("")
                    recordViewModel.markdownContent.postValue("")
                    toolbarTitle.text = "Record"
                    binding.navView.setBackgroundColor(Color.TRANSPARENT)
                }
                R.id.navigation_settings -> {
                    toolbarTitle.text = "Settings"
                    binding.navView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.primary))
                }
                else -> {
                    toolbarTitle.text = "AutoMind"
                    binding.navView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.primary))
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }

        // Initialize the Room database
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, // Provide the database class
            "app-database" // Provide the database name
        ).build()
        noteDao = (db as AppDatabase).noteDao()
        settingsDao = (db as AppDatabase).settingsDao()
        repository = Repository(noteDao,settingsDao)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}