package by.off.photomap.presentation.ui.timeline.search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import by.off.photomap.core.ui.hide
import by.off.photomap.core.ui.show

class SearchRevealAnimator(
    private val viewAnim: View, private val animX: Int, private val animY: Int,
    private val onShowAnimEnd: () -> Unit, private val onHideAnimEnd: () -> Unit
) {

    companion object {
        private const val DURATION_IN = 225L
        private const val DURATION_OUT = 195L

        fun getViewCenterLocation(v: View): Pair<Int, Int> {
            val startLocation = IntArray(2)
            v.getLocationOnScreen(startLocation)
            val startX = startLocation[0] + v.width / 2
            val startY = startLocation[1] + v.height / 2
            return startX to startY
        }
    }

    fun animate(isShow: Boolean) {
        val (animViewInWindowX, animViewInWindowY) = SearchRevealAnimator.getViewCenterLocation(viewAnim)
        val animViewCenterInWindowX = animViewInWindowX + viewAnim.width / 2
        val animViewCenterInWindowY = animViewInWindowY + viewAnim.height / 2

        val rippleSizeX = (if (animX < animViewCenterInWindowX) viewAnim.width - animX else animX - animViewInWindowX).toFloat()
        val rippleSizeY = (if (animY < animViewCenterInWindowY) viewAnim.height - animY else animY - animViewInWindowY).toFloat()
        val rippleRadiusMax = Math.sqrt((rippleSizeX * rippleSizeX + rippleSizeY * rippleSizeY).toDouble()).toFloat()

        val (startRadius, endRadius) = if (isShow) 0.0f to rippleRadiusMax else rippleRadiusMax to 0.0f

        ViewAnimationUtils.createCircularReveal(viewAnim, animX, animY, startRadius, endRadius)
            .apply {
                duration = if (isShow) DURATION_IN else DURATION_OUT
                interpolator = if (isShow) DecelerateInterpolator() else null
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        if (isShow) {
                            viewAnim.show()
                            onShowAnimEnd()
                        } else {
                            viewAnim.hide()
                            onHideAnimEnd()
                        }
                    }
                })
                viewAnim.show()
            }.start()
    }
}