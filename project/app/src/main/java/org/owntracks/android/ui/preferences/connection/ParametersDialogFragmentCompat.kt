package org.owntracks.android.ui.preferences.connection

import android.view.View
import androidx.appcompat.widget.SwitchCompat
import com.rengwuxian.materialedittext.MaterialEditText
import com.rengwuxian.materialedittext.validation.METValidator
import org.owntracks.android.R

class ParametersDialogFragmentCompat constructor(
    key: String,
    private val model: Model,
    private val keepaliveValidator: METValidator,
    private val positiveCallback: (Model) -> Unit
) :
    ValidatingPreferenceDialogFragmentCompatWithKey(key) {
    data class Model(
        internal val cleanSession: Boolean,
        internal val keepalive: Int?
    )

    private var cleanSessionField: SwitchCompat? = null
    private var keepaliveField: MaterialEditText? = null

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        cleanSessionField =
            view?.findViewById<SwitchCompat>(R.id.cleanSession)
                ?.apply { isChecked = model.cleanSession }
        keepaliveField = view?.findViewById<MaterialEditText>(R.id.keepalive)?.apply {
            addValidator(keepaliveValidator)
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