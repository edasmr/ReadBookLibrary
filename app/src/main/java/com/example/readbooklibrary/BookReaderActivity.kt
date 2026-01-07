package com.example.readbooklibrary


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import androidx.core.graphics.toColorInt
import java.net.URL

class BookReaderActivity : AppCompatActivity() {

    private lateinit var textContent: TextView
    private lateinit var textPage: TextView
    private lateinit var rootLayout: View

    private var pages: List<String> = emptyList()
    private var currentPage = 0

    private var textSizeSp = 18f
    private var isNightMode = false

    private val charsPerPage = 1500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_reader)

        textContent = findViewById(R.id.textContent)
        textPage = findViewById(R.id.textPage)
        rootLayout = findViewById(R.id.rootLayout)

        val url = intent.getStringExtra("BOOK_URL")

        if (url.isNullOrEmpty()) {
            textContent.text = "Book contents not found."
            return
        }

        loadBook(url)

        findViewById<Button>(R.id.btnNext).setOnClickListener { nextPage() }
        findViewById<Button>(R.id.btnPrev).setOnClickListener { prevPage() }

        findViewById<Button>(R.id.btnIncrease).setOnClickListener {
            textSizeSp += 2
            updateTextSize()
        }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnDecrease).setOnClickListener {
            if (textSizeSp > 12) {
                textSizeSp -= 2
                updateTextSize()
            }
        }

        // Uzun basınca gece modu
        textContent.setOnLongClickListener {
            toggleNightMode()
            true
        }
    }

    private fun loadBook(url: String) {
        Thread {
            try {
                val text = URL(url).readText(Charsets.UTF_8)
                pages = text.chunked(charsPerPage)

                runOnUiThread {
                    showPage()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    textContent.text = "Book content could not be loaded."
                }
            }
        }.start()
    }

    private fun showPage() {
        if (pages.isEmpty()) return

        textContent.text = pages[currentPage]
        textPage.text = "${currentPage + 1} / ${pages.size}"
    }

    private fun nextPage() {
        if (currentPage < pages.lastIndex) {
            currentPage++
            showPage()
        }
    }

    private fun prevPage() {
        if (currentPage > 0) {
            currentPage--
            showPage()
        }
    }

    private fun updateTextSize() {
        textContent.textSize = textSizeSp
        pages = pages.joinToString("").chunked(charsPerPage)
        showPage()
    }

    // GECE MODU
    private fun toggleNightMode() {
        isNightMode = !isNightMode

        if (isNightMode) {
            rootLayout.setBackgroundColor("#121212".toColorInt())
            textContent.setTextColor("#EAEAEA".toColorInt())
        } else {
            rootLayout.setBackgroundColor("#FAF7F2".toColorInt())
            textContent.setTextColor("#000000".toColorInt())
        }
    }
}





