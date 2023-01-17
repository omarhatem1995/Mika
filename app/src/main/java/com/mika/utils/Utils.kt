package com.mika.utils

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import java.util.*

fun changeLang(context: Context, langCode:String) {
    val config = context.resources.configuration
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    config.setLocale(locale)
    config.setLayoutDirection(locale)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics,)

    }
}