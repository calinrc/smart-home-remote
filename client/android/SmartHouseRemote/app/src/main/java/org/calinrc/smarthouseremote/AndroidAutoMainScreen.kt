package org.calinrc.smarthouseremote

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.model.*
import androidx.core.graphics.drawable.IconCompat

class AndroidAutoMainScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val doorActionBuilder = Action.Builder()
            .setBackgroundColor(CarColor.GREEN)
            .setTitle(carContext.getString(R.string.door))
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.primary_action_title),
                    CarToast.LENGTH_LONG
                ).show()
            }

        val gateActionBuilder = Action.Builder()
            .setBackgroundColor(CarColor.GREEN)
            .setTitle(carContext.getString(R.string.gate))
            .setOnClickListener {
                CarToast.makeText(
                    carContext,
                    carContext.getString(R.string.secondary_action_title),
                    CarToast.LENGTH_LONG
                ).show()
            }

        return MessageTemplate.Builder(
            carContext.getString(R.string.status_msg_text)
        )
            .setTitle(carContext.getString(R.string.app_name))
            .setIcon(
                CarIcon.Builder(
                    IconCompat.createWithResource(
                        carContext,
                        android.R.drawable.presence_online
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