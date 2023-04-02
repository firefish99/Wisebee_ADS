package com.wisebee.autodoor.control

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

object WbTheme {
    val selectContainer : Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xff4444ff)
    val selectContent : Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xffffffff)
    val setButtonContainer : Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xffffc000)
    val setButtonContent : Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xff444444)
    private val setButtonContainerPressed : Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xff7f6000)
    private val setButtonContentPressed : Color
        @Composable
        @ReadOnlyComposable
        get() = Color(0xff222222)

    @Composable
    fun getButtonContainer(bPressed : Boolean) : Color {
        return if(bPressed) setButtonContainerPressed else setButtonContainer
    }

    @Composable
    fun getButtonContent(bPressed : Boolean) : Color {
        return if(bPressed) setButtonContentPressed else setButtonContent
    }
}
