package com.example.listsqre

import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    const val EOF: Int = -1
    const val DELIMITER: Char = '|'
    const val UAText: String = "348934"
    const val cfmText: String = "confirm"
    private const val APP_GUIDE_DESC: String = "Multi purpose tool for creating lists. " +
            "Clickable card views to view its contents or visit https link. " +
            "A button on each card view for more editing option. " +
            "A checkbox on each card view for selection delete. " +
            "Please avoid duplicate list names upon list creation."

    fun appGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("User Guide:")
        builder.setMessage(APP_GUIDE_DESC)
        builder.create().show()
    }
}