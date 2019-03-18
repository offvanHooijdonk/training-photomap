package by.off.photomap.presentation.ui

import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import by.off.photomap.core.ui.BaseActivity
import by.off.photomap.core.ui.ctx
import by.off.photomap.core.ui.dto.CategoryInfo
import by.off.photomap.core.ui.getColorVal
import by.off.photomap.core.utils.di.ViewModelFactory
import by.off.photomap.di.MainScreenComponent
import by.off.photomap.presentation.ui.databinding.ActMainBinding
import by.off.photomap.presentation.ui.login.SplashActivity
import by.off.photomap.presentation.ui.map.MapFragment
import by.off.photomap.presentation.ui.timeline.TimelineFragment
import kotlinx.android.synthetic.main.act_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {
    companion object {
        const val TAB_INDEX_MAP = 0
        const val TAB_INDEX_TIMELINE = 1
    }

    @Inject
    override lateinit var viewModelFactory: ViewModelFactory

    val snackbarRoot: View
        get() = mainRoot

    @VisibleForTesting
    lateinit var viewModel: MainScreenViewModel
    private val filteredCategories = BooleanArray(CategoryInfo.getTitlesOrdered().size) { true }
    private var liveCatFilter = BooleanArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActMainBinding>(this, R.layout.act_main)
        //setContentView(R.layout.act_main)
        MainScreenComponent.get(this).inject(this)

        setSupportActionBar(toolbar)

        initModelObserve()
        binding.model = viewModel

        initTabLayout()

        fabAddPhoto.setOnClickListener {
            val fr = getCurrentFragment()
            if (fr is ButtonPhotoListener) fr.onAddPhotoClicked()
        }
        fabLocation.setOnClickListener {
            val fr = getCurrentFragment()
            if (fr is ButtonLocationListener) fr.onLocationClicked()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        return when (item?.itemId) {
            R.id.item_log_out -> {
                Snackbar.make(mainRoot, R.string.logoff_progress_msg, Snackbar.LENGTH_INDEFINITE).show()
                viewModel.logOut()
                true
            }
            R.id.item_filter_categories -> {
                startCategoryFilterDialog()
                true
            }
            else -> false
        }
    }

    fun setNavigationButtonMode(isOn: Boolean) { // todo to viewModel for testing
        /*val colorRes = if (isOn) R.color.navigation_btn_mode_on else R.color.navigation_btn_mode_off
        fabLocation.backgroundTintList = ColorStateList.valueOf(ctx.getColorVal(colorRes))*/
        viewModel.setLocationButtonStatus(isOn)
    }

    private fun initModelObserve() {
        viewModel = getViewModel(MainScreenViewModel::class.java)
        viewModel.liveData.observe(this, Observer { response ->
            if (response?.data != null) {
                startActivity(
                    Intent(this, SplashActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            } else if (response?.error != null) {
                Snackbar.make(mainRoot, response.error?.message ?: getString(R.string.error_unknown), Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun initTabLayout() {
        container.adapter = MainPagerAdapter(supportFragmentManager)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == TAB_INDEX_MAP) {
                    fabAddPhoto.show()
                    fabLocation.show()
                } else {
                    fabAddPhoto.hide()
                    fabLocation.hide()
                }
            }
        })
    }

    private fun getCurrentFragment(): Fragment? {
        for (fr in supportFragmentManager.fragments) {
            if (fr != null && fr.isVisible)
                return fr
        }
        return null
    }

    private fun startCategoryFilterDialog() {
        val catNames = mutableListOf<String>()
        CategoryInfo.getTitlesOrdered().map { res -> ctx.getString(res) ?: ctx.getString(R.string.label_category_default) }.toCollection(catNames)
        liveCatFilter = filteredCategories.copyOf()
        AlertDialog.Builder(ctx)
            .setTitle("Filter categories")
            .setMultiChoiceItems(catNames.toTypedArray(), liveCatFilter) { _, index, isChecked ->
                liveCatFilter[index] = isChecked
            }
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                liveCatFilter.forEachIndexed { i, value -> filteredCategories[i] = value }
                if (filteredCategories.contains(true)) {
                    applyCategoryFilter()
                    dialog.dismiss()
                } else {
                    Toast.makeText(ctx, R.string.pick_category_least_one, Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun applyCategoryFilter() {
        val categories = CategoryInfo.getAllIds().filter { filteredCategories.size > it && filteredCategories[it] }.toIntArray()
        viewModel.filterCategories(categories)
    }

    private inner class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment =
            when (position) {
                TAB_INDEX_MAP -> MapFragment()
                TAB_INDEX_TIMELINE -> TimelineFragment()
                else -> {
                    Fragment()
                }
            }

        override fun getCount(): Int = tabs.tabCount
    }

    interface ButtonPhotoListener {
        fun onAddPhotoClicked()
    }

    interface ButtonLocationListener {
        fun onLocationClicked()
    }
}