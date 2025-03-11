package com.example.listsqre

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.app.AlertDialog
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class NotiActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var hourNoti: TextView
    private lateinit var minuNoti: TextView
    private lateinit var options: Button
    private lateinit var notify: Button

    private var lastClickTime: Long = 0

    override fun onStart() {
        super.onStart()
        title = "Notifications"
        NotiOfListsqre.deleteAllNodes()     // clear list before re-init happens
        readFromNotiDb(this)                // notification re-init happens here
        refreshView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notipage)

        notify = findViewById(R.id.notify)

        notify.setOnClickListener {
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
                        scheduleAlarm(this, hourTxt.toInt(), minTxt.toInt())
                        Toast.makeText(this, "Notification set", Toast.LENGTH_SHORT).show()
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
        for(obj in NotiOfListsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.noticardview, CardView(this))
            card.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getFormattedString()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Description:")
                builder.setView(dialogView)
                builder.create().show()
            }
            options = card.findViewById(R.id.options)
            options.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
                NotiOfListsqre.deleteNode(obj.getId())
                updateNotiDb(this)
                // scheduleAlarm(this, readNotiFirstEntry(this)) /* not used in this version */
                refreshView()
            }
            cardText = card.findViewById(R.id.info_text)
            cardText.text = obj.getFormattedString()
            cardLists = findViewById(R.id.cardContainer)
            cardLists.addView(card)
        }
    }

    private fun removeAllCardViews() {
        cardLists = findViewById(R.id.cardContainer)
        cardLists.removeAllViews()
    }
}