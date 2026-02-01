package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.remoteDataStore by preferencesDataStore(name = "remote_settings")
data class RemoteState(val isPrecise: Boolean = true, val sensitivity: Int = 50)

class RemoteRepository(private val context: Context) {

    companion object {
        private val PRECISE_CONTROL = booleanPreferencesKey("remote_precise_control")
    }

    val settingsFlow: Flow<RemoteState> = context.remoteDataStore.data.map { prefs ->
        RemoteState(
            isPrecise = prefs[PRECISE_CONTROL] ?: true
        )
    }

    suspend fun update(key: String, value: Any) {
        context.remoteDataStore.edit { prefs ->
            when (key) {
                "remote_precise_control" -> prefs[PRECISE_CONTROL] = value as Boolean
            }
        }
    }
}