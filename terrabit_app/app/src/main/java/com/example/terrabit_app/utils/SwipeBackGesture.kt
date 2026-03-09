package com.example.terrabit_app.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun SwipeBackContainer(
    onSwipeBack: () -> Unit,
    backgroundContent: @Composable () -> Unit,
    edgeWidth: Float = 80f,
    threshold: Float = 100f,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetPx = remember { mutableFloatStateOf(0f) }

    val state = remember {
        object {
            var startX = 0f
            var startY = 0f
            var isDragging = false
            var isAnimating = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val offset = offsetPx.floatValue
                    alpha = (offset / 300f).coerceIn(0f, 1f)
                    translationX = -120f + (offset * 0.35f)
                }
        ) {
            backgroundContent()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetPx.floatValue
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                            val pointer = event.changes.firstOrNull() ?: continue

                            when (event.type) {
                                PointerEventType.Press -> {
                                    state.startX = pointer.position.x
                                    state.startY = pointer.position.y
                                    state.isDragging = false
                                }
                                PointerEventType.Move -> {
                                    if (state.startX > edgeWidth || state.isAnimating) continue
                                    val dx = pointer.position.x - state.startX
                                    val dy = pointer.position.y - state.startY

                                    if (!state.isDragging && abs(dx) > abs(dy) && dx > 10f) {
                                        state.isDragging = true
                                    }

                                    if (state.isDragging) {
                                        offsetPx.floatValue = dx.coerceAtLeast(0f)
                                        pointer.consume()
                                    }
                                }
                                PointerEventType.Release -> {
                                    if (state.isDragging) {
                                        val captured = offsetPx.floatValue
                                        state.isAnimating = true
                                        state.isDragging = false
                                        if (captured >= threshold) {
                                            scope.launch {
                                                val anim = Animatable(captured)
                                                anim.animateTo(
                                                    targetValue = size.width.toFloat(),
                                                    animationSpec = tween(250)
                                                ) {
                                                    offsetPx.floatValue = value
                                                }
                                                onSwipeBack()
                                                offsetPx.floatValue = 0f
                                                state.isAnimating = false
                                            }
                                        } else {
                                            scope.launch {
                                                val anim = Animatable(captured)
                                                anim.animateTo(
                                                    targetValue = 0f,
                                                    animationSpec = tween(200)
                                                ) {
                                                    offsetPx.floatValue = value
                                                }
                                                state.isAnimating = false
                                            }
                                        }
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }
                }
        ) {
            content()
        }
    }
}