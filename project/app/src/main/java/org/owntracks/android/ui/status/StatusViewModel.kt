package org.owntracks.android.ui.status

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import org.owntracks.android.data.EndpointState
import org.owntracks.android.data.repos.EndpointStateRepo
import org.owntracks.android.ui.base.viewmodel.BaseViewModel
import org.owntracks.android.ui.status.logs.LogViewerActivity
import java.util.*
import javax.inject.Inject

@ActivityScoped
class StatusViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    endpointStateRepo: EndpointStateRepo
) :
    BaseViewModel<StatusMvvm.View>() {
    val endpointState: LiveData<EndpointState> = endpointStateRepo.endpointState
    val endpointQueueLength: LiveData<Int> = endpointStateRepo.endpointQueueLength
    private var serviceStarted: Date? = null
    private var locationUpdated: Long = 0

    @Bindable
    fun getServiceStarted(): Date? {
        return serviceStarted
    }

    fun getDozeWhitelisted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                    context.applicationContext.packageName
                )
    }

    @Bindable
    fun getLocationUpdated(): Long {
        return locationUpdated
    }

//    @Subscribe(sticky = true)
//    fun onEvent(e: EndpointState) {
////        endpointState = e
//        endpointMessage = e.message
////        notifyPropertyChanged(BR.endpointState)
//        notifyPropertyChanged(BR.endpointMessage)
//    }

//    @Subscribe(sticky = true)
//    fun onEvent(e: ServiceStarted) {
//        serviceStarted = e.date
//        notifyPropertyChanged(BR.serviceStarted)
//    }
//
//    @Subscribe(sticky = true)
//    fun onEvent(l: Location) {
//        locationUpdated = TimeUnit.MILLISECONDS.toSeconds(l.time)
//        notifyPropertyChanged(BR.locationUpdated)
//    }
//
//    @Subscribe(sticky = true)
//    fun onEvent(e: QueueChanged) {
//        Timber.v("queue changed %s", e.newLength)
//        queueLength = e.newLength
//        notifyPropertyChanged(BR.endpointQueue)
//    }

    fun viewLogs() {
        val intent =
            Intent(context, LogViewerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

