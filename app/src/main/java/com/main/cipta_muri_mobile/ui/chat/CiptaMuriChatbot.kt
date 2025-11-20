package com.main.cipta_muri_mobile.ui.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.main.cipta_muri_mobile.data.chat.ChatMessage
import com.main.cipta_muri_mobile.data.chat.Role
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CiptaMuriChatHost(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 96.dp, start = 20.dp)
        ) {
            FloatingChatButton(
                isOpen = uiState.isSheetOpen,
                isBusy = uiState.isSending,
                onClick = {
                    viewModel.toggleSheet()
                }
            )
        }

        if (uiState.isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.toggleSheet(false)
                    }
                },
                dragHandle = null,
                containerColor = Color.White.copy(alpha = 0.98f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                ChatPanel(
                    messages = uiState.messages,
                    input = uiState.input,
                    isSending = uiState.isSending,
                    errorMessage = uiState.errorMessage,
                    onInputChange = {
                        viewModel.updateInput(it)
                        viewModel.dismissError()
                    },
                    onSend = viewModel::sendMessage,
                    onClose = { viewModel.toggleSheet(false) },
                    onDismissError = viewModel::dismissError,
                )
            }
        }
    }
}

@Composable
private fun FloatingChatButton(
    isOpen: Boolean,
    isBusy: Boolean,
    onClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "idle-glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.9f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-alpha"
    )
    val busyPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "busy-pulse"
    )

    val buttonColor = Brush.linearGradient(
        listOf(Color(0xFF059669), Color(0xFF34D399))
    )
    val containerColor = if (isBusy) Color(0xFF34D399) else Color(0xFF10B981)

    Box(
        modifier = Modifier
            .size(76.dp)
            .drawBehind {
                drawCircle(
                    color = Color(0x6610B981),
                    radius = size.minDimension / 2,
                    alpha = glowAlpha
                )
            }
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(if (isBusy) 64.dp * busyPulse else 64.dp),
            shape = CircleShape,
            color = Color.Transparent,
            shadowElevation = 12.dp
        ) {
            androidx.compose.material3.FloatingActionButton(
                onClick = onClick,
                containerColor = containerColor,
                modifier = Modifier.fillMaxSize(),
            ) {
                Icon(
                    imageVector = if (isOpen) Icons.Rounded.Close else Icons.Rounded.SmartToy,
                    tint = Color.White,
                    contentDescription = if (isOpen) "Tutup chat" else "Buka chat AI"
                )
            }
        }
    }
}

@Composable
private fun ChatPanel(
    messages: List<ChatMessage>,
    input: String,
    isSending: Boolean,
    errorMessage: String?,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onClose: () -> Unit,
    onDismissError: () -> Unit,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 560.dp)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        ChatHeader(
            isSending = isSending,
            onClose = onClose,
        )
        AnimatedVisibility(visible = !errorMessage.isNullOrEmpty()) {
            if (!errorMessage.isNullOrEmpty()) {
                ErrorBanner(message = errorMessage, onDismiss = onDismissError)
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        ) {
            items(items = messages, key = { it.id }) { message ->
                MessageBubble(message = message)
            }
            item {
                if (isSending) {
                    TypingIndicator()
                }
            }
        }
        InputComposer(
            value = input,
            enabled = !isSending,
            onValueChange = onInputChange,
            onSend = onSend,
        )
    }
}

@Composable
private fun ChatHeader(
    isSending: Boolean,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "CiptaMuri AI",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF065F46),
            )
            val subtitle = if (isSending) "Sedang mengetik..." else "Siap membantu kamu ✨"
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF047857),
            )
        }
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Tutup panel",
                tint = Color(0xFF047857)
            )
        }
    }
}

@Composable
private fun ErrorBanner(
    message: String,
    onDismiss: () -> Unit,
) {
    Surface(
        color = Color(0xFFFFE4E6),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = message,
                fontSize = 13.sp,
                color = Color(0xFF9F1239),
                modifier = Modifier.weight(1f)
            )
            TextButtonLink(
                label = "Tutup",
                onClick = onDismiss
            )
        }
    }
}

@Composable
private fun TextButtonLink(
    label: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Text(
        text = label,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFB91C1C),
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
    )
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == Role.USER
    val gradient = Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF34D399)))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp,
                bottomStart = if (isUser) 28.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 28.dp
            ),
            tonalElevation = if (isUser) 0.dp else 2.dp,
            color = if (isUser) Color.Transparent else Color.White,
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(
                    brush = if (isUser) gradient else Brush.linearGradient(
                        listOf(Color.White, Color.White)
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .border(
                    width = if (isUser) 0.dp else 1.dp,
                    color = if (isUser) Color.Transparent else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(28.dp)
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                val paragraphs = message.content.split("\n\n")
                paragraphs.forEachIndexed { index, paragraph ->
                    Text(
                        text = paragraph.trim(),
                        color = if (isUser) Color.White else Color(0xFF065F46),
                        fontSize = 15.sp,
                        modifier = if (index > 0) Modifier.padding(top = 8.dp) else Modifier
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.size(6.dp))
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Dot()
                Dot(delay = 150)
                Dot(delay = 300)
            }
        }
    }
}

@Composable
private fun Dot(delay: Int = 0) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot-$delay")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                delayMillis = delay,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot-alpha-$delay"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF10B981).copy(alpha = alpha))
    )
}

@Composable
private fun InputComposer(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    val sendEnabled = value.isNotBlank() && enabled
    Surface(
        shape = RoundedCornerShape(38.dp),
        color = Color.White,
        shadowElevation = 6.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Tulis pesan...") },
                maxLines = 4,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (sendEnabled) {
                            onSend()
                        }
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            IconButton(
                onClick = onSend,
                enabled = sendEnabled
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Kirim pesan",
                    tint = if (sendEnabled) Color(0xFF10B981) else Color(0xFF9CA3AF)
                )
            }
        }
    }
}
