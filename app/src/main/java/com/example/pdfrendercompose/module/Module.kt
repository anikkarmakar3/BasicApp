package com.example.pdfrendercompose.module

import android.content.Context
import androidx.room.Room
import com.example.pdfrendercompose.data.local.AppDatabase
import com.example.pdfrendercompose.data.remote.ApiService
import com.example.pdfrendercompose.presenter.DeviceRepository
import com.example.pdfrendercompose.presenter.DeviceRepositoryImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppModule {

    fun provideApi(): ApiService = Retrofit.Builder()
        .baseUrl("https://api.restful-api.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_db").build()

    fun provideRepository(api: ApiService, db: AppDatabase): DeviceRepository =
        DeviceRepositoryImpl(api, db.deviceDao())
}