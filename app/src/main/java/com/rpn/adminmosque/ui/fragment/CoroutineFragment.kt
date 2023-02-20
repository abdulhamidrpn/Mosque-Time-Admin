package com.rpn.adminmosque.ui.fragment

import androidx.fragment.app.Fragment
import com.rpn.adminmosque.ui.viewmodel.MainViewModel
import com.rpn.adminmosque.utils.SettingsUtility
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

open class CoroutineFragment : Fragment(), KoinComponent {

    private val mainDispatcher: CoroutineDispatcher get() = Main
    protected val mainViewModel by inject<MainViewModel>()
    protected val settingsUtility by inject<SettingsUtility>()
    private val job = Job()
    private val scope = CoroutineScope(job + mainDispatcher)

    protected fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = scope.launch(context, start, block)

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }
}