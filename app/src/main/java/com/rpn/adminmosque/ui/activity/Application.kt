package com.rpn.adminmosque.ui.activity

import android.app.Application
import com.rpn.adminmosque.repository.di.mainRepositoryModule
import com.rpn.adminmosque.ui.viewmodel.viewModelModule
import com.rpn.adminmosque.utils.utilsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {


    override fun onCreate() {
        super.onCreate()

        val modules = listOf(
            viewModelModule,
            mainRepositoryModule,
            utilsModule
        )
        startKoin {
            androidContext(this@Application)
            modules(modules)
        }
    }

    companion object {
        const val CHANNEL_ID = "ALARM_SERVICE_CHANNEL"
    }
}

