package org.owntracks.android.data.repos

import android.location.Location
import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepo @Inject constructor(private val eventBus: EventBus) {
    var currentLocation: MutableLiveData<Location> = MutableLiveData()

    val currentLocationTime: Long
        get() = currentLocation.value?.time ?: 0

    fun setCurrentLocation(l: Location) {
        currentLocation.postValue(l)
        eventBus.postSticky(l)
    }
}