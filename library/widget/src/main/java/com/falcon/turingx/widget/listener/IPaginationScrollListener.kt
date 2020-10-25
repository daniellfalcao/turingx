package com.falcon.turingx.widget.listener

import androidx.recyclerview.widget.RecyclerView

abstract class IPaginationScrollListener(
    var onScroll: () -> Unit,
    var onLoadMore: (totalItemCount: Int) -> Unit
): RecyclerView.OnScrollListener()  {

    open override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    }

    open override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
    }
}