package com.example

import com.example.data.BookContent
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun chapters_areLoadedCorrectly() {
        val chapters = BookContent.chapters
        assertTrue("Chapters list should not be empty", chapters.isNotEmpty())
        assertEquals(13, chapters.size) // Intro + 12 chapters
        
        val kamalat = BookContent.kamalatManazil
        assertEquals("There should be 21 Kamalat stations", 21, kamalat.size)
        
        val makroohat = BookContent.makroohat
        assertEquals("There should be 36 Makroohat items", 36, makroohat.size)
        
        val duas = BookContent.bookDuas
        assertTrue("Duas should contain Munajat Taibeen and Muhibeen", duas.size >= 4)
    }
}
