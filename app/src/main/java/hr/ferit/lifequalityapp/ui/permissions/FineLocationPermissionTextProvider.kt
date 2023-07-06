package hr.ferit.lifequalityapp.ui.permissions

import android.content.Context
import hr.ferit.lifequalityapp.R

class FineLocationPermissionTextProvider(val context: Context) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            context.resources.getString(R.string.location_permanently_declined)
        } else {
            context.resources.getString(R.string.location_access_needed)
        }
    }
}
