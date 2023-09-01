package com.portalsoup.saas.core.discord

import com.portalsoup.saas.config.AppConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object DiscordChatGPT: KoinComponent {

    val appConfig by inject<AppConfig>()

    fun gpt(prompt: String): String? {
        return "I got this"
//        val request = CompletionRequest.builder()
//            .model("davinci")
//            .prompt(prompt)
//            .maxTokens(128)
//            .build()
//
//        val key = appConfig.openaiToken ?: return null
//
//        val openai = OpenAI(key)
//
//        val response = openai.createCompletion(request)
//        return response[0].text
    }
}

