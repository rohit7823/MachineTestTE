package com.rohit.machinetestte.presentation.ui.composables

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohit.machinetestte.presentation.ui.theme.Secondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


private val alphas = listOf(
    0 to 1f,
    1 to 0.9f,
    2 to 0.8f,
    3 to 0.7f,
    4 to 0.6f,
    5 to 0.5f,
    6 to 0.4f,
    7 to 0.3f,
    8 to 0.2f,
    9 to 0.1f,
    10 to 0.05f
)

private data class Dot(
    val color: Color, val dot: String
) {
    companion object {
        val EMPTY = Dot(color = Color.Transparent, dot = "")
    }
}

fun <T> animationSpec() = tween<T>(
    durationMillis = 550,
    easing = LinearOutSlowInEasing
)

@OptIn(ExperimentalAnimationApi::class)
fun upToBottom() = (slideInVertically(
    initialOffsetY = { -it },
    animationSpec = animationSpec()
) + fadeIn(animationSpec = animationSpec())) with (
    slideOutVertically(
        targetOffsetY = { it },
        animationSpec = animationSpec()
    ) + fadeOut(animationSpec())
)


@OptIn(ExperimentalAnimationApi::class)
fun bottomToUp() = (slideInVertically(
    initialOffsetY = { it },
    animationSpec = animationSpec()
) + fadeIn(animationSpec())) with (
    slideOutVertically(
        targetOffsetY = { -it },
        animationSpec = animationSpec()
    ) + fadeOut(animationSpec = animationSpec())
)
private fun CoroutineScope.loading(
    load: Boolean = false,
    number: Int,
    char: String = "•",
    charColor: Color,
    speed: Duration = 1.seconds,
    backword: Boolean = true,
    print: (Dot, Int) -> Unit
) {
    assert(char != "" && number != 0 && number <= alphas.last().first) {
        throw Exception("Dots can be ${alphas.last().first} at max and cannot be 0")
    }
    launch {
        withContext(Dispatchers.IO) {
            while (load) {
                var upper = 0
                var lower = number
                while (upper < number) {
                    print(
                        Dot(dot = char, color = charColor.copy(alpha = alphas[upper].second)), upper
                    )
                    upper++
                    delay(speed)
                }
                if (backword) {
                    while (lower != 0) {
                        lower--
                        print(Dot.EMPTY, lower)
                        delay(speed)
                    }
                } else {
                    print(Dot.EMPTY, number)
                }
            }
            cancel()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingDotsView(
    dotsNumber: Int,
    coroutineScope: CoroutineScope,
    char: String = "•",
    charColor: Color = Secondary,
    speed: Duration = 600.milliseconds,
    back: Boolean = true,
    loading: Boolean,
    onPositioned: ((LayoutCoordinates) -> Unit)? = null
) {
    val dots = remember {
        val list = MutableStateFlow(List(dotsNumber) { Dot.EMPTY })
        coroutineScope.loading(
            load = loading,
            speed = speed,
            backword = back,
            char = char,
            number = dotsNumber,
            charColor = charColor
        ) { item, idx ->
            list.update {
                val tmp = it.toMutableList()
                try {
                    if (item.dot.isNotEmpty()) {
                        tmp[idx] = item
                    } else if (idx == dotsNumber) {
                        tmp.fill(Dot.EMPTY)
                    } else {
                        tmp[idx] = Dot.EMPTY
                    }
                } catch (ex: Exception) {
                    Log.d("TESTING", "${ex.message}")
                }
                tmp.toList()
            }
        }

        list
    }

    dots.collectAsStateWithLifecycle(context = coroutineScope.coroutineContext).value.let {
        LazyRow(
            modifier = Modifier
                .animateContentSize(animationSpec())
                .onGloballyPositioned { onPositioned?.invoke(it) },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top,
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            items(it) { dot ->
                AnimatedContent(
                    targetState = dot.dot != "",
                    contentAlignment = Alignment.TopCenter,
                    transitionSpec = {
                        if (targetState && !initialState) {
                            upToBottom()
                        } else {
                            bottomToUp()

                        }
                    },
                    label = "",

                    ) {
                    if (it) {
                        Text(
                            dot.dot,
                            modifier = Modifier,
                            style = MaterialTheme.typography.displaySmall.copy(color = dot.color)
                        )
                    }
                }
            }
        }
    }

}
