package org.calinrc.smarthouseremote.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.calinrc.smarthouseremote.data.HomeServerRepository
import org.calinrc.smarthouseremote.data.HomeResponse

class ViewModelHomeServerModel(private val homeServerRepo: HomeServerRepository) : ViewModel() {
    private val baseHomeServerModel=HomeServerModel(homeServerRepo,viewModelScope)

    val statusResult: LiveData<HomeResponse> = baseHomeServerModel.statusResult

    fun suspendStatusPooling() {
        baseHomeServerModel.suspendStatusPooling()
    }


    fun resumeStatusPooling() {
        baseHomeServerModel.resumeStatusPooling()
    }

    fun changeGateState() {
        baseHomeServerModel.changeGateState()

    }

    fun changeDoorState() {
        baseHomeServerModel.changeDoorState()
    }
}