package `in`.org.dawn.helm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import `in`.org.dawn.helm.ui.settings.Config
import `in`.org.dawn.helm.wheels.remote.TVRemote
import `in`.org.dawn.helm.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                HelmApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelmApp() {
    val iconSize = 90.dp
    val navController = rememberNavController()
    val context = LocalContext.current
    val versionName = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
            .versionName ?: "?"
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Scaffold(
                modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(
                        colors = topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ), title = {
                        Text(stringResource(R.string.app_name))
                    }, navigationIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_launcher_foreground),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "App Logo"
                        )
                    })
                }) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            //navController.navigate("drive")
                        },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                            modifier = Modifier.size(iconSize),
                            contentDescription = "Steer",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Steer", style = MaterialTheme.typography.displayMedium
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate("remote")
                        },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.tv_remote_24dp),
                            modifier = Modifier.size(iconSize - 20.dp),
                            contentDescription = "Remote",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Remote", style = MaterialTheme.typography.displayMedium
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            //navController.navigate("drive")
                        },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.rocket_launch_24dp),
                            modifier = Modifier.size(iconSize),
                            contentDescription = "Rocket Launch",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Thrust", style = MaterialTheme.typography.displayMedium
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate("config")
                        },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.anchor_24dp),
                            modifier = Modifier.size(iconSize),
                            contentDescription = "Configuration",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Config", style = MaterialTheme.typography.displayMedium
                        )
                    }
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate("about")
                        },
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.info_24dp),
                            modifier = Modifier.size(iconSize),
                            contentDescription = "About",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "About", style = MaterialTheme.typography.displayMedium
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Version $versionName")
                }

            }
        }
        composable("remote") { TVRemote() }
        composable(route = "config") { Config() }
        composable(route = "about") { About() }
    }
}

@Composable
fun About() {
    val uriHandler = LocalUriHandler.current
    val repoUri = stringResource(R.string.repo_url)
    val deploymentRepoUri = stringResource(R.string.deployment_repo_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)

    ) {
        Text(
            stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(R.string.description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Button(
                onClick = {
                    uriHandler.openUri(repoUri)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary, // Custom background color
                    contentColor = MaterialTheme.colorScheme.onTertiary, // Custom content (text/icon) color
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryFixedDim, // Custom disabled background
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryFixed // Custom disabled content color
                ),
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.code_24dp),
                    contentDescription = "Code",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Text(
                    "Project Repo",
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            Button(
                onClick = {
                    uriHandler.openUri(deploymentRepoUri)
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary, // Custom background color
                    contentColor = MaterialTheme.colorScheme.onTertiary, // Custom content (text/icon) color
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryFixedDim, // Custom disabled background
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryFixed // Custom disabled content color
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.deployed_code_24dp),
                    contentDescription = "Deployed Code",
                    tint = MaterialTheme.colorScheme.onTertiary
                )
                Text(
                    "CI/CD",
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    }
}