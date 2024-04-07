package com.google.android.piyush.dopamine.fragments

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.piyush.database.viewModel.DatabaseViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.activities.AppNotificationView
import com.google.android.piyush.dopamine.adapters.CustomGridAdapter
import com.google.android.piyush.dopamine.adapters.HomeAdapter
import com.google.android.piyush.dopamine.databinding.FragmentHomeBinding
import com.google.android.piyush.dopamine.utilities.NetworkUtilities
import com.google.android.piyush.dopamine.utilities.ToastUtilities
import com.google.android.piyush.dopamine.utilities.Utilities
import com.google.android.piyush.dopamine.utilities.Utilities.getGreeting
import com.google.android.piyush.dopamine.utilities.dopamineSharedPreferences
import com.google.android.piyush.dopamine.viewModels.RealtimeResource
import com.google.android.piyush.dopamine.viewModels.RealtimeViewModel
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import com.google.android.piyush.youtube.utilities.NotificationViewModel
import com.google.android.piyush.youtube.utilities.YoutubeResource
import com.google.android.piyush.youtube.viewModels.HomeViewModel
import com.google.android.piyush.youtube.viewModels.HomeViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess


class Home : Fragment() {

    private var fragmentHomeBinding : FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repository: YoutubeRepositoryImpl
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    @OptIn(ExperimentalBadgeUtils::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)
        fragmentHomeBinding = binding
        repository = YoutubeRepositoryImpl()
        homeViewModelFactory = HomeViewModelFactory(repository)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        firebaseAuth = FirebaseAuth.getInstance()

        //User details
       /* Log.d(TAG, "User Name  : " +firebaseAuth.currentUser?.displayName.toString())
        Log.d(TAG, "User Email : " +firebaseAuth.currentUser?.email.toString())
        Log.d(TAG, "User Photo : " +firebaseAuth.currentUser?.photoUrl.toString())
        Log.d(TAG, "User Uid   : " +firebaseAuth.currentUser?.uid.toString())
        Log.d(TAG, "User PhoneNumber : "  +firebaseAuth.currentUser?.phoneNumber.toString())
        Log.d(TAG, "User ProviderId : "+firebaseAuth.currentUser?.providerId.toString())
        Log.d(TAG, "IsUserAnonymous : "+firebaseAuth.currentUser?.isAnonymous.toString())
        Log.d(TAG, "IsUserEmailVerified : "+firebaseAuth.currentUser?.isEmailVerified.toString())
        Log.d(TAG, "User ProviderData : "+firebaseAuth.currentUser?.providerData.toString())
        Log.d(TAG, "User Metadata : "+firebaseAuth.currentUser?.metadata.toString()) */

        if(firebaseAuth.currentUser?.email.isNullOrEmpty()){
            Glide.with(requireContext())
                .load(R.drawable.default_user)
                .into(binding.imageView)
            binding.topAppBar.subtitle = firebaseAuth.currentUser?.phoneNumber
        }else{
            Glide.with(requireContext())
                .load(firebaseAuth.currentUser?.photoUrl)
                .into(binding.imageView)
            binding.topAppBar.subtitle = firebaseAuth.currentUser?.displayName
        }

