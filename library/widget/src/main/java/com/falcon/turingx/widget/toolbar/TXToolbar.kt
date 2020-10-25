package com.falcon.turingx.widget.toolbar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.appbar.MaterialToolbar
import com.falcon.turingx.core.components.StringWrapper
import com.falcon.turingx.core.utils.afterMeasure
import com.falcon.turingx.widget.R

/**
 * Custom toolbar to centralize title and animate title visibility with scroll.
 * */
class TXToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.toolbarStyle
): MaterialToolbar(context, attrs, defStyleAttr) {

    /** Custom title. */
    private lateinit var _title: ToolbarTitleTextView
    /** View used to calculate the best position to put title. */
    private lateinit var _controlView: RelativeLayout
    /** The title alignment. See [R.styleable.TXToolbar_tx_toolbar_titleAlignment] */
    private var _titleAlignment: Int = 0
    /** Animation controller used to show and hide toolbar title. */
    private lateinit var toolbarTitleRevealAnimation: ToolbarTitleRevealAnimation

    init {
        // obtain the title alignment and recycle attributes.
        context.obtainStyledAttributes(attrs, R.styleable.TXToolbar).also {
            _titleAlignment = it.getInt(R.styleable.TXToolbar_tx_toolbar_titleAlignment, 1)
        }.recycle()
        getTitleTextView()
    }

    /**
     * Configure a external TextView to be used in conjunction with toolbar title and show toolbar
     * title when [title] is not visible and hide toolbar title when [title] is visible inside
     * [scroll].
     *
     * @param animationDuration its the time takes to do the reveal animation.
     *
     * */
    fun animateTitle(title: TextView, scroll: ScrollView, animationDuration: Long = 300L) {
        toolbarTitleRevealAnimation = ToolbarTitleRevealAnimation(
            _title,
            title,
            scroll,
            animationDuration
        )
        toolbarTitleRevealAnimation.observeScroll()
    }

    override fun getTitle(): CharSequence {
        return getTitleTextView().text
    }

    override fun setTitle(title: CharSequence?) {
        getTitleTextView().apply {
            text = title
        }.requestLayout()
    }

    override fun setTitle(resId: Int) {
        title = context.getString(resId)
    }

    fun setTitle(strWrapper: StringWrapper) {
        title = strWrapper(context)
    }

    override fun setTitleTextAppearance(context: Context?, resId: Int) {
        getTitleTextView().setTitleTextAppearance(resId)
    }

    /** Create the custom toolbar title and configure. */
    private fun getTitleTextView(): ToolbarTitleTextView {

        if (!::_title.isInitialized) {
            // create auxiliary view to help toolbar title be positioned in the right place.
            _controlView = RelativeLayout(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).also {
                    it.gravity = Gravity.CENTER
                }
                afterMeasure { _title.requestLayout() }
                this@TXToolbar.addView(this)

            }
            // create the toolbar title its self.
            _title = ToolbarTitleTextView(context).apply {
                setSingleLine()
                ellipsize = TextUtils.TruncateAt.END
                gravity = Gravity.CENTER
                setTitleTextAppearance(R.style.TextAppearance_AppCompat_Widget_ActionBar_Title)
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
                this@TXToolbar._controlView.addView(this)
            }
        }

        return _title
    }

    private inner class ToolbarTitleTextView(context: Context) : AppCompatTextView(context) {

        // in the title measure attempt place the title in the right place.
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            if(this@TXToolbar._titleAlignment == 1) {
                this.textAlignment = TEXT_ALIGNMENT_CENTER
                // centralize toolbar title view.
                centralizeTitle(widthMeasureSpec, heightMeasureSpec)
            } else if(this@TXToolbar._titleAlignment == 2) {
                this.textAlignment = TEXT_ALIGNMENT_VIEW_START
            }
        }

        /**
         * Calculate the right place to put the toolbar title in the middle of toolbar based in the
         * [_controlView] location.
         * */
        private fun centralizeTitle(widthMeasureSpec: Int, heightMeasureSpec: Int) {

            val toolbarWidth = this@TXToolbar.measuredWidth
            val middleToolbar = toolbarWidth / 2

            val controlViewWidth = this@TXToolbar._controlView.measuredWidth
            val controlViewLocationInWindow = intArrayOf(0, 0).apply { _controlView.getLocationInWindow(this) }

            val leftEmptySpace = middleToolbar - controlViewLocationInWindow[0]
            val rightEmptySpace = controlViewWidth - leftEmptySpace

            this.layoutParams = (this.layoutParams as RelativeLayout.LayoutParams).apply {
                if(leftEmptySpace > rightEmptySpace) {
                    setMeasuredDimension(rightEmptySpace * 2, MeasureSpec.getSize(heightMeasureSpec))
                    setMargins(middleToolbar - (this@ToolbarTitleTextView.measuredWidth.div(2)) - controlViewLocationInWindow[0], 0, 0, 0)
                } else {
                    setMeasuredDimension(leftEmptySpace * 2, MeasureSpec.getSize(heightMeasureSpec))
                    setMargins(0, 0, middleToolbar - (this@ToolbarTitleTextView.measuredWidth.div(2)) - controlViewLocationInWindow[0], 0)
                }
            }
        }

        fun setTitleTextAppearance(resId: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setTextAppearance(resId)
            } else {
                setTextAppearance(context, resId)
            }
        }

    }

    private inner class ToolbarTitleRevealAnimation(
        private var toolbarTitle: View,
        private var outsideTitle: View,
        private var scrollView: ScrollView,
        private val duration: Long
    ) {

        /** Coordinate X. */
        private var cX: Int = 0
        /** Coordinate Y. */
        private var cY: Int = 0
        private var circularReveal: Animator? = null

        /** animate toolbar title reveal. */
        private fun showTitle() {
            toolbarTitle.visibility = View.INVISIBLE
            circularReveal?.cancel()
            circularReveal = ViewAnimationUtils.createCircularReveal(
                toolbarTitle, cX, cY, 0f, toolbarTitle.width.toFloat()
            )
            toolbarTitle.visibility = View.VISIBLE
            circularReveal?.duration = duration
            circularReveal?.start()
        }

        /** animate toolbar title hiding. */
        private fun hideTitle() {
            toolbarTitle.visibility = View.VISIBLE
            circularReveal?.cancel()
            circularReveal = ViewAnimationUtils.createCircularReveal(
                toolbarTitle, cX, cY, toolbarTitle.width.toFloat(), 0f
            )
            circularReveal?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    toolbarTitle.visibility = View.INVISIBLE
                }
            })
            circularReveal?.duration = duration
            circularReveal?.start()
        }

        /** Observe scroll to scroll changes and perform animation if applicable. */
        fun observeScroll() {
            toolbarTitle.visibility = View.INVISIBLE
            scrollView.viewTreeObserver.addOnScrollChangedListener {
                val positionView = outsideTitle.y + outsideTitle.measuredHeight
                val toolbarTitleLocationInWindow = intArrayOf(0, 0).apply {
                    this@ToolbarTitleRevealAnimation.toolbarTitle.getLocationInWindow(this)
                }

                cX = 0
                cY = toolbarTitleLocationInWindow[1]
                    .plus(toolbarTitle.measuredHeight / 2)
                    .minus((toolbarTitle as TextView).textSize.toInt())

                if (scrollView.scrollY >= positionView.times(0.8)) {
                    if (toolbarTitle.visibility == View.INVISIBLE) showTitle()
                } else {
                    if (toolbarTitle.visibility == View.VISIBLE && (circularReveal?.isStarted == false || circularReveal?.isRunning == false)) {
                        hideTitle()
                    }
                }
            }
        }
    }

}