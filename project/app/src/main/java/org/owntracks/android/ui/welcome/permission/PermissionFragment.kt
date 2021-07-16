package org.owntracks.android.ui.welcome.permission

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import org.owntracks.android.R
import org.owntracks.android.databinding.UiWelcomePermissionsBinding
import org.owntracks.android.ui.welcome.BaseWelcomeFragment
import javax.inject.Inject

@AndroidEntryPoint
class PermissionFragment @Inject constructor() :
    BaseWelcomeFragment<UiWelcomePermissionsBinding>(R.layout.ui_welcome_permissions) {
    private val viewModel: PermissionFragmentViewModel by viewModels()

    private enum class PermissionStatus {
        NOT_ASKED,
        DENIED_ONCE,
        DENIED_MULTIPLE,
        GRANTED
    }

    private var permissionStatus = PermissionStatus.NOT_ASKED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.vm = viewModel

        binding.fixPermissionsButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                when (permissionStatus) {
                    PermissionStatus.NOT_ASKED -> requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    PermissionStatus.DENIED_ONCE -> AlertDialog.Builder(requireContext())
                        .setCancelable(true)
                        .setMessage(R.string.permissions_description)
                        .setPositiveButton(
                            "OK"
                        ) { _: DialogInterface?, _: Int ->
                            requestLocationPermission.launch(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        }
                        .show()
                    PermissionStatus.DENIED_MULTIPLE -> Snackbar.make(
                        binding.root,
                        R.string.welcomePermissionUnableToProceedWithoutPermissions,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    PermissionStatus.GRANTED -> {
                    }
                }
            } else {
                requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        return binding.root
    }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionStatus = PermissionStatus.GRANTED
                viewModel.permissionGranted.postValue(true)
                activityViewModel.nextEnabled.postValue(true)
            } else {
                permissionStatus =
                    if (permissionStatus == PermissionStatus.DENIED_ONCE &&
                        !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    ) {
                        PermissionStatus.DENIED_MULTIPLE
                    } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        PermissionStatus.DENIED_ONCE
                    } else {
                        PermissionStatus.NOT_ASKED
                    }
            }
        }

    private fun checkPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onResume() {
        super.onResume()
        activityViewModel.doneEnabled.postValue(false)
        checkPermission().run {
            viewModel.permissionGranted.postValue(this)
            viewModel.permissionRequired.postValue(!this)
            activityViewModel.nextEnabled.postValue(this)
        }
    }
}