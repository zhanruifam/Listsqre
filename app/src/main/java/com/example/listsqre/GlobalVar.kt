package com.example.listsqre

import java.net.URL
import android.app.AlertDialog
import android.content.Context

object GlobalVar {
    enum class ErrorType(val code: Int, val description: String) {
        INVALID_INPUT       (900, "Invalid input provided"),
        DUPLICATE_INPUT     (901, "Duplicate input provided"),
        EMPTY_INPUT         (902, "Empty input provided"),
        INVALID_TIME        (904, "Invalid 24h time provided")
    }

    const val EOF: Int = -1
    const val notifId: Int = 0
    const val DELIMITER: Char = '|'
    const val UAText: String = "348934"
    const val cfmText: String = "confirm"

    fun isLinkValid(urlString: String): Boolean {
        // URL() throws an exception if link is invalid
        return try {
            URL(urlString)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun errDialog(context: Context, errType: ErrorType) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error Code: ${errType.code}")
        builder.setMessage(errType.description)
        builder.create().show()
    }
}