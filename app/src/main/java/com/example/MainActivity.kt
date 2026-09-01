package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.NoorApp
import com.example.ui.theme.CanvasMint
import com.example.ui.theme.NoorTheme
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
            val isArabic = appLanguage.equals("Arabic", ignoreCase = true) ||
                    appLanguage == "العربية" ||
                    appLanguage.startsWith("ar", ignoreCase = true)

            val currentLocale = remember(isArabic) {
                if (isArabic) Locale("ar") else Locale("en")
            }
            val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            val configuration = LocalConfiguration.current
            val context = LocalContext.current

            val localizedConfiguration = remember(configuration, currentLocale) {
                Configuration(configuration).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
            }

            val localizedContext = remember(context, currentLocale) {
                val config = Configuration(context.resources.configuration).apply {
                    setLocale(currentLocale)
                    setLayoutDirection(currentLocale)
                }
                context.createConfigurationContext(config)
            }

            CompositionLocalProvider(
                LocalConfiguration provides localizedConfiguration,
                LocalLayoutDirection provides layoutDirection,
                LocalContext provides localizedContext
            ) {
                NoorTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = CanvasMint
                    ) {
                        NoorApp(viewModel = viewModel)
                    }
                }
            }
        }
    }
}


