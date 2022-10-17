package org.calinrc.smarthouseremote.auto

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.preference.PreferenceManager
import org.calinrc.smarthouseremote.R
import org.calinrc.smarthouseremote.data.HomeResponse
import org.calinrc.smarthouseremote.data.HomeServerRepository
import org.calinrc.smarthouseremote.data.ParsersFactory
import org.calinrc.smarthouseremote.data.ServerCredentials
import org.calinrc.smarthouseremote.data.model.HomeServerModel

class AndroidAutoMainScreen(carContext: CarContext) : Screen(carContext), DefaultLifecycleObserver {

    private var resIconId:Int = android.R.drawable.presence_online
    private var textMsg:String = carContext.getString(R.string.status_msg_text)
    private val homeServerModel: HomeServerModel
    init {
        lifecycle.addObserver(this)
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(carContext)
        val serverUrl = sharedPreferences.getString("server_url", "")
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")

        homeServerModel = HomeServerModel(homeServerRepo = HomeServerRepository(
            ServerCredentials(
                    serverUrl ?: "",
                    username ?: "",
                    password ?: ""
                ), ParsersFactory()
        ), coroutineScope = lifecycle.coroutineScope)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        homeServerModel.statusResult.observe(this@AndroidAutoMainScreen, Observer {
            val result = it ?: return@Observer
            when (result) {
                is HomeResponse.HomeStatusResponse -> {
                    resIconId = android.R.drawable.presence_online
                    textMsg = carContext.getString(R.string.status_msg_text)
                    invalidate()
                }
                is HomeResponse.FailedHomeResponse -> {
                    textMsg = result.exception.message?: "Failed"
                    resIconId = if (result.statusCode / 100 == 4) {
                        homeServerModel.suspendStatusPooling()
                        android.R.drawable.presence_busy

                    } else {
                        android.R.drawable.presence_offline
                    }
                    invalidate()
                }
                else -> {

                }
            }
        })
    }

    override fun onResume(owner: LifecycleOwner) {
        homeServerModel.resumeStatusPooling()
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        homeServerModel.suspendStatusPooling()
        super.onPause(owner)
    }

    override fun onGetTemplate(): Template {
        val doorActionBuilder = Action.Builder()
            .setBackgroundColor(CarColor.GREEN)
            .setTitle(carContext.getString(R.string.door))
            .setOnClickListener {
                homeServerModel.changeDoorState()
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.door_state_change_action_title),
                    CarToast.LENGTH_LONG
                ).show()
            }

        val gateActionBuilder = Action.Builder()
            .setBackgroundColor(CarColor.GREEN)
            .setTitle(carContext.getString(R.string.gate))
            .setOnClickListener {
                homeServerModel.changeGateState()
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.gate_state_change_action_title),
                    CarToast.LENGTH_LONG
                ).show()
            }

        return MessageTemplate.Builder(
           textMsg
        )
            .setTitle(carContext.getString(R.string.app_name))
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        resIconId
                    )
                )
                    .setTint(CarColor.GREEN)
                    .build()
            )
            .addAction(doorActionBuilder.build())
            .addAction(gateActionBuilder.build())
            .build()
    }

}