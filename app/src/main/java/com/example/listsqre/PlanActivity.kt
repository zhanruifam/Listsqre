package com.example.listsqre

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import androidx.activity.ComponentActivity

class PlanActivity : ComponentActivity() {
    private lateinit var cardLists: LinearLayout
    private lateinit var guideTxt: TextView
    private lateinit var clrPage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.planpage)

        /* --- ON BOOT-UP START --- */
        title = "PLANNED LIST"
        /* --- ON BOOT-UP END --- */

        clrPage = findViewById(R.id.clr)
        guideTxt = findViewById(R.id.instructions)

        clrPage.setOnClickListener {
            // TODO: clear all card views from planned list
        }

        guideTxt.setOnClickListener {
            GlobalVar.appGuide(this)
        }
    }
}