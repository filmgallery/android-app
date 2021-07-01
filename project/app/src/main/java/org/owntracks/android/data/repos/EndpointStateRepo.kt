package org.owntracks.android.data.repos

import androidx.lifecycle.MutableLiveData
import org.owntracks.android.data.EndpointState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EndpointStateRepo @Inject constructor() {
    fun setState(newEndpointState: EndpointState) {
        endpointState.postValue(newEndpointState)
    }

    fun setQueueLength(queueLength: Int) {
        endpointQueueLength.postValue(queueLength);
    }

    val endpointState: MutableLiveData<EndpointState> = //TODO migrate this to Kotlin flow once we get AGP 7
        MutableLiveData(EndpointState.IDLE)

    val endpointQueueLength: MutableLiveData<Int> =
        MutableLiveData(0)
}