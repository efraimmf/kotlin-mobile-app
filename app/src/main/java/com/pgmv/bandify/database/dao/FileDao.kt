package com.pgmv.bandify.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pgmv.bandify.domain.File

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: File): Long

    @Update
    suspend fun updateFile(file: File)

    @Delete
    suspend fun deleteFile(file: File)

    @Query("SELECT * FROM files")
    suspend fun getAllFiles(): List<File>

    @Query("SELECT * FROM files WHERE id = :id LIMIT 1")
    suspend fun getFileById(id: Long): File?

    @Query("SELECT * FROM files WHERE song_id = :songId")
    suspend fun getFilesBySongId(songId: Long): List<File>

    @Query("SELECT * FROM files WHERE user_id = :userId")
    suspend fun getFilesByUserId(userId: Long): List<File>

    @Query("SELECT * FROM files WHERE name LIKE :name")
    suspend fun getFilesByName(name: String): List<File>

    @Query("SELECT * FROM files WHERE type = :type")
    suspend fun getFilesByType(type: String): List<File>


    @Query("""
    SELECT f.* FROM files f 
    INNER JOIN event_files ef ON ef.file_id = f.id 
    WHERE ef.event_id = :eventId
""")
    suspend fun getFilesForEvent(eventId: Long): List<File>
    abstract fun getFilesByCategory(s: String): Any?
}