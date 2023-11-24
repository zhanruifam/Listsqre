package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.app.AlertDialog
import android.widget.CheckBox
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
    private lateinit var checkBox: CheckBox
    private lateinit var clrPage: Button
    private lateinit var doneSel: Button
    private lateinit var unPlanB: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.planpage)

        clrPage = findViewById(R.id.clear)
        doneSel = findViewById(R.id.remove)
        unPlanB = findViewById(R.id.unplan)
        guideTxt = findViewById(R.id.instructions)

        clrPage.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Done All Items?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    ListsqrePlanned.clrPlannedList()
                    updateDbPlanned(this)
                } else {
                    // do nothing
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
        }

        doneSel.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Done Selected Items?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Proceed") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    ListsqrePlanned.deleteSelNodes()
                    updateDbPlanned(this)
                } else {
                    // do nothing
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
        }

        unPlanB.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Unplan Selected?")
            builder.setMessage("Revert selected item(s) to original list?")
            builder.setPositiveButton("Proceed") { dialog, _ ->
                for(obj in ListsqrePlanned.getEntireSelList()) {
                    Listsqre.searchExisting(this, obj.getDisp(), obj.getDesc())
                }
                ListsqrePlanned.deleteSelNodes()
                updateDbPlanned(this)
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
        title = "Planned List"
        refreshView()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeAllCardViews()
    }

    private fun refreshView() {
        Listsqre.clrSelList()
        ListOfListsqre.clrSelList()
        ListsqrePlanned.clrSelList()
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
            checkBox = card.findViewById(R.id.select_box)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) {
                    ListsqrePlanned.pushToSelList(obj.getId())
                } else {
                    ListsqrePlanned.removeFromSelList(obj)
                }
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