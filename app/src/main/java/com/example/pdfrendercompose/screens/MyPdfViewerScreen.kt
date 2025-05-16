import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pdfrendercompose.presenter.DeviceViewModel

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPdfViewerScreen(viewModel: DeviceViewModel) {
    val pdfUrl = "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"

    Scaffold(topBar = {
        TopAppBar(title = { Text("PDF in WebView") })
    }) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.allowFileAccess = true
                    settings.domStorageEnabled = true
                    settings.setSupportZoom(true)
                    loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
                }
            }, modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}