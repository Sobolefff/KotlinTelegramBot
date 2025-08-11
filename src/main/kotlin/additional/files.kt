package org.example.additional

import java.io.File
import java.nio.charset.StandardCharsets

const val DICTIONARY_FILE_PATH = "words.txt"
const val PERCENT_SCALE = 100
const val MINIMAL_CORRECT_ANSWERS_COUNT = 3
const val ANSWER_OPTIONS_COUNT = 4

data class Word(
    val original: String,
    val translate: String,
    var correctAnswersCount: Int = 0,
)

fun main() {

    val dictionary = loadDictionary(DICTIONARY_FILE_PATH)

    while (true) {
        println("\nМеню:")
        println(
            """
            ---------------
            1 - Учить слова
            2 - Статистика
            0 - Выход
            ---------------
        """.trimIndent()
        )
        print("\nВведите номер пункта меню: ")
        when (readlnOrNull()?.toInt()) {
            1 -> goLearn(dictionary)
            2 -> getStatistics(dictionary)
            0 -> return
            else -> println("Неверный пункт меню, введите 1, 2 или 0\n")
        }
    }

}

fun loadDictionary(path: String): List<Word> {

    val result = mutableListOf<Word>()

    File(path).bufferedReader().useLines { lines ->
        lines.forEach { line ->
            val parts = line.split("|")
            result.add(
                Word(
                    original = parts[0],
                    translate = parts[1],
                    correctAnswersCount = parts.getOrNull(2)?.toInt() ?: 0,
                )
            )
        }
    }

    return result

}

fun getStatistics(dictionary: List<Word>) {

    println("Вы выбрали пункт \"статистика\"")

    val learnedWordsCount = dictionary.filter {
        it.correctAnswersCount >= MINIMAL_CORRECT_ANSWERS_COUNT
    }.size

    val totalWords = dictionary.size

    return if (dictionary.isNotEmpty()) {
        val percentLearnedWords = (learnedWordsCount.toDouble() / dictionary.size * PERCENT_SCALE).toInt()
        println("Выучено $learnedWordsCount из $totalWords слов | $percentLearnedWords%\n")
    } else println("Словарь пустой\n")

}

fun goLearn(dictionary: List<Word>) {

    while (true) {

        val notLearnedList = dictionary.filter { it.correctAnswersCount < MINIMAL_CORRECT_ANSWERS_COUNT }
        if (notLearnedList.isEmpty()) {
            println("Все слова в словаре выучены")
            return
        }
        val questionWords = notLearnedList.shuffled().take(ANSWER_OPTIONS_COUNT)
        val correctAnswer = questionWords.random()
        val correctAnswerIndex = questionWords.indexOf(correctAnswer)
        println("\n${correctAnswer.original}: ")

        for (i in questionWords.indices) {
            println(" ${i + 1} - ${questionWords[i].translate}")
        }
        println(
            """
            -------------
             0 - Меню
        """.trimIndent()
        )

        print("\nВведите номер ответа: ")
        when (val userAnswerInput = readln().toIntOrNull()) {
            0 -> return // выход в меню

            in 1..ANSWER_OPTIONS_COUNT -> {
                if (userAnswerInput != null) {
                    if (userAnswerInput - 1 == correctAnswerIndex) {
                        println("Верно!")
                        correctAnswer.correctAnswersCount++
                        saveDictionary(DICTIONARY_FILE_PATH, dictionary)
                    } else {
                        println("Неправильно! ${correctAnswer.original} это ${correctAnswer.translate}")
                    }
                }
            }

            else -> {
                println("Неправильный ввод! Введите число от 1 до $ANSWER_OPTIONS_COUNT или 0 для выхода в меню")
            }
        }

    }

}

fun saveDictionary(path: String, dictionary: List<Word>) {
    File(path).outputStream().buffered().use { output ->
        for (word in dictionary) {
            val line = "${word.original}|${word.translate}|${word.correctAnswersCount}\n"
            output.write(line.toByteArray(StandardCharsets.UTF_8))
        }
    }
}