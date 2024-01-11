package com.example.listsqre

import android.os.Bundle
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
    private lateinit var options: Button

    private var lastClickTime: Long = 0

    override fun onStart() {
        super.onStart()
        title = "Notification List"
        refreshView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notipage)
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
                scheduleAlarm(this, readNotiFirstEntry(this))
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