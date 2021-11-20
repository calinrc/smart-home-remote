package org.calinrc.smarthouseremote.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.calinrc.smarthouseremote.data.HomeServerRepository
import org.calinrc.smarthouseremote.data.HomeResponse
import java.util.concurrent.atomic.AtomicBoolean

class HomeServerModel(private val homeServerRepo: HomeServerRepository) : ViewModel() {

    private val _statusResult = MutableLiveData<HomeResponse>()
    val statusResult: LiveData<HomeResponse> = _statusResult

    private val _gateChangeResult = MutableLiveData<HomeResponse>()
    val gateChangeResult: LiveData<HomeResponse> = _gateChangeResult

    private val _doorChangeResult = MutableLiveData<HomeResponse>()
    val doorChangeResult: LiveData<HomeResponse> = _doorChangeResult


    private val _cancel = AtomicBoolean()


    fun suspend() {
        _cancel.set(true)
    }


    fun getStatus() {
        // Create a new coroutine to move the execution off the UI thread
        _cancel.set(false)

        viewModelScope.launch {
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

    fun gateStateChange() {
        viewModelScope.launch {
            val result = homeServerRepo.gateStateChange()
            _gateChangeResult.value = result

        }
    }

    fun doorStateChange() {
        viewModelScope.launch {
            val result = homeServerRepo.doorStateChange()
            _doorChangeResult.value = result

        }
    }
}