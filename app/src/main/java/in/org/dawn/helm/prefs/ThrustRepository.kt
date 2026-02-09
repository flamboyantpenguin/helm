package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ThrustState(
    val invertLR: Boolean = false, val invertControls: Boolean = false,
    val stepValue: Int = 1,
)

private val Context.thrustDataStore by preferencesDataStore(name = "thrust_settings")

class ThrustRepository(private val context: Context) {

    companion object {
        private val INVERT_LR = booleanPreferencesKey("thrust_invert_lr")
        private val INVERT_CONTROLS = booleanPreferencesKey("thrust_invert_controls")
        private val STEP_VALUE = intPreferencesKey("thrust_step_value")
    }

    val settingsFlow: Flow<ThrustState> = context.thrustDataStore.data.map { prefs ->
        ThrustState(
            invertLR = prefs[INVERT_LR] ?: false,
            invertControls = prefs[INVERT_CONTROLS] ?: false,
            stepValue = prefs[STEP_VALUE] ?: 1
        )
    }

    suspend fun update(key: String, value: Any) {
        context.thrustDataStore.edit { prefs ->
            when (key) {
                "thrust_invert_lr" -> prefs[INVERT_LR] = value as Boolean
                "thrust_invert_controls" -> prefs[INVERT_CONTROLS] = value as Boolean
                "thrust_step_value" -> prefs[STEP_VALUE] = value as Int
            }
        }
    }
}