package org.calinrc.smarthouseremote

import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.car.app.*
import androidx.car.app.R
import androidx.car.app.validation.HostValidator


class AndroidAutoService() : CarAppService() {

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return object: Session() {
            override fun onCreateScreen(intent: Intent): Screen {
                return AndroidAutoMainScreen(carContext)
            }
        }
    }

    override fun createHostValidator(): HostValidator {
        return if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        } else {
            HostValidator.Builder(applicationContext)
                .addAllowedHosts(R.array.hosts_allowlist_sample)
                .build()
        }
    }

//    fun getCarActivity(): Class<out CarActivity?>? {
//        return MainActivity::class.java
//    }

}