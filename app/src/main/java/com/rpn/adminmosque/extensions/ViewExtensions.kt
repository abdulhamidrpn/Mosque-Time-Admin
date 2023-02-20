package com.rpn.exchangebook.extensions

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout


fun <T : ViewDataBinding> ViewGroup.inflateWithBinding(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): T {
    val layoutInflater = LayoutInflater.from(context)
    return DataBindingUtil.inflate(layoutInflater, layoutRes, this, attachToRoot) as T
}


fun View?.toggle(viewGroup: ViewGroup, show: Boolean) {
    val transition: Transition = Fade()
    transition.setDuration(500)
    this?.let { transition.addTarget(it) }
    TransitionManager.beginDelayedTransition(viewGroup, transition)
    this?.setVisibility(if (show) VISIBLE else GONE)
}

fun View.setPaddings(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    setPadding(
        left ?: paddingLeft,
        top ?: paddingTop,
        right ?: paddingRight,
        bottom ?: paddingBottom
    )
}

fun View?.animateScale(from: Float, to: Float, dur: Long) {
    this ?: return
    val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, from, to)
    val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, from, to)
    val animatorSet = AnimatorSet().apply {
        interpolator = OvershootInterpolator()
        duration = dur
        playTogether(scaleX, scaleY)
    }
    animatorSet.start()
}



fun Snackbar.setIcon(drawable: Drawable, @ColorInt colorTint: Int): Snackbar {
    return this.apply {
        setAction(" ") {}
        val textView = view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.text = ""

        drawable.setTint(colorTint)
        drawable.setTintMode(PorterDuff.Mode.SRC_ATOP)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }
}


internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                return view
            } else {
                fallback = view
            }
        }

        if (view != null) {
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    return fallback
}

fun RecyclerView.snapToPosition(
    position: Int,
    snapMode: Int = LinearSmoothScroller.SNAP_TO_START,
    smooth: Boolean = true
) {
    if (smooth) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    } else {
        (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 20)
    }
}


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}