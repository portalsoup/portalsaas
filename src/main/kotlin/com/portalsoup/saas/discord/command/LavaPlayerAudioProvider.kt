package com.portalsoup.saas.discord.command

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import discord4j.voice.AudioProvider
import java.nio.ByteBuffer

class LavaPlayerAudioProvider(val player: AudioPlayer) : AudioProvider(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())) {

    val frame by lazy {
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