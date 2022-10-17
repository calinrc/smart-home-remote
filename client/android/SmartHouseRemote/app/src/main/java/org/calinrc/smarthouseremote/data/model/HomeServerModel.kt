package org.calinrc.smarthouseremote.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.calinrc.smarthouseremote.data.HomeServerRepository
import org.calinrc.smarthouseremote.data.HomeResponse
import java.util.concurrent.atomic.AtomicBoolean

class HomeServerModel(private val homeServerRepo: HomeServerRepository, private val coroutineScope: CoroutineScope)  {

    private val _statusResult = MutableLiveData<HomeResponse>()
    val statusResult: LiveData<HomeResponse> = _statusResult

    private val _cancel = AtomicBoolean()


    fun suspendStatusPooling() {
        _cancel.set(true)
    }


    fun resumeStatusPooling() {
        // Create a new coroutine to move the execution off the UI thread
        _cancel.set(false)

        coroutineScope.launch {
            while (!_cancel.get()) {
                val result = homeServerRepo.getStatus()
                _statusResult.value = result
                delay(1000)
            }
//            when (result) {
//                is StatusResponse.HomeStatusResponse -> _statusResult.value = result
//                else -> {
//                    //Toast.makeText(this, "Error ${(result as Result.Error).exception}", Toast.LENGTH_SHORT).show()
//                    _statusResult.value = StatusResponse.HomeStatusResponse()
//                }
//            }
        }
    }

    fun changeGateState() {
        coroutineScope.launch {
            homeServerRepo.gateStateChange()
        }
    }

    fun changeDoorState() {
        coroutineScope.launch {
            homeServerRepo.doorStateChange()
        }
    }
}