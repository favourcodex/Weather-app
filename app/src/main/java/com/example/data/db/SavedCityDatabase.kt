package com.example.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey val id: String, // e.g. "lat_lon" or geocoding id
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val customTag: String? = null,
    val isPinned: Boolean = false,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Dao
interface SavedCityDao {
    @Query("SELECT * FROM saved_cities ORDER BY isPinned DESC, addedTimestamp DESC")
    fun getAllSavedCities(): Flow<List<SavedCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCityEntity)

    @Query("DELETE FROM saved_cities WHERE id = :cityId")
    suspend fun deleteCityById(cityId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_cities WHERE id = :cityId)")
    suspend fun isCitySaved(cityId: String): Boolean
}

@Database(entities = [SavedCityEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedCityDao(): SavedCityDao
}
