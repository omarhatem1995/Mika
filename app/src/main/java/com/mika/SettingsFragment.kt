package com.mika

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    val LANGUAGE = "LANGUAGE"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val saveListPreference: ListPreference? = findPreference("save_settings")
        val langListPreference: ListPreference? = findPreference("language")



        val savePreference: Preference? = findPreference("finish")
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        //val value = sp.getString("finish","")

        val saveValue = saveListPreference?.value
        val lang = langListPreference?.value


        savePreference?.setOnPreferenceClickListener {
            if (saveValue == "Save&Exit")
            {
                sp.edit().putString(LANGUAGE,lang).apply()
                if (lang == "Arabic") {
                    changeLang(requireContext(),"ar")
                }else {
                    changeLang(requireContext(),"en")
                }
                findNavController().navigateUp()

                return@setOnPreferenceClickListener true
            }else
            {
                return@setOnPreferenceClickListener false
            }

        }
    }

    fun changeLang(context: Context, langCode:String) {
        val config = context.resources.configuration
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    }
