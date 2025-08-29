package com.goblenstudios.thrainer.dtos

data class ReturnCardDto (
    val idCard: Long,
    val question: String,
    val answer: String,
    val deckId: Long,
    val deckName: String,
)