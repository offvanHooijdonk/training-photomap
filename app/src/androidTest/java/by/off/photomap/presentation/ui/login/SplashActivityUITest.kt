package by.off.photomap.presentation.ui.login

import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import by.off.photomap.model.PhotoInfo
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.*
import by.off.photomap.presentation.ui.di.ServiceMocks
import by.off.photomap.presentation.ui.di.TestStorageComponent
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.Response
import by.off.photomap.storage.parse.exception.UserNotFoundException
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SplashActivityUITest : AbstractActivityUITest<SplashActivity>() {
    companion object {
        const val USER_ID = "123"
    }

    @get:Rule
    override val activityScenarioRule = ActivityTestRule(SplashActivity::class.java, false, false)

    private val testLiveData = MutableLiveData<Response<UserInfo>>()
    private val stubLogoutLiveData = MutableLiveData<Response<UserInfo>>()
    private val stubFilePathLiveData = MutableLiveData<String>()
    private val stubListLiveData = MutableLiveData<ListResponse<PhotoInfo>>()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        Intents.init()

        TestStorageComponent.get().apply {
            `when`(ServiceMocks.userServiceMock.serviceLiveData).thenReturn(testLiveData)
            `when`(ServiceMocks.userServiceMock.logoutLiveData).thenReturn(stubLogoutLiveData)
            `when`(ServiceMocks.photoServiceMock.serviceListLiveData).thenReturn(stubListLiveData)
            `when`(ServiceMocks.photoServiceMock.tempFileLiveData).thenReturn(stubFilePathLiveData)
        }
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun test_loginSuccess() {
        startActivityAndWait()

        testLiveData.postValue(Response(UserInfo(USER_ID), null))
        waitALittle()

        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun test_errorValue() {
        startWithEmptyAuth()
        val model = activityScenarioRule.activity.viewModel

        model.isError.set(true)
        waitALittle()
        onView(withId(R.id.txtError)).checkVisible()

        model.errorObject.set(UserNotFoundException(USER_ID))
        waitALittle()
        onView(withId(R.id.txtError)).checkText(
            activityScenarioRule.activity.getString(R.string.error_user_not_found, USER_ID)
        )

        model.isError.set(false)
        waitALittle()
        onView(withId(R.id.txtError)).checkGone()
    }

    @Test
    fun test_progressBar() {
        startWithEmptyAuth()
        val model = activityScenarioRule.activity.viewModel

        model.isInProgress.set(true)
        waitALittle()
        onView(withId(R.id.progressLogin)).checkVisible()

        model.isInProgress.set(false)
        waitALittle()
        onView(withId(R.id.progressLogin)).checkGone()
    }

    @Test
    fun test_LoginButtons() {
        startWithEmptyAuth()
        val model = activityScenarioRule.activity.viewModel

        model.showLoginButtons.set(true)
        waitALittle()
        onView(withId(R.id.btnLogin)).checkVisible()
        onView(withId(R.id.btnRegister)).checkVisible()

        model.showLoginButtons.set(false)
        waitALittle()
        onView(withId(R.id.btnLogin)).checkGone()
        onView(withId(R.id.btnRegister)).checkGone()
    }

    @Test
    fun test_startLoginDialog() {
        startWithEmptyAuth()

        onView(withId(R.id.btnLogin)).perform(click())
        waitALittle()

        onView(withId(R.id.inputUserName)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.inputPwd)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.inputUserName)).check(matches(ViewMatchers.withText("")))
        onView(withId(R.id.inputPwd)).check(matches(ViewMatchers.withText("")))
    }

    @Test
    fun test_startRegisterDialog() {
        startWithEmptyAuth()

        onView(withId(R.id.btnRegister)).perform(click())
        waitALittle()

        onView(withId(R.id.inputUserName)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.inputEmail)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.inputPwd)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.inputPwdCheck)).check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.inputUserName)).check(matches(ViewMatchers.withText("")))
        onView(withId(R.id.inputEmail)).check(matches(ViewMatchers.withText("")))
        onView(withId(R.id.inputPwd)).check(matches(ViewMatchers.withText("")))
        onView(withId(R.id.inputPwdCheck)).check(matches(ViewMatchers.withText("")))
    }

    private fun startWithEmptyAuth() {
        startActivityAndWait()
        postEmptyResponseAndWait()
    }

    private fun postEmptyResponseAndWait() {
        testLiveData.postValue(Response(null, null))
        waitALittle()
    }
}