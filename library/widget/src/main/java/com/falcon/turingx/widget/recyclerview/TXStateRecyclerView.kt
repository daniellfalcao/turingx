package com.falcon.turingx.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.falcon.turingx.core.exception.TXException
import com.falcon.turingx.core.utils.setGone
import com.falcon.turingx.core.utils.setVisible
import timber.log.Timber
import kotlin.reflect.KClass

open class TXStateRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TXBaseRecyclerView(context, attrs, defStyleAttr) {

    /** RecyclerView States. */
    sealed class State {

        /** Initial state is the primary state of recyclerView. */
        object Initial : State() {
            override fun toString() = "Initial"
        }

        /** WithContent represents the states when recyclerView have some data to show. */
        object WithContent : State() {
            override fun toString() = "WithContent"
        }
        /** NoContent represents the state when recyclerView don't have data do show.*/
        object NoContent : State() {
            override fun toString() = "NoContent"
        }
        /**
         * Loading represents the state when recyclerView is loading some data.
         * [nestedState] tells to recyclerView if the recyclerView have content.
         * [nestedState] cam assume the state [WithContent] or [NoContent].
         * */
        class Loading : State() {
            var nestedState: State = NoContent
            override fun toString() = "Loading"
        }
        /** Paging represents the state when recyclerView is paging. */
        object Paging : State() {
            override fun toString() = "Paging"
        }
        /**
         * Error represents the state when an error occurs to data loading in recyclerView.
         * [nestedState] tells to recyclerView if the recyclerView have content.
         * [nestedState] cam assume the state [WithContent] or [NoContent].
         * */
        class Error(val exception: TXException) : State() {
            var nestedState: State = NoContent
            override fun toString() = "Error"
        }
    }

    open var state: State = State.Initial

    // configurations in state initial
    // TODO()

    // configurations in state withContent
    // TODO()

    // configurations in state noContent
    // TODO()

    // TODO(estado de swipe não tratado)
    open var viewOfSwipeLoadingState: SwipeRefreshLayout? = null

    /** Configurations in state loading. */
    open var viewOfLoadingState: View? = null
    protected open var loadingRule: (TXStateExecutor.() -> Unit)? = null

    /** Configurations in state paging. */
    open var viewOfPagingState: View? = null
    protected open var pagingRule: (TXStateExecutor.() -> Unit)? = null

    /** Configurations in state empty. */
    open var viewOfEmptyState: View? = null
    open var itemsWhenEmpty: Int = 0
    protected open var noContentRule: (TXStateExecutor.() -> Unit)? = null

    /** Configurations in state error. */
    open var viewOfErrorState: View? = null
    protected open var errorRule: (TXStateExecutor.() -> Unit)? = null

    /** The state changer executor. */
    protected open val stateExecutor = TXStateExecutor()

    /** Easy way to returns adapter item count. */
    open val adapterItemCount: Int
        get() = adapter?.itemCount ?: 0
    /** Easy way to returns adapter item count without items defined as disposable. */
    open val adapterRealItemCount: Int
        get() = adapter?.itemCount?.minus(itemsWhenEmpty) ?: 0
    /**
     * Easy way to return the real adapter item count.
     * See [com.falcon.turingx.widget.recyclerview.TXBaseRecyclerView.TXAdapter.instantAdapterItemCount]
     * */
    open val instantAdapterItemCount: Int
        get() = (adapter as? TXAdapter<*, *>?)?.instantAdapterItemCount ?: 0
    /**
     * Easy way to return the real adapter item count without items defined as disposable.
     * See [com.falcon.turingx.widget.recyclerview.TXBaseRecyclerView.TXAdapter.instantAdapterItemCount]
     * */
    open val instantRealAdapterItemCount: Int
        get() = (adapter as? TXAdapter<*, *>?)?.instantAdapterItemCount?.minus(itemsWhenEmpty) ?: 0

    /**
     * Add a callback to be called when error state is called when recyclerView is displaying some
     * data.
     * */
    private var _onErrorStateCalledWithContent: (exception: TXException) -> Unit = { }
    open fun onErrorStateCalledWithContent(event: (exception: TXException) -> Unit) = apply {
        _onErrorStateCalledWithContent = event
    }

