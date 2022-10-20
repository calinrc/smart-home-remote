package org.calinrc.smarthouseremote.auto

import android.graphics.BitmapFactory
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

    private var resIconId: Int = android.R.drawable.presence_online
    private val homeServerModel: HomeServerModel
    private val mPaneImage: IconCompat

    init {
        lifecycle.addObserver(this)
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(carContext)
        val serverUrl = sharedPreferences.getString("server_url", "")
        val username = sharedPreferences.getString("username", "")
        val password = sharedPreferences.getString("password", "")

        val resources = carContext.resources
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.house_v3)
        mPaneImage = IconCompat.createWithBitmap(bitmap)


        homeServerModel = HomeServerModel(
            homeServerRepo = HomeServerRepository(
                ServerCredentials(
                    serverUrl ?: "",
                    username ?: "",
                    password ?: ""
                ), ParsersFactory()
            ), coroutineScope = lifecycle.coroutineScope
        )
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        homeServerModel.statusResult.observe(this@AndroidAutoMainScreen, Observer {
            val result = it ?: return@Observer
            when (result) {
                is HomeResponse.HomeStatusResponse -> {
                    resIconId = android.R.drawable.presence_online
                    invalidate()
                }
                is HomeResponse.FailedHomeResponse -> {
//                    val textMsg = result.exception.message?: "Failed"
//
//                    CarToast.makeText(
//                        carContext,
//                        textMsg,
//                        CarToast.LENGTH_SHORT
//                    ).show()

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
            .setBackgroundColor(CarColor.BLUE)
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
            .setBackgroundColor(CarColor.BLUE)
            .setTitle(carContext.getString(R.string.gate))
            .setOnClickListener {
                homeServerModel.changeGateState()
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.gate_state_change_action_title),
                    CarToast.LENGTH_LONG
                ).show()
            }

        val paneBuilder = Pane.Builder()

        paneBuilder.addRow(
            Row.Builder()
                .setTitle(carContext.getString(R.string.app_name))
                .build()
        )

        paneBuilder.addRow(
            Row.Builder()
                .setTitle(carContext.getString(R.string.status_msg_text))
                .setImage(
                    CarIcon.Builder(
                        IconCompat.createWithResource(
                            carContext,
                            resIconId
                        )
                    ).build(), Row.IMAGE_TYPE_LARGE
                )
                .build()
        )

//        // Also set a large image outside of the rows.
        paneBuilder.setImage(CarIcon.Builder(mPaneImage).build())
        paneBuilder.addAction(doorActionBuilder.build())
            .addAction(gateActionBuilder.build())
        return PaneTemplate.Builder(paneBuilder.build()).build()
    }

}