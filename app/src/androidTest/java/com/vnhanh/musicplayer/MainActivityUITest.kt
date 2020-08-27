package com.vnhanh.musicplayer

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.facebook.testing.screenshot.Screenshot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityUITest {
    companion object {
        const val TAG = "LOG"
    }

//    @Rule
//    @JvmField
//    val grantPermissionRule = GrantPermissionRule.grant(
//        "android.permission.ACCESS_FINE_LOCATION",
//        "android.permission.ACCESS_COARSE_LOCATION",
//        "android.permission.READ_PHONE_STATE",
//        "android.permission.READ_EXTERNAL_STORAGE",
//        "android.permission.WRITE_EXTERNAL_STORAGE")

    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setup() {
        val intent = Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            MainActivity::class.java
        )

        activityTestRule.launchActivity(intent)
    }

    fun getCurrentActivityOrNull(): Activity? {
        var activity: Activity? = null
        try {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                activity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).elementAtOrNull(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "CommonViewAction | getCurrentActivityOrNull() | e: ${e.message}")
        }
        Log.d(TAG, "CommonViewAction | getCurrentActivityOrNull() | $activity")
        return activity
    }


    @Test
    fun verify(){
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        Thread.sleep(1000)
        val currentActivity = getCurrentActivityOrNull()
        Screenshot.snapActivity(currentActivity).setName("main_activity_screen").record()
    }
}