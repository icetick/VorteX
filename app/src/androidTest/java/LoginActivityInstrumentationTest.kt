import Utils.TestUtils
import alex.orobinsk.vortex.BuildConfig
import alex.orobinsk.vortex.R
import alex.orobinsk.vortex.ui.view.MainActivity
import alex.orobinsk.vortex.ui.view.SplashLoginActivity
import android.util.Log
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4;
import com.flaviofaria.kenburnsview.KenBurnsView
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentationTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(SplashLoginActivity::class.java)

    @Rule
    private val userName = BuildConfig.DEFAULT_EMAIL
    private val correctPassword = BuildConfig.DEFAULT_PASSWORD
    private val wrongPassword = BuildConfig.DEFAULT_PASSWORD + "afa"
    private val idWaitingDelay = 2000.toLong()

    @Before
    fun testRemoveAnimatedView() {

    }

    @Test
    fun testLoginSuccess() {
        Log.e("@Test", "Successful login")
        TestUtils().waitId(R.string.confirm_password, idWaitingDelay)
        Espresso.onView(ViewMatchers.withHint(R.string.email)).perform(ViewActions.typeTextIntoFocusedView(userName))
        Espresso.onView(ViewMatchers.withHint(R.string.password)).perform(ViewActions.typeText(correctPassword))
        Espresso.onView(ViewMatchers.withHint(R.string.confirm_password)).perform(ViewActions.typeText(correctPassword))
        Espresso.onView(ViewMatchers.withText(R.string.sign_in)).perform(ViewActions.click())
        TestUtils().waitUntilActivityVisible<MainActivity>()
    }


    @Test
    fun testCheatLogin() {
        Log.e("@Test", "Cheat login success test")
        Espresso.onView(ViewMatchers.withText(R.string.sign_in)).perform(ViewActions.longClick())
    }

    @Test
    fun testLoginFailure() {
        Log.e("@Test", "Failed login")
        TestUtils().waitId(R.string.confirm_password, idWaitingDelay)
        Espresso.onView(ViewMatchers.withHint(R.string.email)).perform(ViewActions.typeText(userName))
        Espresso.onView(ViewMatchers.withHint(R.string.password)).perform(ViewActions.typeText(wrongPassword))
        Espresso.onView(ViewMatchers.withHint(R.string.confirm_password)).perform(ViewActions.typeText(wrongPassword))
    }

}
