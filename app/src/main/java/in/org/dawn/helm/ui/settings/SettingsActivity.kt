package `in`.org.dawn.helm.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.R
import `in`.org.dawn.helm.prefs.LanternState
import `in`.org.dawn.helm.prefs.SteerState

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Config() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Steer(
                state = state.steer,
                onSettingsChanged = { key, value -> viewModel.updateSetting(key, value) })
            Lantern(
                state = state.lantern,
                onSettingsChanged = { key, value -> viewModel.updateSetting(key, value) })
        }
    }
}


@Composable
fun Steer(state: SteerState, onSettingsChanged: (String, Any) -> Unit) {
    Text(
        "Steer",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.displayMedium
    )
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Precise Control")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = state.isPrecise,
            onCheckedChange = { onSettingsChanged("steer_precise_control", it) },
            thumbContent = if (state.isPrecise) {
                {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize),
                    )
                }
            } else {
                null
            })
    }
}

@Composable
fun Lantern(state: LanternState, onSettingsChanged: (String, Any) -> Unit) {
    Text(
        "Lantern",
        color = MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.displayMedium
    )
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.host,
        label = { Text(text = "Hostname") },
        onValueChange = {
            onSettingsChanged("lantern_host_name", it)
        })

}