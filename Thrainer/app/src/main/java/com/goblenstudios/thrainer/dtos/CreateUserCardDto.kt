package com.goblenstudios.thrainer.dtos

import java.time.LocalDateTime

data class CreateUserCardDto(
    val userId: Long,
    val cardId: Long,
    val score: Integer,
    val firstStudyDate: LocalDateTime,
    val lastStudyDate: LocalDateTime,
)