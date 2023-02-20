package com.rpn.adminmosque.utils

import org.koin.dsl.module

val utilsModule = module {
    factory { SettingsUtility(get()) }
}