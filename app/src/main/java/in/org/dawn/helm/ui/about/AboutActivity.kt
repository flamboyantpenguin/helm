package `in`.org.dawn.helm.ui.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import `in`.org.dawn.helm.R

data class GamepadInfo(
    val nameRes: Int, val descRes: Int
)

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About() {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(
                        stringResource(R.string.app_name),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.displayLarge,
                    )
                }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())

        ) {
            AppDesc()
            Spacer(Modifier.height(24.dp))
            GamepadDesc()
            Spacer(Modifier.height(24.dp))
            LanternDesc()
        }
    }
}

@Composable
fun AppDesc() {
    val uriHandler = LocalUriHandler.current
    val repoUri = stringResource(R.string.repo_url)
    val deploymentRepoUri = stringResource(R.string.deployment_repo_url)

    val customBtnColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        disabledContainerColor = MaterialTheme.colorScheme.tertiaryFixedDim,
        disabledContentColor = MaterialTheme.colorScheme.onTertiaryFixed
    )

    Text(
        stringResource(R.string.description),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary
    )

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Button(
            onClick = {
                uriHandler.openUri(repoUri)
            },
            colors = customBtnColors,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.code_24dp),
                contentDescription = "Code",
                tint = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                "Project Repo", color = MaterialTheme.colorScheme.onTertiary
            )
        }
        Button(
            onClick = {
                uriHandler.openUri(deploymentRepoUri)
            }, colors = customBtnColors
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.deployed_code_24dp),
                contentDescription = "Deployed Code",
                tint = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                "CI/CD", color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun GamepadDesc() {

    Column(
        modifier = Modifier
            .border(
                width = 2.dp, // Specify border width
                color = MaterialTheme.colorScheme.tertiary, // Specify border color
                shape = RoundedCornerShape(10.dp) // Specify border shape (optional, defaults to RectangleShape)
            )
            .padding(10.dp)
    ) {

        Text(
            text = "Gamepad",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.gamepad_info),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Justify
        )

        Spacer(modifier = Modifier.height(16.dp))

        val gamepads = listOf(
            GamepadInfo(R.string.gamepad_name_steer, R.string.gamepad_desc_steer),
            GamepadInfo(R.string.gamepad_name_remote, R.string.gamepad_desc_remote),
            GamepadInfo(R.string.gamepad_name_thrust, R.string.gamepad_desc_thrust),
            GamepadInfo(R.string.gamepad_name_askew, R.string.gamepad_desc_askew)
        )

        Column {
            gamepads.forEach { gamepad ->
                Text(
                    text = stringResource(gamepad.nameRes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(gamepad.descRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun LanternDesc() {

    Column(
        modifier = Modifier
            .border(
                width = 2.dp, // Specify border width
                color = MaterialTheme.colorScheme.tertiary, // Specify border color
                shape = RoundedCornerShape(10.dp) // Specify border shape (optional, defaults to RectangleShape)
            )
            .padding(10.dp)
    ) {

        Text(
            text = "Lantern",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.lantern_info),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Justify
        )
    }
}