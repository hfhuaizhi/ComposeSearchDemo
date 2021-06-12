package com.hfhuaizhi.composedemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfhuaizhi.composedemo.app.SearchDemoApp
import com.hfhuaizhi.composedemo.ui.theme.ComposeSearchDemoTheme
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSearchDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainPage()
                }
            }
        }
    }
}

// borderColor 8f8f8f
@Composable
fun MainPage() {
    var text by remember { mutableStateOf("") }
    var open by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        if (open) Color.Black else Color(0xFF8f8f8f),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
    )
    val textColor by animateColorAsState(
        if (open) Color(0x00000000) else Color.Black,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
    )
    val hintColor by animateColorAsState(
        if (open) Color.Gray else Color(0x00000000),
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow),
    )
    val paddingHeight by animateDpAsState(
        if (open) 60.dp else 30.dp,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )
    val inputConor by animateDpAsState(
        if (open) 30.dp else 10.dp,
        animationSpec = spring(stiffness = Spring.StiffnessVeryLow)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(140.dp))
        Text(text = "Compose", fontWeight = FontWeight.Bold, fontSize = 35.sp, color = textColor)
        Spacer(modifier = Modifier.size(paddingHeight))
        Box(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp)
                .fillMaxWidth()
                .height(60.dp)
                .border(BorderStroke(2.dp, borderColor), shape = RoundedCornerShape(inputConor))
        ) {
            TextField(
                value = text,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    Toast.makeText(SearchDemoApp.context, "search:${text}", Toast.LENGTH_SHORT)
                        .show()
                }),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color(0x00000000),
                    textColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color(0x00000000),
                    unfocusedIndicatorColor = Color(0x00000000)
                ),
                onValueChange = {
                    text = it
                },
                placeholder = {
                    Text("Please input", color = hintColor)
                },
                interactionSource = remember {
                    SearchInteractionSourceImpl {
                        if (it is FocusInteraction.Focus) {
                            open = true
                        } else if (it is FocusInteraction.Unfocus) {
                            open = false
                            text = ""
                        }
                    }
                },
                modifier = Modifier
                    .padding(top = 2.dp)

            )
        }
    }
}

@Stable
private class SearchInteractionSourceImpl(val intercept: ((interaction: Interaction) -> Unit)) :
    MutableInteractionSource {
    override val interactions = MutableSharedFlow<Interaction>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override suspend fun emit(interaction: Interaction) {
        interactions.emit(interaction)
        intercept.invoke(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        return interactions.tryEmit(interaction)
    }
}