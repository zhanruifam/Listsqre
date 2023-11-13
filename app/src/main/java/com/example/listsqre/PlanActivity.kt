package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.app.AlertDialog
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity

class PlanActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var cardDesc: TextView
    private lateinit var cardDisp: TextView
    private lateinit var guideTxt: TextView
    private lateinit var clrPage: Button

    companion object {
        var PAEnteredOnce: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.planpage)

        /* --- ON BOOT-UP START --- */
        title = "Planned List"
        if(!PAEnteredOnce) {
            PAEnteredOnce = true
            readFromDbPlanned(this)
        }
        refreshView()
        /* --- ON BOOT-UP END --- */

        clrPage = findViewById(R.id.clr)
        guideTxt = findViewById(R.id.instructions)

        clrPage.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Clear Planned List?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    ListsqrePlanned.clearPlannedList()
                    updateDbPlanned(this)
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
        Listsqre.clearSelList()
        ListOfListsqre.clearSelList()
        removeAllCardViews()
        showCardViews()
    }

    private fun showCardViews() {
        for(obj in ListsqrePlanned.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.plannedcardview, CardView(this))
            card.setOnClickListener {
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getDesc()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Description:")
                builder.setView(dialogView)
                builder.create().show()
            }
            cardDesc = card.findViewById(R.id.desc)
            cardDisp = card.findViewById(R.id.disp)
            cardDesc.text = obj.getDesc()
            cardDisp.text = obj.getDisp()
            cardLists = findViewById(R.id.cardContainer)
            cardLists.addView(card)
        }
    }

    private fun removeAllCardViews() {
        cardLists = findViewById(R.id.cardContainer)
        cardLists.removeAllViews()
    }
}