package org.owntracks.android.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.PermissionGranter
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.TestWithAnActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class OSSMapActivityTests : TestWithAnActivity<MapActivity>(MapActivity::class.java) {
    @Test
    @AllowFlaky(attempts = 1)
    fun welcomeActivityShouldNotRunWhenFirstStartPreferencesSet() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(context.getString(R.string.preferenceKeyFirstStart), false)
            .putBoolean(context.getString(R.string.preferenceKeySetupNotCompleted), false)
            .apply()
        baristaRule.launchActivity()
        PermissionGranter.allowPermissionsIfNeeded(ACCESS_FINE_LOCATION)
        assertDisplayed(R.id.osm_map_view)
    }
}
