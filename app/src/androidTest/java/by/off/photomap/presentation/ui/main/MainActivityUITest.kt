package by.off.photomap.presentation.ui.main

import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.*
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import by.off.photomap.R
import by.off.photomap.model.PhotoInfo
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.*
import by.off.photomap.presentation.ui.di.ServiceMocks
import by.off.photomap.presentation.ui.di.TestStorageComponent
import by.off.photomap.presentation.ui.viewactions.checkFABColor
import by.off.photomap.presentation.ui.viewactions.checkTabSelected
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.Response
import junit.framework.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MainActivityUITest : AbstractActivityUITest<MainActivity>() {

    @get:Rule
    override val activityScenarioRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    private val stubLogoutLiveData = MutableLiveData<Response<UserInfo>>()
    private val stubFilePathLiveData = MutableLiveData<String>()
    private val stubListLiveData = MutableLiveData<ListResponse<PhotoInfo>>()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        TestStorageComponent.get().apply {
            Mockito.`when`(ServiceMocks.userServiceMock.logoutLiveData).thenReturn(stubLogoutLiveData)
            Mockito.`when`(ServiceMocks.photoServiceMock.serviceListLiveData).thenReturn(stubListLiveData)
            Mockito.`when`(ServiceMocks.photoServiceMock.tempFileLiveData).thenReturn(stubFilePathLiveData)
        }
    }

    @Test
    fun test_tabsInitScreen() {
        startActivityAndWait()

        onView(withId(R.id.tabs)).check(checkTabSelected(0))

    }

    @Test
    fun test_tabsLocationBtnStatus() {
        startActivityAndWait()

        val model = activityScenarioRule.activity.viewModel

        model.btnLocationStatus.set(true)
        waitALittle()
        val colorOn = activityScenarioRule.activity.resources.getColor(R.color.navigation_btn_mode_on, null)
        onView(withId(R.id.fabLocation)).check(checkFABColor(colorOn))

        model.btnLocationStatus.set(false)
        waitALittle()
        val colorOff = activityScenarioRule.activity.resources.getColor(R.color.navigation_btn_mode_off, null)
        onView(withId(R.id.fabLocation)).check(checkFABColor(colorOff))

    }

    @Test
    fun test_locationBtnMoveMap() {
        startActivityAndWait()

        val model = activityScenarioRule.activity.viewModel
        model.btnLocationStatus.set(true)

        Thread.sleep(2500)
        onView(withId(R.id.map)).perform(GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_LEFT, Press.FINGER))
        assertFalse("Location Button status must be 'false' after map motion", model.btnLocationStatus.get())
    }
}