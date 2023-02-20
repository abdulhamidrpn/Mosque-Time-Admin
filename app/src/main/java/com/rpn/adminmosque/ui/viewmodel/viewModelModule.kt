package com.rpn.adminmosque.ui.viewmodel

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {

    single { MainViewModel() }
    viewModel { LoginRegisterViewModel(get (),get()) }
    single { HomeViewModel() }
}
// AlbumViewModel by inject { parametersOf(a, b) }
