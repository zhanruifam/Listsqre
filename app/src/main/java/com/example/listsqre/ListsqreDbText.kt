// TODO: for the time being text Db is implemented here
// TODO: at the moment cannot have conflicting filenames, implement a unique filename generator

package com.example.listsqre

import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.BufferedReader
import java.io.BufferedWriter
import android.content.Context

// ----- ----- ----- CD text file handler ----- ----- ----- //

fun createTextFile(context: Context, fileName: String) {
    val file = File(context.filesDir, fileName)
    try {
        file.createNewFile()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun deleteTextFile(context: Context, fileName: String) {
    val file = File(context.filesDir, fileName)
    if (file.exists()) {
        file.delete()
    } else {
        // do nothing
    }
}

fun deleteSelTextFile(context: Context, list: List<Listsqre.Node>) {
    for(obj in list) {
        deleteTextFile(context, obj.getListname())
    }
}

// ----- ----- ----- individual text file handler ----- ----- ----- //

fun storeDataToFile(context: Context, fileName: String?, data: String) {
    val file = fileName?.let { File(context.filesDir, it) }
    try {
        val writer = BufferedWriter(FileWriter(file, true))
        writer.write(data)
        writer.flush()
        writer.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun readFromFile(context: Context, fileName: String?) {
    val file = fileName?.let { File(context.filesDir, it) }
    try {
        val reader = BufferedReader(FileReader(file))
        var currentChar: Int
        var currentPart = StringBuilder()
        while (reader.read().also { currentChar = it } != GlobalVar.EOF) {
            val char = currentChar.toChar()
            if (char == GlobalVar.DELIMITER) {
                ListOfListsqre.addNode(currentPart.toString())
                currentPart = StringBuilder()
            } else {
                currentPart.append(char)
            }
        }
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun addAllToPlanned(context: Context, fileName: String?, dispName: String) {
    val file = fileName?.let { File(context.filesDir, it) }
    try {
        val reader = BufferedReader(FileReader(file))
        var currentChar: Int
        var currentPart = StringBuilder()
        while (reader.read().also { currentChar = it } != GlobalVar.EOF) {
            val char = currentChar.toChar()
            if (char == GlobalVar.DELIMITER) {
                ListsqrePlanned.addNode(currentPart.toString(), dispName)
                currentPart = StringBuilder()
            } else {
                currentPart.append(char)
            }
        }
        reader.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun updateTextFile(context: Context, fileName: String?) {
    val file = fileName?.let { File(context.filesDir, it) }
    try {
        if (file != null) {
            if(file.exists()) {
                val fileWriter = FileWriter(file, false)
                fileWriter.close()
                for(obj in ListOfListsqre.getEntireList()) {
                    storeDataToFile(context, fileName, obj.fileFormatted())
                }
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

/* --- obsolete ---
fun clearTextFile(context: Context, fileName: String?) {
    val file = fileName?.let { File(context.filesDir, it) }
    try {
        if (file != null) {
            if(file.exists()) {
                val fileWriter = FileWriter(file, false)
                fileWriter.close()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
*/