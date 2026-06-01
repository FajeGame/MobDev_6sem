package com.example.semka_6sem.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.example.semka_6sem.data.di.DataEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// contentprovider для чтения списка привычек другими приложениями
class HabitContentProvider : ContentProvider() {

    private lateinit var matcher: UriMatcher

    override fun onCreate(): Boolean {
        val authority = requireNotNull(context).packageName + ".habits"
        AUTHORITY = authority
        matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(authority, "habits", HABITS)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        if (matcher.match(uri) != HABITS) return null
        val dao = entryPoint().habitDao()
        val habits = runBlocking { dao.observeHabits().first() }
        val cursor = MatrixCursor(arrayOf("_id", "title"))
        habits.forEach { cursor.addRow(arrayOf(it.id, it.title)) }
        return cursor
    }

    override fun getType(uri: Uri): String? =
        if (matcher.match(uri) == HABITS) "vnd.android.cursor.dir/vnd.semka.habit" else null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0

    private fun entryPoint(): DataEntryPoint {
        val app = requireNotNull(context).applicationContext
        return EntryPointAccessors.fromApplication(app, DataEntryPoint::class.java)
    }

    companion object {
        private const val HABITS = 1
        var AUTHORITY: String = ""
    }
}
