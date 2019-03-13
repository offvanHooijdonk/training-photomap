package by.off.photomap.presentation.ui.login

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.R
import by.off.photomap.presentation.ui.di.ServiceMocks
import by.off.photomap.presentation.ui.di.TestStorageComponent
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.UserService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SplashActivityUITest {

    @get:Rule
    var activityScenarioRule = ActivityTestRule(SplashActivity::class.java, false, false)

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val testLiveData = MutableLiveData<Response<UserInfo>>()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        TestStorageComponent.get().apply {
            `when`(ServiceMocks.userServiceMock.serviceLiveData).thenReturn(testLiveData)
        }
    }

    @Test
    fun testLogin() {
        activityScenarioRule.launchActivity(null)
        Thread.sleep(1500)
        onView(withId(R.id.progressLogin)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        testLiveData.postValue(Response(null, null))

        Thread.sleep(500)

        onView(withId(R.id.progressLogin)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.btnLogin)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.btnRegister)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}