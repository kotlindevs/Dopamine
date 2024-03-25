package com.google.android.piyush.youtube.utilities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object YoutubeClient {

    const val YOUTUBE = "https://youtube.googleapis.com/youtube/v3/"

    const val REGION_CODE = "IN"

    private val SHORTS = arrayListOf(
        "e0b088ecf082c9d119f9","df2a4f0cdf426639ff66","82a4bfb6062016fab407"
    ).random()

    const val VIDEO = "videos"

    const val PLAYLIST = "playlistItems"

    const val CHANNEL = "channels"

    const val CHART = "mostPopular"

    const val PART = "snippet,contentDetails,statistics"

    const val CHANNEL_PART = "snippet,contentDetails,statistics,brandingSettings"

    const val PLAYLIST_PART = "snippet,contentDetails"

    const val MAX_RESULTS = "50"

    val API_KEY = arrayListOf(
        "AIzaSyDetnr3eHcdt6oqv_poZkrHB_T63cMRMsc","AIzaSyCgLZsNdWFWuJb4GQvfS_HJvc5n7cV6Pyk","AIzaSyDthuStFPH6bdtsDBFHVm30wjprKKOd5b8",
        "AIzaSyDMQuMItUqW2QrSQUtLtCpKmdCfniKD1zE","AIzaSyDaHGB5Z5nq29U46YGINN4Xjku3f-U8AIs"
    ).random()

    val HIDDEN_CLIENT = "https://api.npoint.io/$SHORTS/"

    const val DEVELOPER = "https://api.npoint.io/7479b06945cacafcad89/developers"

    const val SHORTS_PART = "shorts"

    const val SEARCH= "search"

    const val PLAYLISTS= "playlists"

    const val SEARCH_PART = "snippet"

    const val DOPAMINE_UPDATE = "https://api.npoint.io/0178e5b07792668c9a58"

    const val PRE_RELEASE = "https://api.npoint.io/255cbfc840e9bf199c9d"

    const val EXPERIMENTAL_API = "https://yt.lemnoslife.com/noKey/"

    val CODING_VIDEOS = arrayListOf(
        "PLfqMhTWNBTe0PY9xunOzsP5kmYIz2Hu7i","PLfqMhTWNBTe0gqgFk-CUE-ktO5Cek1GdP",
        "PLfqMhTWNBTe0sPLFF91REaJQEteFZtLzA","PLfqMhTWNBTe25HU2y-3Kx6MBsasawd61U",
        "PLfqMhTWNBTe3LtFWcvwpqTkUSlB32kJop","PLfqMhTWNBTe3H6c9OGXb5_6wcc1Mca52n",
        "PLu0W_9lII9ahR1blWXxgSlL4y9iQBnLpR","PLu0W_9lII9aikXkRE0WxDt1vozo3hnmtR",
        "PLu0W_9lII9aiL0kysYlfSOUgY5rNlOhUd","PLRAV69dS1uWQGDQoBYMZWKjzuhCaOnBpa",
        "PLjVLYmrlmjGcQfNj_SLlLV4Ytf39f8BF7","PL5PR3UyfTWvfacnfUsvNcxIiKIgidNRoW",
        "PLai5B987bZ9CoVR-QEIN9foz4QCJ0H2Y8","PL9ooVrP1hQOG6DQnOD6ujdCEchaqADfCU",
    ).random()

    val SPORTS_VIDEOS = arrayListOf(
        "PLCGIzmTE4d0hhfpwtaWR-mgtRZmsy5e4t","PLn5vww_8o5KuZBHmg673Qmy7FdBKiKxqg",
        "PLfoNZDHitwjX-oU5YVAkfuXkALZqempRS","PLu8uIfcaMMW58ql_wEWJ8VJ0Bs8TN5DWP",
        "PLrfBm83TZJt-t6SOdimRizi3_hyHyj83G","PL1NbHSfosBuG_HB8WTgFaCVsDWCcnOsUW",
        "PLQHHr8gPOsH48qDASzYskAYiWHgKhPl26","PLHKIZtgW3Stx7FBBV1fU5EziT_2HCGciV",
    ).random()

    val TECH_VIDEOS =  arrayListOf(
        "PLflqtq8EOGAJJDNAct-tz9X8C6-MSljjB","PLBsP89CPrMeOYPjeabTfPW8UDC-WRn2Gi",
        "PLG2K6CpAgCSqTTu87JRtBXnxgbXU_BqVy","PLWMr6-kiy-EymYr84AW65YK5HQqa3c3pM",
        "PLubgCncKqwofL7DwwOZ4PisRcVQcbn8A1","PL0W2eFwhS9h6Cw0PUmKFoTiKAITx0PMWu",
        "PL54E1sm59KWNyAKQ_UDUKf73pAnhyhg93","PLHcmK2sS7-mScUCt2qPtYFDrEd2uC_h7q",
        "PLNyLhAZuODp3k1-V8eZp2J3aQU_82TAaT","PLMYDmB_aqcVaduG-18hAfh6NEN6iAZuDX",
        "PLSBvbeScevHqnaRfybw-Jtuf14T_KCsNZ","PL1htDegCU2nViVRvpYlI67llR49eUm5I9",
        "PLM6PG6pQfVYHlhE0GbtmciPBu4NIiQoJ4","PLBsP89CPrMeM2MmF4suOeT0vsic9nEC2Y"
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
    }
}