    protected open val dataObserver = object : RecyclerView.AdapterDataObserver() {

        /** When data change verify if the actual state is applicable to the given context*/
        fun onDataChanged() {
            // recover the last state
            var requestState = state
            // check if the last state is WithContent or NoContent or Initial State and verify if
            // the state still makes sense. If no makes sense update state.
            if (requestState is State.WithContent || requestState is State.NoContent || requestState is State.Initial) {
                requestState = if (adapterRealItemCount > 0) State.WithContent else State.NoContent
            }
            // call update state to recyclerView changes his state.
            updateState(requestState)
        }

        override fun onChanged() {
            super.onChanged()
            onDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            onDataChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            onDataChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            onDataChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            onDataChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            onDataChanged()
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        try {
            // unregister the last data observer
            adapter?.unregisterAdapterDataObserver(dataObserver)
        } catch (e: Exception) {
        }
        // register the adapter observer to the new adapter
        adapter?.registerAdapterDataObserver(dataObserver)
        super.setAdapter(adapter)
    }

    /** Calling [setRuleOfState] you can change the default behavior of how states are treated. */
    open fun setRuleOfState(state: KClass<*>, rule: TXStateExecutor.() -> Unit) {
        when (state) {
            State.NoContent::class -> noContentRule = rule
            State.NoContent::class -> noContentRule = rule
            State.Loading::class -> loadingRule = rule
            State.Paging::class -> pagingRule = rule
            State.Error::class -> errorRule = rule
        }
    }

    /** Updates the recycler view state. */
    open fun updateState(newState: State) = synchronized(this) {
        Timber.d("updating state of recycler view to -> $newState")
        // save the new state
        state = newState
        // if the new state will be Loading or Paging, discover if the nested state will be
        // [WithContent] or [NoContent].
        if (newState is State.Loading || newState is State.Error) {
            val nestedState = if (instantRealAdapterItemCount > 0) State.WithContent else State.NoContent
            (newState as? State.Loading)?.nestedState = nestedState
            (newState as? State.Error)?.nestedState = nestedState
            Timber.d("nested state is -> $nestedState, itemCount -> $adapterRealItemCount, instantItemCount -> $instantRealAdapterItemCount")
        }

        // handle state checking if have some ruler changer. See [setRuleOfState]
        when (newState) {
            is State.Initial -> {
                // TODO(?)
            }
            // if you have a withContent state, hide all state views.
            is State.WithContent -> {
                stateExecutor.hideAllStates()
            }
            // if you have noContent state, hide all state views and show empty view.
            is State.NoContent -> {
                noContentRule?.let { stateExecutor.apply(it) } ?: run {
                    stateExecutor.hideAllStates()
                    stateExecutor.showEmptyState()
                }
            }
            // if you have a loading state the default behavior it is hide all state views and show
            // a loading view if no have data to be displayed, if have data displayed don't show
            // loading state.
            is State.Loading -> {
                loadingRule?.let { stateExecutor.apply(it) } ?: run {
                    stateExecutor.hideAllStates()
                    if (newState.nestedState !is State.WithContent) stateExecutor.showLoadingState()
                }
            }
            // if you have a paging state the default behavior it is hide all state views and show
            // the paging view.
            is State.Paging -> {
                pagingRule?.let { stateExecutor.apply(it) } ?: run {
                    stateExecutor.hideAllStates()
                    stateExecutor.showPagingState()
                }
            }
            // if you have a error state the default behavior it is hide all views state and show
            // error view state if no have data being displayed. Call the callback of
            // errorStateCalledWIthContent if have some data being displayed.
            is State.Error -> {
                if (newState.nestedState is State.WithContent) {
                    _onErrorStateCalledWithContent(newState.exception)
                }
                errorRule?.let { stateExecutor.apply(it) } ?: run {
                    stateExecutor.hideAllStates()
                    if (newState.nestedState is State.NoContent) {
                        stateExecutor.showErrorState()
                    }
                }
            }
        }
    }

    inner class TXStateExecutor {

        fun showSwipeState() {
            viewOfSwipeLoadingState?.isRefreshing = true
        }

        fun hideSwipeState() {
            viewOfSwipeLoadingState?.isRefreshing = false
        }

        fun showLoadingState() {
            viewOfLoadingState.setVisible()
        }

        fun hideLoadingState() {
            viewOfLoadingState.setGone()
        }

        fun showPagingState() {
            viewOfPagingState.setVisible()
        }

        fun hidePagingState() {
            viewOfPagingState.setGone()
        }

        fun showEmptyState() {
            viewOfEmptyState.setVisible()
        }

        fun hideEmptyState() {
            viewOfEmptyState.setGone()
        }

        fun showErrorState() {
            viewOfErrorState.setVisible()
        }

        fun hideErrorState() {
            viewOfErrorState.setGone()
        }

        fun hideAllStates() {
            hideEmptyState()
            hideErrorState()
            hideLoadingState()
            hidePagingState()
            hideSwipeState()
        }
    }
}