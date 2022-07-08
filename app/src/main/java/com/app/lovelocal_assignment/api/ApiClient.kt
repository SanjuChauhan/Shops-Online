package com.app.lovelocal_assignment.api

import com.app.lovelocal_assignment.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.my.favourite.movies.constants.API_BASE_URL
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

class ApiClient {
    private var okBuilder: OkHttpClient.Builder? = null
    private var adapterBuilder: Retrofit.Builder? = null
    private fun createDefaultAdapter() {

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime::class.java, DateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .create()

        okBuilder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.apply {
            if (BuildConfig.DEBUG) {
                logging.level = HttpLoggingInterceptor.Level.BODY
            } else {
                logging.level = HttpLoggingInterceptor.Level.NONE
            }
        }
        okBuilder!!.addInterceptor(logging)
        // connection timeout 60 seconds/ 1 minute
        okBuilder!!.connectTimeout(240, TimeUnit.SECONDS)
        okBuilder!!.readTimeout(240, TimeUnit.SECONDS)

        adapterBuilder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(CustomConverterFactory(gson))
    }

    fun <S> createService(serviceClass: Class<S>): S? {
        return adapterBuilder
            ?.client(okBuilder!!.build())
            ?.build()
            ?.create(serviceClass)
    }

    init {
        createDefaultAdapter()
    }
}

/**
 * This wrapper is to take care of this case:
 * when the deserialization fails due to JsonParseException and the
 * expected type is String, then just return the body string.
 */
internal class ResponseBodyConverterToString<T>(
    private val gson: Gson,
    private val type: Type
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T {
        val returned = value.string()
        return try {
            gson.fromJson(returned, type)
        } catch (e: JsonParseException) {
            returned as T
        }
    }

}

internal class CustomConverterFactory(private val gson: Gson) : Converter.Factory() {
    private val converterFactory: GsonConverterFactory = GsonConverterFactory.create(gson)

    override fun responseBodyConverter(
        type: Type, annotations: Array<Annotation>, retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (type == String::class.java) ResponseBodyConverterToString<Any>(
            gson,
            type
        ) else {
            converterFactory.responseBodyConverter(type, annotations, retrofit)
        }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return converterFactory.requestBodyConverter(
            type,
            parameterAnnotations,
            methodAnnotations,
            retrofit
        )
    }

}

/**
 * Gson TypeAdapter for Joda DateTime type
 */
internal class DateTimeTypeAdapter : TypeAdapter<DateTime?>() {
    private val parseFormatter =
        ISODateTimeFormat.dateOptionalTimeParser()
    private val printFormatter =
        ISODateTimeFormat.dateTime()

    @Throws(IOException::class)
    override fun write(
        out: JsonWriter,
        date: DateTime?
    ) {
        if (date == null) {
            out.nullValue()
        } else {
            out.value(printFormatter.print(date))
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): DateTime? {
        val i = `in`.peek()
        return if (i == JsonToken.NULL) {
            `in`.nextNull()
            null
        } else {
            val date = `in`.nextString()
            parseFormatter.parseDateTime(date)
        }
    }
}

/**
 * Gson TypeAdapter for Joda LocalDate type
 */
internal class LocalDateTypeAdapter : TypeAdapter<LocalDate?>() {
    private val formatter = ISODateTimeFormat.date()

    @Throws(IOException::class)
    override fun write(
        out: JsonWriter,
        date: LocalDate?
    ) {
        if (date == null) {
            out.nullValue()
        } else {
            out.value(formatter.print(date))
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader): LocalDate? {
        val i = `in`.peek()
        return if (i == JsonToken.NULL) {
            `in`.nextNull()
            null
        } else {
            val date = `in`.nextString()
            formatter.parseLocalDate(date)
        }
    }
}