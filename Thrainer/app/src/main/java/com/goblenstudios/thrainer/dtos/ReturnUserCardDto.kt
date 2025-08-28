package com.goblenstudios.thrainer.dtos

import java.time.LocalDateTime


data class ReturnUserCardDto (
    val id:Long,
    val score: Integer,
    val firstStudyDate: LocalDateTime,
    val lastStudyDate: LocalDateTime,
    val userId: Long,
    val userName: String,
    val cardId: Long,
    val question: String,
    val answer: String,
    val deckId: Long,
    val deckName: String,

)