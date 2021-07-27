package org.owntracks.android.ui.preferences.connection

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import org.owntracks.android.R
import org.owntracks.android.ui.preferences.ValidatingEditText

class ParametersDialogFragmentCompat constructor(
    key: String,
    private val model: Model,
    private val minimumKeepalive: Long,
    private val positiveCallback: (Model) -> Unit
) :
    ValidatingPreferenceDialogFragmentCompatWithKey(key) {
    data class Model(
        internal val cleanSession: Boolean,
        internal val keepalive: Int?
    )

    private var cleanSessionField: SwitchCompat? = null
    private var keepaliveField: ValidatingEditText? = null

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        cleanSessionField =
            view?.findViewById<SwitchCompat>(R.id.cleanSession)
                ?.apply { isChecked = model.cleanSession }
        keepaliveField = view?.findViewById<ValidatingEditText>(R.id.keepalive)?.apply {
            validationErrorMessage = getString(
                R.string.preferencesKeepaliveValidationError,
                minimumKeepalive
            )
            validationFunction = { text: String ->
                try {
                    val intValue = text.toInt()
                    intValue >= minimumKeepalive
                } catch (e: NumberFormatException) {
                    false
                }
            }
            model.keepalive?.also { setText(it.toString()) }
        }
        validatedFields = listOf(keepaliveField)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            ifLet(
                cleanSessionField,
                keepaliveField
            ) { (cleanSessionField, keepaliveField) ->
                positiveCallback(
                    Model(
                        (cleanSessionField as SwitchCompat).isChecked,
                        keepaliveField.text.toString().toIntOrNull()
                    )
                )
            }
        }
    }
}