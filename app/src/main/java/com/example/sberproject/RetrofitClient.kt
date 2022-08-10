package com.example.sberproject

import com.example.sberproject.ui.articles.ArticlesApi
import com.example.sberproject.ui.map.data.RecyclingPlacesApi
import com.example.sberproject.view.fragments.InstructionApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.lang.reflect.Type

object RetrofitClient {
    val RECYCLING_PLACES_SERVICE: RecyclingPlacesApi by lazy {
        val baseUrl = "http://158.101.217.50/city/"
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(RecyclingPlace::class.java, RecyclingPlaceDeserializer())
            .create()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(RecyclingPlacesApi::class.java)
    }

    val ARTICLES_SERVICE: ArticlesApi by lazy {
        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .baseUrl("http://158.101.217.50/news/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ArticlesApi::class.java)
    }

    val INSTRUCTION_API: InstructionApi by lazy{
        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .baseUrl("http://158.101.217.50/instruction/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(InstructionApi::class.java)
    }

    /*val NEURAL_NETWORK_API: NeuralNetworkApi by lazy{
        val gson = GsonBuilder().setLenient().create()

        Retrofit.Builder()
            .baseUrl("http://5.167.232.197/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(NeuralNetworkApi::class.java)
    }*/
}

/*interface NeuralNetworkApi{
    @Multipart
    @POST("detect")
    suspend fun detect(@Part("file") file: File, @Part("uid") json: String): List<NeuralNetworkAnswer>
}

data class NeuralNetworkAnswer(val cls: String, val box: List<Double>, val type: TrashType)

class NeuralNetworkAnswerDeserializer: JsonDeserializer<NeuralNetworkAnswer>{
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext?
    ): NeuralNetworkAnswer {
        val cls = json.asJsonObject.get("class").asString
        val box = json.asJsonObject.get("box").asJsonArray.toList().map { x -> x.asDouble }
        val type = TrashType.fromInt(json.asJsonObject.get("type").asInt)
        return NeuralNetworkAnswer(cls, box, type)
    }
}*/