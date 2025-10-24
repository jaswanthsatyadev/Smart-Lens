package com.evolvarc.smartlens.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.evolvarc.smartlens.data.local.dao.ProductDao
import com.evolvarc.smartlens.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SmartLensDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
