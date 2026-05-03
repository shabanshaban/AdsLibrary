package com.naturrre.provider_yandex

import com.naturrre.core.AdProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val yandexModule = module {
    single<AdProvider>(named("yandex")) { YandexProvider() }
}