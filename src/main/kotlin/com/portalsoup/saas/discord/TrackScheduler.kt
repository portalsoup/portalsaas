package com.portalsoup.saas.discord

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

/**
 * This track scheduler allows the Discord bot to stream audio to voice channels.
 *
 * Only basic play functionality exists yet, once a track is started, it can't be stopped or paused
 */
class TrackScheduler(private val player: AudioPlayer): AudioLoadResultHandler {
    override fun trackLoaded(track: AudioTrack?) {
        player.playTrack(track)
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        TODO("Not yet implemented")
    }

    override fun noMatches() {
        TODO("Not yet implemented")
    }

    override fun loadFailed(exception: FriendlyException?) {
        TODO("Not yet implemented")
    }
}