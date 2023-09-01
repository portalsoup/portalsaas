package com.portalsoup.saas.core.koin

import com.portalsoup.saas.config.AppConfig
import com.portalsoup.saas.extensions.Logging
import com.portalsoup.saas.extensions.log
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor

object KoinMain: Logging {

    fun init(appConfig: AppConfig) {
        startKoin {
            modules(getModules(appConfig))
        }
    }

    fun getModules(appConfig: AppConfig): List<Module> {
        return KoinModules::class.sealedSubclasses
            .asSequence()
            .onEach(::validateModuleSignature)
            .onEach { log().info("About to initialize the Koin module: ${it.simpleName}") }
            .mapNotNull { it.primaryConstructor }
            .onEach { log().info("Initialized.") }
            .map { it.call(appConfig) }
            .filter { it.shouldInitialize() }
            .map { it.initialize() }
            .toList()
    }

    private fun validateModuleSignature(module: KClass<out KoinModules>) {
        log().info("${module.simpleName} ${module.primaryConstructor?.parameters?.map { "parameter=[${it.name} ${it.type}" }}]")
        val constructor = module.primaryConstructor ?: throw RuntimeException("Could not find primary constructor for ${module.simpleName}")
        val constructorArgs = constructor.parameters

        if (constructorArgs.size != 1) {
            throw RuntimeException("The Koin module ${module.simpleName}'s primary constructor requires exactly one argument for the app config!")
        }

        if (constructorArgs.first().type != AppConfig::class.createType()) {
            throw RuntimeException("The Koin module ${module.simpleName}'s primary constructor parameter is not the app config")
        }
    }

}