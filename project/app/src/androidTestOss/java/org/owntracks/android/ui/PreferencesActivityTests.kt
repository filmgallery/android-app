package org.owntracks.android.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotExist
import com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogPositiveButton
import com.schibsted.spain.barista.interaction.BaristaEditTextInteractions.writeTo
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.TestWithAnActivity
import org.owntracks.android.ui.preferences.PreferencesActivity

@LargeTest
@RunWith(AndroidJUnit4::class)
class PreferencesActivityTests :
    TestWithAnActivity<PreferencesActivity>(PreferencesActivity::class.java) {
    @Test
    @AllowFlaky(attempts = 1)
    fun initialViewShowsTopLevelMenu() {
        assertDisplayed(R.string.preferencesServer)
        assertDisplayed(R.string.preferencesReporting)
        assertDisplayed(R.string.preferencesNotification)
        assertDisplayed(R.string.preferencesAdvanced)
        assertDisplayed(R.string.configurationManagement)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun httpURLBlankIsInvalidURL() {
        clickOnAndWait(R.string.preferencesServer)
        clickOnAndWait(R.string.mode_heading)
        clickOnAndWait(R.string.mode_http_private_label)
        clickOnAndWait(R.string.preferencesHost)
        writeTo(R.id.url, "")
        clickDialogPositiveButton()
        assertDisplayed(R.id.textinput_error)
        assertContains(R.id.textinput_error, R.string.preferencesUrlValidationError)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun httpURLSimpleStringIsInvalidURL() {
        clickOnAndWait(R.string.preferencesServer)
        clickOnAndWait(R.string.mode_heading)
        clickOnAndWait(R.string.mode_http_private_label)
        clickOnAndWait(R.string.preferencesHost)
        writeTo(R.id.url, "testText")
        clickDialogPositiveButton()
        assertDisplayed(R.id.textinput_error)
        assertContains(R.id.textinput_error, R.string.preferencesUrlValidationError)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun mqttKeepaliveBelowMinimumIsInvalid() {
        clickOnAndWait(R.string.preferencesServer)
        clickOnAndWait(R.string.mode_heading)
        clickOnAndWait(R.string.mode_mqtt_private_label)
        clickOnAndWait(R.string.preferencesParameters)
        writeTo(R.id.keepalive, "899")
        clickDialogPositiveButton()
        assertDisplayed(R.id.textinput_error)
        assertContains(R.id.textinput_error, "should be a minimum")
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun mqttKeepaliveAtMinimumIsValid() {
        clickOnAndWait(R.string.preferencesServer)
        clickOnAndWait(R.string.mode_heading)
        clickOnAndWait(R.string.mode_mqtt_private_label)
        clickOnAndWait(R.string.preferencesParameters)
        writeTo(R.id.keepalive, "900")
        clickDialogPositiveButton()
        assertNotExist(R.id.textinput_error)
    }

    @Test
    @AllowFlaky(attempts = 3)
    @Ignore
    fun settingSimpleHTTPConfigSettingsCanBeShownInEditor() {
        clickOnAndWait(R.string.preferencesServer)
        clickOnAndWait(R.string.mode_heading)
        clickOnAndWait(R.string.mode_http_private_label)
        clickOnAndWait(R.string.preferencesHost)
        writeTo(R.id.url, "https://www.example.com:8080/")
        clickDialogPositiveButton()
        clickOnAndWait(R.string.preferencesIdentification)
        writeTo(R.id.username, "testUsername")
        writeTo(R.id.password, "testPassword")
        writeTo(R.id.deviceId, "testDeviceId")
        writeTo(R.id.trackerId, "t1")
        clickDialogPositiveButton()
        clickBackAndWait()


        clickOnAndWait(R.string.preferencesReporting)
        clickOnAndWait(R.string.preferencesPubExtendedData)
        clickBackAndWait()

        clickOnAndWait(R.string.preferencesNotification)
        clickOnAndWait(R.string.preferencesNotificationEvents)
        clickBackAndWait()

        clickOnAndWait(R.string.preferencesAdvanced)
        clickOnAndWait(R.string.preferencesRemoteCommand)
        clickOnAndWait(R.string.preferencesIgnoreInaccurateLocations)

        writeTo(android.R.id.edit, "950")

        clickDialogPositiveButton()

        clickOnAndWait(R.string.preferencesLocatorInterval)

        writeTo(android.R.id.edit, "123")

        clickDialogPositiveButton()

        scrollToText(R.string.preferencesMoveModeLocatorInterval)
        clickOnAndWait(R.string.preferencesMoveModeLocatorInterval)

        writeTo(android.R.id.edit, "5")

        clickDialogPositiveButton()

        scrollToText(R.string.preferencesAutostart)
        clickOnAndWait(R.string.preferencesAutostart)

        scrollToText(R.string.preferencesReverseGeocodeProvider)
        clickOnAndWait(R.string.preferencesReverseGeocodeProvider)

        clickOnAndWait("OpenCage")

        scrollToText(R.string.preferencesOpencageGeocoderApiKey)
        clickOnAndWait(R.string.preferencesOpencageGeocoderApiKey)
        writeTo(android.R.id.edit, "geocodeAPIKey")
        clickDialogPositiveButton()

        clickBackAndWait()

        clickOnAndWait(R.string.configurationManagement)

        assertContains(R.id.effectiveConfiguration, "\"_type\" : \"configuration\"")
        assertContains(R.id.effectiveConfiguration, " \"waypoints\" : [ ]")

        assertContains(R.id.effectiveConfiguration, "\"url\" : \"https://www.example.com:8080/\"")
        assertContains(R.id.effectiveConfiguration, "\"username\" : \"testUsername\"")
        assertContains(R.id.effectiveConfiguration, "\"password\" : \"********\"")
        assertContains(R.id.effectiveConfiguration, "\"deviceId\" : \"testDeviceId\"")
        assertContains(R.id.effectiveConfiguration, "\"tid\" : \"t1\"")
        assertContains(R.id.effectiveConfiguration, "\"notificationEvents\" : false")
        assertContains(R.id.effectiveConfiguration, "\"pubExtendedData\" : false")
        assertContains(R.id.effectiveConfiguration, "\"ignoreInaccurateLocations\" : 950")
        assertContains(R.id.effectiveConfiguration, "\"locatorInterval\" : 123")
        assertContains(R.id.effectiveConfiguration, "\"moveModeLocatorInterval\" : 5")
        assertContains(R.id.effectiveConfiguration, "\"autostartOnBoot\" : false")
        assertContains(R.id.effectiveConfiguration, "\"reverseGeocodeProvider\" : \"OpenCage\"")
        assertContains(R.id.effectiveConfiguration, "\"opencageApiKey\" : \"geocodeAPIKey\"")

        // Make sure that the MQTT-specific settings aren't present
        assertNotContains(R.id.effectiveConfiguration, "\"host\"")
        assertNotContains(R.id.effectiveConfiguration, "\"port\"")
        assertNotContains(R.id.effectiveConfiguration, "\"pubQos\"")
        assertNotContains(R.id.effectiveConfiguration, "\"subQos\"")
        assertNotContains(R.id.effectiveConfiguration, "\"info\"")
        assertNotContains(R.id.effectiveConfiguration, "\"tlsCaCrt\"")
        assertNotContains(R.id.effectiveConfiguration, "\"tls\"")
        assertNotContains(R.id.effectiveConfiguration, "\"mqttProtocolLevel\"")
        assertNotContains(R.id.effectiveConfiguration, "\"subTopic\"")
        assertNotContains(R.id.effectiveConfiguration, "\"pubTopicBase\"")
        assertNotContains(R.id.effectiveConfiguration, "\"clientId\"")
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun defaultGeocoderIsSelected() {
        clickOnAndWait(R.string.preferencesAdvanced)
        scrollToText(R.string.preferencesReverseGeocodeProvider)
        assertDisplayed(R.string.valDefaultGeocoder)
    }

    private fun scrollToText(textResource: Int) {
        onView(withId(androidx.preference.R.id.recycler_view))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(textResource)), scrollTo()
                )
            )
    }
}