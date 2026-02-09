package `in`.org.dawn.helm

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import `in`.org.dawn.helm.ui.about.About
import `in`.org.dawn.helm.ui.settings.Config
import `in`.org.dawn.helm.ui.settings.SettingsViewModel
import `in`.org.dawn.helm.ui.theme.AppTheme
import `in`.org.dawn.helm.wheels.gyro.Earth
import `in`.org.dawn.helm.wheels.remote.TVRemote
import `in`.org.dawn.helm.wheels.thrust.BooleanThrust

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val mainState = state.main

            AppTheme(mainState.themeMode, mainState.isDynamic) {
                HelmApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelmApp() {
    val iconSize = 90.dp
    val context = LocalContext.current
    val navController = rememberNavController()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val isLandScape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val versionName = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
                    TopAppBar(
                        colors = topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.primary,
                        ), title = {
                            Text(
                                stringResource(R.string.app_name),
                                Modifier.fillMaxWidth(),
                                textAlign = if (isLandScape) TextAlign.Left else TextAlign.Center,
                                style = MaterialTheme.typography.displayLarge,
                            )
                        }, scrollBehavior = scrollBehavior
                    )
                }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        maxItemsInEachRow = 3
                    ) {
                        MenuButton(
                            navController,
                            "",
                            iconSize,
                            "Steer",
                            R.drawable.helm_24dp,
                            "Steer",
                            isLandScape
                        )
                        MenuButton(
                            navController,
                            "remote",
                            iconSize,
                            "Remote",
                            R.drawable.tv_remote_24dp,
                            "Remote",
                            isLandScape
                        )
                        MenuButton(
                            navController,
                            "thrust",
                            iconSize,
                            "Thrust",
                            R.drawable.rocket_launch_24dp,
                            "Rocket Launch",
                            isLandScape
                        )
                        MenuButton(
                            navController,
                            "askew",
                            iconSize,
                            "Askew",
                            R.drawable.video_stable_24dp,
                            "Video Stability",
                            isLandScape
                        )
                        MenuButton(
                            navController,
                            "config",
                            iconSize,
                            "Config",
                            R.drawable.anchor_24dp,
                            "Configuration",
                            isLandScape
                        )
                        MenuButton(
                            navController,
                            "about",
                            iconSize,
                            "About",
                            R.drawable.info_24dp,
                            "About",
                            isLandScape
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Version $versionName", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        composable("remote") { TVRemote() }
        composable("thrust") { BooleanThrust() }
        composable("askew") { Earth() }
        composable(route = "config") { Config() }
        composable(route = "about") { About() }
    }
}

@Composable
fun MenuButton(
    navController: NavHostController,
    route: String,
    iconSize: Dp,
    text: String,
    icon: Int,
    iconDesc: String,
    isLandscape: Boolean = false,
) {
    OutlinedButton(
        onClick = {
            if (route.isNotEmpty()) navController.navigate(route)
        },
        Modifier
            .fillMaxWidth(if (isLandscape) 0.333f else 0.5f)
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.tertiary)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                modifier = Modifier.size(iconSize),
                contentDescription = iconDesc,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}