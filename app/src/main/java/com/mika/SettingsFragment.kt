package com.mika

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.mika.utils.changeLang
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    val LANGUAGE = "LANGUAGE"
    val mainViewModel : MainViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

      //  val saveListPreference: ListPreference? = findPreference("save_settings")
        val langListPreference: ListPreference? = findPreference("language")



        val savePreference: Preference? = findPreference("finish")
        val sp: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        //val value = sp.getString("finish","")

       // val saveValue = saveListPreference?.value
        val lang = langListPreference?.value


        savePreference?.setOnPreferenceClickListener {
            /*if (saveValue == "Save&Exit")
            {
             */
            sp.edit().putString(LANGUAGE,lang).apply()
                if (lang == "Arabic") {
                    changeLang(requireActivity(),"ar")
                    mainViewModel.languageLiveDate.value = "ar"
                }else {
                    changeLang(requireActivity(),"en")
                    mainViewModel.languageLiveDate.value = "en"
                }
                findNavController().navigate(R.id.action_settingsFragment_to_SecondFragment)

                return@setOnPreferenceClickListener true
//            }else
//            {
//                return@setOnPreferenceClickListener false
//            }

        }
    }



    }
