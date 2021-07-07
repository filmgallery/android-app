package org.owntracks.android.ui

import android.content.Intent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.hamcrest.CoreMatchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.TestWithAnActivity
import org.owntracks.android.ui.preferences.about.AboutActivity


@LargeTest
@RunWith(AndroidJUnit4::class)
class AboutActivityTests : TestWithAnActivity<AboutActivity>(AboutActivity::class.java) {
    @Test
    @AllowFlaky(attempts = 1)
    fun documentationLinkOpensSite() {
        clickOn(R.string.preferencesDocumentation)
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(baristaRule.activityTestRule.activity.getString(R.string.documentationUrl))
            )
        )
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun twitterLinkOpensSite() {
        clickOn(R.string.preferencesTwitter)
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(baristaRule.activityTestRule.activity.getString(R.string.twitterUrl))
            )
        )
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun sourceLinkOpensSite() {
        clickOn(R.string.preferencesRepository)
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(baristaRule.activityTestRule.activity.getString(R.string.repoUrl))
            )
        )
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun translationLinkOpensSite() {
        clickOn(R.string.aboutTranslations)
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData(baristaRule.activityTestRule.activity.getString(R.string.translationContributionUrl))
            )
        )
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun librariesLinkListsLibraries() {
        clickOn(R.string.preferencesLicenses)
        assertDisplayed(R.string.preferencesLicenses)
    }
}