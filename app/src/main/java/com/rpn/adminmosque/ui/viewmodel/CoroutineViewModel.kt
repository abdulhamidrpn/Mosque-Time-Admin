package com.rpn.adminmosque.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

open class CoroutineViewModel(
    private val mainDispatcher: CoroutineDispatcher
) : ViewModel(),KoinComponent {
    private val job = Job()
    protected val scope = CoroutineScope(job + mainDispatcher)

    protected fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}