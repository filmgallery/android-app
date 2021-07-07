package org.owntracks.android.ui

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerBack
import com.schibsted.spain.barista.interaction.BaristaViewPagerInteractions.swipeViewPagerForward
import com.schibsted.spain.barista.interaction.PermissionGranter
import com.schibsted.spain.barista.internal.util.resourceMatcher
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.TestWithAnActivity
import org.owntracks.android.ui.welcome.WelcomeActivity


@LargeTest
@RunWith(AndroidJUnit4::class)
class WelcomeActivityTests : TestWithAnActivity<WelcomeActivity>(WelcomeActivity::class.java) {
    @Test
    @AllowFlaky(attempts = 1)
    fun welcomeActivityCanBeSwipedThroughToTheEnd() {
        swipeViewPagerForward()
        swipeViewPagerForward()
        try {
            BaristaSleepInteractions.sleep(1000)
            Espresso.onView(R.id.fix_permissions_button.resourceMatcher()).check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(
                        ViewMatchers.Visibility.VISIBLE
                    )
                )
            )
            BaristaClickInteractions.clickOn(R.id.fix_permissions_button)
            PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.ACCESS_FINE_LOCATION)
            BaristaSleepInteractions.sleep(1000)
            clickOnAndWait(R.id.btn_next)
        } catch (e: NoMatchingViewException) {

        }
        BaristaSleepInteractions.sleep(500)
        assertDisplayed(R.id.done)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun welcomeActivityCanBeSwipedBackToStart() {
        swipeViewPagerForward()
        swipeViewPagerBack()
        assertDisplayed(R.string.welcome_heading)
    }
}