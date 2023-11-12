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
    private lateinit var guideTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var plannedL: CardView
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var mtplan: Button
    private lateinit var create: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        /* --- ON BOOT-UP START --- */
        title = "LISTSQRE"
        if(!UserAuthActivity.enteredOnce) {
            UserAuthActivity.enteredOnce = true
            readFromDb(this)
        }
        refreshView()
        /* --- ON BOOT-UP END --- */

        plannedL = findViewById(R.id.pList)
        resetA = findViewById(R.id.rst)
        mtplan = findViewById(R.id.plan)
        create = findViewById(R.id.add)
        guideTxt = findViewById(R.id.instructions)

        plannedL.setOnClickListener {
            val intent = Intent(this, PlanActivity::class.java)
            startActivity(intent)
        }

        resetA.setOnClickListener {
            val rstdialogView = layoutInflater.inflate(R.layout.rstdialogview, FrameLayout(this))
            resetTxt = rstdialogView.findViewById(R.id.rstdialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Selected?")
            builder.setView(rstdialogView)
            builder.setPositiveButton("Delete") { dialog, _ ->
                val rstTxt = resetTxt.text.toString()
                if(rstTxt == GlobalVar.cfmText) {
                    deleteSelTextFile(this, Listsqre.getEntireSelList())
                    Listsqre.deleteSelNodes()
                    updateDb(this)
                } else {
                    // do nothing
                }
                refreshView()
                dialog.dismiss()
            }
            builder.create().show()
        }

        mtplan.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmation:")
            builder.setMessage("Move selected to planned list?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                // TODO: move all list contents into planned list
                // TODO: still keep the original list
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
            builder.setPositiveButton("Create") { dialog, _ ->
                val listName = createTxt.text.toString()
                if(listName.isNotEmpty() && !checkDuplicate(this, listName)) {
                    createTextFile(this, listName)
                    Listsqre.addNode(listName, listName)
                    feedIntoDb(this, Listsqre.getRecent().getId(), listName, listName)
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
                builder.setPositiveButton("Edit") { dialog, _ ->
                    obj.setDisplayname(dialogTxt.text.toString())
                    updateDb(this)
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