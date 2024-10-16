package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.app.AlertDialog
import android.widget.CheckBox
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.content.pm.PackageManager
import androidx.cardview.widget.CardView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var dialogTxt: TextView
    private lateinit var createTxt: TextView
    private lateinit var resetTxt: TextView
    private lateinit var cardText: TextView
    private lateinit var checkBox: CheckBox
    private lateinit var options: Button
    private lateinit var resetA: Button
    private lateinit var create: Button

    /* --- for widget feature --- */
    private lateinit var wgetCard: CardView
    private lateinit var wgetList: Button

    private var lastClickTime: Long = 0

    override fun onStart() {
        super.onStart()
        title = "Listsqre"
        Listsqre.deleteAllNodes()
        readFromDb(this)
        refreshView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)
        permissionReq()

        wgetCard = findViewById(R.id.repCard)
        // wgetList = findViewById(R.id.n_list) TODO: widget functionality
        resetA = findViewById(R.id.rst)
        create = findViewById(R.id.add)

        wgetCard.setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            // something else
        }

        /*  TODO: widget functionality
        wgetList.setOnClickListener {
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            // something else
        }
        */

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
            if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                return@setOnClickListener
            } else { lastClickTime = System.currentTimeMillis() }
            val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
            createTxt = dialogView.findViewById(R.id.dialogTxt)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Create list:")
            builder.setView(dialogView)
            builder.setPositiveButton(R.string.proceed) { dialog, _ ->
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

    private fun permissionReq() {
        val permission = "android.permission.POST_NOTIFICATIONS"
        val permissionState = ContextCompat.checkSelfPermission(this, permission)
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (!isGranted) {
                    GlobalVar.errDialog(this, GlobalVar.ErrorType.PERMISSION_DENIED)
                } else { /* do nothing */ }
            }
        if (permissionState != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(permission)
        } else { /* do nothing */ }
    }

    private fun showCardViews() {
        for(obj in Listsqre.getEntireList()) {
            val card = layoutInflater.inflate(R.layout.cardview, CardView(this))
            card.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
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
                if (System.currentTimeMillis() - lastClickTime < GlobalVar.clickThreshold) {
                    return@setOnClickListener
                } else { lastClickTime = System.currentTimeMillis() }
                val dialogView = layoutInflater.inflate(R.layout.dialogview, FrameLayout(this))
                dialogTxt = dialogView.findViewById(R.id.dialogTxt)
                dialogTxt.text = obj.getDisplayname()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Make changes:")
                builder.setView(dialogView)
                builder.setPositiveButton(R.string.proceed) { dialog, _ ->
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