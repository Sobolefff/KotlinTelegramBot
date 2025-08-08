package org.example.additional

import java.io.File

const val DICTIONARY_FILE_PATH = "words.txt"
const val PERCENT_SCALE = 100
const val MINIMAL_CORRECT_ANSWERS_COUNT = 3

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
        val questionWords = notLearnedList.shuffled().take(4)
        val correctAnswer = questionWords.random()
        val correctAnswerIndex = questionWords.indexOf(correctAnswer)
        println("\n${correctAnswer.original}:")

        for (i in questionWords.indices) {
            println(" ${i + 1} - ${questionWords[i].translate}")
        }
        println("""
            -------------
             0 - Меню
        """.trimIndent())

        print("\nВведите номер ответа: ")
        val userAnswerInput = readln().toIntOrNull()

        if (userAnswerInput == 0) return

        if (userAnswerInput !in 1..questionWords.size || userAnswerInput == null) {
            println("Введите число от 1 до ${questionWords.size}")
            continue
        }

        if (userAnswerInput - 1 == correctAnswerIndex) {
            println("Верно!")
            correctAnswer.correctAnswersCount++
            saveDictionary(DICTIONARY_FILE_PATH, dictionary)
        } else {
            println("Неправильно! ${correctAnswer.original} это ${correctAnswer.translate}")
        }

    }

}

fun saveDictionary(path: String, dictionary: List<Word>) {
    File(path).writeText(
        dictionary.joinToString("\n") {"${it.original}|${it.translate}|${it.correctAnswersCount}"}
    )
}