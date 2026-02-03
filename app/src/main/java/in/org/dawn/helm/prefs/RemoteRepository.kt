package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.remoteDataStore by preferencesDataStore(name = "remote_settings")
data class RemoteState(val isTank: Boolean = true, val sensitivity: Int = 50)

class RemoteRepository(private val context: Context) {

    companion object {
        private val SKID_STEERING = booleanPreferencesKey("remote_is_tank")
    }

    val settingsFlow: Flow<RemoteState> = context.remoteDataStore.data.map { prefs ->
        RemoteState(
            isTank = prefs[SKID_STEERING] ?: true
        )
    }

    suspend fun update(key: String, value: Any) {
        context.remoteDataStore.edit { prefs ->
            when (key) {
                "remote_is_tank" -> prefs[SKID_STEERING] = value as Boolean
            }
        }
    }
}