package com.adriantache.projecttracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.adriantache.projecttracker.R

// 1. Google Fonts Setup
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

val OranienbaumFamily = FontFamily(
    Font(googleFont = GoogleFont("Oranienbaum"), fontProvider = provider, weight = FontWeight.Normal),
)

val PlayfairFamily = FontFamily(
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = GoogleFont("Playfair Display"), fontProvider = provider, weight = FontWeight.Normal)
)

val InterFamily = FontFamily(
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Inter"), fontProvider = provider, weight = FontWeight.Medium)
)

// 2. Typography Object
val ProjectHubTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = OranienbaumFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = InterFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)

// 3. Color Palette
val BackgroundDark = Color(0xFF121212)
val SurfaceDark = Color(0xFF1E1E1E)
val TextCream = Color(0xFFE5D5C0)
val TextMuted = Color(0xFFB5A897)
val AccentTeal = Color(0xFF4DB6AC)

// Card Specific Colors (not part of standard Scheme, but used in theme)
val CardGreen = Color(0xFF2D3E33)
val CardPurple = Color(0xFF4A2B44)
val CardBrown = Color(0xFF4E4134)
val CardDarkGrey = Color(0xFF2B2B2B)
val CardBlue = Color(0xFF2B3A4E)
val CardDeepRed = Color(0xFF4E2D2D)
val CardIndigo = Color(0xFF343D52)
val CardOlive = Color(0xFF434D32)

val MyCardColors = listOf(
    CardGreen,
    CardPurple,
    CardBrown,
    CardDarkGrey,
    CardBlue,
    CardDeepRed,
    CardIndigo,
    CardOlive
)

fun myCardColors(index: Int) = MyCardColors[index.mod(MyCardColors.size)]

private val DarkColorScheme = darkColorScheme(
    primary = TextCream,
    secondary = AccentTeal,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = TextCream,
    onSurface = TextCream,
    onSurfaceVariant = TextMuted
)

@Composable
fun ProjectTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to maintain our custom aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> DarkColorScheme // Keeping it dark by default to match your request
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ProjectHubTypography,
        content = content
    )
}
