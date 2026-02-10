package `in`.org.dawn.helm.boards

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.org.dawn.helm.comms.Lantern
import `in`.org.dawn.helm.ui.settings.SettingsViewModel


@Composable
fun Board7(lantern: Lantern, size: Dp = 60.dp) {
    val context = LocalContext.current

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    val state = settingsState.lantern
    val keys: String = state.ctrl7

    for (key in keys.split(",")) {
        OutlinedButton(
            onClick = {
                Toast.makeText(context, key, Toast.LENGTH_SHORT).show()
                if (lantern.ready) lantern.sendCtrl(key, state.token)
            },
            modifier = Modifier.size(size),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.tertiary),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                key,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun Board8(lantern: Lantern, size: Dp = 60.dp) {
    val context = LocalContext.current

    val viewModel: SettingsViewModel = hiltViewModel()
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    val state = settingsState.lantern
    val keys: String = state.ctrl8

    for (key in keys.split(",")) {
        OutlinedButton(
            onClick = {
                Toast.makeText(context, key, Toast.LENGTH_SHORT).show()
                if (lantern.ready) lantern.sendCtrl(key, state.token)
            },
            modifier = Modifier.size(size),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.tertiary),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                key,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
