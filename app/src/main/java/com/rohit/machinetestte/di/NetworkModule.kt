package com.rohit.machinetestte.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): Retrofit = Retrofit.Builder()
        .baseUrl(
            HttpUrl
                .Builder()
                .scheme("https").host("navkiraninfotech.com/g-mee-api/api").build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}