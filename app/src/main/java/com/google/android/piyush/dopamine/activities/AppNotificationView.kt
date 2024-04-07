package com.google.android.piyush.dopamine.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAppNotificationViewBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.android.piyush.youtube.utilities.NotificationViewModel
import com.google.android.piyush.youtube.utilities.Notifications
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AppNotificationView : AppCompatActivity() {
    private lateinit var binding: ActivityAppNotificationViewBinding
    private lateinit var notificationViewModel: RealtimeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAppNotificationViewBinding.inflate(layoutInflater)
        notificationViewModel = RealtimeViewModel()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(NetworkUtilities.isNetworkAvailable(this).equals(true)){
            notificationViewModel.getNotifications(this)
            notificationViewModel.listOfNotifications.observe(this){ notifications ->
                when(notifications){
                    is RealtimeResource.Loading -> {}
                    is RealtimeResource.Success -> {
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@AppNotificationView)
                            adapter = NotificationAdapter(context,notifications.data)
                        }
                    }
                    is RealtimeResource.Error -> {
                        ToastUtilities.showToast(this, notifications.message.toString())
                    }
                }
            }
        }


        onBackPressedDispatcher.addCallback {
            startActivity(
                Intent(
                    this@AppNotificationView,
                    DopamineHome::class.java
                )
            )
        }
    }
}


class NotificationAdapter(private val context : Context, private val notifications : List<Notifications>?) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(notificationView : View) : RecyclerView.ViewHolder(notificationView) {
        val title: MaterialTextView = notificationView.findViewById<MaterialTextView>(R.id.title)
        val description: MaterialTextView = notificationView.findViewById<MaterialTextView>(R.id.description)
        val date : MaterialTextView = notificationView.findViewById<MaterialTextView>(R.id.date)
        val time: MaterialTextView = notificationView.findViewById<MaterialTextView>(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_notification_view, parent, false))
    }

    override fun getItemCount(): Int = notifications?.size!!

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications?.get(position)
        holder.title.text = notification?.title
        holder.description.text = notification?.description
        holder.date.text = notification?.date
        holder.time.text = notification?.time
        val databaseViewModel = DatabaseViewModel(context = context )
        databaseViewModel.initializeNotifications()
        val isNotification =  databaseViewModel.checkIsNotificationExist(notificationId = notification?.id!!)
        if(isNotification.equals(false)) {
            databaseViewModel.insertNotification(notification)
        }
    }
}