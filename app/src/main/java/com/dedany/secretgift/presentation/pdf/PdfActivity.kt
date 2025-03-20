package com.dedany.secretgift.presentation.pdf

import android.os.Bundle
import android.widget.Toast
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dedany.secretgift.databinding.ActivityPdfBinding

class PdfActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webView
        webView.settings.javaScriptEnabled = true

        // Habilitar la comunicación entre JavaScript y Kotlin
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        try {
            // Cargar el archivo HTML desde los assets
            webView.loadUrl("file:///android_asset/terminos_condiciones.html")
        } catch (e: Exception) {
            // En caso de que haya algún error, mostrar un mensaje al usuario
            Toast.makeText(this, "Error al cargar los términos", Toast.LENGTH_SHORT).show()
        }
    }

    // Clase que implementa la interfaz para manejar los botones desde JavaScript
    class WebAppInterface(private val activity: PdfActivity) {
        @android.webkit.JavascriptInterface
        fun acceptTerms() {
            // Aquí manejamos la aceptación
            Toast.makeText(activity, "Términos Aceptados", Toast.LENGTH_SHORT).show()
            activity.finish() // Cerrar la actividad al aceptar los términos
        }

        @android.webkit.JavascriptInterface
        fun rejectTerms() {
            // Aquí manejamos el rechazo
            Toast.makeText(activity, "Términos Rechazados", Toast.LENGTH_SHORT).show()
            activity.finish() // Cerrar la actividad al rechazar los términos
        }
    }

    // Sobrescribir el comportamiento de la tecla atrás
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack() // Si se puede ir atrás en el WebView, hacerlo
        } else {
            super.onBackPressed() // Si no hay más historial en el WebView, hacer el retroceso normal
        }
    }
}
