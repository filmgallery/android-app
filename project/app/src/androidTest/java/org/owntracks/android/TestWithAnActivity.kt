package org.owntracks.android

import android.app.Activity
import androidx.test.espresso.intent.Intents
import com.schibsted.spain.barista.rule.BaristaRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain


abstract class TestWithAnActivity<T : Activity>(
    activity: Class<T>,
    private val startActivity: Boolean = true
) {
    @get:Rule
    var baristaRule = BaristaRule.create(activity)

    private val screenshotRule = ScreenshotTakingOnTestEndRule()

    @get:Rule
    val ruleChain: RuleChain = RuleChain
        .outerRule(baristaRule.activityTestRule)
        .around(screenshotRule)

    @Before
    fun initIntents() {
        Intents.init()
    }

    @After
    fun releaseIntents() {
        Intents.release()
    }

    @Before
    fun setUp() {
        if (startActivity) {
            baristaRule.launchActivity()
        }
    }
}