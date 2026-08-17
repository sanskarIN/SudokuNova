package com.sanskar.sudokunova.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sanskar.sudokunova.engine.Difficulty
import com.sanskar.sudokunova.ui.about.AboutScreen
import com.sanskar.sudokunova.ui.challenges.ChallengesRoute
import com.sanskar.sudokunova.ui.custom.CustomPuzzleRoute
import com.sanskar.sudokunova.ui.game.GameRoute
import com.sanskar.sudokunova.ui.home.HomeRoute
import com.sanskar.sudokunova.ui.learn.LearnScreen
import com.sanskar.sudokunova.ui.settings.SettingsScreen
import com.sanskar.sudokunova.ui.statistics.StatisticsRoute
import com.sanskar.sudokunova.ui.theme.SudokuNovaTheme

private object Routes {
    const val HOME = "home"
    const val GAME =
        "game/{difficulty}?daily={daily}&resume={resume}&custom={custom}&challengeType={challengeType}&challengeKey={challengeKey}"
    const val CUSTOM = "custom"
    const val CHALLENGES = "challenges"
    const val LEARN = "learn"
    const val STATISTICS = "statistics"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
}

@Composable
fun SudokuNovaApp(
    appViewModel: AppViewModel = viewModel(),
) {
    val settings by appViewModel.settings.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val uriHandler = LocalUriHandler.current

    SudokuNovaTheme(
        appTheme = settings.theme,
        dynamicColor = settings.dynamicColor,
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
        ) {
            composable(Routes.HOME) {
                HomeRoute(
                    onStartGame = { difficulty ->
                        navController.navigate("game/${difficulty.name}?daily=false&resume=false")
                    },
                    onContinue = {
                        navController.navigate("game/${Difficulty.EASY.name}?daily=false&resume=true")
                    },
                    onDailyChallenge = {
                        navController.navigate(Routes.CHALLENGES)
                    },
                    onCustomPuzzle = { navController.navigate(Routes.CUSTOM) },
                    onLearn = { navController.navigate(Routes.LEARN) },
                    onStatistics = { navController.navigate(Routes.STATISTICS) },
                    onSettings = { navController.navigate(Routes.SETTINGS) },
                    onAbout = { navController.navigate(Routes.ABOUT) },
                    onSupport = { uriHandler.openUri("https://buymeacoffee.com/sanskarIN") },
                )
            }

            composable(
                route = Routes.GAME,
                arguments = listOf(
                    navArgument("difficulty") { type = NavType.StringType },
                    navArgument("daily") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("resume") {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                    navArgument("custom") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("challengeType") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("challengeKey") {
                        type = NavType.LongType
                        defaultValue = Long.MIN_VALUE
                    },
                ),
            ) {
                GameRoute(
                    onBack = { navController.popBackStack() },
                    onNewGame = {
                        navController.popBackStack(Routes.HOME, inclusive = false)
                    },
                )
            }

            composable(Routes.CHALLENGES) {
                ChallengesRoute(
                    onBack = { navController.popBackStack() },
                    onPlay = { descriptor ->
                        navController.navigate(
                            "game/${descriptor.difficulty.name}?daily=false&resume=false" +
                                "&challengeType=${descriptor.type.name}&challengeKey=${descriptor.key}",
                        )
                    },
                )
            }

            composable(Routes.CUSTOM) {
                CustomPuzzleRoute(
                    onBack = { navController.popBackStack() },
                    onPlay = { puzzle ->
                        navController.navigate(
                            "game/${Difficulty.MEDIUM.name}?daily=false&resume=false&custom=$puzzle",
                        )
                    },
                )
            }

            composable(Routes.LEARN) {
                LearnScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.STATISTICS) {
                StatisticsRoute(onBack = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    settings = settings,
                    onBack = { navController.popBackStack() },
                    onTheme = appViewModel::setTheme,
                    onDynamicColor = appViewModel::setDynamicColor,
                    onInputMode = appViewModel::setInputMode,
                    onHighlightPeers = appViewModel::setHighlightPeers,
                    onHighlightSame = appViewModel::setHighlightSameNumbers,
                    onAutoCheck = appViewModel::setAutoCheck,
                    onAutoRemoveNotes = appViewModel::setAutoRemoveNotes,
                    onShowTimer = appViewModel::setShowTimer,
                    onHaptics = appViewModel::setHaptics,
                    onSounds = appViewModel::setSounds,
                    onReducedMotion = appViewModel::setReducedMotion,
                    onHighContrast = appViewModel::setHighContrast,
                    onMistakeLimit = appViewModel::setMistakeLimit,
                    onResetStatistics = appViewModel::resetStatistics,
                )
            }

            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
