package com.falcon.turingx.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.falcon.turingx.widget.listener.IPaginationScrollListener
import com.falcon.turingx.widget.listener.TXPaginationScrollListener

open class TXRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TXStateRecyclerView(context, attrs, defStyleAttr) {

    private var scrollListener: IPaginationScrollListener = TXPaginationScrollListener()
    private var _onLoadMore: (totalItemCount: Int) -> Unit = { }
    private var _onScroll: () -> Unit = { }

    /**
     * Add a callback to be called when recyclerView list get to the bottom of the list.
     *
     * @param scrollParent if recycler view is inside a nestedScrollView or Scroll view, pass the
     * scroll view as param to [onLoadMore] to setup oLoadMore properly.
     *
     * */
    open fun onLoadMore(scrollParent: FrameLayout? = null, event: (totalItemCount: Int) -> Unit) {
        setupScrollListener()
        _onLoadMore = event
        if (scrollParent != null) {
            (scrollParent as View).viewTreeObserver.addOnScrollChangedListener {
                try {
                    val scrollDiff = scrollParent.getChildAt(0)?.bottom?.minus(scrollParent.height + scrollParent.scrollY) ?: 0
                    if (scrollDiff == 0) _onLoadMore(0)
                } catch (e: Exception) {
                }
            }
            return
        }
        scrollListener.onLoadMore = _onLoadMore
    }

    /**
     * Add a callback to be called when recyclerView is scrolled.
     *
     * @param scrollParent if recycler view is inside a nestedScrollView or Scroll view, pass the
     * scroll view as param to [onLoadMore] to setup oLoadMore properly.
     *
     * */
    open fun onScroll(scrollParent: FrameLayout? = null, event: () -> Unit) {
        setupScrollListener()
        _onScroll = event
        if (scrollParent != null) {
            (scrollParent as View).viewTreeObserver.addOnScrollChangedListener { _onScroll() }
            return
        }
        scrollListener.onScroll = _onScroll
    }

    /** Configure the scroll view listener. */
    private fun setupScrollListener() {
        try {
            // remove the old scroll listener
            removeOnScrollListener(scrollListener)
        } catch (e: Exception) {
        }
        // add the new scroll listener
        addOnScrollListener(scrollListener)
    }

}