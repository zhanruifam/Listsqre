package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.app.AlertDialog
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var createTxt: TextView
    private lateinit var deleteTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var guideTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var create: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        // ON BOOT-UP...
        title = "Listsqre"
        readFromDb(this)
        refreshView()

        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)
        guideTxt = findViewById(R.id.instructions)

        resetA.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete All?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Delete") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    deleteAllTextFile(this, Listsqre.getEntireList())
                    Listsqre.deleteAllNodes()
                    updateDb(this, true)
                } else {
                    // do nothing
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
        }

        create.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
            createTxt = dialogView.findViewById(R.id.dialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Create List:")
            builder.setView(dialogView)
            builder.setPositiveButton("Create") { dialog, _ ->
                val listName = createTxt.text.toString()
                if(listName.isNotEmpty() && !checkDuplicate(this, listName)) {
                    createTextFile(this, listName)
                    Listsqre.addNode(listName, listName)
                    feedIntoDb(this, Listsqre.getRecent().getId(), listName, listName)
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

    private fun showCardViews() {
        for(obj in Listsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.cardview, CardView(this))
            card.setOnClickListener {
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra("LISTNAME", obj.getListname())
                intent.putExtra("DISPNAME", obj.getDisplayname())
                startActivity(intent)
            }
            options = card.findViewById(R.id.options)
            options.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.optdialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                deleteTxt = dialogView.findViewById(R.id.deldialogTxt)
                dialogTxt.text = obj.getDisplayname()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Settings:")
                builder.setView(dialogView)
                builder.setPositiveButton("Edit") { dialog, _ ->
                    obj.setDisplayname(dialogTxt.text.toString())
                    updateDb(this, false)
                    refreshView()
                    dialog.dismiss()
                }
                builder.setNegativeButton("Delete") { dialog, _ ->
                    val deltxt = deleteTxt.text.toString()
                    if(deltxt == GlobalVar.cfmText) {
                        deleteTextFile(this, obj.getListname())
                        Listsqre.deleteNode(obj.getId())
                        updateDb(this, false)
                    } else {
                        // do nothing
                    }
                    refreshView()
                    dialog.dismiss()
                }
                builder.create().show()
            }
            cardText = card.findViewById(R.id.info_text)
            cardText.text = obj.getDisplayname()
            cardLists = findViewById(R.id.cardContainer)
            cardLists.addView(card)
        }
    }

    private fun removeAllCardViews() {
        cardLists = findViewById(R.id.cardContainer)
        cardLists.removeAllViews()
    }
}