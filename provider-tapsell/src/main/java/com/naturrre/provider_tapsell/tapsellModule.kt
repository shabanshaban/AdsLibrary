package com.naturrre.provider_tapsell

import com.naturrre.core.AdProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val tapsellModule = module {
    single<AdProvider>(named("tapsell")) { TapsellProvider() }
}