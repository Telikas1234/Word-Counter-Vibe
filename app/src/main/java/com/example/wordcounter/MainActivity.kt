package com.example.wordcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.wordcounter.ui.WordCounterScreen
import com.example.wordcounter.ui.theme.WordCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordCounterTheme {
                WordCounterScreen()
            }
        }
    }
}
