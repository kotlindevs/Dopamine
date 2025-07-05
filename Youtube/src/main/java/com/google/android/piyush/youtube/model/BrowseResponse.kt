package com.google.android.piyush.youtube.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
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
                    val content : Content? = null,
                    val selected : Boolean? = null
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
                                        val reelShelfRenderer: ReelShelfRenderer? = null,
                                        val shelfRenderer : ShelfRenderer? = null
                                    ){
                                        @Serializable
                                        data class ShelfRenderer(
                                            val title : Title? = null,
                                            val endpoint : Endpoint? = null,
                                            val content : Content? = null,
                                            val subtitle : Subtitle? = null
                                        ){
                                            @Serializable
                                            data class Title(
                                                val runs : List<Run>? = null
                                            ){
                                                @Serializable
                                                data class Run(
                                                    val text : String? = null
                                                )
                                            }
                                            @Serializable
                                            data class Endpoint(
                                                val browseEndpoint : BrowseEndpoint? = null
                                            ){
                                                @Serializable
                                                data class BrowseEndpoint(
                                                    val browseId : String? = null
                                                )
                                            }
                                            @Serializable
                                            data class Content(
                                                val horizontalListRenderer : HorizontalListRenderer? = null,
                                                val expandedShelfContentsRenderer : ExpandedShelfContentsRenderer? = null
                                            ){
                                                @Serializable
                                                data class HorizontalListRenderer(
                                                    val items : List<Item>? = null
                                                ){
                                                    @Serializable
                                                    data class Item(
                                                        val gridVideoRenderer : GridVideoRenderer? = null,
                                                        val gridChannelRenderer : GridChannelRenderer? = null,
                                                        val lockupViewModel : LockupViewModel? = null
                                                    )
                                                }
                                                @Serializable
                                                data class ExpandedShelfContentsRenderer(
                                                    val items : List<Item>? = null
                                                ){
                                                    @Serializable
                                                    data class Item(
                                                        val videoRenderer : VideoRenderer? = null
                                                    )
                                                }
                                            }

                                            @Serializable
                                            data class Subtitle(
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