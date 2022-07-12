package com.cristianboicu.wherevertaxi.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.cristianboicu.wherevertaxi.data.local.ILocalDataSource
import com.cristianboicu.wherevertaxi.data.local.LocalDataSource
import com.cristianboicu.wherevertaxi.data.local.TaxiDao
import com.cristianboicu.wherevertaxi.data.local.TaxiDatabase
import com.cristianboicu.wherevertaxi.data.remote.IRemoteDataSource
import com.cristianboicu.wherevertaxi.data.remote.RemoteDataSource
import com.cristianboicu.wherevertaxi.data.remote.cloud.*
import com.cristianboicu.wherevertaxi.data.remote.firebase.FirebaseApi
import com.cristianboicu.wherevertaxi.data.remote.firebase.IFirebaseApi
import com.cristianboicu.wherevertaxi.data.repository.IRepository
import com.cristianboicu.wherevertaxi.data.repository.Repository
import com.cristianboicu.wherevertaxi.utils.ProjectConstants
import com.cristianboicu.wherevertaxi.utils.ProjectConstants.MAPS_URL
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
    abstract fun bindLocalDataSource(
        localDataSource: LocalDataSource,
    ): ILocalDataSource

    @Binds
    abstract fun bindFirebaseApi(
        firebaseApi: FirebaseApi,
    ): IFirebaseApi

    @Binds
    abstract fun bindCloudServiceApi(
        cloudServiceApi: CloudServiceApi,
    ): ICloudServiceApi

    @Binds
    abstract fun bindPlacesApi(
        placesApi: PlacesApi,
    ): IPlacesApi

    @Binds
    abstract fun bindRepository(
        repository: Repository,
    ): IRepository

    companion object {
        @Singleton
        @Provides
        fun provideDatabase(
            @ApplicationContext context: Context,
        ) = Room.databaseBuilder(
            context.applicationContext,
            TaxiDatabase::class.java,
            "user"
        ).build()

        @Singleton
        @Provides
        fun provideDao(db: TaxiDatabase) = db.getTaxiDao()

        @Provides
        @Singleton
        fun providePlacesApi(placesClient: PlacesClient): PlacesApi {
            return PlacesApi(placesClient)
        }

        @Provides
        @Singleton
        fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
            Places.initialize(context, ProjectConstants.API_KEY)
            return Places.createClient(context)
        }

        @Provides
        fun provideRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(MAPS_URL)
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
        @Singleton
        fun provideFirebaseApi(
            firebaseAuth: FirebaseAuth,
            database: DatabaseReference,
        ): FirebaseApi {
            return FirebaseApi(firebaseAuth, database)
        }

        @Provides
        fun provideFirebaseDatabaseInstance(): DatabaseReference {
            return Firebase.database(ProjectConstants.DATABASE_URL).reference
        }

        @Provides
        fun provideCloudServiceApi(apiService: ApiService, placesApi: PlacesApi): CloudServiceApi {
            return CloudServiceApi(apiService, placesApi)
        }

        @Singleton
        @Provides
        fun provideRemoteDataSource(firebaseApi: FirebaseApi, cloudServiceApi: CloudServiceApi) =
            RemoteDataSource(firebaseApi, cloudServiceApi)

        @Singleton
        @Provides
        fun provideLocalDataSource(taxiDao: TaxiDao) = LocalDataSource(taxiDao)

        @Singleton
        @Provides
        fun provideRepository(
            remoteDataSource: RemoteDataSource,
            localDataSource: LocalDataSource,
        ) = Repository(remoteDataSource, localDataSource)
    }

}