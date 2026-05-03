package com.naturrre.provider_admob

import com.naturrre.core.AdProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val admobModule = module {
    single<AdProvider>(named("admob")) { AdMobProvider() }
}