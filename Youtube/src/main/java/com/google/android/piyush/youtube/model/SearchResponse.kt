package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

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
                        val itemSectionRenderer : ItemSectionRenderer? = null,
                        val continuationItemRenderer : ContinuationItemRenderer? = null
                    ){
                        @Serializable
                        data class ContinuationItemRenderer(
                            val continuationEndpoint : ContinuationEndpoint? = null
                        ){
                            @Serializable
                            data class ContinuationEndpoint(
                                val continuationCommand : ContinuationCommand? = null
                            ){
                                @Serializable
                                data class ContinuationCommand(
                                    val token : String? = null,
                                    val request : String? = null
                                )
                            }
                        }
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
                            }
                        }
                    }
                }
            }
        }
    }
}
