package com.example.automind

import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.automind.ui.record.RecordFragment
import com.example.automind.ui.record.Timer
import com.example.automind.ui.record.WaveformView
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper


@Config(sdk = [Config.OLDEST_SDK])
@RunWith(RobolectricTestRunner::class)
class RecordingTest {

    private lateinit var waveformView: WaveformView

    @Before
    fun setUp() {
        val context = Robolectric.buildActivity(AppCompatActivity::class.java).create().get()
        waveformView = WaveformView(context, null)
    }

    @Test
    fun `when start is called, timer should increase duration`() {
        val listener = mock(Timer.OnTimerTickListener::class.java)
        val timer = Timer(listener)

        timer.start()
        //ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        ShadowLooper.idleMainLooper(2000)
        timer.pause()

        verify(listener, atLeastOnce()).OnTimerTick("00:01")
    }

    @Test
    fun `when pause is called, timer should not tick further`() {
        val listener = mock(Timer.OnTimerTickListener::class.java)
        val timer = Timer(listener)


        // Before starting the timer, no ticks
        val preStartInvocationCount = Mockito.mockingDetails(listener).invocations.size
        assertEquals(0, preStartInvocationCount)
        println("Ticks before starting: $preStartInvocationCount")


        timer.start()
        ShadowLooper.idleMainLooper(2000)

        // Ensure timer ticked at least once before pausing
        val prePauseInvocationCount = Mockito.mockingDetails(listener).invocations.size
        assertTrue(prePauseInvocationCount > preStartInvocationCount)
        println("Ticks before pausing: $prePauseInvocationCount")

        timer.pause()
        ShadowLooper.idleMainLooper(2000)  // Give some additional time

        // Ensure timer didn't tick after pausing
        val postPauseInvocationCount = Mockito.mockingDetails(listener).invocations.size
        assertEquals(prePauseInvocationCount, postPauseInvocationCount)
        println("Ticks after pausing: $postPauseInvocationCount")
    }

    @Test
    fun `when addAmplitude is called, amplitudes should have new value`() {
        // Given
        val amplitudeToAdd = 500f

        // When
        waveformView.addAmplitude(amplitudeToAdd)

        // Then
        val lastAmplitude = waveformView.amplitudes.lastOrNull()
        val lastSpike = waveformView.spikes.lastOrNull()
        assertEquals(lastAmplitude != 0.0f, true)  // Ensure that the amplitude was added
        assertEquals(lastSpike!!.top != 0.0f, true)
        assertEquals(lastSpike!!.bottom != 0.0f, true)
    }

    @Test
    fun `when reset is called, amplitudes should be empty`() {
        // Given
        waveformView.addAmplitude(50f)

        // When
        waveformView.reset()

        // Then
        assertEquals(true, waveformView.amplitudes.isEmpty())  // Ensure that amplitudes list is empty
    }

    @Test
    fun `lottieLoadingAnimation should appear after stopping recording`() {
        val fragment = RecordFragment()

        val activity = Robolectric.buildActivity(FragmentActivity::class.java)
            .create()
            .start()
            .resume()
            .get()

        activity.supportFragmentManager.beginTransaction()
            .add(fragment, null)
            .commitNow()

        // At this point, fragment's `onCreateView` would have been called.
        // Therefore, _binding should have been initialized if following typical fragment patterns.

        fragment.lottieLoadingAnimationOnRecordingStopped()  // Simulate stopping recording and loading animation

        assertTrue(fragment._binding!!.lottieLoadingAnimation.visibility == View.VISIBLE)
        // If lottieLoadingAnimation is a real view, avoid verifying the animation play as it's more of an Espresso test concern
    }

    @After
    fun tearDown() {
        // Ensure resources are cleaned up to prevent issues with subsequent tests
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
}