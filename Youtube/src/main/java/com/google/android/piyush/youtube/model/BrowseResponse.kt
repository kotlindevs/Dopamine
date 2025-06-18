package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class BrowseResponse(
    val contents : Contents? = null,
){
    @Serializable
    data class Contents(
        val twoColumnBrowseResultsRenderer : TwoColumnBrowseResultsRenderer? = null
    ){
        @Serializable
        data class TwoColumnBrowseResultsRenderer(
            val tabs : List<Tab>? = null
        ){
            @Serializable
            data class Tab(
                val tabRenderer : TabRenderer? = null
            ){
                @Serializable
                data class TabRenderer(
                    val title : String? = null,
                    val endpoint : EndPoint? = null,
                    val content : Content? = null
                ){
                    @Serializable
                    data class EndPoint(
                        val browseEndpoint : BrowseEndpoint? = null
                    ) {
                        @Serializable
                        data class BrowseEndpoint(
                            val browseId : String? = null,
                            val params : String? = null
                        )
                    }
                    @Serializable
                    data class Content(
                        val sectionListRenderer : SectionListRenderer? = null
                    ){
                        @Serializable
                        data class SectionListRenderer(
                            val contents : List<Contents>? = null
                        ){
                            @Serializable
                            data class Contents(
                                val itemSectionRenderer : ItemSectionRenderer? = null
                            ){
                                @Serializable
                                data class ItemSectionRenderer(
                                    val contents : List<Contents>? = null
                                ){
                                    @Serializable
                                    data class Contents(
                                        val shelfRenderer : ShelfRenderer? = null
                                    ){
                                        @Serializable
                                        data class ShelfRenderer(
                                            val content : Content? = null
                                        ){
                                            @Serializable
                                            data class Content(
                                                val expandedShelfContentsRenderer : ExpandedShelfContentsRenderer? = null
                                            ){
                                                @Serializable
                                                data class ExpandedShelfContentsRenderer(
                                                    val items : List<Item>? = null
                                                ){
                                                    @Serializable
                                                    data class Item(
                                                        val videoRenderer : VideoRenderer? = null
                                                    ){
                                                        @Serializable
                                                        data class VideoRenderer(
                                                            val videoId : String? = null,
                                                            val thumbnail : Thumbnail? = null,
                                                            val title : Title? = null,
                                                            val longBylineText : LongBylineText? = null,
                                                            val publishedTimeText : PublishedTimeText? = null,
                                                            val shortViewCountText : ShortViewCountText? = null,
                                                            val avatar : Avatar? = null
                                                        ){
                                                            @Serializable
                                                            data class Thumbnail(
                                                                val thumbnails : List<Thumbnails>? = null
                                                            ){
                                                                @Serializable
                                                                data class Thumbnails(
                                                                    val url : String? = null,
                                                                    val width : Int? = 0,
                                                                    val height : Int? = 0
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
                                                            data class LongBylineText(
                                                                val runs : List<Runs>? = null
                                                            ){
                                                                @Serializable
                                                                data class Runs(
                                                                    val text : String? = null,
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
        }
    }
}