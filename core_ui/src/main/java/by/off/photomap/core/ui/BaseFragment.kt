package by.off.photomap.core.ui

import android.Manifest
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import by.off.photomap.core.utils.di.ViewModelFactory

abstract class BaseFragment : Fragment() {
    companion object {
        const val PERMISSION_REQUEST_LATEST = 1
    }

    abstract var viewModelFactory: ViewModelFactory
    private var onPermissionResult: (Boolean) -> Unit = {}

    protected fun <T : ViewModel> getViewModel(modelClass: Class<T>): T =
        ViewModelProviders.of(this, viewModelFactory).get(modelClass)

    protected fun checkPermission(permission: String) = ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED

    protected fun requestPermission(permission: String, force: Boolean, rationale: () -> Unit, onResult: (Boolean) -> Unit) {
        onPermissionResult = onResult
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission) && !force) {
            rationale()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), PERMISSION_REQUEST_LATEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val result: Boolean = when (requestCode) {
            PERMISSION_REQUEST_LATEST -> {
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
            else -> false
        }

        onPermissionResult(result)
    }
}