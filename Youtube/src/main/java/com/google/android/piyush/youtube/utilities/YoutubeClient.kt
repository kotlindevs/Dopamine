package com.google.android.piyush.youtube.utilities

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object YoutubeClient {

    const val YOUTUBE = "https://youtube.googleapis.com/youtube/v3/"

    val REGION_CODE = arrayListOf(
        "IN",
        "US"
    ).random()

    private val SHORTS = arrayListOf(
        "e0b088ecf082c9d119f9","df2a4f0cdf426639ff66","82a4bfb6062016fab407"
    ).random()

    const val VIDEO = "videos"

    const val PLAYLIST = "playlistItems"

    const val CHART = "mostPopular"

    const val PART = "snippet,contentDetails,statistics"

    const val PLAYLIST_PART = "snippet,contentDetails"

    const val MAX_RESULTS = "50"

    const val API_KEY = "AIzaSyBuO1S8x52SWLSQwFm_T12Wnsseao5Q9x8"

    val HIDDEN_CLIENT = "https://api.npoint.io/$SHORTS/"

    const val SHORTS_PART = "shorts"

    val REVERB_AND_SLOWED = arrayListOf(
        "PLnMEO-E9-ZnMlcG_XrFHfBzhiaTfwRB1K", "PLBTanuC8SLeZaIXwjc-vDZ-CmRak-KITf",
        "PLZehTKibbEcEMpQB7WgR0Uh_CSirRX1ja", "PLZinczBQn9b1YST_aqZp4FdXhUPTDYGSu",
        "PLFf0Wzvy9UVrSdb9my7kThyLJwijK8sYZ", "PLoQQjA3VvcE4721CxuT6yY3luWl5ez-JV",
        "PL7BOVx1utY9LPhbsjJTelTAVbtI9o-d95", "PL0SRsMXL71UjmtLxBsVHaXZKgpibKCRZ9",
        "PLiP8-CkZzKEg3SpB8E9DDbUHU2JfMm6oD", "PLY50G60VnAbyu_sWx97DaTVDroXOLcGa2",
        "PLz_XBAk7X4F1t0vh__8LXL6-2vE25_hqm", "PLTE0tRo9HHc9loqlgt4bGkSkoudeUcn0t"
    ).random()

    val GAMING_VIDEOS = arrayListOf(
        "PL3tRBEVW0hiBSFOFhTC5wt75P2BES0rAo", "PLe8jmEHFkvsasy1XhS5w4rWpvwBvOSFL0",
        "PLBUBxRIrD2exTPnwjcS_H130mD1srsHW0", "PLqVvtGEPyn1xplqlxx_9BSCQ46lF42tLy",
        "PLKTyKnsCvPUYwEkqueADvfX0aHx88ndCt", "PLhdhcwETqX-BqbssnCUS32dQ3fXE9fjk9",
        "PLhJUcbNkR99aSzTAMwZVoWhqA6kIfxd6Q", "PLRnX6J5CPQSwOHqOASvPKr_Uvbzddj7h4",
        "PL-TaHOq8pJ-a9S-EWx9MmQs-8Em1-vQ-k", "PLFfYcxV5z_c5zM2zPvGbFHfehUSIuvpx0",
        "PLRaYMMxEZyMXcSrQRh2xZulbfNpxYIAiJ", "PLzXmNUicjAvy_81u9RzZdMUpCFuXO1Ihl",
        "PL8vL-_f27zDQl33vDZEdIT3vacbB17J3H"
    ).random()

    val LOFI_BHAJAN =  arrayListOf(
        "PLuH-I1ovyzeioJ_j4DcQgoxtAbgBc3YD2", "PL6pXmupaeNZ2q9xnEroqVyZwvwXygFWPJ",
        "PL5RtZw3_Rjtx-4F5obOXpSuAVWsfVzmbf", "PLhmcRXNItagwuwlaO3slDOOLsLZCmrusg",
        "PLvc9tfarNFkXRyEcWzsHecc8T4TcZY4kt", "PLPhN6_svX3lJL5t8qziY1EwfgyfbKOsYq",
        "PLEtkiKkqwTOE526XvoQDwkcnqIV506edf", "PLmn9r2NQijPAAgQvVFOHvl4tejc7Gb5EZ",
        "PLgcK2qYcWCwRpG8CuuDSmIcKtzc426ukn", "PLPpmV2P0FYbLxP04VwXpZT4jDbUrUaX8v",
        "PLcMQbgxnh9hh9XK-y_0LCyU2N3FpAfDz2", "PLhwUrf4RX562HgDUMWiSrclejuBoCk7yD",
        "PLdbOyAhlSn6THjqQ263k8qnWOLkaAePP8", "PLiae937rUykQrQWLjNzDK4AA1OjS92v17",
        "PLb2tT1CRI1O4vQ71L4G1qj-4uSSec_5AR"
    ).random()

    val INDIAN_BOLLEYWOOD_PLAYLIST = arrayListOf(
        "UC_A7K2dXFsTMAciGmnNxy-Q","UC56gTxNs4f9xZ7Pa2i5xNzg",
        "UCoFItYS4A3akb1QWpTh3__A","UC4zvcd2F0UebXfG4LfygTmQ",
        "UCy7cSI6R6vpdDkfh6s8Fk6g","UCrmGw6VFijulOnpfx1whS0g",
        "UC3PfAVCunEokSP7zCgStqKg","UCbsNK24_37cAub0QDtcS55w",
        "UChSOKgrbaAQwCMID1WEaRjQ","UCxINHpad6T0YSwFWwyTARfw",
        "UCJrDMFOdv1I2k8n9oK_V21w","UC2xG0z-J7DsSK7_Y0f4sksA",
        "UCCDNEUYMki6c7Q8ykuN5dEg","UCq-Fj5jknLsUf-MWSy4_brA",
        "UCsuSg5nYeDwNJQ4_TKsZa8w","UCXifLfuAhlT3aafVdFgHD7A",
        "UC0tzkl1ZqoQNA2T60hAqxEw","UC0qCOMZMH8Md1kBZIXuSmlA",
        "UCmL1WlDI8UkXDXCXcBQN9CA","UCvMoEHmm8bv-4lEoONNf8-g",
        "UCnZIwOrjzsyi2qO2LV0Ve7w","UCqcBvLZoL6kWMoNekD7iQPQ"
    ).random()

    @OptIn(ExperimentalSerializationApi::class)
    val CLIENT = HttpClient(CIO){
        expectSuccess = false

        install(ContentNegotiation){
            json(
                Json{
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = true
                    prettyPrint = true
                    explicitNulls = false
                    coerceInputValues = true
                }
            )
        }
//        defaultRequest {
//            url(YOUTUBE)
//        }
    }
}