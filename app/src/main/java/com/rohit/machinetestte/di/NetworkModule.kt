package com.rohit.machinetestte.di

import android.content.Context
import com.rohit.machinetestte.presentation.others.AppConnectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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
    fun provideGlobalCoroutineScope(): CoroutineScope = CoroutineScope(
        context = Dispatchers.Main + SupervisorJob()
    )

    @Provides
    @Singleton
    fun provideConnectivity(
        @ApplicationContext context: Context, coroutineScope: CoroutineScope
    ): AppConnectivity = AppConnectivity(context, coroutineScope)

    @Provides
    @Singleton
    fun provideHttpClient(): Retrofit = Retrofit.Builder()
        .baseUrl(
            HttpUrl
                .Builder()
                .scheme("https").host("navkiraninfotech.com").build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}