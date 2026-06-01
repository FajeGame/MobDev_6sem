package com.example.semka_6sem.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.semka_6sem.R

// делится списком привычек через contentprovider
object HabitShareHelper {

    fun share(context: Context) {
        val uri = Uri.parse("content://${context.packageName}.habits/habits")
        val cursor = context.contentResolver.query(uri, null, null, null, null) ?: return
        val lines = StringBuilder()
        var index = cursor.getColumnIndex("title")
        if (index < 0) index = 1
        while (cursor.moveToNext()) {
            lines.append("• ").append(cursor.getString(index)).append('\n')
        }
        cursor.close()
        if (lines.isEmpty()) return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_habits_title))
            putExtra(Intent.EXTRA_TEXT, lines.toString())
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_habits)))
    }
}
