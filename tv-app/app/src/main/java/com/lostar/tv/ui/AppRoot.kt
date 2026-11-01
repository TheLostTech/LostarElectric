package com.lostar.tv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lostar.tv.AppContainer
import com.lostar.tv.data.CatalogRepository
import com.lostar.tv.data.SourceState
import com.lostar.tv.ui.components.LoadingView
import com.lostar.tv.ui.components.MessageView
import com.lostar.tv.ui.detail.MovieDetailScreen
import com.lostar.tv.ui.detail.SeriesDetailScreen
import com.lostar.tv.ui.main.MainShell
import com.lostar.tv.ui.player.PlayRequest
import com.lostar.tv.ui.player.PlayerScreen
import com.lostar.tv.ui.setup.SetupScreen
import com.lostar.tv.ui.theme.Palette
import kotlinx.coroutines.launch

object Routes {
    const val MAIN = "main"
    const val PLAYER = "player"
    const val MOVIE = "movie/{id}"
    const val SERIES = "series/{id}"
    fun movie(id: String) = "movie/${android.net.Uri.encode(id)}"
    fun series(id: String) = "series/${android.net.Uri.encode(id)}"
}

/** Actions every screen needs, so they don't depend on the nav controller directly. */
class Navigator(private val nav: NavHostController, private val app: AppContainer) {
    fun openMovie(id: String) = nav.navigate(Routes.movie(id))
    fun openSeries(id: String) = nav.navigate(Routes.series(id))
    fun play(request: PlayRequest) {
        app.pendingPlayback = request
        nav.navigate(Routes.PLAYER) { launchSingleTop = true }
    }
    fun back() = nav.popBackStack()
}

@Composable
fun AppRoot() {
    val app = LocalApp.current
    val source by app.userStore.source.collectAsState(initial = null)

    Box(Modifier.fillMaxSize().background(Palette.Background)) {
        when (val s = source) {
            null -> LoadingView()
            SourceState.None -> SetupScreen()
            is SourceState.Some -> {
                LaunchedEffect(s.source) { app.catalog.activate(s.source) }
                CatalogGate()
            }
        }
    }
}

@Composable
private fun CatalogGate() {
    val app = LocalApp.current
    val scope = rememberCoroutineScope()
    val state by app.catalog.state.collectAsState()
    when (val st = state) {
        CatalogRepository.State.Idle, CatalogRepository.State.Loading -> LoadingView("Caricamento della tua lista…")
        is CatalogRepository.State.Error -> MessageView(
            title = "Impossibile caricare la lista",
            message = st.message,
            actionLabel = "Riprova",
            onAction = { app.catalog.reload() },
            secondaryLabel = "Cambia lista",
            onSecondary = {
                scope.launch {
                    app.catalog.deactivate()
                    app.userStore.setSource(null)
                }
            },
        )
        is CatalogRepository.State.Ready -> MainNavHost()
    }
}

@Composable
private fun MainNavHost() {
    val app = LocalApp.current
    val nav = rememberNavController()
    val navigator = androidx.compose.runtime.remember(nav) { Navigator(nav, app) }

    NavHost(navController = nav, startDestination = Routes.MAIN) {
        composable(Routes.MAIN) { MainShell(navigator) }
        composable(Routes.MOVIE, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
            MovieDetailScreen(entry.arguments?.getString("id").orEmpty(), navigator)
        }
        composable(Routes.SERIES, arguments = listOf(navArgument("id") { type = NavType.StringType })) { entry ->
            SeriesDetailScreen(entry.arguments?.getString("id").orEmpty(), navigator)
        }
        composable(Routes.PLAYER) {
            val request = androidx.compose.runtime.remember { app.pendingPlayback }
            if (request == null) {
                LaunchedEffect(Unit) { navigator.back() }
            } else {
                PlayerScreen(request, onExit = { navigator.back() })
            }
        }
    }
}