@Serializable
data class DopamineVersion(
    val versionName : String? = null,
    val url : String? = null,
    val changelog : String? = null
)

class DopamineVersionViewModel() : ViewModel() {
    private val _update : MutableLiveData<YoutubeResource<DopamineVersion>> = MutableLiveData()
    val update : MutableLiveData<YoutubeResource<DopamineVersion>> = _update

    private val _preRelease : MutableLiveData<YoutubeResource<DopamineVersion>> = MutableLiveData()
    val preRelease : MutableLiveData<YoutubeResource<DopamineVersion>> = _preRelease

    init{
        try {
            _update.postValue(YoutubeResource.Loading)
            viewModelScope.launch {
                _update.postValue(
                    YoutubeResource.Success(
                        getVersion()
                    )
                )
            }
        }catch (e : Exception){
            _update.postValue(YoutubeResource.Error(e))
        }
    }

    fun preReleaseUpdate() {
        viewModelScope.launch {
            try {
                _preRelease.postValue(YoutubeResource.Loading)
                viewModelScope.launch {
                    _preRelease.postValue(
                        YoutubeResource.Success(
                            getPreRelease()
                        )
                    )
                }
            }catch (e : Exception){
                _preRelease.postValue(YoutubeResource.Error(e))
            }
        }
    }
}

suspend fun getVersion() : DopamineVersion {
    return YoutubeClient.CLIENT.get(
        YoutubeClient.DOPAMINE_UPDATE
    ).body()
}

suspend fun getPreRelease() : DopamineVersion {
    return YoutubeClient.CLIENT.get(
        YoutubeClient.PRE_RELEASE
    ).body()
}

@Serializable
data class Developer(
    val userId : String? = null,
    val userName : String? = null,
    val userDesignation : String? = null,
    val userImage : String? = null,
    val userBanner : String? = null,
    val userEmail : String? = null,
    val userAbout : String? = null,
    val userLocation : String? = null,
    val userPhotos : List<Photos>? = null
)

@Serializable
data class Photos(
    val photo : String? = null
)

class DevelopersViewModel : ViewModel() {

    private val _devModel : MutableLiveData<YoutubeResource<List<Developer>>> = MutableLiveData()
    val devModel : MutableLiveData<YoutubeResource<List<Developer>>> = _devModel

    init {
        viewModelScope.launch {
            try {
                _devModel.postValue(YoutubeResource.Loading)
                val response = YoutubeClient.CLIENT.get(
                    YoutubeClient.DEVELOPER
                ).body<List<Developer>>()
                if(response.isNotEmpty()){
                    _devModel.postValue(YoutubeResource.Success(response))
                }
            }catch (exception : Exception){
                _devModel.postValue(YoutubeResource.Error(exception))
            }
        }
    }
}