package com.team2.todolistteam2_practice.okhttp

import com.team2.todolistteam2_practice.common.ADDRESS
import com.team2.todolistteam2_practice.common.GET_TODOS
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class OkHttpManager {
    companion object {
        private lateinit var okHttpClient: OkHttpClient

        fun getOkHttpClient(): OkHttpClient {
            if (this::okHttpClient.isInitialized) {
                return okHttpClient
            } else {
                okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
            }
            return okHttpClient
        }

        // Get 방식에서는 이렇게!
        fun getOkHttpUrl(targetURL: String): HttpUrl {
            return HttpUrl.Builder()
                .scheme("https")
                .host(ADDRESS)
                .addPathSegments(targetURL)
                .build()
        }

        fun getOkHttpUrlMe(targetURL1: String, targetURL2: String): HttpUrl {
            return HttpUrl.Builder()
                .scheme("https")
                .host(ADDRESS)
                .addPathSegment(targetURL1)
                .addPathSegment(targetURL2)
                .build()
        }
    }

}