package com.example.listsqre

import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    enum class ErrorType(val code: Int, val description: String) {
        INVALID_INPUT       (900, "Invalid input provided"),
        DUPLICATE_INPUT     (901, "Duplicate input provided"),
        EMPTY_INPUT         (902, "Empty input provided"),
        EMPTY_SELECTION     (903, "No selection made"),
        UNKNOWN_ERROR       (999, "Unknown error")
    }

    var notiTitle: String = ""  // TODO: will need to store this in a database
    var notiDescr: String = ""  // TODO: will need to store this in a database

    const val EOF: Int = -1
    const val DELIMITER: Char = '|'
    const val UAText: String = "348934"
    const val cfmText: String = "confirm"
    private const val APP_GUIDE_DESC: String = "Multi purpose tool for creating lists. " +
            "Clickable card views to view its contents or visit https link. " +
            "A button on each card view for more editing option. " +
            "A checkbox on each card view for select operations. " +
            "Please avoid duplicate list names upon list creation."

    fun errDialog(context: Context, errType: ErrorType) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error Code: ${errType.code}")
        builder.setMessage("${errType.description}, try again")
        builder.create().show()
    }

    fun appGuide(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("User Guide:")
        builder.setMessage(APP_GUIDE_DESC)
        builder.create().show()
    }
}