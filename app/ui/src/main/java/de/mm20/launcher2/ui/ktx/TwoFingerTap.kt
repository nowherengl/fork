package de.mm20.launcher2.ui.ktx

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Ultra-responsive, zero-latency two-finger tap detector.
 * Passes single-finger touches directly to scroll/click without consuming events,
 * preventing any frame drops or gesture lag.
 */
fun Modifier.onTwoFingerTap(onTap: () -> Unit): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val firstDown = awaitPointerEvent(PointerEventPass.Initial)
            if (firstDown.changes.any { it.changedToDown() }) {
                val secondDown = withTimeoutOrNull(150) {
                    var event = firstDown
                    while (event.changes.count { it.pressed } < 2) {
                        event = awaitPointerEvent(PointerEventPass.Initial)
                    }
                    event
                }
                if (secondDown != null && secondDown.changes.count { it.pressed } == 2) {
                    val released = withTimeoutOrNull(350) {
                        var event = awaitPointerEvent(PointerEventPass.Initial)
                        while (event.changes.any { it.pressed }) {
                            event = awaitPointerEvent(PointerEventPass.Initial)
                        }
                        true
                    }
                    if (released == true) {
                        onTap()
                    }
                }
            }
        }
    }
}
