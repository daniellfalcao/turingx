@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.falcon.turingx.controller.pagination

import com.falcon.turingx.core.exception.TXException
import java.util.concurrent.atomic.AtomicBoolean

class TXPaginationController(val pageSize: Int, val firstPage: Int = 1) {

    open class PaginationControllerException : TXException()
    class EndOfPaginationException : PaginationControllerException()
    class PaginationInProgressException : PaginationControllerException()

    private val hasPendingPagination = AtomicBoolean(false)

    var currentPage = firstPage - 1
    var isLastPage = false

    /**
     * @return if the current page is the first page
     *
     * */
    val isFirstPage: Boolean
        get() = currentPage.plus(1) == firstPage

    /**
     * @return the next page available
     *
     * */
    val nextPage: Int
        get() = currentPage.plus(1)

    /**
     * Attempt start a pagination.
     *
     * @param resetPagination tells to controller that the paging parameters must be reset.
     *
     * EX:
     *      attemptPaging(true,
     *          onPaginationAllowed = { page, size, isFirstPage ->
     *              fetchSomething(page, size, isFirstPage)
     *                  .onSuccess { pageSize ->
     *                      onPageSuccessful(pageSize)
     *                  }.onFinish {
     *                      onPageFinished()
     *                  }
     *              }, onPaginationNotAllowed = {
     *                  \// do something with exception
     *              })
     *
     * */
    fun attemptPaging(
        resetPagination: Boolean = false,
        onPaginationAllowed: (page: Int, size: Int, isFirstPage: Boolean) -> Unit,
        onPaginationNotAllowed: (exception: PaginationControllerException) -> Unit
    ) {
        // check if requested page is going reset paging or continue
        if (resetPagination || allowPaging()) {
            // if is going reset page, reset controller params to initial state
            if (resetPagination) reset()
            // block new pages
            hasPendingPagination.set(true)
            onPaginationAllowed(currentPage, pageSize, isFirstPage)
        } else {
            onPaginationNotAllowed(
                // discovers the reason for not allowing pages
                when {
                    isLastPage -> EndOfPaginationException()
                    hasPendingPagination.get() -> PaginationInProgressException()
                    else -> PaginationControllerException()
                }
            )
        }
    }

    /**
     * Check if [page] is equals to [firstPage] defined in constructor.
     *
     * @return true if [page] is equals to [firstPage].
     * @return false if [page] is not equals to [firstPage].
     *
     * */
    fun isFirstPage(page: Int) = page == firstPage

    /**
     * Check if the controller allows paging a new page.
     *
     * @return true if the pagination has not arrived on the last page and has no pending pages.
     *  See [currentPage] [hasPendingPagination].
     * @return false if the pagination has arrived on the last page. See [currentPage].
     * @return false if has pending pages. See [hasPendingPagination].
     *
     * */
    fun allowPaging(): Boolean {
        if (isLastPage) return false
        if (hasPendingPagination.get()) return false
        return true
    }

    /**
     * Tells to controller that pagination finished successfully and that its parameters must be
     * updated.
     *
     * @param pageSize the received size of requested pagination.
     *
     * */
    fun onPageSuccessful(pageSize: Int) {
        if (pageSize != this.pageSize) isLastPage = true
        currentPage += 1
    }

    /**
     * Tells to controller thar pagination is finished and now must permit new paginations.
     *
     * */
    fun onPageFinished() {
        hasPendingPagination.set(false)
    }

    /**
     * Reset controller params to initial state.
     *
     * */
    fun reset() {
        currentPage = firstPage.minus(1)
        isLastPage = false
        hasPendingPagination.set(false)
    }

}