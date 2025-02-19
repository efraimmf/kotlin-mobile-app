package com.pgmv.bandify.utils

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getCurrentTime(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
}

fun Long.millisToLocalDate(millis: Long = this): LocalDate {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("GMT")).toLocalDate()
}

fun formatTimeDifference(createdAt: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val createdDateTime = LocalDateTime.parse(createdAt, formatter)


    val now = LocalDateTime.now(ZoneId.systemDefault())


    val duration = Duration.between(createdDateTime, now)
    val seconds = duration.seconds
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()

    return when {
        seconds < 10 -> "Agora"
        seconds < 60 -> "Há $seconds segundos"
        minutes < 2 -> "Há 1 minuto"
        minutes < 60 -> "Há $minutes minutos"
        hours < 24 -> "Há $hours horas"
        hours < 48 -> "Ontem"
        days < 7 -> "Há ${days} dias"
        else -> createdDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}

fun getScreenTitle(route: String): String {
    return when (route) {
        "home" -> "Bandify"
        "login" -> "Login"
        "register" -> "Register"
        "agenda" -> "Agenda"
        "repertorio?event_id={eventId}" -> "Repertório"
        "arquivos" -> "Arquivos"
        "perfil" -> "Perfil"
        "novo_evento" -> "Adicionar Evento"
        "nova_musica" -> "Adicionar Música"
        else -> "Bandify"
    }
}

fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d", minutes, remainingSeconds)
}