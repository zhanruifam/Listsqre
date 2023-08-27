package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.app.AlertDialog
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var createTxt: EditText
    private lateinit var cardText: TextView
    private lateinit var resetA: Button
    private lateinit var create: Button
    private lateinit var delete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        // on start function calls
        readFromDb(this)
        refreshView()

        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)

        resetA.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation")
            builder.setMessage("Delete all?")
            builder.setCancelable(false)
            builder.setPositiveButton("Yes") { dialog, _ ->
                deleteAllTextFile(this, Listsqre.getEntireList())
                Listsqre.deleteAllNodes()
                updateDb(this, true)
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
                val listName = createTxt.text.toString()
                if(listName.isNotEmpty()) {
                    createTextFile(this, listName)
                    Listsqre.addNode(listName)
                    feedIntoDb(this, Listsqre.getRecent().getId(), listName)
                } else {
                    // do nothing
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
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
                startActivity(intent)
            }
            delete = card.findViewById(R.id.del)
            delete.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmation")
                builder.setMessage("Delete this?")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { dialog, _ ->
                    deleteTextFile(this, obj.getListname())
                    Listsqre.deleteNode(obj.getId())
                    updateDb(this, false)
                    refreshView()
                    dialog.dismiss()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.create().show()
            }
            cardText = card.findViewById(R.id.info_text)
            cardText.text = obj.getListname()
            cardLists = findViewById(R.id.cardContainer)
            cardLists.addView(card)
        }
    }

    private fun removeAllCardViews() {
        cardLists = findViewById(R.id.cardContainer)
        cardLists.removeAllViews()
    }
}