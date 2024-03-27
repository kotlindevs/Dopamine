package com.google.android.piyush.dopamine.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivityAppNotificationViewBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.youtube.utilities.NotificationViewModel
import com.google.android.piyush.youtube.utilities.Notifications
import com.google.android.piyush.youtube.utilities.YoutubeResource

class AppNotificationView : AppCompatActivity() {
    private lateinit var binding: ActivityAppNotificationViewBinding
    private lateinit var notificationViewModel: NotificationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAppNotificationViewBinding.inflate(layoutInflater)
        notificationViewModel = NotificationViewModel()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(NetworkUtilities.isNetworkAvailable(this).equals(true)){
            notificationViewModel.notifications.observe(this){ notifications ->
                when(notifications){
                    is YoutubeResource.Loading -> {}
                    is YoutubeResource.Success -> {
                        binding.recyclerView.apply {
                            layoutManager = LinearLayoutManager(this@AppNotificationView)
                            adapter = NotificationAdapter(notifications.data)
                        }
                    }
                    is YoutubeResource.Error -> {
                        ToastUtilities.showToast(this, notifications.exception.message.toString())
                    }
                }
            }
        }
    }
}


class NotificationAdapter(private val notifications : List<Notifications>?) :
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
    }
}