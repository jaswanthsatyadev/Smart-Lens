package com.evolvarc.smartlens.di

import com.evolvarc.smartlens.data.remote.api.OpenBeautyFactsApi
import com.evolvarc.smartlens.data.remote.api.OpenFoodFactsApi
import com.evolvarc.smartlens.data.remote.api.OpenProductsFactsApi
import com.evolvarc.smartlens.data.remote.api.UsdaApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FoodRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BeautyRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProductsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UsdaRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    @FoodRetrofit
    fun provideFoodRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    @BeautyRetrofit
    fun provideBeautyRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openbeautyfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    @ProductsRetrofit
    fun provideProductsRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://world.openproductsfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(@FoodRetrofit retrofit: Retrofit): OpenFoodFactsApi {
        return retrofit.create(OpenFoodFactsApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideOpenBeautyFactsApi(@BeautyRetrofit retrofit: Retrofit): OpenBeautyFactsApi {
        return retrofit.create(OpenBeautyFactsApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideOpenProductsFactsApi(@ProductsRetrofit retrofit: Retrofit): OpenProductsFactsApi {
        return retrofit.create(OpenProductsFactsApi::class.java)
    }
    
    @Provides
    @Singleton
    @UsdaRetrofit
    fun provideUsdaRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(UsdaApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideUsdaApi(@UsdaRetrofit retrofit: Retrofit): UsdaApi {
        return retrofit.create(UsdaApi::class.java)
    }
}
