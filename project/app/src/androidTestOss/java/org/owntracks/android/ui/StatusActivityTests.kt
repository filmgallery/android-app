package org.owntracks.android.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.TestWithAnActivity
import org.owntracks.android.ui.status.StatusActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class StatusActivityTests : TestWithAnActivity<StatusActivity>(StatusActivity::class.java) {
    @Test
    @AllowFlaky(attempts = 1)
    fun statusActivityShowsEndpointState() {
        assertDisplayed(R.string.status_endpoint_state_hint)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun statusActivityShowsLogsLauncher() {
        assertDisplayed(R.string.viewLogs)
    }
}