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

class DictionaryRepository(private val path: String) {

    fun load(): MutableList<Word> {
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

    fun save(dictionary: List<Word>) {
        File(path).outputStream().buffered().use { output ->
            for (word in dictionary) {
                val line = "${word.original}|${word.translate}|${word.correctAnswersCount}\n"
                output.write(line.toByteArray(StandardCharsets.UTF_8))
            }
        }
    }

}

class Statistic(private val dictionary: List<Word>) {
    fun printStats() {
        if (dictionary.isEmpty()) {
            println("Словарь пустой")
            return
        }
        val learnedWordsCount = dictionary.count { it.isLearned() }
        val percentLearnedWords = (learnedWordsCount.toDouble() / dictionary.size * PERCENT_SCALE).toInt()
        println("Выучено $learnedWordsCount из ${dictionary.size} слов | $percentLearnedWords%\n")
    }
}

class Quiz(
    private val words: MutableList<Word>,
    private val repository: DictionaryRepository,
) {
    fun startQuiz() {
        while (true) {
            val notLearnedList = words.filter { !it.isLearned() }
            if (notLearnedList.isEmpty()) {
                println("Все слова в словаре выучены")
                return
            }

            val questionWords = notLearnedList.shuffled().take(ANSWER_OPTIONS_COUNT)
            val correctAnswer = questionWords.random()
            val correctAnswerIndex = questionWords.indexOf(correctAnswer)

            println("\n${correctAnswer.original}: ")
            questionWords.forEachIndexed { index, word -> println("\t${index + 1} - ${word.translate}") }
            println(
                """
                ----------------
                    0 - Меню
                """.trimIndent()
            )

            print("\nВведите номер ответа: ")
            when (val userAnswerInput = readln().toIntOrNull()) {
                0 -> return

                in 1..ANSWER_OPTIONS_COUNT -> {
                    if (userAnswerInput != null) {
                        if (userAnswerInput - 1 == correctAnswerIndex) {
                            println("Верно!")
                            correctAnswer.correctAnswersCount++
                            repository.save(words)
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
}

fun Word.isLearned(): Boolean = correctAnswersCount >= MINIMAL_CORRECT_ANSWERS_COUNT

fun main() {
    val repository = DictionaryRepository(DICTIONARY_FILE_PATH)
    val dictionary = repository.load()

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
            1 -> Quiz(dictionary, repository).startQuiz()
            2 -> Statistic(dictionary).printStats()
            0 -> return
            else -> println("Неверный пункт меню, введите 1, 2 или 0\n")
        }
    }

}

