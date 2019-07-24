package Utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.view.View
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.util.TreeIterables
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException


class TestUtils {
    fun waitId(viewId: Int, millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id <$viewId> during $millis millis."
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                val viewMatcher = withId(viewId)

                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        // found view with required ID
                        if (viewMatcher.matches(child)) {
                            return
                        }
                    }

                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                // timeout happens
                throw PerformException.Builder()
                    .withActionDescription(this.description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException())
                    .build()
            }
        }
    }

    inline fun <reified T : Activity> isVisible() : Boolean {
        val am = InstrumentationRegistry.getContext().getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val visibleActivityName = am.appTasks[0].taskInfo.baseActivity.className
        return visibleActivityName == T::class.java.name
    }

    val TIMEOUT = 5000L
    val CONDITION_CHECK_INTERVAL = 100L

    inline fun <reified T : Activity> waitUntilActivityVisible() {
        val startTime = System.currentTimeMillis()
        while (!isVisible<T>()) {
            Thread.sleep(CONDITION_CHECK_INTERVAL)
            if (System.currentTimeMillis() - startTime >= TIMEOUT) {
                throw AssertionError("Activity ${T::class.java.simpleName} not visible after $TIMEOUT milliseconds")
            }
        }
    }
}