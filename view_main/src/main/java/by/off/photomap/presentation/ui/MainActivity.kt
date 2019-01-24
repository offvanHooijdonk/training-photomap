package by.off.photomap.presentation.ui

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import by.off.photomap.core.utils.LOGCAT
import by.off.photomap.presentation.ui.map.MapFragment
import by.off.photomap.presentation.ui.timeline.TimelineFragment
import kotlinx.android.synthetic.main.act_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAB_INDEX_MAP = 0
        const val TAB_INDEX_TIMELINE = 1
        const val FLAG_PHOTO_LISTENER = 0b1
        const val FLAG_LOCATION_LISTENER = 0b10
    }

    val registeredFlags = mutableMapOf<Int, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        setSupportActionBar(toolbar)

        container.adapter = MainPagerAdapter(supportFragmentManager)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                val flag = registeredFlags[position] ?: 0
                if (flag and FLAG_PHOTO_LISTENER != 0) fabAddPhoto.show() else fabAddPhoto.hide()
                if (flag and FLAG_LOCATION_LISTENER != 0) fabLocation.show() else fabLocation.hide()
            }
        })

        fabAddPhoto.setOnClickListener {
            val fr = getCurrentFragment()
            if (fr is ButtonPhotoListener) fr.onPhotoClicked()
        }
        fabLocation.setOnClickListener {
            val fr = getCurrentFragment()
            if (fr is ButtonLocationListener) fr.onLocationClicked()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.item_log_out -> {} // TODO call logout
        }
        return true
    }

    private fun getFragmentFlag(f: Fragment) =
        (if (f is ButtonPhotoListener) FLAG_PHOTO_LISTENER else 0) or
                (if (f is ButtonLocationListener) FLAG_LOCATION_LISTENER else 0)

    private fun getCurrentFragment(): Fragment? {
        for (fr in supportFragmentManager.fragments) {
            if (fr != null && fr.isVisible)
                return fr
        }
        return null
    }

    private inner class MainPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment = when (position) {
            TAB_INDEX_MAP -> MapFragment()
            TAB_INDEX_TIMELINE -> TimelineFragment()
            else -> {
                Fragment()
            }
        }.also { registeredFlags[position] = getFragmentFlag(it) }

        override fun getCount(): Int = tabs.tabCount
    }

    interface ButtonPhotoListener {
        fun onPhotoClicked()
    }

    interface ButtonLocationListener {
        fun onLocationClicked()
    }
}