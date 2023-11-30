package com.dicoding.todoapp.setting

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Notifications permission granted")
            } else {
                showToast("Notifications will not show without permission")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                val wManager: WorkManager = WorkManager.getInstance(requireContext())
                lateinit var periodicRequest: PeriodicWorkRequest

                if (preference.key == getString(R.string.pref_key_notify)){
                    if (newValue == true){
                        val sets = Data.Builder()
                            .putString(NOTIFICATION_CHANNEL_ID, channelName)
                            .build()

                        periodicRequest = PeriodicWorkRequest.Builder(
                            NotificationWorker::class.java, 1, TimeUnit.DAYS
                        ).setInputData(sets).build()

                        wManager.enqueue(periodicRequest)
                        wManager.getWorkInfoByIdLiveData(periodicRequest.id).observe(viewLifecycleOwner){ info ->

                            if (info.state == WorkInfo.State.ENQUEUED){
                                Log.d("SettingFragment","Pengingat has been enqueued")
                            }
                        }
                    } else{
                        try {
                            wManager.getWorkInfoByIdLiveData(periodicRequest.id).observe(viewLifecycleOwner){ info ->

                                if (info.state == WorkInfo.State.ENQUEUED){
                                    try {
                                        wManager.cancelWorkById(periodicRequest.id)

                                    }catch (exception: Exception){
                                        Log.e("SettingFragment","Gagal membatalkan Periodic Work")
                                    }
                                    Toast.makeText(requireContext(),"Task pengingat dibatalkan!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }catch (exception: Exception){
                            Log.e("SettingFragment", "Gagal membatalkan pengingat : ${exception.message}")
                        }
                    }
                }
                true
            }

        }

    }
}