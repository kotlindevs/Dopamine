package com.google.android.piyush.youtube.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@InternalSerializationApi
@Serializable
data class ChannelResponse(
    val contents : Contents? = null,
    val header : Header? = null
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
                    val endpoint : Endpoint? = null,
                    val title : String? = null,
                    val content : Content? = null
                ){
                    @Serializable
                    data class Endpoint(
                        val browseEndpoint : BrowseEndpoint? = null
                    ){
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
                                        val reelShelfRenderer : ReelShelfRenderer? = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Serializable
    data class Header(
        val pageHeaderRenderer : PageHeaderRenderer? = null
    ){
        @Serializable
        data class PageHeaderRenderer(
            val pageTitle : String? = null,
            val content : Content? = null
        ){
            @Serializable
            data class Content(
                val pageHeaderViewModel : PageHeaderViewModel? = null
            ){
                @Serializable
                data class PageHeaderViewModel(
                    val title : Title? = null,
                    val image : Image? = null,
                    val metadata : Metadata? = null,
                    val description : Description? = null,
                    val banner : Banner? = null
                ){
                    @Serializable
                    data class Title(
                        val dynamicTextViewModel : DynamicTextViewModel? = null
                    ){
                        @Serializable
                        data class DynamicTextViewModel(
                            val text : Text? = null
                        ){
                            @Serializable
                            data class Text(
                                val content : String? = null
                            )
                        }
                    }

                    @Serializable
                    data class Image(
                        val decoratedAvatarViewModel : DecoratedAvatarViewModel? = null
                    ){
                        @Serializable
                        data class DecoratedAvatarViewModel(
                            val avatar : Avatar? = null
                        ){
                            @Serializable
                            data class Avatar(
                                val avatarViewModel : AvatarViewModel? = null
                            ){
                                @Serializable
                                data class AvatarViewModel(
                                    val image : Image? = null
                                ){
                                    @Serializable
                                    data class Image(
                                        val sources: List<Source>? = null
                                    ){
                                        @Serializable
                                        data class Source(
                                            val url : String? = null,
                                            val width : Int? = null,
                                            val height : Int? = null
                                        )
                                    }
                                }
                            }
                        }
                    }

                    @Serializable
                    data class Metadata(
                        val contentMetadataViewModel : ContentMetadataViewModel? = null
                    ){
                        @Serializable
                        data class ContentMetadataViewModel(
                            val metadataRows : List<MetadataRow>? = null
                        ){
                            @Serializable
                            data class MetadataRow(
                                val metadataParts : List<MetadataPart>? = null
                            ){
                                @Serializable
                                data class MetadataPart(
                                    val text : Text? = null
                                ){
                                    @Serializable
                                    data class Text(
                                        val content : String? = null
                                    )
                                }
                            }
                        }
                    }

                    @Serializable
                    data class Description(
                        val descriptionPreviewViewModel : DescriptionPreviewViewModel? = null
                    ){
                        @Serializable
                        data class DescriptionPreviewViewModel(
                            val description : Description? = null
                        ){
                            @Serializable
                            data class Description(
                                val content : String? = null
                            )
                        }
                    }

                    @Serializable
                    data class Banner(
                        val imageBannerViewModel : ImageBannerViewModel? = null
                    ){
                        @Serializable
                        data class ImageBannerViewModel(
                            val image : Image? = null
                        ){
                            @Serializable
                            data class Image(
                                val sources : List<Source>? = null
                            ){
                                @Serializable
                                data class Source(
                                    val url : String? = null,
                                    val width : Int? = null,
                                    val height : Int? = null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
