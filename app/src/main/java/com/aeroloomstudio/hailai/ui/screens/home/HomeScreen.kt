package com.aeroloomstudio.hailai.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aeroloomstudio.hailai.data.model.*
import com.aeroloomstudio.hailai.ui.components.*
import com.aeroloomstudio.hailai.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToTrace: (String) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val hasFirstResponse by viewModel.hasFirstResponse.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var showBottomSheet by remember { mutableStateOf(false) }

    // Booking flow state
    var bookingProvider by remember { mutableStateOf<Provider?>(null) }
    var bookedProviderIds by remember { mutableStateOf(setOf<String>()) }

    // Auto-scroll to bottom whenever messages list grows
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    // Gradient logic
    val gradientState = when {
        hasFirstResponse -> GradientState.DONE
        isProcessing     -> GradientState.THINKING
        else             -> GradientState.IDLE
    }

    GradientBackground(
        state = gradientState,
        modifier = modifier,
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = SurfaceWhite,
                ) {
                    SideDrawerContent(
                        recentChats = conversations.map { DrawerItem(title = it) },
                        onChatClick = { index ->
                            viewModel.loadConversation(index)
                            scope.launch { drawerState.close() }
                        },
                        onNewChat = {
                            viewModel.clearChat()
                            scope.launch { drawerState.close() }
                        },
                        onBookingsClick = {
                            onNavigateToBookings()
                            scope.launch { drawerState.close() }
                        },
                        onSettingsClick = {
                            onNavigateToSettings()
                            scope.launch { drawerState.close() }
                        },
                    )
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .imePadding(),
            ) {
                // === CONTENT LAYER ===
                if (messages.isEmpty()) {
                    EmptyStateContent(
                        onChipClick = { chip ->
                            viewModel.sendQuickAction(chip.category, chip.label)
                        },
                        onSeeMoreClick = { showBottomSheet = true },
                        modifier = Modifier.fillMaxSize(),
                        topPadding = 68.dp,
                        bottomPadding = 100.dp,
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 72.dp,
                            bottom = 130.dp,
                        ),
                    ) {
                        items(
                            items = messages,
                            key = { it.id },
                        ) { message ->
                            ChatBubble(
                                message = message,
                                onProviderClick = { },
                                onBookingClick = { booking ->
                                    onNavigateToBooking(booking.bookingId)
                                },
                                onBookNowClick = { provider ->
                                    if (provider.id !in bookedProviderIds) {
                                        bookingProvider = provider
                                    }
                                },
                                bookedProviderIds = bookedProviderIds,
                            )
                        }
                    }
                }

                // === FADE-EDGE SCRIMS — only after first AI response ===
                if (hasFirstResponse && messages.isNotEmpty()) {
                    // Top scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.92f),
                                        Color.White.copy(alpha = 0.60f),
                                        Color.Transparent,
                                    ),
                                )
                            )
                    )

                    // Bottom scrim
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.60f),
                                        Color.White.copy(alpha = 0.92f),
                                    ),
                                )
                            )
                    )
                }

                // === TOP BAR ===
                FloatingTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onNewChatClick = { viewModel.clearChat() },
                    onModelClick = { showBottomSheet = true },
                    onNavigateToBookings = onNavigateToBookings,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToAbout = onNavigateToAbout,
                    modifier = Modifier.align(Alignment.TopCenter),
                )

                // === BOTTOM INPUT BAR ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding(),
                ) {
                    FloatingInputBar(
                        value = inputText,
                        onValueChange = { viewModel.updateInput(it) },
                        onSend = { viewModel.sendMessage() },
                        onPlusClick = { showBottomSheet = true },
                        isProcessing = isProcessing,
                    )
                }
            }
        }
    }

    // === SERVICE BOTTOM SHEET ===
    if (showBottomSheet) {
        HailBottomSheet(
            onDismiss = { showBottomSheet = false },
            onServiceClick = { chip ->
                viewModel.sendQuickAction(chip.category, chip.label)
            },
            onSettingsClick = onNavigateToSettings,
            onBookingsClick = onNavigateToBookings,
            onHistoryClick = onNavigateToBookings,
            onTraceClick = { onNavigateToTrace("current") },
        )
    }

    // === BOOKING FLOW BOTTOM SHEET ===
    bookingProvider?.let { provider ->
        BookingFlowSheet(
            provider = provider,
            onDismiss = { bookingProvider = null },
            onBookingConfirmed = { confirmedProvider, selectedSlot ->
                bookedProviderIds = bookedProviderIds + confirmedProvider.id
                bookingProvider = null
                viewModel.bookProvider(confirmedProvider, selectedSlot)
            },
        )
    }
}

@Composable
private fun EmptyStateContent(
    onChipClick: (ServiceChip) -> Unit,
    onSeeMoreClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    topPadding: androidx.compose.ui.unit.Dp = 68.dp,
    bottomPadding: androidx.compose.ui.unit.Dp = 100.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topPadding, bottom = bottomPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.3f))

        Text(
            text = "Kya chahiye aaj?",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Ask me to find any service provider near you",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        QuickActionChips(
            onChipClick = onChipClick,
            onSeeMoreClick = onSeeMoreClick,
        )

        Spacer(Modifier.weight(0.7f))
    }
}
