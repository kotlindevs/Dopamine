package com.google.android.piyush.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Avatar(
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
                    val sources : List<Source>? = null
                ){
                    @Serializable
                    data class Source(
                        val url : String? = null,
                        val height : Int? = 0,
                        val width : Int? = 0
                    )
                }
            }
        }
    }
}