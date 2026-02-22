package com.example.gestaoderisco.view

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gestaoderisco.R
import com.example.gestaoderisco.utils.TacticalUtils
import com.google.android.material.textfield.TextInputEditText

class SitrepDecoderActivity : AppCompatActivity() {

    private lateinit var etInput: TextInputEditText
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sitrep_decoder)

        etInput = findViewById(R.id.etEncryptedInput)
        tvResult = findViewById(R.id.tvDecryptedResult)
        
        setupButtons()
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnPaste).setOnClickListener {
            colarDoClipboard()
        }

        findViewById<Button>(R.id.btnDecrypt).setOnClickListener {
            descriptografarRelatorio()
        }
    }

    private fun colarDoClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard.hasPrimaryClip()) {
            val item = clipboard.primaryClip?.getItemAt(0)
            etInput.setText(item?.text)
        } else {
            Toast.makeText(this, "Área de transferência vazia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun descriptografarRelatorio() {
        val textoCifrado = etInput.text.toString().trim()
        
        if (textoCifrado.isEmpty()) {
            Toast.makeText(this, "Insira o código criptografado", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = TacticalUtils.descriptografarLog(textoCifrado)
        tvResult.text = resultado
    }
}