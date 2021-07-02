package org.owntracks.android.ui.status

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.PowerManager
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import org.owntracks.android.data.EndpointState
import org.owntracks.android.data.repos.EndpointStateRepo
import org.owntracks.android.data.repos.LocationRepo
import org.owntracks.android.ui.base.viewmodel.BaseViewModel
import org.owntracks.android.ui.status.logs.LogViewerActivity
import java.util.*
import javax.inject.Inject

@ActivityScoped
class StatusViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    endpointStateRepo: EndpointStateRepo,
    locationRepo: LocationRepo
) :
    BaseViewModel<StatusMvvm.View>() {
    val endpointState: LiveData<EndpointState> = endpointStateRepo.endpointState
    val endpointQueueLength: LiveData<Int> = endpointStateRepo.endpointQueueLength
    val serviceStarted: LiveData<Date> = endpointStateRepo.serviceStartedDate
    val currentLocation: LiveData<Location> = locationRepo.currentPublishedLocation

    fun getDozeWhitelisted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                (context.applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                    context.applicationContext.packageName
                )
    }

    fun viewLogs() {
        val intent =
            Intent(context, LogViewerActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

