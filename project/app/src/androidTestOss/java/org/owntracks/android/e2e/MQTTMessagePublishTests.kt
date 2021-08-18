package org.owntracks.android.e2e

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaDrawerInteractions.openDrawer
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mqtt.broker.Broker
import mqtt.broker.interfaces.Authentication
import mqtt.broker.interfaces.PacketInterceptor
import mqtt.packets.MQTTPacket
import mqtt.packets.Qos
import mqtt.packets.mqtt.MQTTPublish
import mqtt.packets.mqttv5.MQTT5Properties
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.owntracks.android.R
import org.owntracks.android.TestWithAnActivity
import org.owntracks.android.model.BatteryStatus
import org.owntracks.android.model.messages.MessageBase
import org.owntracks.android.model.messages.MessageCard
import org.owntracks.android.model.messages.MessageLocation
import org.owntracks.android.support.Parser
import org.owntracks.android.ui.clickOnAndWait
import org.owntracks.android.ui.map.MapActivity
import org.owntracks.android.ui.scrollToText
import org.owntracks.android.ui.writeToEditTextDialog
import java.time.Instant
import java.util.concurrent.TimeUnit


@ExperimentalUnsignedTypes
@LargeTest
@RunWith(AndroidJUnit4::class)
class MQTTMessagePublishTests : TestWithAnActivity<MapActivity>(MapActivity::class.java, false) {
    private val mqttTestUsername = "testUser"
    private val mqttTestPassword = "testPassword"
    private val mqttPacketsReceived: MutableList<MQTTPacket> = mutableListOf()
    private val broker =
        Broker(host = "127.0.0.1", port = 18883, authentication = object : Authentication {
            override fun authenticate(
                clientId: String,
                username: String?,
                password: UByteArray?
            ): Boolean {
                return username == mqttTestUsername && password.contentEquals(
                    mqttTestPassword.toByteArray().toUByteArray()
                )
            }
        },
            packetInterceptor = object : PacketInterceptor {
                override fun packetReceived(
                    clientId: String,
                    username: String?,
                    password: UByteArray?,
                    packet: MQTTPacket
                ) {
                    mqttPacketsReceived.add(packet)
                }
            })

    @DelicateCoroutinesApi
    @Before
    fun startMQTTBroker() {
        mqttPacketsReceived.clear()
        GlobalScope.launch { broker.listen() }
    }

    @Before
    fun setupEmulator() {
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("adb emu geo fix 0 51");
    }

    @After
    fun stopMQTTBroker() {
        broker.stop()
    }

