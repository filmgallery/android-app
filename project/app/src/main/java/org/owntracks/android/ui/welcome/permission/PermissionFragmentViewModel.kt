package org.owntracks.android.ui.welcome.permission

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionFragmentViewModel @Inject internal constructor() :
    ViewModel() {
    val permissionGranted = MutableLiveData(false)
    val permissionRequired = MutableLiveData(false)
}

