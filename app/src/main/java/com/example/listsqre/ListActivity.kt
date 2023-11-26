package com.example.listsqre

import java.net.URL
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.app.AlertDialog
import android.widget.CheckBox
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class ListActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var createTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var guideTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var fileName: String
    private lateinit var dispName: String
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var create: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listpage)
        fileName = intent.getStringExtra("LISTNAME").toString()
        dispName = intent.getStringExtra("DISPNAME").toString()

        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)
        guideTxt = findViewById(R.id.instructions)

        resetA.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Selected?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    ListOfListsqre.deleteSelNodes()
                    updateTextFile(this, fileName)
                } else { /** error handling **/
                    if(rstTxt.isEmpty()) {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
                    } else if(rstTxt.isNotEmpty()) {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.INVALID_INPUT)
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.UNKNOWN_ERROR)
                    }
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
            builder.setTitle("Add Onto List:")
            builder.setView(dialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val elemName = createTxt.text.toString()
                if(elemName.isNotEmpty()) {
                    ListOfListsqre.addNode(elemName)
                    updateTextFile(this, fileName)
                } else { /** error handling **/
                    if(elemName.isEmpty()) {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
                    } else if(elemName.isNotEmpty()) {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.DUPLICATE_INPUT)
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.UNKNOWN_ERROR)
                    }
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

    override fun onStart() {
        super.onStart()
        title = dispName
        ListOfListsqre.deleteAllNodes()
        readFromFile(this, fileName)
        refreshView()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllCardViews()
    }

    private fun refreshView() {
        Listsqre.clrSelList()
        ListOfListsqre.clrSelList()
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
                    builder.setTitle("Description:")
                    builder.setView(dialogView)
                    builder.create().show()
                }
            }
            checkBox = card.findViewById(R.id.select_box)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ListOfListsqre.pushToSelList(obj.getId())
                } else {
                    ListOfListsqre.removeFromSelList(obj)
                }
            }
            options = card.findViewById(R.id.options)
            options.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getElemname()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Make Changes:")
                builder.setView(dialogView)
                builder.setPositiveButton("Proceed") { dialog, _ ->
                    val elemName = dialogTxt.text.toString()
                    if(elemName.isNotEmpty()) {
                        obj.setElemname(elemName)
                        updateTextFile(this, fileName)
                    } else { /** error handling **/
                        if(elemName.isEmpty()) {
                            GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
                        } else {
                            GlobalVar.errDialog(this, GlobalVar.ErrorType.UNKNOWN_ERROR)
                        }
                    }
                    refreshView()
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