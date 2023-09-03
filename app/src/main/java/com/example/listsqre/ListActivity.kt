package com.example.listsqre

import java.net.URL
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.app.AlertDialog
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class ListActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var createTxt: TextView
    private lateinit var guideTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var fileName: String
    private lateinit var dispName: String
    private lateinit var resetA: Button
    private lateinit var create: Button
    private lateinit var delete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listpage)
        fileName = intent.getStringExtra("LISTNAME").toString()
        dispName = intent.getStringExtra("DISPNAME").toString()

        // ON BOOT-UP...
        title = dispName
        ListOfListsqre.deleteAllNodes()
        readFromFile(this, fileName)
        refreshView()

        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)
        guideTxt = findViewById(R.id.instructions)

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
            val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
            createTxt = dialogView.findViewById(R.id.dialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setView(dialogView)
            builder.setPositiveButton("Create") { dialog, _ ->
                val elemName = createTxt.text.toString()
                if(elemName.isNotEmpty()) {
                    ListOfListsqre.addNode(elemName)
                    updateTextFile(this, fileName)
                } else {
                    // do nothing
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
        }

        guideTxt.setOnClickListener {
            GlobalVar.appGuide(this)
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

    private fun isLinkValid(urlString: String): Boolean {
        // URL() throws an exception if link is invalid
        return try {
            URL(urlString)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showCardViews() {
        for(obj in ListOfListsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.listcardview, CardView(this))
            card.setOnClickListener {
                if(isLinkValid(obj.getElemname())) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(obj.getElemname()))
                    startActivity(intent)
                } else {
                    val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                    dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                    dialogTxt.text = obj.getElemname()
                    val builder = AlertDialog.Builder(this)
                    builder.setView(dialogView)
                    builder.create().show()
                }
            }
            card.setOnLongClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getElemname()
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)
                builder.setPositiveButton("Edit") { dialog, _ ->
                    obj.setElemname(dialogTxt.text.toString())
                    updateTextFile(this, fileName)
                    refreshView()
                    dialog.dismiss()
                }
                builder.create().show()
                true
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