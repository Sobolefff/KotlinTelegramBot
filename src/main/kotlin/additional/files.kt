package org.example.additional

import java.io.File

const val DICTIONARY_FILE_PATH = "words.txt"

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {

    val dictionary = loadDictionary(DICTIONARY_FILE_PATH)

    while (true) {
        println("Меню:")
        println(
            """
            1 - Учить слова
            2 - Статистика
            0 - Выход
        """.trimIndent()
        )
        when (readlnOrNull()?.toInt()) {
            1 -> println("Вы выбрали пункт \"учить слова\"")
            2 -> println("Вы выбрали пункт \"статистика\"")
            0 -> return
            else -> println("Неверный пункт меню, введите 1, 2 или 0")
        }
    }

}

fun loadDictionary(path: String): List<Word> {

    val wordsFile = File(path)
    val result = mutableListOf<Word>()

    for (i in wordsFile.readLines()) {
        val line = i.split("|")
        val word = Word(
            original = line[0],
            translate = line[1],
            correctAnswersCount = line.getOrNull(2)?.toInt() ?: 0,
        )
        result.add(word)
    }

    return result

}