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

class SLActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var options: Button
    private lateinit var resetA: Button

    private var lastClickTime: Long = 0

    override fun onStart() {
        super.onStart()
        title = "Spotlight Item(s)"
        SpotlightList.deleteAllNodes()
        // read from Db
        refreshView()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spotlightlistpage)

        resetA = findViewById(R.id.rst)

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
                    SpotlightList.deleteSelNodes()
                    // update Db
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
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllCardViews()
    }

    private fun refreshView() {
        Listsqre.clrSelList()
        ListOfListsqre.clrSelList()
        SpotlightList.clrSelList()
        removeAllCardViews()
        showCardViews()
    }

    private fun showCardViews() {
        for(obj in SpotlightList.getEntireList()) {
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
                    SpotlightList.pushToSelList(obj.getId())
                } else {
                    SpotlightList.removeFromSelList(obj)
                }
            }
            options = card.findViewById(R.id.options)
            options.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
                // not use for spotlight page
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