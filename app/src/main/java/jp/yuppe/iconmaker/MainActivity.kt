package jp.yuppe.iconmaker

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import jp.yuppe.iconmaker.ui.theme.IconMakerTheme
import kotlinx.coroutines.launch
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IconMakerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainView()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    IconMakerTheme {
        MainView()
    }
}

@Composable
fun MainView() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current
    var composeViewRef by remember { mutableStateOf<ComposeView?>(null) }
    var isGranted by remember { mutableStateOf(true) }
    val icon: ImageVector = Icons.Default.Draw
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            isGranted = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        true
    } else {
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // キャプチャ時にインスタンスを取得するために、AndroidViewでアイコンを描画
            AndroidView(
                factory = { ctx ->
                    ComposeView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            (300 * density.density).toInt(),
                            (300 * density.density).toInt()
                        )
                        setContent {
                            AppIconView(
                                iconVector = icon,
                                baseColor = Color(235, 149, 0),
                                modifier = Modifier
                                    .size(300.dp)
                                    .background(Color.White)
                            )
                        }
                        composeViewRef = this
                    }
                }
            )

            Text(icon.name)
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 16.dp),
            onClick = {
                if (isGranted) {
                    val view = composeViewRef
                    view?.let {
                        val bitmap = it.drawToBitmap()
                        scope.launch {
                            saveImageToGallery(context, bitmap)
                        }
                    }
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        ) {
            Text("Save Image")
        }
    }
}

fun saveImageToGallery(context: Context, bitmap: Bitmap) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "Icon_${System.currentTimeMillis()}.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
    }
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    uri?.let {
        val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
        outputStream.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            Toast.makeText(context, "Image file saved to: " + it.path, Toast.LENGTH_LONG).show()
        }
    }
}

