package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.app.AlertDialog
import android.widget.EditText
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class ListActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var cardText: TextView
    private lateinit var dialgtxt: TextView
    private lateinit var elemName: EditText
    private lateinit var resetA: Button
    private lateinit var create: Button
    private lateinit var delete: Button
    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listpage)
        fileName = intent.getStringExtra("LISTNAME").toString()

        // on start function calls
        ListOfListsqre.deleteAllNodes()
        readFromFile(this, fileName)
        refreshView()

        elemName = findViewById(R.id.elemname)
        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)

        resetA.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete all?")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { dialog, _ ->
                ListOfListsqre.deleteAllNodes()
                clearTextFile(this, fileName)
                refreshView()
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }

        create.setOnClickListener {
            if(elemName.text.toString().isNotEmpty()) {
                ListOfListsqre.addNode(elemName.text.toString())
                storeDataToFile(this, fileName, ListOfListsqre.getRecent().fileFormatted())
            }
            elemName.setText("")
            refreshView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllCardViews()
    }

    private fun refreshView() {
        removeAllCardViews()
        showCardViews()
    }

    private fun showCardViews() {
        for(obj in ListOfListsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.listcardview, CardView(this))
            card.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialgtxt = dialogView.findViewById(R.id.dialogTxt)
                dialgtxt.text = obj.getElemname()
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                builder.setPositiveButton("Edit") { dialog, _ ->
                    obj.setElemname(dialgtxt.text.toString())
                    updateTextFile(this, fileName)
                    refreshView()
                    dialog.dismiss()
                }
                builder.create().show()
            }
            delete = card.findViewById(R.id.del)
            delete.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmation")
                builder.setMessage("Delete this?")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { dialog, _ ->
                    ListOfListsqre.deleteNode(obj.getId())
                    updateTextFile(this, fileName)
                    refreshView()
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
            cardText = card.findViewById(R.id.info_text)
            cardText.text = obj.getElemname()
            cardLists = findViewById(R.id.cardContainer)
            cardLists.addView(card)
        }
    }

    private fun removeAllCardViews() {
        cardLists = findViewById(R.id.cardContainer)
        cardLists.removeAllViews()
    }
}