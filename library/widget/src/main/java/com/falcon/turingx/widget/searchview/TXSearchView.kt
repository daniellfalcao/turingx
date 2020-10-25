@file:Suppress("MemberVisibilityCanBePrivate")

package com.falcon.turingx.widget.searchview

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.falcon.turingx.core.utils.*
import com.falcon.turingx.widget.R
import com.falcon.turingx.widget.databinding.TxSearchViewBinding
import kotlinx.coroutines.*
import java.lang.ref.WeakReference

class TXSearchView : ConstraintLayout {

    private lateinit var binding: TxSearchViewBinding

    private val _coroutineScope = MainScope()
    private var _animator: Animator? = null
    private var _activity: WeakReference<Activity>? = null
    private var _searchJob: Job? = null

    private var _onBackButtonClicked: () -> Unit = { }
    private var _onCancelButtonClicked: () -> Unit = { }
    private var _onSearchViewOpened: MutableList<() -> Unit> = mutableListOf()
    private var _onSearchViewClosed: MutableList<() -> Unit> = mutableListOf()
    private var _onSearch: MutableList<(query: String) -> Unit> = mutableListOf()

    private val _textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            _searchJob?.cancel()
            _searchJob = _coroutineScope.launch(Dispatchers.Default) {
                val query = s?.toString() ?: ""
                if (query.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        binding.cancelButton.setInvisible()
                        onSearch(query)
                    }
                } else {
                    withContext(Dispatchers.Main) { binding.cancelButton.setVisible() }
                    delay(searchInterval)
                    withContext(Dispatchers.Main) { onSearch(query) }
                }
            }
        }
    }

    /** The menu item id to change searchView visibility. */
    var menuActionId = 0
    /** The revel animation duration performed by the searchView. */
    var animateDuration: Long = 400
    /** The delay between each onTextChanged. */
    var searchInterval: Long = 1000
    /** Set a drawable to be displayed in the back action of the search. */
    var backButtonDrawable: Drawable? = null
        set(value) = let { binding.backButton.setImageDrawable(value) }
    /** Set a tint to back button.*/
    var backButtonTint: Int = Color.BLACK
        set(value) = let { binding.backButton.drawable.setTint(value) }
    /** Set a Drawable to be displayed in the cancel action of the searchView. */
    var cancelButtonDrawable: Drawable? = null
        set(value) = let { binding.cancelButton.setImageDrawable(value) }
    /** Set a tint to cancel button. */
    var cancelButtonTint: Int = Color.BLACK
        set(value) = let { binding.cancelButton.drawable.setTint(value) }
    /** Set a color to searchView background .*/
    var searchBackground: Int = Color.WHITE
        set(value) = let { binding.container.setCardBackgroundColor(value) }
    /** Change the searchView elevation. */
    var searchElevation: Float = 0F
        set(value) = let { binding.container.cardElevation = value }
    /** Change the searchView background border radius. */
    var searchBorderRadius: Float = 0F
        set(value) = let { binding.container.radius = value }
    /** Change the text appearance of the searchView. */
    var searchTextAppearance: Int = 0
        set(value) = let { if (value != 0) binding.query.setTextAppearance(context, value) }
    /** Set a hint to be displayed in the searchView. */
    var searchHint: String = ""
        set(value) = let { binding.query.hint = value }
    /** Change the searchView displayed mode. See [ViewMode]*/
    var searchMode: ViewMode = ViewMode.ANIMATED
        set(value) {
            field = value
            if (value == ViewMode.ANIMATED) setInvisible() else setVisible()
        }
    /** Returns if the serachView is displayed or not, iconified true = not displayed. */
    var iconified = true
        private set
    /** the current searched text. */
    val query: String
        get() = binding.query.text.toString()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.tx_search_view,
            this,
            true
        )

        // retrive aattributes from styleAttributes and configure searchView.
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.TXSearchView) ?: return

        backButtonDrawable = ContextCompat.getDrawable(
            context,
            attributes.getResourceId(
                R.styleable.TXSearchView_tx_search_backIcon,
                R.drawable.ic_arrow_back
            )
        )
        backButtonTint = attributes.getColor(
            R.styleable.TXSearchView_tx_search_backIconTint,
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
        cancelButtonDrawable = ContextCompat.getDrawable(
            context,
            attributes.getResourceId(
                R.styleable.TXSearchView_tx_search_cancelIcon,
                R.drawable.ic_close
            )
        )
        cancelButtonTint = attributes.getColor(
            R.styleable.TXSearchView_tx_search_cancelIconTint,
            ContextCompat.getColor(
                context,
                R.color.black
            )
        )
        searchBackground = attributes.getColor(
            R.styleable.TXSearchView_tx_search_background,
            ContextCompat.getColor(
                context,
                android.R.color.white
            )
        )
        searchElevation = attributes.getDimension(
            R.styleable.TXSearchView_tx_search_elevation,
            -1F
        )
        searchBorderRadius = attributes.getDimension(
            R.styleable.TXSearchView_tx_search_borderRadius,
            -1F
        )
        searchTextAppearance = attributes.getResourceId(
            R.styleable.TXSearchView_tx_search_textAppearance,
            0
        )
        menuActionId = attributes.getInt(
            R.styleable.TXSearchView_tx_search_menuId, 0
        )
        searchHint = attributes.getString(R.styleable.TXSearchView_tx_search_hint) ?: ""
        iconified = attributes.getBoolean(
            R.styleable.TXSearchView_tx_search_iconified,
            true
        )
        searchMode = ViewMode.getOrNull(
            attributes.getInt(R.styleable.TXSearchView_tx_search_mode, 1)
        )!!

        binding.backButton.apply {
            setOnClickListener {
                if (searchMode == ViewMode.ANIMATED) hide(_activity?.get())
                onBackButtonClicked()
            }
            isEnabled = true
        }

        binding.cancelButton.apply {
            setOnClickListener {
                binding.query.setText("", TextView.BufferType.EDITABLE)
                onCancelButtonClicked()
            }
            isEnabled = true
        }

        binding.query.apply {
            addTextChangedListener(_textWatcher)
            setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    _onSearch.forEach { it(this@TXSearchView.query) }
                    binding.query.hideKeyboard()
                    this@TXSearchView.clearFocus()
                    true
                } else {
                    false
                }
            })
        }

        attributes.recycle()
    }

    /**
     * Given a [activity] and a [animate] boolean value, show searchView.
     *
     * If [animate] == true, perform a circular reveal animation starting from the menu item
     * position.
     * if [animate] == false just show searchView without animations.
     *
     * */
    fun show(activity: Activity? = null, animate: Boolean = true) {

        this._activity = activity?.let { WeakReference(it) }

        binding.backButton.isEnabled = true
        binding.query.apply {
            removeTextChangedListener(_textWatcher)
            setText("", TextView.BufferType.EDITABLE)
            addTextChangedListener(_textWatcher)
        }
        // only perform animation if the Build Version is > 21
        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val currentActivity: Activity? = activity ?: context.toActivity()
            ?: throw IllegalStateException("Activity must be not null")
            if (menuActionId == 0) throw IllegalStateException("idMenu has a invalid item ID")
            currentActivity?.findViewById<View>(menuActionId)?.let {
                createCircularReveal(it, true)
            }
        } else {
            setVisible()
            onSearchStarted()
        }
    }

    /**
     * Given a [activity] and a [animate] boolean value, hide searchView.
     *
     * If [animate] == true, perform a circular reveal animation ending in the menu item position.
     * if [animate] == false just hide searchView without animations.
     *
     * */
    fun hide(activity: Activity? = null, animate: Boolean = true) {

        binding.backButton.isEnabled = false
        binding.query.removeTextChangedListener(_textWatcher)

        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val currentActivity: Activity? = activity ?: _activity?.get() ?: context.toActivity()
            ?: throw IllegalStateException("Activity must be not null")
            if (menuActionId == 0) throw IllegalStateException("idMenu has a invalid item ID")
            currentActivity?.findViewById<View>(menuActionId)?.let {
                createCircularReveal(it, false)
            }
        } else {
            setInvisible()
            onSearchFinished()
        }
    }

    /** Perform the circular animation. */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun createCircularReveal(view: View, isRevealing: Boolean) {

        // retrieve the current view location in window.
        val location = IntArray(2).apply { view.getLocationInWindow(this) }
        // based in view location, calculate the center of the view.
        val centerX = location[0] + (view.width / 2)
        val centerY = location[1]
        val startRadius: Float
        val endRadius: Float
        // based in the type of animation, configure the radius of the circular animation
        if (isRevealing) {
            startRadius = 0F
            endRadius = width.times(1.5F)
        } else {
            startRadius = width.times(1.5F)
            endRadius = 0F
        }
        // configure the animator and start animation.
        _animator = ViewAnimationUtils.createCircularReveal(
            this,
            centerX,
            centerY,
            startRadius,
            endRadius
        )
        _animator?.interpolator = DecelerateInterpolator()
        _animator?.duration = animateDuration
        _animator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                if (isRevealing) {
                    onSearchStarted()
                } else {
                    setInvisible()
                    onSearchFinished()
                }
            }

            override fun onAnimationStart(animation: Animator?) {
                if (isRevealing) this@TXSearchView.setVisible()
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }
        })
        _animator?.start()
    }

    /** Add a callback where is called when backButton is clicked */
    fun setOnBackButtonClicked(callback: () -> Unit) = apply {
        _onBackButtonClicked = callback
    }

    /** Add a callback where is called when cancelButton is clicked */
    fun setOnCancelButtonClicked(callback: () -> Unit) = apply {
        _onCancelButtonClicked = callback
    }
    /** Add a callback where is called when searchView is opened. */
    fun addOnSearchOpened(callback: () -> Unit) = apply {
        _onSearchViewOpened.add(callback)
    }

    /** Add a callback where is called when searchView is closed. */
    fun addOnSearchClosed(callback: () -> Unit) = apply {
        _onSearchViewClosed.add(callback)
    }

    /** Add a callback where is called when text from searchView is changed. */
    fun addOnSearch(callback: (query: String) -> Unit) = apply {
        _onSearch.add(callback)
    }

    /** Clear all callbacks. */
    fun removeAllListeners() {
        _onBackButtonClicked = { }
        _onCancelButtonClicked = { }
        _onSearchViewOpened = mutableListOf()
        _onSearchViewClosed = mutableListOf()
        _onSearch = mutableListOf()
        _activity = null
    }

    /** Called when searchView turns visible to user. */
    private fun onSearchStarted() {
        binding.apply {
            query.requestFocus()
            query.showKeyboard()
            backButton.isEnabled = true
            cancelButton.isEnabled = true
        }
        _onSearchViewOpened.forEach { it() }
    }

    /** Called when searchView turns invisible to user. */
    private fun onSearchFinished() {
        binding.apply {
            query.hideKeyboard()
            backButton.isEnabled = false
            cancelButton.isEnabled = false
        }
        _onSearchViewClosed.forEach { it() }
    }

    /** Called every time when user text something in query editText. */
    private fun onSearch(query: String) {
        _onSearch.forEach { it(query) }
    }

    /** Called when user click on back button. */
    private fun onBackButtonClicked() {
        _onBackButtonClicked()
    }

    /** Called when user click in cancel button. */
    private fun onCancelButtonClicked() {
        _onCancelButtonClicked()
    }

    /** Remove focus from [binding.query] too. */
    override fun clearFocus() {
        super.clearFocus()
        binding.query.clearFocus()
    }

    enum class ViewMode(val id: Int) {
        /** Animate the search view visibility changes.*/
        ANIMATED(1),
        /** Turn the searchView a fixed view, not animated. */
        FIXED(2);

        companion object {
            fun getOrNull(id: Int): ViewMode? {
                return when (id) {
                    1 -> ANIMATED
                    2 -> FIXED
                    else -> null
                }
            }
        }
    }

}
