import org.junit.Test
import org.junit.Assert.assertThat
import alex.orobinsk.vortex.util.ViewModelFactory
import org.hamcrest.CoreMatchers.*;
import alex.orobinsk.vortex.ui.viewModel.SplashLoginViewModel
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UtilsTest {
    @Test
    fun viewModelValidator() {
        assertThat(ViewModelFactory().create(SplashLoginViewModel::class.java), isA(SplashLoginViewModel::class.java))
    }
}