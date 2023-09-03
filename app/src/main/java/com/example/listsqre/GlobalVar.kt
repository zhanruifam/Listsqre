package com.example.listsqre

import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    const val EOF: Int = -1
    const val DELIMITER: Char = '|'
    private const val APP_GUIDE_DESC: String = "Create multiple lists to track your notes, " +
            "Short click the card views to view its contents or visit the link, " +
            "Long click the card views to edit its contents, " +
            "Avoid duplicate list names upon creation."

    fun appGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("App Guide")
        builder.setMessage(APP_GUIDE_DESC)
        builder.create().show()
    }
}