package `in`.org.dawn.helm.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.org.dawn.helm.prefs.LanternRepository
import `in`.org.dawn.helm.prefs.LanternState
import `in`.org.dawn.helm.prefs.RemoteRepository
import `in`.org.dawn.helm.prefs.RemoteState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val lantern: LanternState = LanternState(), val remote: RemoteState = RemoteState()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val lanternRepo: LanternRepository, private val remoteRepo: RemoteRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        lanternRepo.settingsFlow, remoteRepo.settingsFlow
    ) { lantern, remote ->
        SettingsUiState(lantern, remote)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun updateSetting(key: String, value: Any) {
        viewModelScope.launch {
            when {
                key.startsWith("lantern_") -> lanternRepo.update(key, value)
                key.startsWith("remote_") -> remoteRepo.update(key, value)
                else -> Log.e("SettingsVM | Error", "Orphan key detected: $key")
            }
        }
    }
}