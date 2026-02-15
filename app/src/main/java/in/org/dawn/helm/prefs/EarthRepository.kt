package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.earthDataStore by preferencesDataStore(name = "earth_settings")

data class EarthState(
    val isTank: Boolean = true,
    val invertControls: Boolean = false,
    val delay: Long = 10,
)

class EarthRepository(private val context: Context) {

    companion object {
        private val SKID_STEERING = booleanPreferencesKey("earth_is_tank")
        private val INVERT_CONTROLS = booleanPreferencesKey("earth_invert_controls")
        private val STEP_POWER = longPreferencesKey("earth_step_power")

    }

    val settingsFlow: Flow<EarthState> = context.earthDataStore.data.map { prefs ->
        EarthState(
            isTank = prefs[SKID_STEERING] ?: true,
            invertControls = prefs[INVERT_CONTROLS] ?: false,
            delay = prefs[STEP_POWER] ?: 10
        )
    }

    suspend fun update(key: String, value: Any) {
        context.earthDataStore.edit { prefs ->
            when (key) {
                "earth_is_tank" -> prefs[SKID_STEERING] = value as Boolean
                "earth_invert_controls" -> prefs[INVERT_CONTROLS] = value as Boolean
                "earth_step_power" -> prefs[STEP_POWER] = value as Long
            }
        }
    }
}