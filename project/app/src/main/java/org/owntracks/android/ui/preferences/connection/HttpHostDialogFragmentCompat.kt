package org.owntracks.android.ui.preferences.connection

import android.view.View
import org.owntracks.android.R
import org.owntracks.android.ui.preferences.ValidatingEditText
import java.net.MalformedURLException
import java.net.URL

class HttpHostDialogFragmentCompat constructor(
    key: String,
    private val model: Model,
    private val positiveCallback: (Model) -> Unit
) :
    ValidatingPreferenceDialogFragmentCompatWithKey(key) {
    data class Model(internal val url: String)

    private var urlField: ValidatingEditText? = null

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        urlField = view?.findViewById<ValidatingEditText>(R.id.url)?.apply {
            setText(model.url)
            validationErrorMessage = getString(R.string.preferencesUrlValidationError)
            validationFunction = { text: String ->
                try {
                    URL(text.toString())
                    true
                } catch (e: MalformedURLException) {
                    false
                }
            }
        }
        validatedFields = listOf(urlField)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            urlField?.run { positiveCallback(Model(this.text.toString())) }
        }
    }
}

