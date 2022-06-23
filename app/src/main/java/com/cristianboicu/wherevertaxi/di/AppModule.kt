package com.cristianboicu.wherevertaxi.di

import android.content.Context
import android.util.Log
import com.cristianboicu.wherevertaxi.R
import com.cristianboicu.wherevertaxi.data.remote.ApiService
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import com.cristianboicu.wherevertaxi.data.remote.RemoteDataSource
import com.cristianboicu.wherevertaxi.data.remote.places.IPlacesApi
import com.cristianboicu.wherevertaxi.data.remote.places.PlacesApi
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.data.repository.Repository
import com.cristianboicu.wherevertaxi.utils.ProjectConstants
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.ROUTES_URL
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    abstract fun bindRemoteDataSource(
        remoteDataSource: RemoteDataSource,
    ): IRemoteDataSource

    @Binds
    abstract fun bindPlacesApi(
        placesApi: PlacesApi,
    ): IPlacesApi

    @Binds
    abstract fun bindRepository(
        repository: Repository,
    ): IRepository

    companion object {
        @Provides
        @Singleton
        fun providePlacesApi(placesClient: PlacesClient): PlacesApi {
            return PlacesApi(placesClient)
        }

        @Provides
        @Singleton
        fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
            Places.initialize(context, context.getString(R.string.api_key))
            Log.d("AppModule", "Singleton init")
            return Places.createClient(context)
        }

        @Provides
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(ROUTES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @Provides
        fun provideApiClient(retrofit: Retrofit): ApiService {
            return retrofit.create(ApiService::class.java)
        }

        @Provides
        fun provideFirebaseAuthInstance(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        @Provides
        fun provideFirebaseDatabaseInstance(): DatabaseReference {
            return Firebase.database(ProjectConstants.DATABASE_URL).reference
        }

        @Singleton
        @Provides
        fun provideRemoteDataSource(firebaseAuth: FirebaseAuth, database: DatabaseReference) =
            RemoteDataSource(firebaseAuth, database)

        @Singleton
        @Provides
        fun provideRepository(remoteDataSource: RemoteDataSource) = Repository(remoteDataSource)
    }

}