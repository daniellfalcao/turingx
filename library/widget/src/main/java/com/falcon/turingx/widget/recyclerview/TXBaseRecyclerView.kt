@file:Suppress("MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.falcon.turingx.widget.recyclerview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.falcon.turingx.core.utils.doOnLock
import com.falcon.turingx.core.utils.safeUnlock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

/** The base recycler view that use diff util to change data.*/
open class TXBaseRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    /** Easy access to recycler view adapter. */
    val txAdapter: TXAdapter<Any, TXViewHolder<Any>>?
        get() = adapter as? TXAdapter<Any,TXViewHolder<Any>>?

    override fun setAdapter(adapter: Adapter<*>?) {
        // only accept adapter who is instance of TXAdapter.
        if (adapter !is TXAdapter<*, *>) {
            throw IllegalArgumentException("This RecyclerView is only used with TXAdapter")
        }
        super.setAdapter(adapter)
    }

    /** Base RecyclerView adapter. */
    abstract class TXAdapter<T : Any, VH : TXViewHolder<T>>(
        coroutineScope: CoroutineScope,
        var enableDiffUtil: Boolean = false, 
        var updateDelay: Long = 100L,
    ) : RecyclerView.Adapter<VH>(), CoroutineScope by coroutineScope {

        /** The blocker of operations that diffUtil use, safeThread.*/
        protected val refreshOperationManager = Mutex(false)
        /** The list of the pending data to be updated in the recycler view, waiting diffUtil.*/
        protected var refreshOperations = mutableListOf<List<T>>()
        /** The current displayed items in recyclerView. */
        protected var items = mutableListOf<T>()
        /** A easy reference to recyclerView. */
        protected var recyclerView: RecyclerView? = null
        /** Represents the instant ItemCount of the adapter, maybe items its not displayed yet. */
        var instantAdapterItemCount: Int = 0

        /**
         * Refresh items from recycler view.
         *
         * If diffUtil is enable, perform the calculation of changes of each element in the old list
         * displayed by the recyclerView.
         * If diffUtil is not enable, just clear old list and update the items.
         * */
        open fun refresh(items: List<T>) {
            refreshOperations.add(items)
            instantAdapterItemCount = items.size
            if (enableDiffUtil) refreshWithDiffUtil() else refreshWithoutDiffUtil()
        }

        /** Returns a element in the given [position]. */
        open fun getItemAt(position: Int): T? {
            return try {
                items[position]
            } catch (e: IndexOutOfBoundsException) {
                null
            }
        }

        /** Perform the update operation using diffUtil.*/
        protected open fun refreshWithDiffUtil() {
            suspend fun update(newItems: List<T>) {
                // calculate the deference between lists.
                val difference = DiffUtil.calculateDiff(TXDiffUtilCallback(newItems, items), true)
                // wait the delay to avoiding overlap of lists.
                delay(updateDelay)
                // in the main dispatcher, dispatch the list changes in the adapter.
                withContext(Dispatchers.Main) {
                    if (refreshOperations.isEmpty()) {
                        items.clear()
                        items.addAll(newItems)
                        difference.dispatchUpdatesTo(this@TXAdapter)
                        refreshOperationManager.safeUnlock()
                        refreshWithDiffUtil()
                    } else {
                        refreshOperationManager.safeUnlock()
                        refreshWithDiffUtil()
                    }
                }
            }
            // attempt start the refresh operation if the mutex is not blocked.
            if (!refreshOperationManager.isLocked && refreshOperations.isNotEmpty()) {
                launch(Dispatchers.Default) {
                    refreshOperationManager.doOnLock {
                        try { update(refreshOperations.removeAt(0)) } catch (e: Exception) { }
                    }
                }
            }
        }

        /** Perform the update operation without use diffUtil. Just replace data and notify.*/
        protected open fun refreshWithoutDiffUtil() {
            this.items.clear()
            this.items.addAll(refreshOperations.removeAt(0))
            notifyDataSetChanged()
        }

        override fun getItemCount() = items.size

        /** Save the recyclerView instance. */
        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            this.recyclerView = recyclerView
        }

        /** Release recyclerView instance. */
        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
            super.onDetachedFromRecyclerView(recyclerView)
            this.recyclerView = null
        }

        /** Create the viewHolder instance. */
        protected inline fun <reified V: TXViewHolder<*>> buildViewHolder(@LayoutRes res: Int, root: ViewGroup): V {
            val view = LayoutInflater.from(root.context).inflate(res, root, false)
            return V::class.java.constructors[0].newInstance(view) as V
        }
    }

    /** RecyclerView Adapter that uses ViewDataBinding. */
    abstract class TXDataBindingAdapter<T : Any, VH : TXDataBindingViewHolder<T, *>>(
        coroutineScope: CoroutineScope,
        useDiffUtil: Boolean = false,
        updateDelay: Long = 100L
    ) : TXAdapter<T, VH>(coroutineScope, useDiffUtil, updateDelay) {

        /** Create the viewHolder instance using dataBinding. */
        inline fun <reified V : TXDataBindingViewHolder<*,VDB>, VDB: ViewDataBinding> buildDataBindViewHolder(@LayoutRes res: Int, root: ViewGroup): V {
            val inflater = LayoutInflater.from(root.context)
            val binding = DataBindingUtil.inflate<VDB>(inflater, res, root, false)
            return V::class.java.constructors[0].newInstance(binding) as V
        }
    }

    /** Base ViewHolder. */
    open class TXViewHolder<T : Any>(view: View) : RecyclerView.ViewHolder(view), BindableViewHolder<T>

    /** Base ViewHolder to dataBinding. */
    open class TXDataBindingViewHolder<T : Any, VDB : ViewDataBinding>(
        val binding: VDB
    ) : TXViewHolder<T>(binding.root)

    /** DiffUtil implementation. */
    open class TXDiffUtilCallback<T : Any>(
        private var newList: List<T>,
        private var oldList: List<T>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

    interface BindableViewHolder<T> {
        fun bind(item: T) { }
    }

}