// TODO: for now this will have to do for user auth. (temporary method)
// TODO: next implementation to include selective user auth. and customizable passcode

package com.example.listsqre

import android.os.Bundle
import android.widget.Toast
import android.widget.Button
import android.content.Intent
import android.widget.EditText
import androidx.activity.ComponentActivity
import android.text.method.PasswordTransformationMethod

class UserAuthActivity : ComponentActivity() {
    private lateinit var passwordIpt: EditText
    private lateinit var passwordBtn: Button

    companion object {
        var enteredOnce: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uauthpage)

        passwordIpt = findViewById(R.id.passwordfield)
        passwordBtn = findViewById(R.id.enterpw)

        passwordIpt.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        passwordIpt.transformationMethod = PasswordTransformationMethod.getInstance()

        passwordBtn.setOnClickListener {
            if(passwordIpt.text.toString() == GlobalVar.UAText) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid passcode", Toast.LENGTH_SHORT).show()
                passwordIpt.setText("")
            }
        }
    }
}