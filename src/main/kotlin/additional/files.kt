package org.example.additional

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {

    val wordsFile = File("words.txt")
    val dictionary = mutableListOf<Word>()

    for (i in wordsFile.readLines()) {
        val line = i.split("|")
        val word = Word(
            original = line[0],
            translate = line[1],
            correctAnswersCount = line.getOrNull(2)?.toInt() ?: 0,
        )
        dictionary.add(word)
    }

    println(dictionary)

}