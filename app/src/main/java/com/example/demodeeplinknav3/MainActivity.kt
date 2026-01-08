package com.example.demodeeplinknav3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.example.demodeeplinknav3.ui.theme.DemoDeepLinkNav3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoDeepLinkNav3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        this@MainActivity,
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(context: Context?, name: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Button(onClick = {
            val intent = Intent(
                context,
                CreateDeepLinkActivity::class.java
            )
            // start activity with the url
//            intent.data = finalUrl.toUri()
            context?.startActivity(intent)
        }) {
            Text(text = "Open CreateDeepLinkActivity")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DemoDeepLinkNav3Theme {
        Greeting(null, "Android")
    }
}