package com.ets.onlinebiblioteka.viewmodels

import androidx.lifecycle.ViewModel
import com.ets.onlinebiblioteka.util.FcmService
import com.ets.onlinebiblioteka.util.GlobalData

class SettingsViewModel : ViewModel() {
    fun getNovaKnjiga(): Boolean {
        return GlobalData.getSharedPreferences().getBoolean(FcmService.TOPIC_NOVA_KNJIGA, true)
    }

    fun toggleNovaKnjiga(v: Boolean) {
        GlobalData.getSharedPreferences()
            .edit()
            .putBoolean(FcmService.TOPIC_NOVA_KNJIGA, v)
            .commit()

        /*if (v) {
            Firebase.messaging.subscribeToTopic(FcmService.TOPIC_NOVA_KNJIGA)
        } else {
            Firebase.messaging.unsubscribeFromTopic(FcmService.TOPIC_NOVA_KNJIGA)
        }*/
    }
}