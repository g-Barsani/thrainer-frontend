package com.goblenstudios.thrainer.dtos

import java.time.LocalDateTime

data class UpdateUserCardDto (
    val userId: Long,
    val cardId: Long,
    val score: Integer,
    val lastStudyDate: LocalDateTime,
)