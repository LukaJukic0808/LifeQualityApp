package hr.ferit.lifequalityapp.ui.permissions

import android.content.Context
import hr.ferit.lifequalityapp.R

class MicrophonePermissionTextProvider(val context: Context) : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            context.resources.getString(R.string.microphone_permanently_declined)
        } else {
            context.resources.getString(R.string.microphone_access_needed)
        }
    }
}
