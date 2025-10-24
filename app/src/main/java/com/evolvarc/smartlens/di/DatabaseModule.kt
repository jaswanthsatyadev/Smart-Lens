package com.evolvarc.smartlens.di

import android.content.Context
import androidx.room.Room
import com.evolvarc.smartlens.data.local.SmartLensDatabase
import com.evolvarc.smartlens.data.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SmartLensDatabase {
        return Room.databaseBuilder(
            context,
            SmartLensDatabase::class.java,
            "smartlens_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideProductDao(database: SmartLensDatabase): ProductDao {
        return database.productDao()
    }
}
