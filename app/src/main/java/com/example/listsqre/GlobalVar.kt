package com.example.listsqre

import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    const val EOF: Int = -1
    const val DELIMITER: Char = '|'
    const val UAText: String = "348934"
    const val cfmText: String = "confirm"
    private const val APP_GUIDE_DESC: String = "Create multiple lists to track your notes. " +
            "Clickable card views to view its contents or visit the link. " +
            "Button on each card view for more setting options (delete & edit). " +
            "Please avoid duplicate list names upon list creation. " +
            "TAKE NOTE: Be sure to kill the app in the background after exit. "

    fun appGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("User Guide:")
        builder.setMessage(APP_GUIDE_DESC)
        builder.create().show()
    }
}