        binding.topAppBar.title = getGreeting()
        binding.topAppBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.notification -> {
                    startActivity(
                        Intent(
                            context,
                            AppNotificationView::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }

        if(NetworkUtilities.isNetworkAvailable(context = requireContext())) {
            val notificationViewModel = RealtimeViewModel()
            val databaseViewModel = DatabaseViewModel(requireContext())
            notificationViewModel.getNotifications(requireContext())
            notificationViewModel.listOfNotifications.observe(viewLifecycleOwner) { notifications ->
                when (notifications) {
                    is RealtimeResource.Loading -> {}
                    is RealtimeResource.Success -> {
                        databaseViewModel.initializeNotifications()
                        val oldNotifications = databaseViewModel.getListOfNotifications()
                        if (oldNotifications.isNotEmpty()) {
                            val newNotifications = notifications.data?.subtract(
                                oldNotifications.toSet()
                            )
                            if (newNotifications != null) {
                                if (newNotifications.isNotEmpty()) {
                                    binding.topAppBar.menu.findItem(R.id.notification).apply {
                                        isVisible = true
                                        setIcon(
                                            R.drawable.unread_notification
                                        )
                                    }
                                    val defaultTap = dopamineSharedPreferences(requireContext()).getBoolean("ExperimentalUserColor",false)
                                    if(!defaultTap) {
                                        TapTargetView.showFor(
                                            requireActivity(),
                                            TapTarget.forView(
                                                binding.topAppBar.findViewById(R.id.notification)!!,
                                                "Dopamine Notifications",
                                                "Notifications are available please tap to see them 😀"
                                            )
                                                .outerCircleColor(R.color.md_theme_light_primaryContainer)
                                                .outerCircleAlpha(0.96f)
                                                .targetCircleColor(R.color.md_theme_light_primary)
                                                .titleTextSize(20)
                                                .titleTextColor(R.color.md_theme_light_onPrimaryContainer)
                                                .descriptionTextSize(10)
                                                .descriptionTextColor(R.color.md_theme_light_onPrimaryContainer)
                                                .textColor(R.color.md_theme_light_onPrimaryContainer)
                                                .textTypeface(Typeface.SANS_SERIF)
                                                .dimColor(R.color.md_theme_light_surface)
                                                .drawShadow(true)
                                                .cancelable(false)
                                                .tintTarget(true)
                                                .transparentTarget(false)
                                                .icon(
                                                    resources.getDrawable(
                                                        R.drawable.unread_notification,
                                                        null
                                                    )
                                                )
                                                .targetRadius(60),
                                            object : TapTargetView.Listener() {
                                                override fun onTargetClick(view: TapTargetView) {
                                                    super.onTargetClick(view)
                                                    startActivity(
                                                        Intent(
                                                            context,
                                                            AppNotificationView::class.java
                                                        )
                                                    )
                                                }
                                            })
                                    }
                                }
                            }
                        }
                    }

                    is RealtimeResource.Error -> {}
                }
            }
        }


        val regionPref = dopamineSharedPreferences(requireContext())
        if(regionPref.getBoolean("saveRegion", false)){
            val code = regionPref.getString("region", "").toString()
            homeViewModel.getHomeVideos(
                regionCode = code
            )
        }else {
            MaterialAlertDialogBuilder(requireContext()).apply {
                this.setTitle("Select your home region")
                this.setIcon(R.drawable.home_region)
                this.setSingleChoiceItems(
                    Utilities.REGIONS,
                    if (regionPref.getString("region", "") == Utilities.DEFAULT_REGION[0]) 0
                    else if (regionPref.getString("region", "") == Utilities.ARGENTINA[0]) 1
                    else if (regionPref.getString("region", "") == Utilities.AUSTRALIA[0]) 2
                    else if (regionPref.getString("region", "") == Utilities.BANGLADESH[0]) 3
                    else if (regionPref.getString("region", "") == Utilities.BRAZIL[0]) 4
                    else if (regionPref.getString("region", "") == Utilities.BHUTAN[0]) 5
                    else if (regionPref.getString("region", "") == Utilities.CANADA[0]) 6
                    else if (regionPref.getString("region", "") == Utilities.CHINA[0]) 7
                    else if (regionPref.getString("region", "") == Utilities.COLOMBIA[0]) 8
                    else if (regionPref.getString("region", "") == Utilities.DENMARK[0]) 9
                    else if (regionPref.getString("region", "") == Utilities.EGYPT[0]) 10
                    else if (regionPref.getString("region", "") == Utilities.FRANCE[0]) 11
                    else if (regionPref.getString("region", "") == Utilities.GERMANY[0]) 12
                    else if (regionPref.getString("region", "") == Utilities.HONG_KONG[0]) 13
                    else if (regionPref.getString("region", "") == Utilities.ISRAEL[0]) 14
                    else if (regionPref.getString("region", "") == Utilities.ITALY[0]) 15
                    else if (regionPref.getString("region", "") == Utilities.IRAN[0]) 16
                    else if (regionPref.getString("region", "") == Utilities.JAPAN[0]) 17
                    else if (regionPref.getString("region", "") == Utilities.JERSEY[0]) 18
                    else if (regionPref.getString("region", "") == Utilities.KENYA[0]) 19
                    else if (regionPref.getString("region", "") == Utilities.KOREA[0]) 20
                    else if (regionPref.getString("region", "") == Utilities.LEBANON[0]) 21
                    else if (regionPref.getString("region", "") == Utilities.MALAYSIA[0]) 22
                    else if (regionPref.getString("region", "") == Utilities.MALDIVES[0]) 23
                    else if (regionPref.getString("region", "") == Utilities.MEXICO[0]) 24
                    else if (regionPref.getString("region", "") == Utilities.MONGOLIA[0]) 25
                    else if (regionPref.getString("region", "") == Utilities.MYANMAR[0]) 26
                    else if (regionPref.getString("region", "") == Utilities.NETHERLANDS[0]) 27
                    else if (regionPref.getString("region", "") == Utilities.NEPAL[0]) 28
                    else if (regionPref.getString("region", "") == Utilities.NEW_ZEALAND[0]) 29
                    else if (regionPref.getString("region", "") == Utilities.NIGERIA[0]) 30
                    else if (regionPref.getString("region", "") == Utilities.NORWAY[0]) 31
                    else if (regionPref.getString("region", "") == Utilities.PAKISTAN[0]) 32
                    else if (regionPref.getString("region", "") == Utilities.PANAMA[0]) 33
                    else if (regionPref.getString("region", "") == Utilities.PARAGUAY[0]) 34
                    else if (regionPref.getString("region", "") == Utilities.PERU[0]) 35
                    else if (regionPref.getString("region", "") == Utilities.PHILIPPINES[0]) 36
                    else if (regionPref.getString("region", "") == Utilities.POLAND[0]) 37
                    else if (regionPref.getString("region", "") == Utilities.PORTUGAL[0]) 38
                    else if (regionPref.getString("region", "") == Utilities.RUSSIA[0]) 39
                    else if (regionPref.getString("region", "") == Utilities.ROMANIA[0]) 40
                    else if (regionPref.getString("region", "") == Utilities.SAUDI_ARABIA[0]) 41
                    else if (regionPref.getString("region", "") == Utilities.SINGAPORE[0]) 42
                    else if (regionPref.getString("region", "") == Utilities.SPAIN[0]) 43
                    else if (regionPref.getString("region", "") == Utilities.SRI_LANKA[0]) 44
                    else if (regionPref.getString("region", "") == Utilities.SWEDEN[0]) 45
                    else if (regionPref.getString("region", "") == Utilities.SWITZERLAND[0]) 46
                    else if (regionPref.getString("region", "") == Utilities.TAIWAN[0]) 47
                    else if (regionPref.getString("region", "") == Utilities.UKRAINE[0]) 48
                    else if (regionPref.getString("region", "") == Utilities.UNITED_KINGDOM[0]) 49
                    else if (regionPref.getString("region", "") == Utilities.UNITED_STATES[0]) 50
                    else if (regionPref.getString("region", "") == Utilities.ZIMBABWE[0]) 51
                    else 0
                ) { _, which ->
                    when (which) {
                        0 -> regionPref.edit().putString("region", Utilities.DEFAULT_REGION[0]).apply()
                        1 -> {
                            regionPref.edit().putString("region", Utilities.ARGENTINA[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        2 -> regionPref.edit().putString("region", Utilities.AUSTRALIA[0]).apply()
                        3 -> regionPref.edit().putString("region", Utilities.BANGLADESH[0]).apply()
                        4 -> regionPref.edit().putString("region", Utilities.BRAZIL[0]).apply()
                        5 -> {
                            regionPref.edit().putString("region", Utilities.BHUTAN[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        6 -> regionPref.edit().putString("region", Utilities.CANADA[0]).apply()
                        7 -> {
                            regionPref.edit().putString("region", Utilities.CHINA[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        8 -> regionPref.edit().putString("region", Utilities.COLOMBIA[0]).apply()
                        9 -> regionPref.edit().putString("region", Utilities.DENMARK[0]).apply()
                        10 -> regionPref.edit().putString("region", Utilities.EGYPT[0]).apply()
                        11 -> regionPref.edit().putString("region", Utilities.FRANCE[0]).apply()
                        12 -> regionPref.edit().putString("region", Utilities.GERMANY[0]).apply()
                        13 -> regionPref.edit().putString("region", Utilities.HONG_KONG[0]).apply()
                        14 -> regionPref.edit().putString("region", Utilities.ISRAEL[0]).apply()
                        15 -> regionPref.edit().putString("region", Utilities.ITALY[0]).apply()
                        16 -> {
                            regionPref.edit().putString("region", Utilities.IRAN[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        17 -> regionPref.edit().putString("region", Utilities.JAPAN[0]).apply()
                        18 -> {
                            regionPref.edit().putString("region", Utilities.JERSEY[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        19 -> regionPref.edit().putString("region", Utilities.KENYA[0]).apply()
                        20 -> regionPref.edit().putString("region", Utilities.KOREA[0]).apply()
                        21 -> regionPref.edit().putString("region", Utilities.LEBANON[0]).apply()
                        22 -> regionPref.edit().putString("region", Utilities.MALAYSIA[0]).apply()
                        23 -> {
                            regionPref.edit().putString("region", Utilities.MALDIVES[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        24 -> regionPref.edit().putString("region", Utilities.MEXICO[0]).apply()
                        25 -> regionPref.edit().putString("region", Utilities.MONGOLIA[0]).apply()
                        26 -> {
                            regionPref.edit().putString("region", Utilities.MYANMAR[0]).apply()
                            ToastUtilities.showToast(
                                requireContext(),
                                "Service not available yet for this region"
                            )
                        }
                        27 -> regionPref.edit().putString("region", Utilities.NETHERLANDS[0]).apply()
                        28 -> regionPref.edit().putString("region", Utilities.NEPAL[0]).apply()
                        29 -> regionPref.edit().putString("region", Utilities.NEW_ZEALAND[0]).apply()
                        30 -> regionPref.edit().putString("region", Utilities.NIGERIA[0]).apply()
                        31 -> regionPref.edit().putString("region", Utilities.NORWAY[0]).apply()
                        32 -> regionPref.edit().putString("region", Utilities.PAKISTAN[0]).apply()
                        33 -> regionPref.edit().putString("region", Utilities.PANAMA[0]).apply()
                        34 -> regionPref.edit().putString("region", Utilities.PARAGUAY[0]).apply()
                        35 -> regionPref.edit().putString("region", Utilities.PERU[0]).apply()
                        36 -> regionPref.edit().putString("region", Utilities.PHILIPPINES[0]).apply()
                        37 -> regionPref.edit().putString("region", Utilities.POLAND[0]).apply()
                        38 -> regionPref.edit().putString("region", Utilities.PORTUGAL[0]).apply()
                        39 -> regionPref.edit().putString("region", Utilities.RUSSIA[0]).apply()
                        40 -> regionPref.edit().putString("region", Utilities.ROMANIA[0]).apply()
                        41 -> regionPref.edit().putString("region", Utilities.SAUDI_ARABIA[0]).apply()
                        42 -> regionPref.edit().putString("region", Utilities.SINGAPORE[0]).apply()
                        43 -> regionPref.edit().putString("region", Utilities.SPAIN[0]).apply()
                        44 -> regionPref.edit().putString("region", Utilities.SRI_LANKA[0]).apply()
                        45 -> regionPref.edit().putString("region", Utilities.SWEDEN[0]).apply()
                        46 -> regionPref.edit().putString("region", Utilities.SWITZERLAND[0]).apply()
                        47 -> regionPref.edit().putString("region", Utilities.TAIWAN[0]).apply()
                        48 -> regionPref.edit().putString("region", Utilities.UKRAINE[0]).apply()
                        49 -> regionPref.edit().putString("region", Utilities.UNITED_KINGDOM[0]).apply()
                        50 -> regionPref.edit().putString("region", Utilities.UNITED_STATES[0]).apply()
                        51 -> regionPref.edit().putString("region", Utilities.ZIMBABWE[0]).apply()
                    }
                }
                this.setCancelable(true)
                this.setPositiveButton("Save") { dialog, _ ->
                    regionPref.edit().putBoolean("saveRegion", true).apply()
                    val regionCode = regionPref.getString("region", "").toString()
                    if(regionCode.isEmpty()){
                        ToastUtilities.showToast(
                            requireContext(),
                            "You have not selected a region yet"
                        )
                        regionPref.edit().putString("region", Utilities.DEFAULT_REGION[0]).apply()
                    }
                    val code = regionPref.getString("region", "").toString()
                    homeViewModel.getHomeVideos(
                        regionCode = code
                    )
                    dialog.dismiss()
                }
                this.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create().show()
        }

        if(NetworkUtilities.isNetworkAvailable(requireContext())) {
            homeViewModel.videos.observe(viewLifecycleOwner) { videos ->
                when (videos) {
                    is YoutubeResource.Loading -> {
                        binding.shimmerRecyclerView?.visibility = View.VISIBLE
                        binding.shimmerRecyclerView?.startShimmer()
                    }

                    is YoutubeResource.Success -> {
                        binding.shimmerRecyclerView?.visibility = View.INVISIBLE
                        binding.shimmerRecyclerView?.stopShimmer()
                        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                            binding.recyclerView?.apply {
                                setHasFixedSize(true)
                                layoutManager = LinearLayoutManager(context)
                                homeAdapter = HomeAdapter(requireContext(), videos.data.items)
                                homeAdapter.notifyDataSetChanged()
                                adapter = homeAdapter

                                val totalResults = videos.data.pageInfo?.totalResults!!
                                val resultsPerPage = videos.data.pageInfo?.resultsPerPage!!
                                val totalPages = totalResults.div(resultsPerPage)

                                dopamineSharedPreferences(
                                    requireContext()
                                ).edit()
                                    .putString("pageToken", videos.data.nextPageToken)
                                    .putInt("totalPages", totalPages)
                                    .apply()
                            }
                        }else{
                            binding.gridView?.apply {

                                adapter = CustomGridAdapter(requireContext(), videos.data.items?.toMutableList())


                                val totalResults = videos.data.pageInfo?.totalResults!!
                                val resultsPerPage = videos.data.pageInfo?.resultsPerPage!!
                                val totalPages = totalResults.div(resultsPerPage)

                                dopamineSharedPreferences(
                                    requireContext()
                                ).edit()
                                    .putString("pageToken", videos.data.nextPageToken)
                                    .putInt("totalPages", totalPages)
                                    .apply()
                            }
                        }
                    }

                    is YoutubeResource.Error -> {
                        Log.d(TAG, "Error: ${videos.exception.message.toString()}")
                        MaterialAlertDialogBuilder(requireContext())
                            .apply {
                                this.setTitle("Something went wrong")
                                this.setMessage(videos.exception.message.toString())
                                this.setIcon(R.drawable.ic_dialog_error)
                                this.setCancelable(false)
                                this.setNegativeButton("Cancel") { dialog, _ ->
                                    dialog?.dismiss()
                                }
                                this.setPositiveButton("Retry") { _, _ ->
                                    homeViewModel.reGetHomeVideos(
                                        regionPref.getString("region", "").toString()
                                    )
                                    homeViewModel.reGetVideos.observe(viewLifecycleOwner) { videos ->
                                        when (videos) {
                                            is YoutubeResource.Loading -> {
                                                binding.shimmerRecyclerView?.visibility =
                                                    View.VISIBLE
                                                binding.shimmerRecyclerView?.startShimmer()
                                                Log.d(TAG, "Loading: True")
                                            }

                                            is YoutubeResource.Success -> {
                                                binding.shimmerRecyclerView?.visibility =
                                                    View.INVISIBLE
                                                binding.shimmerRecyclerView?.stopShimmer()
                                                binding.recyclerView?.apply {
                                                    setHasFixedSize(true)
                                                    layoutManager = LinearLayoutManager(context)
                                                    homeAdapter =
                                                        HomeAdapter(requireContext(), videos.data.items)
                                                    adapter = homeAdapter
                                                }
                                                //Log.d(TAG, "Success: ${videos.data}")
                                            }

                                            is YoutubeResource.Error -> {
                                                Log.d(
                                                    TAG,
                                                    "Error: ${videos.exception.message.toString()}"
                                                )
                                                MaterialAlertDialogBuilder(requireContext())
                                                    .apply {
                                                        this.setTitle("Something went wrong")
                                                        this.setMessage(videos.exception.message.toString())
                                                        this.setIcon(R.drawable.ic_dialog_error)
                                                        this.setCancelable(false)
                                                        this.setPositiveButton("Try again later") { dialog, _ ->
                                                            dialog?.dismiss()
                                                            exitProcess(0)
                                                        }.create().show()
                                                    }
                                            }
                                        }
                                    }
                                }.create().show()
                            }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentHomeBinding = null
        homeViewModel.videos.removeObservers(viewLifecycleOwner)
    }
}