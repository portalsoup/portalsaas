package com.portalsoup.saas.discord

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import discord4j.voice.AudioProvider
import java.nio.ByteBuffer

/**
 * A Discord provider to allow LavaPlayer to send audio frames to Discord voice channels
 */
class LavaPlayerAudioProvider(private val player: AudioPlayer) : AudioProvider(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())) {

    private val frame: MutableAudioFrame by lazy {
        val it = MutableAudioFrame()
        it.setBuffer(buffer)
        it
    }
    override fun provide(): Boolean {
        val didProvide = player.provide(frame)
        if (didProvide) {
            buffer.flip()
        }
        return didProvide
    }
}