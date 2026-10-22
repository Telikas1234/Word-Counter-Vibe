package com.example.wordcounter.domain

/**
 * Repository of sample test sentences for on-demand generation.
 */
object SampleSentences {

    val SAMPLES: List<String> = listOf(
        "Labas rytas",
        "Rugsėjo 1 prasidėjo karas. Liūdna",
        "Vilnius yra Lietuvos sostinė jau daugiau nei 700 metų! Ar žinojote tai?",
        "Today is a wonderful day. We counted 42 apples and 3.14 pies!",
        "Kaunas yra antras pagal dydį miestas, įkurtas 1361 metais.",
        "Programavimas su Kotlin ir Android Studio yra labai smagus užsiėmimas!"
    )

    private var currentIndex = 0
    /**
     * Cycles through the sample sentences sequentially and thread-safely.
     * Keeps currentIndex strictly bounded within [0, SAMPLES.size) to avoid integer overflow and negative index crash.
     */
    @Synchronized
    fun nextSample(): String {
        val sample = SAMPLES[currentIndex]
        currentIndex = (currentIndex + 1) % SAMPLES.size
        return sample
    }
}