package com.portalsoup.saas.dto.gpx

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val description: String,
    val distance: Float,
    val elevationGain: Float,
    val name: String,
    val timestamp: Long,
    val createdAt: String,
    val estimatedMovingTime: Int,
    val segments: List<SummarySegment>
)

@Serializable
data class SummarySegment(
    val name: String,
    val distance: Float,
    val averageGrade: Float,
    val maximumGrade: Float,
    val evelationLow: Float,
    val elevationHigh: Float,
    val startLatLng: LatLng,
    val endLatLng: LatLng,
)

@Serializable
data class LatLng(val lat: Float, val lng: Float)