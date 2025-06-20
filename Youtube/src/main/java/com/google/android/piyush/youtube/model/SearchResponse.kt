package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class SearchResponse(
    val contents : Contents? = null
){
    @Serializable
    data class Contents(
        val twoColumnSearchResultsRenderer : TwoColumnSearchResultsRenderer? = null
    ){
        @Serializable
        data class TwoColumnSearchResultsRenderer(
            val primaryContents : PrimaryContents? = null
        ){
            @Serializable
            data class PrimaryContents(
                val sectionListRenderer : SectionListRenderer? = null
            ){
                @Serializable
                data class SectionListRenderer(
                    val contents : List<Content>? = null
                ){
                    @Serializable
                    data class Content(
                        val itemSectionRenderer : ItemSectionRenderer? = null
                    ){
                        @Serializable
                        data class ItemSectionRenderer(
                            val contents : List<Content>? = null
                        ){
                            @Serializable
                            data class Content(
                                val reelShelfRenderer : ReelShelfRenderer? = null,
                                val videoRenderer : VideoRenderer? = null,
                                val channelRenderer : ChannelRenderer? = null,
//                                val shelfRenderer : ShelfRenderer? = null
                            ){
                                @Serializable
                                data class ChannelRenderer(
                                    val channelId : String? = null,
                                    val thumbnail : Thumbnail? = null,
                                    val shortBylineText : ShortByLineText? = null,
                                    val videoCountText : VideoCountText? = null,
                                    val subscriberCountText : SubscriberCountText? = null
                                ){
                                    @Serializable
                                    data class VideoCountText(
                                        val simpleText : String? = null
                                    )
                                    @Serializable
                                    data class SubscriberCountText(
                                        val simpleText : String? = null
                                    )
                                    @Serializable
                                    data class Thumbnail(
                                        val thumbnails : List<Thumbnails>? = null
                                    ){
                                        @Serializable
                                        data class Thumbnails(
                                            val url : String? = null,
                                            val width : Int? = null,
                                            val height : Int? = null
                                        )
                                    }
                                    @Serializable
                                    data class ShortByLineText(
                                        val runs : List<Runs>? = null
                                    ){
                                        @Serializable
                                        data class Runs(
                                            val text : String? = null,
                                            val navigationEndpoint : NavigationEndpoint? = null
                                        ){
                                            @Serializable
                                            data class NavigationEndpoint(
                                              val browseEndpoint : BrowseEndpoint? = null
                                            ){
                                                @Serializable
                                                data class BrowseEndpoint(
                                                    val browseId : String? = null,
                                                    val canonicalBaseUrl : String? = null
                                                )
                                            }
                                        }
                                    }
                                }

                                @Serializable
                                data class VideoRenderer(
                                    val videoId : String? = null,
                                    val thumbnail : Thumbnail? = null,
                                    val title : Title? = null,
                                    val longBylineText : LongByLineText? = null,
                                    val publishedTimeText : PublishedTimeText? = null,
                                    val shortViewCountText : ShortViewCountText? = null,
                                    val channelThumbnailSupportedRenderers : ChannelThumbnailSupportedRenderers? = null,
                                ){
                                    @Serializable
                                    data class Thumbnail(
                                        val thumbnails : List<Thumbnails>? = null
                                    ){
                                        @Serializable
                                        data class Thumbnails(
                                            val url : String? = null,
                                            val width : Int? = null,
                                            val height : Int? = null
                                        )
                                    }
                                    @Serializable
                                    data class Title(
                                        val runs : List<Runs>? = null
                                    ){
                                        @Serializable
                                        data class Runs(
                                            val text : String? = null
                                        )
                                    }
                                    @Serializable
                                    data class LongByLineText(
                                        val runs : List<Runs>? = null
                                    ){
                                        @Serializable
                                        data class Runs(
                                            val text : String? = null
                                        )
                                    }
                                    @Serializable
                                    data class PublishedTimeText(
                                        val simpleText : String? = null
                                    )
                                    @Serializable
                                    data class ShortViewCountText(
                                        val simpleText : String? = null
                                    )
                                    @Serializable
                                    data class ChannelThumbnailSupportedRenderers(
                                        val channelThumbnailWithLinkRenderer : ChannelThumbnailWithLinkRenderer? = null
                                    ){
                                        @Serializable
                                        data class ChannelThumbnailWithLinkRenderer(
                                            val thumbnail : Thumbnail? = null
                                        ){
                                            @Serializable
                                            data class Thumbnail(
                                                val thumbnails : List<Thumbnails>? = null
                                            ){
                                                @Serializable
                                                data class Thumbnails(
                                                    val url : String? = null,
                                                    val width : Int? = null,
                                                    val height : Int? = null
                                                )
                                            }
                                        }
                                    }
                                }

                                @Serializable
                                data class ReelShelfRenderer(
                                    val title : Title? = null,
                                    val items : List<Item>? = null
                                ){
                                    @Serializable
                                    data class Title(
                                        val simpleText : String? = null
                                    )
                                    @Serializable
                                    data class Item(
                                        val shortsLockupViewModel : ShortsLockupViewModel? = null
                                    ){
                                        @Serializable
                                        data class ShortsLockupViewModel(
                                            val thumbnail : Thumbnail? = null,
                                            val overlayMetadata : OverlayMetadata? = null,
                                            val onTap : OnTap? = null
                                        ){
                                            @Serializable
                                            data class OnTap(
                                                val innertubeCommand : InnertubeCommand? = null
                                            ){
                                                @Serializable
                                                data class InnertubeCommand(
                                                    val reelWatchEndpoint : ReelWatchEndpoint? = null
                                                ){
                                                    @Serializable
                                                    data class ReelWatchEndpoint(
                                                        val videoId : String? = null
                                                    )
                                                }
                                            }
                                            @Serializable
                                            data class Thumbnail(
                                                val sources : List<Source>? = null
                                            ){
                                                @Serializable
                                                data class Source(
                                                    val url : String? = null,
                                                    val width : Int? = null,
                                                    val height : Int? = null
                                                )
                                            }
                                            @Serializable
                                            data class OverlayMetadata(
                                                val primaryText : PrimaryText? = null,
                                                val secondaryText : SecondaryText? = null
                                            ){
                                                @Serializable
                                                data class PrimaryText(
                                                    val content : String? = null
                                                )
                                                @Serializable
                                                data class SecondaryText(
                                                    val content : String? = null
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
