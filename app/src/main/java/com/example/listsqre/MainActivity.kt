package com.example.listsqre

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

class MainActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var createTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var hourNoti: TextView
    private lateinit var minuNoti: TextView
    private lateinit var cardText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var create: Button
    private lateinit var notify: Button

    override fun onStart() {
        super.onStart()
        title = "Listsqre"
        if(!UserAuthActivity.enteredOnce) {
            UserAuthActivity.enteredOnce = true
            readFromDb(this)
        }
        refreshView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)
        notify = findViewById(R.id.nfy)

        resetA.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Selected Item?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    deleteSelTextFile(this, Listsqre.getEntireSelList())
                    Listsqre.deleteSelNodes()
                    updateDb(this)
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
            builder.setTitle("Create List:")
            builder.setView(dialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val listName = createTxt.text.toString()
                if(listName.isNotEmpty() && !checkDuplicate(this, listName)) {
                    createTextFile(this, listName)
                    Listsqre.addNode(listName, listName)
                    feedIntoDb(this, Listsqre.getRecent().getId(), listName, listName)
                } else {
                    if(listName.isEmpty()) {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.DUPLICATE_INPUT)
                    }
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
            builder.setTitle("Set Time: (" + upcomingNoti(this) + ")")
            builder.setView(notiView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val hourTxt = hourNoti.text.toString()
                val minTxt = minuNoti.text.toString()
                if(hourTxt.isNotEmpty() && minTxt.isNotEmpty()) {
                    if((hourTxt.toInt() in 0..23) && (minTxt.toInt() in 0..59)) {
                        clearNotiDb(this)
                        feedIntoNotiDb(
                            this,
                            0, // first entry
                            Listsqre.createNotiTitle(),
                            Listsqre.createNotiDescr(),
                            hourTxt.toInt(),
                            minTxt.toInt())
                        scheduleAlarm(this, hourTxt.toInt(), minTxt.toInt())
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
        for(obj in Listsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.cardview, CardView(this))
            card.setOnClickListener {
                val intent = Intent(this, ListActivity::class.java)
                intent.putExtra("LISTNAME", obj.getListname())
                intent.putExtra("DISPNAME", obj.getDisplayname())
                startActivity(intent)
            }
            checkBox = card.findViewById(R.id.select_box)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    Listsqre.pushToSelList(obj.getId())
                } else {
                    Listsqre.removeFromSelList(obj)
                }
            }
            options = card.findViewById(R.id.options)
            options.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getDisplayname()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Make Changes:")
                builder.setView(dialogView)
                builder.setPositiveButton("Proceed") { dialog, _ ->
                    val dispName = dialogTxt.text.toString()
                    if(dispName.isNotEmpty()) {
                        obj.setDisplayname(dispName)
                        updateDb(this)
                    } else {
                        GlobalVar.errDialog(this, GlobalVar.ErrorType.EMPTY_INPUT)
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