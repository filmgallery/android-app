package org.owntracks.android.ui.preferences.connection

import android.view.View
import com.rengwuxian.materialedittext.MaterialEditText
import com.rengwuxian.materialedittext.validation.METValidator
import org.owntracks.android.R

class HttpHostDialogFragmentCompat constructor(
    key: String,
    private val model: Model,
    private val urlValidator: METValidator,
    private val positiveCallback: (Model) -> Unit
) :
    ValidatingPreferenceDialogFragmentCompatWithKey(key) {
    data class Model(internal val url: String)

    private var urlField: MaterialEditText? = null

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        urlField = view?.findViewById<MaterialEditText>(R.id.url)?.apply {
            setText(model.url)
            addValidator(urlValidator)
        }
//        validatedFields = listOf(urlField)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            urlField?.run { positiveCallback(Model(this.text.toString())) }
        }
    }
}

