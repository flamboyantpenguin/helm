package `in`.org.dawn.helm.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.org.dawn.helm.prefs.EarthRepository
import `in`.org.dawn.helm.prefs.EarthState
import `in`.org.dawn.helm.prefs.LanternRepository
import `in`.org.dawn.helm.prefs.LanternState
import `in`.org.dawn.helm.prefs.MainRepository
import `in`.org.dawn.helm.prefs.MainState
import `in`.org.dawn.helm.prefs.RemoteRepository
import `in`.org.dawn.helm.prefs.RemoteState
import `in`.org.dawn.helm.prefs.ThrustRepository
import `in`.org.dawn.helm.prefs.ThrustState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val main: MainState = MainState(),
    val lantern: LanternState = LanternState(),
    val earth: EarthState = EarthState(),
    val remote: RemoteState = RemoteState(),
    val thrust: ThrustState = ThrustState()
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val mainRepo: MainRepository,
    private val lanternRepo: LanternRepository,
    private val earthRepo: EarthRepository,
    private val remoteRepo: RemoteRepository,
    private val thrustRepo: ThrustRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        mainRepo.settingsFlow,
        lanternRepo.settingsFlow,
        earthRepo.settingsFlow,
        remoteRepo.settingsFlow,
        thrustRepo.settingsFlow
    ) { main, lantern, earth, remote, thrust ->
        SettingsUiState(main, lantern, earth, remote, thrust)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun updateSetting(key: String, value: Any) {
        viewModelScope.launch {
            when {
                key.startsWith("main_") -> mainRepo.update(key, value)
                key.startsWith("lantern_") -> lanternRepo.update(key, value)
                key.startsWith("earth_") -> earthRepo.update(key, value)
                key.startsWith("remote_") -> remoteRepo.update(key, value)
                key.startsWith("thrust_") -> thrustRepo.update(key, value)
                else -> Log.e("SettingsVM | Error", "Orphan key detected: $key")
            }
        }
    }
}