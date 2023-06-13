package com.fhz.music_lib_player

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
        var s = "aad.MP3"
        val substring = s.substring(0, s.lastIndexOf("."))
        println(substring)
    }
}