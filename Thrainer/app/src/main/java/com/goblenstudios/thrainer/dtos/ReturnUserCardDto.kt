package com.goblenstudios.thrainer.dtos

data class ReturnUserCardDto (
    val id:Long,
    val score: Integer,
    val firstStudyDate: String,
    val lastStudyDate: String,
    val userId: Long,
    val userName: String,
    val cardId: Long,
    val question: String,
    val answer: String,
    val deckId: Long,
    val deckName: String,

)