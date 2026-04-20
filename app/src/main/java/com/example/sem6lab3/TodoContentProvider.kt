package com.example.sem6lab3

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.sqlite.db.SimpleSQLiteQuery

class TodoContentProvider : ContentProvider() {
    private val matcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(TodoContract.AUTHORITY, TodoContract.PATH_TASKS, TASKS)
        addURI(TodoContract.AUTHORITY, TodoContract.PATH_TASKS_ID, TASK_ID)
    }

    override fun onCreate() = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = AppGraph.database(context ?: return null).openHelper.readableDatabase
        val cursor = when (matcher.match(uri)) {
            TASKS -> db.query(
                SimpleSQLiteQuery(
                    "SELECT id AS _id, title, completed, remoteId, synced FROM todos ORDER BY id DESC"
                )
            )
            TASK_ID -> {
                val id = uri.lastPathSegment ?: return null
                db.query(
                    SimpleSQLiteQuery(
                        "SELECT id AS _id, title, completed, remoteId, synced FROM todos WHERE id = ?",
                        arrayOf(id)
                    )
                )
            }
            else -> throw IllegalArgumentException("Unknown uri: $uri")
        }
        context?.contentResolver?.let { cursor.setNotificationUri(it, uri) }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (matcher.match(uri) != TASKS) throw IllegalArgumentException("Unknown uri: $uri")
        val db = AppGraph.database(context ?: return null).openHelper.writableDatabase
        val data = ContentValues().apply {
            put(TodoContract.COLUMN_TITLE, values?.getAsString(TodoContract.COLUMN_TITLE) ?: "")
            put(TodoContract.COLUMN_COMPLETED, values?.getAsBoolean(TodoContract.COLUMN_COMPLETED) ?: false)
            putNull(TodoContract.COLUMN_REMOTE_ID)
            put(TodoContract.COLUMN_SYNCED, false)
        }
        val id = db.insert(TodoContract.TABLE_NAME, 0, data)
        context?.contentResolver?.notifyChange(TodoContract.CONTENT_URI, null)
        TodoNotifications.showExternalTaskAdded(context ?: return null, data.getAsString(TodoContract.COLUMN_TITLE) ?: "")
        return ContentUris.withAppendedId(TodoContract.CONTENT_URI, id)
    }

    override fun getType(uri: Uri): String? = when (matcher.match(uri)) {
        TASKS -> "vnd.android.cursor.dir/vnd.${TodoContract.AUTHORITY}.task"
        TASK_ID -> "vnd.android.cursor.item/vnd.${TodoContract.AUTHORITY}.task"
        else -> null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    companion object {
        private const val TASKS = 1
        private const val TASK_ID = 2
    }
}
