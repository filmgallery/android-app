package org.owntracks.android.support.preferences

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/***
 * Implements a PreferencesStore that uses a SharedPreferecnces as a backend.
 */
@Singleton
class SharedPreferencesStore @Inject constructor(@ApplicationContext private val context: Context) :
    PreferenceDataStoreShim() {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        migrateToSingleSharedPreferences()
    }

    private fun migrateToSingleSharedPreferences() {

        val oldSharedPreferenceNames = listOf(
            "org.owntracks.android.preferences.private",
            "org.owntracks.android.preferences.http"
        )

        with(sharedPreferences.edit()) {
            oldSharedPreferenceNames
                .map { context.getSharedPreferences(it, Context.MODE_PRIVATE) }
                .forEach {
                    it.all.forEach { (key, value) ->
                        Timber.d("Migrating legacy preference $key from $it")
                        when (value) {
                            is String -> putString(key, value)
                            is Set<*> -> putStringSet(key, value as Set<String>)
                            is Boolean -> putBoolean(key, value)
                            is Int -> putInt(key, value)
                            is Long -> putLong(key, value)
                            is Float -> putFloat(key, value)
                        }
                    }
                }
            if (commit()) {
                oldSharedPreferenceNames.forEach {
                    context.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().apply()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    oldSharedPreferenceNames.forEach {
                        context.deleteSharedPreferences(it)
                    }
                }
            }
        }
    }


    override fun putString(key: String, value: String?) =
        sharedPreferences.edit().putString(key, value).apply()


    override fun getString(key: String, default: String?): String? =
        sharedPreferences.getString(key, default)

    override fun remove(key: String) =
        sharedPreferences.edit().remove(key).apply()


    override fun getBoolean(key: String, default: Boolean): Boolean =
        sharedPreferences.getBoolean(key, default)


    override fun putBoolean(key: String, value: Boolean) =
        sharedPreferences.edit().putBoolean(key, value).apply()


    override fun getInt(key: String, default: Int): Int =
        sharedPreferences.getInt(key, default)


    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    override fun putStringSet(key: String, values: Set<String>?) {
        sharedPreferences.edit().putStringSet(key, values).apply()
    }

    override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? {
        return sharedPreferences.getStringSet(key, defValues) ?: defValues
    }

    override fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    override fun registerOnSharedPreferenceChangeListener(listenerModeChanged: OnModeChangedPreferenceChangedListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listenerModeChanged)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listenerModeChanged: OnModeChangedPreferenceChangedListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listenerModeChanged)
    }
}