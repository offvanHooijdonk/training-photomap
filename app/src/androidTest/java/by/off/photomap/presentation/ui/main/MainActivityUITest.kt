package by.off.photomap.presentation.ui.main

import android.arch.lifecycle.MutableLiveData
import android.graphics.Point
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.GeneralLocation
import android.support.test.espresso.action.GeneralSwipeAction
import android.support.test.espresso.action.Press
import android.support.test.espresso.action.Swipe
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import by.off.photomap.R
import by.off.photomap.model.PhotoInfo
import by.off.photomap.model.UserInfo
import by.off.photomap.presentation.ui.*
import by.off.photomap.presentation.ui.di.ServiceMocks
import by.off.photomap.presentation.ui.di.TestStorageComponent
import by.off.photomap.presentation.ui.main.MainActivityRobot.Companion.TABS_COUNT
import by.off.photomap.presentation.ui.map.AddPhotoBottomSheet
import by.off.photomap.presentation.ui.map.MapFragment
import by.off.photomap.presentation.ui.viewactions.checkFABColor
import by.off.photomap.presentation.ui.viewactions.checkTabSelected
import by.off.photomap.presentation.ui.viewactions.longClickAt
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.Response
import com.google.android.gms.maps.model.LatLng
import  org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MainActivityUITest : AbstractActivityUITest<MainActivity>() {
    companion object {
        const val MAP_X = 255
        const val MAP_Y = 500
    }

    @get:Rule
    override val activityScenarioRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java, false, false)

    private val stubLogoutLiveData = MutableLiveData<Response<UserInfo>>()
    private val stubFilePathLiveData = MutableLiveData<String>()
    private val stubListLiveData = MutableLiveData<ListResponse<PhotoInfo>>()
    private val stubGeoInfoLiveData = MutableLiveData<String>()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        TestStorageComponent.get().apply {
            Mockito.`when`(ServiceMocks.userServiceMock.logoutLiveData).thenReturn(stubLogoutLiveData)
            Mockito.`when`(ServiceMocks.photoServiceMock.serviceListLiveData).thenReturn(stubListLiveData)
            Mockito.`when`(ServiceMocks.photoServiceMock.tempFileLiveData).thenReturn(stubFilePathLiveData)
            Mockito.`when`(ServiceMocks.geoPointServiceMock.placeLiveData).thenReturn(stubGeoInfoLiveData)
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

        waitMap()
        onView(withId(R.id.map)).perform(GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, GeneralLocation.CENTER_LEFT, Press.FINGER))
        assertFalse("Location Button status must be 'false' after map motion", model.btnLocationStatus.get())
    }

    @Test
    fun test_longClickMap() {
        startActivityAndWait()
        waitMap()

        val mapFragment = activityScenarioRule.activity.supportFragmentManager.fragments[0] as? MapFragment
        val map = (mapFragment)?.googleMap
        assertNotNull("Expected to receive Google Map view object.", map)
        var latLng: LatLng? = null
        activityScenarioRule.runOnUiThread {
            val projection = map!!.projection
            latLng = projection.fromScreenLocation(Point(MAP_X, MAP_Y))
            assertNotNull("Expect to receive a non-null Lat Long coordinates from Google Map")
        }

        onView(withId(R.id.map)).perform(longClickAt(MAP_X, MAP_Y))
        waitBottomDialog()

        val dialogViewModel = (mapFragment?.childFragmentManager?.findFragmentByTag(MapFragment.TAG_DIALOG_ADD_PHOTO) as? AddPhotoBottomSheet)?.viewModel
        assertNotNull("Expecting to get View Model from Add Photo dialog", dialogViewModel)
        assertEquals(
            "Expecting Latitude in Add Photo dialog to be the same that clicked on the Google Map",
            latLng!!.latitude, dialogViewModel!!.placeGeoPoint.get()!!.latitude, 0.01
        )
        assertEquals(
            "Expecting Longitude in Add Photo dialog to be the same that clicked on the Google Map",
            latLng!!.longitude, dialogViewModel.placeGeoPoint.get()!!.longitude, 0.01
        )
    }

    @Test
    fun test_FABVisibility() {
        startActivityAndWait()

        mainActivity {
            tabIndex = 0
            checkFABsVisibility()

            testFABsOnTabsSwitch(TABS_COUNT + 1)

            requestLandscapeOrientation(activityScenarioRule.activity)
            checkFABsVisibility()

            testFABsOnTabsSwitch(TABS_COUNT + 1)

            requestPortraintOrientation(activityScenarioRule.activity)
            checkFABsVisibility()

            testFABsOnTabsSwitch(TABS_COUNT)
        }
    }
}