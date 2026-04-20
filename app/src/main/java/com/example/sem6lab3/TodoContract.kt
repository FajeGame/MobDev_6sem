package com.example.sem6lab3

import android.net.Uri
import android.provider.BaseColumns

object TodoContract {
    const val AUTHORITY = "com.example.sem6lab3.provider"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/tasks")
    const val PATH_TASKS = "tasks"
    const val PATH_TASKS_ID = "tasks/#"
    const val TABLE_NAME = "todos"
    const val COLUMN_TITLE = "title"
    const val COLUMN_COMPLETED = "completed"
    const val COLUMN_REMOTE_ID = "remoteId"
    const val COLUMN_SYNCED = "synced"

    object Tasks : BaseColumns {
        const val TITLE = COLUMN_TITLE
        const val COMPLETED = COLUMN_COMPLETED
        const val REMOTE_ID = COLUMN_REMOTE_ID
        const val SYNCED = COLUMN_SYNCED
    }
}
