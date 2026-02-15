package `in`.org.dawn.helm.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.R
import `in`.org.dawn.helm.prefs.EarthState
import `in`.org.dawn.helm.prefs.LanternState
import `in`.org.dawn.helm.prefs.MainState
import `in`.org.dawn.helm.prefs.RemoteState
import `in`.org.dawn.helm.prefs.ThrustState

data class SettingsGroup(
    val title: Int, val icon: Int, val fn: @Composable () -> Unit
)


@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Config() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
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
                        stringResource(R.string.title_settings),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.displayMedium
                    )
                }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->

        val settingGroups = listOf(
            SettingsGroup(
                R.string.title_app, R.drawable.helm_24dp, {
                    Main(state.main) { key, value ->
                        viewModel.updateSetting(
                            key, value
                        )
                    }
                }), SettingsGroup(
                R.string.title_lantern, R.drawable.lantern_24, {
                    Lantern(
                        state = state.lantern,
                        onSettingsChanged = { key, value -> viewModel.updateSetting(key, value) })
                }), SettingsGroup(
                R.string.gamepad_name_remote, R.drawable.tv_remote_24dp, {
                    Remote(
                        state = state.remote,
                        onSettingsChanged = { key, value -> viewModel.updateSetting(key, value) })
                }), SettingsGroup(
                R.string.gamepad_name_askew, R.drawable.video_stable_24dp, {
                    Earth(
                        state = state.earth,
                        onSettingsChanged = { key, value -> viewModel.updateSetting(key, value) })
                }), SettingsGroup(
                R.string.gamepad_name_thrust, R.drawable.rocket_launch_24dp, {
                    Thrust(
                        state = state.thrust,
                        onSettingsChanged = { key, value -> viewModel.updateSetting(key, value) })
                })
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            settingGroups.forEach { settings ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(settings.title),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(settings.icon),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null
                    )
                }
                Spacer(Modifier.size(8.dp))
                settings.fn()
                Spacer(Modifier.size(24.dp))
            }

        }
    }
}

@Composable
fun Main(state: MainState, onSettingsChanged: (String, Any) -> Unit) {
    val radioOptions = mapOf(
        "Light" to R.drawable.day_24dp,
        "Dark" to R.drawable.night_24dp,
        "Auto" to R.drawable.day_night_24
    )
    var isDynamic by remember(state.isDynamic) { mutableStateOf(state.isDynamic) }
    var themeMode by remember(state.themeMode) { mutableStateOf(state.themeMode) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Dynamic Theme?")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isDynamic, onCheckedChange = {
            isDynamic = it
            onSettingsChanged("main_is_dynamic", it)
        }, thumbContent = if (isDynamic) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
    }
    Row(
        Modifier
            .selectableGroup()
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        radioOptions.forEach { (text, icon) ->
            Row(
                Modifier
                    .height(56.dp)
                    .selectable(
                        selected = (text == themeMode), onClick = {
                            themeMode = text
                            onSettingsChanged("main_theme_mode", themeMode)
                        }, role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == themeMode), onClick = null
                )
                Icon(
                    imageVector = ImageVector.vectorResource(icon),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
            }
        }
    }

}

@Composable
fun Lantern(state: LanternState, onSettingsChanged: (String, Any) -> Unit) {
    var hostName by remember(state.host) { mutableStateOf(state.host) }
    var token by remember(state.token) { mutableStateOf(state.token) }
    var isSecure by remember(state.secure) { mutableStateOf(state.secure) }
    var sendDelay by remember(state.delay) { mutableStateOf(state.delay.toString()) }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = hostName,
        label = { Text(text = "Host") },
        onValueChange = {
            hostName = it
            onSettingsChanged("lantern_host_name", hostName)
        })
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = token,
        label = { Text(text = "Token") },
        onValueChange = {
            token = it
            onSettingsChanged("lantern_token", token)
        })
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Websocket Secure?")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isSecure, onCheckedChange = {
            isSecure = it
            onSettingsChanged("lantern_secure", it)
        }, thumbContent = if (isSecure) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
    }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = sendDelay,
        label = { Text(text = "Transmission Delay") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        onValueChange = {
            sendDelay = it
            if (sendDelay.isNotEmpty() && sendDelay.isDigitsOnly()) {
                onSettingsChanged("lantern_delay", sendDelay.toLong())
            }
        })
}


@Composable
fun Remote(state: RemoteState, onSettingsChanged: (String, Any) -> Unit) {
    var isPrecise by remember(state.isTank) { mutableStateOf(state.isTank) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Skid Steering")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isPrecise, onCheckedChange = {
            isPrecise = it
            onSettingsChanged("remote_is_tank", it)
        }, thumbContent = if (isPrecise) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
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
fun Earth(state: EarthState, onSettingsChanged: (String, Any) -> Unit) {
    var isPrecise by remember(state.isTank) { mutableStateOf(state.isTank) }
    var stepValue by remember(state.delay) { mutableStateOf(state.delay.toString()) }
    var invertControls by remember(state.invertControls) { mutableStateOf(state.invertControls) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Skid Steering")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = isPrecise, onCheckedChange = {
            isPrecise = it
            onSettingsChanged("earth_is_tank", it)
        }, thumbContent = if (isPrecise) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
    }
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Invert Controls?")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = invertControls, onCheckedChange = {
            invertControls = it
            onSettingsChanged("earth_invert_controls", it)
        }, thumbContent = if (invertControls) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = stepValue,
        label = { Text(text = "Step Value") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        onValueChange = {
            stepValue = it
            if (stepValue.isNotEmpty() && stepValue.isDigitsOnly()) {
                onSettingsChanged("earth_step_power", stepValue.toLong())
            }
        })
}

@Composable
fun Thrust(state: ThrustState, onSettingsChanged: (String, Any) -> Unit) {

    var invertDirection by remember(state.invertLR) { mutableStateOf(state.invertLR) }
    var stepValue by remember(state.stepValue) { mutableStateOf(state.stepValue.toString()) }
    var invertControls by remember(state.invertControls) { mutableStateOf(state.invertControls) }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Invert Controls?")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = invertControls, onCheckedChange = {
            invertControls = it
            onSettingsChanged("thrust_invert_controls", it)
        }, thumbContent = if (invertControls) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
    }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Text("Invert Direction Slider?")
        Spacer(Modifier.weight(1f))
        Switch(
            checked = invertDirection, onCheckedChange = {
            invertDirection = it
            onSettingsChanged("thrust_invert_lr", it)
        }, thumbContent = if (invertDirection) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.helm_24dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(SwitchDefaults.IconSize),
                )
            }
        } else {
            null
        })
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = stepValue,
        label = { Text(text = "Step Value") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        singleLine = true,
        onValueChange = {
            stepValue = it
            if (stepValue.isNotEmpty() && stepValue.isDigitsOnly()) {
                onSettingsChanged("thrust_step_value", stepValue.toInt())
            }
        })

}