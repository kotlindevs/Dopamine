package com.example.dopamine.SqliteDb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.dopamine.TracksList.TracksDataClass.Track
import com.example.dopamine.TracksList.TracksDataClass.trackId
import java.text.FieldPosition

class SqliteHelper(context: Context) : SQLiteOpenHelper(context,
    DATABASE_NAME,null,
    DATABASE_VERSION
)  {
    companion object{
        private const val DATABASE_NAME = "tracks.db"
        private const val DATABASE_VERSION = 1

        private const val TRACKS = "tracks"
        private const val TRACKS_ID = "id"
        private const val TRACKS_ARTIST_NAME = "artist_name"
        private const val TRACKS_SONG_NAME = "song_name"
        private const val TRACKS_TYPE = "type"
        private const val IS_PLAY_TRACK = "is_playable"
        private const val TRACKS_SMALL_PHOTO = "rc_url"
        private const val TRACKS_PHOTO = "mp_url"
        private const val TRACKS_PREVIEW = "preview_url"
        private const val TRACKS_DATE = "release_date"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTracksTable = "CREATE TABLE $TRACKS($TRACKS_ID TEXT NOT NULL ,$TRACKS_ARTIST_NAME TEXT,$TRACKS_SONG_NAME TEXT,$TRACKS_TYPE TEXT,$IS_PLAY_TRACK TEXT,$TRACKS_SMALL_PHOTO TEXT,$TRACKS_PHOTO TEXT,$TRACKS_PREVIEW TEXT,$TRACKS_DATE TEXT);"
        db?.execSQL(createTracksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TRACKS")
        onCreate(db)
    }

    fun addTracks(track: Track) : Long{
        val db = this.writableDatabase
        val tracks = ContentValues()
        tracks.put(TRACKS_ID,track.id)
        tracks.put(TRACKS_ARTIST_NAME,track.artist_name)
        tracks.put(TRACKS_SONG_NAME,track.song_name)
        tracks.put(TRACKS_TYPE,track.type)
        tracks.put(IS_PLAY_TRACK,track.is_playable)
        tracks.put(TRACKS_SMALL_PHOTO,track.rc_url)
        tracks.put(TRACKS_PHOTO,track.mp_url)
        tracks.put(TRACKS_PREVIEW,track.preview_url)
        tracks.put(TRACKS_DATE,track.release_date)
        val addTrack = db.insert(TRACKS,null,tracks)
        db.close()
        return addTrack
    }

    fun Exists(searchItem: String): Boolean {
        val db = this.readableDatabase

        val columns = arrayOf<String>("id")
        val selection: String = "id" + " =?"
        val selectionArgs = arrayOf(searchItem)
        val limit = "1"
        val cursor: Cursor =
            db.query(TRACKS, columns, selection, selectionArgs, null, null, null, limit)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun trackLists(id: String,position: Int): ArrayList<Track> {
        val tracks : ArrayList<Track> = ArrayList()
        val selectQuery = "SELECT * FROM $TRACKS WHERE $TRACKS_ID = '$id'  "
        val db = this.readableDatabase

        val cursor : Cursor?

        try{
            cursor = db.rawQuery(selectQuery,null)
        } catch (e : Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var artist_name : String
        var song_name : String
        var type : String
        var is_playable : String
        var small_photo : String
        var big_photo : String
        var preview_song : String
        var track_date : String


        if(cursor.moveToFirst()){
            do{

                artist_name = cursor.getString(cursor.getColumnIndex(TRACKS_ARTIST_NAME))
                song_name = cursor.getString(cursor.getColumnIndex(TRACKS_SONG_NAME))
                type = cursor.getString(cursor.getColumnIndex(TRACKS_TYPE))
                is_playable = cursor.getString(cursor.getColumnIndex(IS_PLAY_TRACK))
                small_photo = cursor.getString(cursor.getColumnIndex(TRACKS_SMALL_PHOTO))
                big_photo = cursor.getString(cursor.getColumnIndex(TRACKS_PHOTO))
                preview_song = cursor.getString(cursor.getColumnIndex(TRACKS_PREVIEW))
                track_date = cursor.getString(cursor.getColumnIndex(TRACKS_DATE))

                val track = Track(id,artist_name,song_name,type,is_playable,small_photo,big_photo,preview_song,track_date)
                tracks.add(track)

            } while (cursor.moveToNext())
        }
        return tracks
    }

    fun trackLists1(): ArrayList<trackId> {
        val tracks : ArrayList<trackId> = ArrayList()
        val selectQuery = "SELECT $TRACKS_ID FROM $TRACKS"
        val db = this.readableDatabase
        val cursor : Cursor?

        try{
            cursor = db.rawQuery(selectQuery,null)
        } catch (e : Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id : String

        if(cursor.moveToFirst()){
            do{

                id = cursor.getString(cursor.getColumnIndex(TRACKS_ID))

                val track = trackId(id)
                tracks.add(track)

            } while (cursor.moveToNext())
        }
        return tracks
    }
    fun trackLists0(): ArrayList<Track> {
        val tracks : ArrayList<Track> = ArrayList()
        val selectQuery = "SELECT * FROM $TRACKS "
        val db = this.readableDatabase

        val cursor : Cursor?

        try{
            cursor = db.rawQuery(selectQuery,null)
        } catch (e : Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id : String
        var artist_name : String
        var song_name : String
        var type : String
        var is_playable : String
        var small_photo : String
        var big_photo : String
        var preview_song : String
        var track_date : String


        if(cursor.moveToFirst()){
            do{
                id = cursor.getString(cursor.getColumnIndex(TRACKS_ID))
                artist_name = cursor.getString(cursor.getColumnIndex(TRACKS_ARTIST_NAME))
                song_name = cursor.getString(cursor.getColumnIndex(TRACKS_SONG_NAME))
                type = cursor.getString(cursor.getColumnIndex(TRACKS_TYPE))
                is_playable = cursor.getString(cursor.getColumnIndex(IS_PLAY_TRACK))
                small_photo = cursor.getString(cursor.getColumnIndex(TRACKS_SMALL_PHOTO))
                big_photo = cursor.getString(cursor.getColumnIndex(TRACKS_PHOTO))
                preview_song = cursor.getString(cursor.getColumnIndex(TRACKS_PREVIEW))
                track_date = cursor.getString(cursor.getColumnIndex(TRACKS_DATE))

                val track = Track(id,artist_name,song_name,type,is_playable,small_photo,big_photo,preview_song,track_date)
                tracks.add(track)

            } while (cursor.moveToNext())
        }
        return tracks
    }
}