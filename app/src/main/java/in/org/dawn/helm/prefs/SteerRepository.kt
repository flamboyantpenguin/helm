package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.steerDataStore by preferencesDataStore(name = "steer_settings")
data class SteerState(val isPrecise: Boolean = true, val sensitivity: Int = 50)

class SteerRepository(private val context: Context) {

    companion object {
        private val PRECISE_CONTROL = booleanPreferencesKey("steer_precise_control")
    }

    val settingsFlow: Flow<SteerState> = context.steerDataStore.data.map { prefs ->
        SteerState(
            isPrecise = prefs[PRECISE_CONTROL] ?: true
        )
    }

    suspend fun update(key: String, value: Any) {
        context.steerDataStore.edit { prefs ->
            when (key) {
                "steer_precise_control" -> prefs[PRECISE_CONTROL] = value as Boolean
            }
        }
    }
}