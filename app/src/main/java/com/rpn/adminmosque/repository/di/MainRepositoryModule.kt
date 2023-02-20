package com.rpn.adminmosque.repository.di

import com.rpn.adminmosque.api.ApiInterface
import com.rpn.adminmosque.repository.AuthAppRepository
import com.rpn.adminmosque.repository.FireStoreRepository
import com.rpn.adminmosque.repository.MainRepository
import com.rpn.jardindekashmir.api.ApiUtilities
import org.koin.dsl.module

/**
 * Repository dependency injection module.
 */
val mainRepositoryModule = module {
    // Repositories
    single { MainRepository(ApiUtilities.getInstance().create(ApiInterface::class.java)) }
    single { AuthAppRepository(get(),get()) }
    single { FireStoreRepository() }
}
