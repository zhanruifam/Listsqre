package com.example.listsqre

import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    const val EOF: Int = -1
    const val DELIMITER: Char = '|'
    const val UAText: String = "348934"
    const val cfmText: String = "confirm"
    private const val APP_GUIDE_DESC: String = "Multi purpose tool for creating lists."

    fun appGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("User Guide:")
        builder.setMessage(APP_GUIDE_DESC)
        builder.create().show()
    }

    fun nullStrHandler(input: String?): String {
        // Using the safe call operator and the null-coalescing operator
        return input?: "Error: Null string"
    }
}