    @After
    fun unregisterIdlingResource() {
        baristaRule.activityTestRule.activity.locationIdlingResource?.run {
            IdlingRegistry.getInstance().unregister(this)
        }
        baristaRule.activityTestRule.activity.outgoingQueueIdlingResource.run {
            IdlingRegistry.getInstance().unregister(this)
        }
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun given_an_MQTT_configured_client_when_the_report_button_is_pressed_then_the_broker_receives_a_packet_with_the_correct_location_message_in() {
        baristaRule.launchActivity()
        doWelcomeProcess()
        configureMQTTConnectionToLocal()
        assertContains(R.id.connectedStatus, R.string.CONNECTED)
        reportLocationFromMap()

        assertTrue(mqttPacketsReceived
            .filterIsInstance<MQTTPublish>()
            .map {
                Parser(null).fromJson((it.payload)!!.toByteArray())
            }
            .any {
                it is MessageLocation && it.latitude == 51.0 && it.longitude == 0.0
            })
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun given_an_MQTT_configured_client_when_the_broker_sends_a_message_card_without_a_location_then_a_new_contact_appears() {
        baristaRule.launchActivity()
        doWelcomeProcess()
        configureMQTTConnectionToLocal()
        assertContains(R.id.connectedStatus, R.string.CONNECTED)
        reportLocationFromMap()

        openDrawer()
        clickOnAndWait(R.string.title_activity_contacts)

        val messageCard = MessageCard().apply {
            name = "TestName"
            face =
                "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"
        }
        val bytes = Parser(null).toJsonBytes(messageCard)

        broker.publish(
            false,
            "owntracks/someuser/somedevice",
            Qos.AT_LEAST_ONCE,
            MQTT5Properties(),
            bytes.toUByteArray()
        )
        sleep(1000) // Need to wait for the message to percolate through the app.
        assertRecyclerViewItemCount(R.id.contactsRecyclerView, 2)
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 1, R.id.name, "TestName")
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 1, R.id.location, R.string.na)
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun given_an_MQTT_configured_client_when_the_broker_sends_a_message_card_with_a_location_then_a_new_contact_appears() {
        baristaRule.launchActivity()
        doWelcomeProcess()
        configureMQTTConnectionToLocal()
        assertContains(R.id.connectedStatus, R.string.CONNECTED)
        reportLocationFromMap()

        openDrawer()
        clickOnAndWait(R.string.title_activity_contacts)
        listOf(
            MessageLocation().apply {
                latitude = 52.123
                longitude = 0.56789
                timestamp = Instant.parse("2006-01-02T15:04:05Z").epochSecond
            }, MessageCard().apply {
                name = "TestName"
                face =
                    "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"
            }
        )
            .map(Parser(null)::toJsonBytes)
            .forEach {
                broker.publish(
                    false,
                    "owntracks/someuser/somedevice",
                    Qos.AT_LEAST_ONCE,
                    MQTT5Properties(),
                    it.toUByteArray()
                )
            }
        sleep(1000) // Need to wait for the message to percolate through the app.
        assertRecyclerViewItemCount(R.id.contactsRecyclerView, 2)
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 1, R.id.name, "TestName")
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 1, R.id.location, "52.123, 0.56789")
        assertDisplayedAtPosition(
            R.id.contactsRecyclerView,
            1,
            R.id.locationDate,
            "1/2/06" // Default locale for emulator is en_US
        )
    }

    @Test
    @AllowFlaky(attempts = 1)
    fun given_an_MQTT_configured_client_when_the_broker_sends_a_location_for_a_cleared_contact_then_a_the_contact_returns_with_the_correct_details() {
        baristaRule.launchActivity()
        doWelcomeProcess()
        configureMQTTConnectionToLocal()
        assertContains(R.id.connectedStatus, R.string.CONNECTED)
        reportLocationFromMap()

        openDrawer()
        clickOnAndWait(R.string.title_activity_contacts)
        listOf(
            MessageLocation().apply {
                latitude = 52.123
                longitude = 0.56789
                altitude = 123
                accuracy = 456
                battery = 78
                batteryStatus = BatteryStatus.CHARGING
                velocity = 99
                timestamp = Instant.parse("2006-01-02T15:04:05Z").epochSecond
            }, MessageCard().apply {
                name = "TestName"
                face =
                    "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"
            }
        ).sendFromBroker(broker)

        sleep(1000) // Need to wait for the message to percolate through the app.
        clickOnAndWait("TestName")
        sleep(1000) // Apparently espresso won't wait for the MapActivity to finish rendering
        clickOnAndWait(R.id.contactPeek)
        assertDisplayed(R.id.moreButton)
        clickOnRegardlessOfVisibility(R.id.moreButton)

        // Hacky way to click on the first thing in a popup menu
        // https://stackoverflow.com/questions/28061300/espresso-click-a-single-list-view-item
        onData(anything()).atPosition(0).perform(click())

        openDrawer()
        clickOnAndWait(R.string.title_activity_contacts)

        assertRecyclerViewItemCount(R.id.contactsRecyclerView, 1)
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 0, R.id.name, "64")

        listOf(MessageLocation().apply {
            latitude = 52.123
            longitude = 0.56789
            timestamp = Instant.parse("2006-01-02T15:04:05Z").epochSecond
        }).sendFromBroker(broker)

        sleep(1000)

        assertRecyclerViewItemCount(R.id.contactsRecyclerView, 2)
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 1, R.id.name, "TestName")
        assertDisplayedAtPosition(R.id.contactsRecyclerView, 1, R.id.location, "52.123, 0.56789")
        assertDisplayedAtPosition(
            R.id.contactsRecyclerView,
            1,
            R.id.locationDate,
            "1/2/06" // Default locale for emulator is en_US
        )
    }


    @Test
    @AllowFlaky(attempts = 1)
    fun given_an_MQTT_configured_client_when_the_wrong_credentials_are_used_then_the_status_screen_shows_that_the_broker_is_not_connected() {
        baristaRule.launchActivity()
        doWelcomeProcess()
        configureMQTTConnectionToLocal("not the right password")
        assertContains(R.id.connectedStatus, R.string.ERROR)
        assertContains(R.id.connectedStatusMessage, "Connection lost")
    }

    private fun reportLocationFromMap() {
        openDrawer()
        clickOnAndWait(R.string.title_activity_map)

        val locationIdlingResource = baristaRule.activityTestRule.activity.locationIdlingResource
        IdlingPolicies.setIdlingResourceTimeout(2, TimeUnit.MINUTES)
        IdlingRegistry.getInstance().register(locationIdlingResource)

        clickOnAndWait(R.id.menu_report)
    }

    private fun configureMQTTConnectionToLocal(password: String = mqttTestPassword) {
        openDrawer()
        clickOnAndWait(R.string.title_activity_preferences)
        clickOnAndWait(R.string.preferencesServer)
        clickOnAndWait(R.string.mode_heading)
        clickOnAndWait(R.string.mode_mqtt_private_label)
        writeToEditTextDialog(R.string.preferencesHost, "127.0.0.1")
        writeToEditTextDialog(R.string.preferencesPort, "18883")
        writeToEditTextDialog(R.string.preferencesClientId, "testClientId")
        writeToEditTextDialog(R.string.preferencesUserUsername, mqttTestUsername)
        writeToEditTextDialog(R.string.preferencesBrokerPassword, password)
        scrollToText(R.string.tls)
        clickOnAndWait(R.string.tls)
        openDrawer()
        clickOnAndWait(R.string.title_activity_status)
        sleep(1000L * baristaRule.activityTestRule.activity.resources.getInteger(R.integer.defaultMqttTimeout))
    }

    private fun List<MessageBase>.sendFromBroker(broker: Broker) {
        map(Parser(null)::toJsonBytes)
            .forEach {
                broker.publish(
                    false,
                    "owntracks/someuser/somedevice",
                    Qos.AT_LEAST_ONCE,
                    MQTT5Properties(),
                    it.toUByteArray()
                )
            }
    }
}