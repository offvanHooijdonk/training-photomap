package by.off.photomap.presentation.ui.timeline

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import by.off.photomap.model.PhotoInfo
import by.off.photomap.presentation.ui.StubLifecycleOwner
import by.off.photomap.storage.parse.ListResponse
import by.off.photomap.storage.parse.PhotoService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class TimelineViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var photoService: PhotoService

    private lateinit var viewModel: TimelineViewModel

    private val testServiceLiveData = MutableLiveData<ListResponse<PhotoInfo>>()

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        `when`(photoService.serviceListLiveData).thenReturn(testServiceLiveData)

        viewModel = TimelineViewModel(photoService)
        viewModel.liveData.observe(StubLifecycleOwner(), Observer { })
    }

    @Test
    fun test_notEmptyList() {
        testServiceLiveData.postValue(ListResponse(arrayListOf(samplePhotoInfo())))

        assertTrue("Expecting showList 'true' for non-empty list", viewModel.isShowList.get())
        assertFalse("Expecting showEmptyView 'false' for non-empty list", viewModel.isShowEmptyView.get())
        assertFalse("Expecting isRefreshing 'false' on list arrival", viewModel.isRefreshing.get())
        assertArrayEquals("", testServiceLiveData.value?.list?.toTypedArray(), viewModel.listData.toArray())
    }

    @Test
    fun test_emptyList() {
        testServiceLiveData.postValue(ListResponse(emptyList()))

        assertFalse("Expecting showList 'false' for an empty list", viewModel.isShowList.get())
        assertTrue("Expecting showEmptyView 'true' for an empty list", viewModel.isShowEmptyView.get())
        assertFalse("Expecting isRefreshing 'false' on list arrival", viewModel.isRefreshing.get())
        assertArrayEquals("", testServiceLiveData.value?.list?.toTypedArray(), viewModel.listData.toArray())
    }

    @Test
    fun test_loadingState() {
        viewModel.loadData()

        assertFalse("Expecting showList 'false' when loading is in progress", viewModel.isShowList.get())
        assertFalse("Expecting showEmptyView 'false' when loading is in progress", viewModel.isShowEmptyView.get())
        assertTrue("Expecting isRefreshing 'true' when loading is in progress", viewModel.isRefreshing.get())
    }

    private fun samplePhotoInfo() = PhotoInfo(id = "", description = "", shotTimestamp = Date(), category = 0)
}