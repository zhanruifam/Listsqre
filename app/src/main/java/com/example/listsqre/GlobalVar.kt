package com.example.listsqre

import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    const val EOF: Int = -1
    const val DELIMITER: Char = '|'
    const val cfmText: String = "confirm"
    private const val APP_GUIDE_DESC: String = "Create multiple lists to track your notes\n" +
            "Short click the card views to view its contents or visit the link\n" +
            "Click on button with \". . .\" for more options (delete & edit)\n" +
            "Please avoid duplicate list names upon creation"

    fun appGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("App Guide")
        builder.setMessage(APP_GUIDE_DESC)
        builder.create().show()
    }
}