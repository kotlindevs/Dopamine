package com.google.android.piyush.dopamine.activities

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivitySendNotificationsBinding
import com.google.android.piyush.dopamine.databinding.BottomSheetAddNotificationBinding
import com.google.android.piyush.dopamine.utilities.Utilities
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SendNotifications : AppCompatActivity() {

    private lateinit var binding: ActivitySendNotificationsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySendNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.coordinatorLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.topAppBar.setNavigationOnClickListener {

        }

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.addNotification -> {
                    val addNotification = NotificationBottomSheet()
                    addNotification.show(supportFragmentManager, addNotification.tag)
                    true
                }
                else -> false
            }
        }
    }
}

class NotificationBottomSheet : BottomSheetDialogFragment() {

    private lateinit var bottomSheetBinding: BottomSheetAddNotificationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_add_notification, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BottomSheetAddNotificationBinding.bind(view)
        if(!binding.equals(null)) {
            bottomSheetBinding = binding

            val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")).toString()
            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")).toString()
            binding.notificationDate.setText(currentDate)
            binding.notificationTime.setText(currentTime)
            binding.notificationBuild.setText(
                Utilities.PRE_RELEASE_VERSION
            )
        }
    }
}