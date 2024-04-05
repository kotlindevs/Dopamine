package com.google.android.piyush.dopamine.activities

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.databinding.ActivitySendNotificationsBinding
import com.google.android.piyush.dopamine.databinding.BottomSheetAddNotificationBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.android.piyush.youtube.utilities.Notifications
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SendNotifications : AppCompatActivity() {

    private lateinit var binding: ActivitySendNotificationsBinding
    private val viewModel by viewModels<RealtimeViewModel>()
    private lateinit var adminNotification : AdminNotification

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
            binding.topAppBar.visibility = View.GONE
            binding.notificationView.apply {
                visibility = View.VISIBLE
            }
        }
        val notificationId = dopamineSharedPreferences(applicationContext).getInt("notificationId", 0).toString().toInt()

        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.deleteNotification -> {
                    MaterialAlertDialogBuilder(this@SendNotifications).apply {
                        setTitle("Delete Notification")
                        setMessage("Are you sure you want to delete this notification?")
                        setIcon(R.drawable.delete)
                        setPositiveButton("Delete") { dialog, _ ->
                            viewModel.deleteNotification(notificationId, applicationContext)
                            startActivity(
                                Intent(
                                    applicationContext,
                                    AdminHome::class.java
                                )
                            )
                            dialog.dismiss()
                        }
                        setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }.create().show()
                    true
                }
                else -> false
            }
        }

        if(NetworkUtilities.isNetworkAvailable(applicationContext)){
            viewModel.getNotifications(applicationContext)
            viewModel.listOfNotifications.observe(this) { notifications ->
                when (notifications) {
                    is RealtimeResource.Loading -> {}
                    is RealtimeResource.Success -> {
                        val notificationData = notifications.data
                        binding.notificationView.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(applicationContext)
                            adminNotification = AdminNotification(
                                notifications = notificationData,
                                notificationsClickListener = {
                                    binding.topAppBar.visibility = View.VISIBLE
                                },
                                context = applicationContext
                            )
                            adapter = adminNotification.apply {
                                setNotifications(notificationData)
                            }
                        }
                    }
                    is RealtimeResource.Error -> {
                        Log.e(TAG, notifications.message.toString())
                    }
                }
            }
        }
    }
}

class NotificationBottomSheet : BottomSheetDialogFragment() {

    private var bottomSheetBinding: BottomSheetAddNotificationBinding? = null
    private val viewModel by viewModels<RealtimeViewModel>()

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

            binding.publishNotification.setOnClickListener {
                if(binding.notificationId.text.toString().isEmpty()){
                    binding.notificationIdLayout.error = "Enter Notification ID"
                    binding.notificationIdLayout.isErrorEnabled = true
                }else if(binding.notificationTitle.text.toString().isEmpty()){
                    binding.notificationTitleLayout.error = "Enter Notification Title"
                    binding.notificationTitleLayout.isErrorEnabled = true
                }else if(binding.notificationDescription.text.toString().isEmpty()){
                    binding.notificationDescriptionLayout.error = "Enter Notification Description"
                    binding.notificationDescriptionLayout.isErrorEnabled = true
                }else {
                    val id = binding.notificationId.text.toString().toInt()
                    val title = binding.notificationTitle.text.toString()
                    val description = binding.notificationDescription.text.toString()
                    val date = binding.notificationDate.text.toString()
                    val time = binding.notificationTime.text.toString()
                    val build = binding.notificationBuild.text.toString()
                    val notification = Notifications(
                        id = id,
                        title = title,
                        description = description,
                        date = date,
                        time = time,
                        build = build
                    )

                    viewModel.sendNotification(notification, requireContext())
                    viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
                        when (notifications) {
                            is RealtimeResource.Loading -> {}
                            is RealtimeResource.Success -> {
                                val notificationData = notifications.data
                                Toast.makeText(requireContext(), "Notification Sent", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, notificationData.toString())
                                this.dismiss()
                            }

                            is RealtimeResource.Error -> {
                                Log.e(TAG, notifications.message.toString())
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.notifications.removeObservers(viewLifecycleOwner)
        bottomSheetBinding = null
    }
}

class AdminNotification(
    private var notifications: List<Notifications>?,
    private val notificationsClickListener : (Notifications) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<AdminNotification.NotificationViewHolder>() {

    fun setNotifications(newNotifications : List<Notifications>?) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    class NotificationViewHolder(notificationView : View) : RecyclerView.ViewHolder(notificationView) {
        val title: MaterialTextView = notificationView.findViewById(R.id.title)
        val description: MaterialTextView = notificationView.findViewById(R.id.description)
        val date : MaterialTextView = notificationView.findViewById(R.id.date)
        val time: MaterialTextView = notificationView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_notification_view, parent, false))
    }

    override fun getItemCount(): Int = notifications?.size?: 0

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications?.get(position)
        holder.title.text = notification?.title
        holder.description.text = notification?.description
        holder.date.text = notification?.date
        holder.time.text = notification?.time
        holder.itemView.setOnLongClickListener {
            notificationsClickListener(notification!!)
            dopamineSharedPreferences(context).edit()
                .putInt("notificationId", notification.id)
                .apply()
            true
        }
    }
}