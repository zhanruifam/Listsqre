package com.example.listsqre

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
    private lateinit var hourNoti: TextView
    private lateinit var minuNoti: TextView
    private lateinit var cardText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var fileName: String
    private lateinit var dispName: String
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var create: Button
    private lateinit var notify: Button

    override fun onStart() {
        super.onStart()
        title = dispName
        ListOfListsqre.deleteAllNodes()
        readFromFile(this, fileName)
        refreshView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listpage)

        fileName = intent.getStringExtra("LISTNAME").toString()
        dispName = intent.getStringExtra("DISPNAME").toString()

        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)
        notify = findViewById(R.id.nfy)

        resetA.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Selected?")
            builder.setView(rstdialogView)
            builder.setPositiveButton(R.string.proceed) { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    ListOfListsqre.deleteSelNodes()
                    updateTextFile(this, fileName)
                } else {
                    if(rstTxt.isEmpty()) {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.INVALID_INPUT)
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
            builder.setPositiveButton(R.string.proceed) { dialog, _ ->
                val elemName = createTxt.text.toString()
                if(elemName.isNotEmpty()) {
                    ListOfListsqre.addNode(elemName)
                    updateTextFile(this, fileName)
                } else {
                    GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
        }

        notify.setOnClickListener {
            val notiView = layoutInflater.inflate(R.layout.notidialogview, FrameLayout(this))
            hourNoti = notiView.findViewById(R.id.hour)
            minuNoti = notiView.findViewById(R.id.min)
            hourNoti.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            minuNoti.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set Daily: (" + upcomingNoti(this) + ")")
            builder.setView(notiView)
            builder.setPositiveButton(R.string.proceed) { dialog, _ ->
                val hourTxt = hourNoti.text.toString()
                val minTxt = minuNoti.text.toString()
                if(hourTxt.isNotEmpty() && minTxt.isNotEmpty()) {
                    if((hourTxt.toInt() in 0..23) && (minTxt.toInt() in 0..59)) {
                        clearNotiDb(this)
                        feedIntoNotiDb(
                            this,
                            0, // first entry
                            ListOfListsqre.createNotiTitle(),
                            ListOfListsqre.createNotiDescr(),
                            hourTxt.toInt(),
                            minTxt.toInt())
                        scheduleAlarm(this, readNotiDb(this))
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.INVALID_TIME)
                    }
                } else {
                    GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
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
        Listsqre.clrSelList()
        ListOfListsqre.clrSelList()
        removeAllCardViews()
        showCardViews()
    }

    private fun showCardViews() {
        for(obj in ListOfListsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.cardview, CardView(this))
            card.setOnClickListener {
                if(GlobalVar.isLinkValid(obj.getElemname())) {
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
                builder.setPositiveButton(R.string.proceed) { dialog, _ ->
                    val elemName = dialogTxt.text.toString()
                    if(elemName.isNotEmpty()) {
                        obj.setElemname(elemName)
                        updateTextFile(this, fileName)
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
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