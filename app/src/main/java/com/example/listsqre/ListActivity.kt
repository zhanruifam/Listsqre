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
    private lateinit var notifCard: CardView
    private lateinit var dialogTxt: TextView
    private lateinit var createTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var hourNoti: TextView
    private lateinit var minuNoti: TextView
    private lateinit var cardText: TextView
    private lateinit var notiText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var fileName: String
    private lateinit var dispName: String
    private lateinit var notiList: Button
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var create: Button

    private var lastClickTime: Long = 0

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

        notifCard = findViewById(R.id.repCard)
        notiList = findViewById(R.id.n_list)
        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)

        notifCard.setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            val notiView = layoutInflater.inflate(R.layout.notidialogview, FrameLayout(this))
            hourNoti = notiView.findViewById(R.id.hour)
            minuNoti = notiView.findViewById(R.id.min)
            hourNoti.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            minuNoti.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Set reminder for selected?")
            builder.setView(notiView)
            builder.setPositiveButton(R.string.proceed) { dialog, _ ->
                val hourTxt = hourNoti.text.toString()
                val minTxt = minuNoti.text.toString()
                if(hourTxt.isNotEmpty() && minTxt.isNotEmpty()) {
                    if((hourTxt.toInt() in 0..23) && (minTxt.toInt() in 0..59)) {
                        NotiOfListsqre.addNode(
                            ListOfListsqre.createNotiTitle(),
                            ListOfListsqre.createNotiDescr(),
                            hourTxt.toInt(),
                            minTxt.toInt())
                        updateNotiDb(this)
                        scheduleAlarm(this, readNotiFirstEntry(this))
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

        notiList.setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            val intent = Intent(this, NotiActivity::class.java)
            startActivity(intent)
        }

        resetA.setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete selected?")
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
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
            createTxt = dialogView.findViewById(R.id.dialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add onto list:")
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
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllCardViews()
    }

    private fun refreshView() {
        Listsqre.clrSelList()
        ListOfListsqre.clrSelList()
        updateNotificationTxt()
        removeAllCardViews()
        showCardViews()
    }

    private fun updateNotificationTxt() {
        notiText = findViewById(R.id.n_info)
        notiText.text = upcomingNoti(this)
    }

    private fun showCardViews() {
        for(obj in ListOfListsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.cardview, CardView(this))
            card.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
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
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getElemname()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Make changes:")
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