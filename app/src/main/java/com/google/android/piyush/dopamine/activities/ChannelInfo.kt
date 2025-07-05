package com.google.android.piyush.dopamine.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.piyush.dopamine.DopamineDbViewModel
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.YoutubeViewModel
import com.google.android.piyush.dopamine.databinding.ActivityChannelInfoBinding
import com.google.android.piyush.dopamine.fragments.ChannelHome
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelInfo : AppCompatActivity() {

    private lateinit var binding: ActivityChannelInfoBinding
    private val viewModel : YoutubeViewModel by viewModels<YoutubeViewModel>()
    private val database : DopamineDbViewModel by viewModels<DopamineDbViewModel>()
    private var channelId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChannelInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intent.getStringExtra("channelId").let {
            channelId = it
            Log.d("ChannelId => ", it.toString())
        }

        viewModel.channelDetails(channelId = channelId!!, filter = null)

        viewModel.channelInfo.observe(this){ response ->
            when(response){
                is Response.Loading -> {
                    binding.apply {
                        progressBar.visibility = View.VISIBLE
                        shimmerEffectChannelImage.visibility = View.GONE
                        shimmerEffectChannelBanner.visibility = View.GONE
                        channelBanner.visibility = View.GONE
                        channelImage.visibility = View.GONE
                        channelTitle.visibility = View.GONE
                        channelUsername.visibility = View.GONE
                        channelOtherInfo.visibility = View.GONE
                        channelDescription.visibility = View.GONE
                        addToFavorites.visibility = View.GONE
                        channelTabsView.visibility = View.GONE
                    }
                }
                is Response.Success -> {
                    val channel = response.data
                    binding.apply {
                        progressBar.visibility = View.GONE
                        shimmerEffectChannelBanner.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                        shimmerEffectChannelImage.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                        shimmerEffectChannelTitle.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                        shimmerEffectChannelUsername.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                        shimmerEffectChannelOtherInfo.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                        shimmerEffectChannelDescription.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                        shimmerEffectAddToFavorites.apply {
                            visibility = View.VISIBLE
                            startShimmer()
                        }
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(555).run {
                            channel.header?.pageHeaderRenderer?.content?.pageHeaderViewModel?.let { channelInfo ->
                                val channelBanner = channelInfo.banner?.imageBannerViewModel?.image?.sources?.let { image ->
                                    image.getOrNull(2)?.url ?: image.firstOrNull()?.url
                                }
                                val channelImage = channelInfo.image?.decoratedAvatarViewModel?.avatar?.avatarViewModel?.image?.sources?.let{ image ->
                                    image.getOrNull(2)?.url ?: image.getOrNull(1)?.url ?: image.firstOrNull()?.url
                                }
                                val channelTitle = channelInfo.title?.dynamicTextViewModel?.text?.content
                                val channelUserName = channelInfo.metadata?.contentMetadataViewModel?.metadataRows?.getOrNull(0)?.metadataParts?.getOrNull(0)?.text?.content
                                val channelSubscribers = channelInfo.metadata?.contentMetadataViewModel?.metadataRows?.getOrNull(1)?.metadataParts?.getOrNull(0)?.text?.content
                                val channelVideos = channelInfo.metadata?.contentMetadataViewModel?.metadataRows?.getOrNull(1)?.metadataParts?.getOrNull(1)?.text?.content
                                val channelDescription = channelInfo.description?.descriptionPreviewViewModel?.description?.content
                                channelBanner?.let {
                                    if(it.isNotEmpty()){
                                        binding.channelBanner.visibility = View.VISIBLE
                                        Glide.with(this@ChannelInfo)
                                            .load(channelBanner)
                                            .listener(object : RequestListener<Drawable>{
                                                override fun onLoadFailed(
                                                    e: GlideException?,
                                                    model: Any?,
                                                    target: Target<Drawable?>,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    binding.shimmerEffectChannelBanner.apply {
                                                        visibility = View.GONE
                                                        stopShimmer()
                                                    }
                                                    binding.channelBanner.visibility = View.GONE
                                                    return false
                                                }

                                                override fun onResourceReady(
                                                    resource: Drawable,
                                                    model: Any,
                                                    target: Target<Drawable?>?,
                                                    dataSource: DataSource,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    binding.shimmerEffectChannelBanner.apply {
                                                        visibility = View.GONE
                                                        stopShimmer()
                                                    }
                                                    binding.channelBanner.visibility = View.VISIBLE
                                                    return false
                                                }
                                            })
                                            .into(binding.channelBanner)
                                    }else{
                                        binding.shimmerEffectChannelBanner.apply {
                                            visibility = View.GONE
                                            stopShimmer()
                                        }
                                        binding.channelBanner.visibility = View.GONE
                                    }
                                }

                                channelImage?.let {
                                    binding.channelImage.visibility = View.VISIBLE
                                    if(it.isNotEmpty()) {
                                        Glide.with(this@ChannelInfo)
                                            .load(channelImage)
                                            .listener(object : RequestListener<Drawable> {
                                                override fun onLoadFailed(
                                                    e: GlideException?,
                                                    model: Any?,
                                                    target: Target<Drawable?>,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    binding.shimmerEffectChannelImage.apply {
                                                        visibility = View.GONE
                                                        stopShimmer()
                                                    }
                                                    binding.channelImage.visibility = View.GONE
                                                    return false
                                                }

                                                override fun onResourceReady(
                                                    resource: Drawable,
                                                    model: Any,
                                                    target: Target<Drawable?>?,
                                                    dataSource: DataSource,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    binding.shimmerEffectChannelImage.apply {
                                                        visibility = View.GONE
                                                        stopShimmer()
                                                    }
                                                    binding.channelImage.visibility = View.VISIBLE
                                                    return false
                                                }

                                            })
                                            .into(binding.channelImage)
                                    }else{
                                        binding.shimmerEffectChannelImage.apply {
                                            visibility = View.GONE
                                            stopShimmer()
                                        }
                                        binding.channelImage.visibility = View.GONE
                                    }
                                }

                                channelTitle?.let {
                                    binding.channelTitle.visibility = View.VISIBLE
                                    if(it.isNotEmpty()){
                                        binding.shimmerEffectChannelTitle.visibility = View.GONE
                                        binding.channelTitle.text = it
                                    }else{
                                        binding.channelTitle.visibility = View.GONE
                                    }
                                }

                                channelUserName?.let {
                                    binding.channelUsername.visibility = View.VISIBLE
                                    if(it.isNotEmpty()){
                                        binding.shimmerEffectChannelUsername.visibility = View.GONE
                                        binding.channelUsername.text = it
                                    }else{
                                        binding.channelUsername.visibility = View.GONE
                                    }
                                }

                                channelSubscribers?.let { subscriber ->
                                    channelVideos?.let { videos ->
                                        binding.channelOtherInfo.visibility = View.VISIBLE
                                        if(subscriber.isNotEmpty() && videos.isNotEmpty()){
                                            val channelOtherInfo = "$subscriber • $videos"
                                            binding.shimmerEffectChannelOtherInfo.visibility = View.GONE
                                            binding.channelOtherInfo.text = channelOtherInfo
                                        }else{
                                            binding.channelOtherInfo.visibility = View.GONE
                                        }
                                    }
                                }

                                channelDescription?.let {
                                    binding.channelDescription.visibility = View.VISIBLE
                                    if(it.isNotEmpty()){
                                        binding.shimmerEffectChannelDescription.visibility = View.GONE
                                        binding.channelDescription.text = it.trim()
                                    }else{
                                        binding.channelDescription.visibility = View.GONE
                                    }
                                }

                                channelId?.let { id ->
                                    binding.shimmerEffectAddToFavorites.visibility = View.GONE
                                    binding.addToFavorites.visibility = View.VISIBLE
                                    binding.channelTabs.visibility = View.VISIBLE
                                    replaceTab(ChannelHome(id))
                                    binding.addToFavorites.setOnClickListener {
                                        Snackbar.make(
                                            binding.root,
                                            "Coming soon.",
                                            Snackbar.LENGTH_SHORT
                                        ).apply {
                                            animationMode = Snackbar.ANIMATION_MODE_SLIDE
                                            setAction("Close"){
                                                dismiss()
                                            }
                                        }.show()
                                    }
                                }
                            }
                        }
                    }
                }
                is Response.Error -> {
                    Log.d("Error => ", response.exception.toString())
                }
            }
        }

        binding.channelTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                p0?.text?.toString()?.let { selectedTab->
                    when(selectedTab){
                        getString(R.string.home) -> {
                            replaceTab(ChannelHome(channelId))
                        }
                        getString(R.string.videos) -> {}
                        getString(R.string.shorts) -> {}
                        getString(R.string.playlists) -> {}
                        getString(R.string.posts) -> {}
                    }
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabReselected(p0: TabLayout.Tab?) {}
        })
    }

    private fun replaceTab(tab : Fragment){
        binding.channelTabsView.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.channelTabsView, tab)
            .commit()
    }
}