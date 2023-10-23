package com.example.automind

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.automind.ui.record.RecordViewModel
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito.mock

class RecordViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val viewModel = RecordViewModel(mock(Application::class.java))
    private val observer: Observer<String> = mockk(relaxed = true) // Mocked observer for the LiveData

    @Test
    fun `updateOriginalText should post value to originalText LiveData`() {
        // Given
        val testText = "Test Text"

        viewModel.originalText.observeForever(observer) // Attach the mocked observer

        // When
        viewModel.updateOriginalText(testText)

        // Then
        verify { observer.onChanged(testText) }
    }

    @Test
    fun `generatePrompt should return expected string for List type`() {
        // Given
        val type = "List"
        val question = "Hello world!"
        val inputLanguage = "English"
        val outputLanguage = "English"
        val writingStyle = "Regular"

        // Expected string
        val expected = """
Summarize the key words of the following $inputLanguage text in $outputLanguage and using bullet points list with $writingStyle style:\n\n- $question.
"""

        // When
        val result = viewModel.generatePrompt(type, question, inputLanguage, outputLanguage, writingStyle)

        // Then
        assertEquals(expected, result)

    }
}
