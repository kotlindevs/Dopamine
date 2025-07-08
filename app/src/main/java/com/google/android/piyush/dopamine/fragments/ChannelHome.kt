package com.google.android.piyush.dopamine.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.piyush.dopamine.R
import com.google.android.piyush.dopamine.YoutubeViewModel
import com.google.android.piyush.dopamine.adapters.ChannelHomeAdapter
import com.google.android.piyush.dopamine.databinding.FragmentChannelHomeBinding
import com.google.android.piyush.youtube.model.ChannelHomeContent
import com.google.android.piyush.youtube.model.ChannelHomeHeader
import com.google.android.piyush.youtube.model.GridChannelRenderer
import com.google.android.piyush.youtube.model.GridVideoRenderer
import com.google.android.piyush.youtube.model.LockupViewModel
import com.google.android.piyush.youtube.model.ReelShelfRenderer
import com.google.android.piyush.youtube.utilities.Response
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChannelHome() : Fragment() {

    private var binding : FragmentChannelHomeBinding? = null
    private val channelId by lazy {
        arguments?.getString(CHANNEL_ID)
    }
    private val viewModel : YoutubeViewModel by viewModels<YoutubeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_channel_home, container, false)
    }

    companion object{
        private const val CHANNEL_ID = "channel_id"

        @JvmStatic
        fun newInstance(channelId : String) : ChannelHome{
            val fragment = ChannelHome()
            val args = Bundle()
            args.putString(CHANNEL_ID, channelId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChannelHomeBinding.bind(view)
        if(channelId != null){
            viewModel.channelHomeContent(browseId = channelId!!)
        }

        viewModel.channelHomeContent.observe(viewLifecycleOwner){ response ->
            when(response){
                is Response.Loading -> {
                    binding?.progressBar?.visibility = View.VISIBLE
                    binding?.channelHomeContent?.visibility = View.GONE
                }
                is Response.Success -> {
                    val content = response.data
                    val playlistData = mutableListOf<ChannelHomeContent>()
                    val reelRenderer = mutableListOf<ReelShelfRenderer.Item.ShortsLockupViewModel>()
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(777).run {
                            binding?.progressBar?.visibility = View.GONE
                            binding?.channelHomeContent?.visibility = View.VISIBLE
                            content.contents?.twoColumnBrowseResultsRenderer?.tabs?.forEach { tabs ->
                                tabs.tabRenderer?.content?.sectionListRenderer?.contents?.forEach { sectionListContent ->
                                    sectionListContent.itemSectionRenderer?.contents?.forEach { itemSectionContent ->
                                        itemSectionContent.reelShelfRenderer?.let { reelShelf ->
                                            reelShelf.items?.forEach { item ->
                                                item.shortsLockupViewModel?.let {
                                                    reelRenderer.add(it)
                                                }
                                            }
                                        }
                                        itemSectionContent.shelfRenderer?.let { shelf ->
                                            val currentPlaylistTitle =
                                                shelf.title?.runs?.firstOrNull()?.text
                                            val currentPlaylistSubtitle = shelf.subtitle?.simpleText

                                            val currentPlaylistVideos =
                                                mutableListOf<GridVideoRenderer>()
                                            shelf.content?.horizontalListRenderer?.items?.forEach { item ->
                                                item.gridVideoRenderer?.let { video ->
                                                    currentPlaylistVideos.add(video)
                                                }
                                            }

                                            val currentPlaylistChannels =
                                                mutableListOf<GridChannelRenderer>()
                                            val currentPlaylist = mutableListOf<LockupViewModel>()
                                            shelf.content?.horizontalListRenderer?.items?.forEach { item ->
                                                item.gridChannelRenderer?.let { channel ->
                                                    currentPlaylistChannels.add(channel)
                                                }
                                                item.lockupViewModel?.let { playlists ->
                                                    currentPlaylist.add(playlists)
                                                }
                                            }
                                            val playlistContent = ChannelHomeContent(
                                                header = ChannelHomeHeader(
                                                    title = currentPlaylistTitle,
                                                    subtitle = currentPlaylistSubtitle
                                                ),
                                                videos = currentPlaylistVideos,
                                                channels = currentPlaylistChannels,
                                                playlists = currentPlaylist,
                                                reels = reelRenderer
                                            )
                                            playlistData.add(playlistContent)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    binding?.channelHomeContent?.apply {
                        adapter = ChannelHomeAdapter(playlistData)
                        layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                }
                is Response.Error -> {
                    Log.d("ChannelHome", response.exception.toString())
                }
            }
        }
    }
}