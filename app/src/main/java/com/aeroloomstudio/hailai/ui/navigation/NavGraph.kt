package com.aeroloomstudio.hailai.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aeroloomstudio.hailai.ui.screens.about.AboutScreen
import com.aeroloomstudio.hailai.ui.screens.home.HomeScreen
import com.aeroloomstudio.hailai.ui.screens.home.HomeViewModel
import com.aeroloomstudio.hailai.ui.screens.booking.BookingConfirmationScreen
import com.aeroloomstudio.hailai.ui.screens.location.LocationMapScreen
import com.aeroloomstudio.hailai.ui.screens.mybookings.MyBookingsScreen
import com.aeroloomstudio.hailai.ui.screens.privacy.PrivacyScreen
import com.aeroloomstudio.hailai.ui.screens.privacy.PrivacyPolicyScreen
import com.aeroloomstudio.hailai.ui.screens.privacy.TermsOfServiceScreen
import com.aeroloomstudio.hailai.ui.screens.privacy.LicensesScreen
import com.aeroloomstudio.hailai.ui.screens.settings.SettingsScreen
import com.aeroloomstudio.hailai.ui.screens.trace.AgentTraceScreen

object Routes {
    const val SPLASH      = "splash"
    const val HOME        = "home"
    const val BOOKING     = "booking/{bookingId}"
    const val MY_BOOKINGS = "my_bookings"
    const val AGENT_TRACE = "agent_trace"
    const val SETTINGS    = "settings"
    const val ABOUT       = "about"
    const val PRIVACY     = "privacy"
    const val PRIVACY_POLICY = "privacy_policy"
    const val TERMS       = "terms"
    const val LICENSES    = "licenses"
    const val LOCATION    = "location"

    fun booking(bookingId: String) = "booking/$bookingId"
}

// ── Shared transition durations ───────────────────────────────────────────────
private const val SLIDE_MS = 340
private const val FADE_MS  = 200

// ── Enter: new screen slides in from the RIGHT ───────────────────────────────
private val enterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(SLIDE_MS),
    ) + fadeIn(animationSpec = tween(FADE_MS))
}

// ── Exit: current screen slides out to the LEFT ──────────────────────────────
private val exitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(SLIDE_MS),
    ) + fadeOut(animationSpec = tween(FADE_MS))
}

// ── Pop-enter: destination slides in from the LEFT (going back) ───────────────
private val popEnterTransition: AnimatedContentTransitionScope<*>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(SLIDE_MS),
    ) + fadeIn(animationSpec = tween(FADE_MS))
}

// ── Pop-exit: current screen slides out to the RIGHT (going back) ─────────────
private val popExitTransition: AnimatedContentTransitionScope<*>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(SLIDE_MS),
    ) + fadeOut(animationSpec = tween(FADE_MS))
}

@Composable
fun HailNavGraph(
    viewModel: HomeViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition  = enterTransition,
        exitTransition   = exitTransition,
        popEnterTransition  = popEnterTransition,
        popExitTransition   = popExitTransition,
    ) {
        composable(Routes.SPLASH) {
            com.aeroloomstudio.hailai.ui.screens.splash.SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToBooking = { bookingId ->
                    navController.navigate(Routes.booking(bookingId))
                },
                onNavigateToTrace = {
                    navController.navigate(Routes.AGENT_TRACE)
                },
                onNavigateToBookings = {
                    navController.navigate(Routes.MY_BOOKINGS)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToAbout = {
                    navController.navigate(Routes.ABOUT)
                },
            )
        }

        composable(Routes.BOOKING) { backStackEntry ->
            val bookingId  = backStackEntry.arguments?.getString("bookingId")
            val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
            val booking = allBookings.find { it.bookingId == bookingId }
                ?: viewModel.currentBooking.collectAsStateWithLifecycle().value

            BookingConfirmationScreen(
                booking     = booking,
                onBackClick = { navController.popBackStack() },
                onTraceClick = { navController.navigate(Routes.AGENT_TRACE) },
            )
        }

        composable(Routes.MY_BOOKINGS) {
            val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()

            MyBookingsScreen(
                bookings    = allBookings,
                onBackClick = { navController.popBackStack() },
                onBookingClick = { booking ->
                    navController.navigate(Routes.booking(booking.bookingId))
                },
            )
        }

        composable(Routes.AGENT_TRACE) {
            val steps   by viewModel.agentSteps.collectAsStateWithLifecycle()
            val booking by viewModel.currentBooking.collectAsStateWithLifecycle()

            AgentTraceScreen(
                steps     = steps,
                bookingId = booking?.bookingId,
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onAboutClick = { navController.navigate(Routes.ABOUT) },
                onPrivacyClick = { navController.navigate(Routes.PRIVACY) },
                onTermsClick = { navController.navigate(Routes.TERMS) },
                onLocationClick = { navController.navigate(Routes.LOCATION) },
            )
        }

        composable(Routes.ABOUT) {
            AboutScreen(
                onBackClick = { navController.popBackStack() },
                onTermsClick = { navController.navigate(Routes.TERMS) },
                onPrivacyClick = { navController.navigate(Routes.PRIVACY_POLICY) },
                onLicensesClick = { navController.navigate(Routes.LICENSES) },
            )
        }

        composable(Routes.PRIVACY) {
            PrivacyScreen(
                onBackClick = { navController.popBackStack() },
                onPrivacyPolicyClick = { navController.navigate(Routes.PRIVACY_POLICY) },
                onTermsClick = { navController.navigate(Routes.TERMS) },
            )
        }

        composable(Routes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Routes.TERMS) {
            TermsOfServiceScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Routes.LICENSES) {
            LicensesScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(Routes.LOCATION) {
            LocationMapScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
