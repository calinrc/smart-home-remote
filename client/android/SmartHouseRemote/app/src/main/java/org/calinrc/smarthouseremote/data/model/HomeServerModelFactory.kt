package org.calinrc.smarthouseremote.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.calinrc.smarthouseremote.data.HomeServerRepository
import org.calinrc.smarthouseremote.data.ParsersFactory
import org.calinrc.smarthouseremote.data.ServerCredentials

class HomeServerModelFactory(private val creds: ServerCredentials,
                             private val parserFactory: ParsersFactory
) : ViewModelProvider.Factory{

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeServerModel::class.java)) {
            return HomeServerModel(homeServerRepo = HomeServerRepository(creds, parserFactory)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}