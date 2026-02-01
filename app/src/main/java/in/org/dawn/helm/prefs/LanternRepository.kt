package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class LanternState(val host: String = "10.0.0.1")

private val Context.lanternDataStore by preferencesDataStore(name = "lantern_settings")

class LanternRepository(private val context: Context) {

    companion object {
        private val HOST_NAME = stringPreferencesKey("lantern_host_name")
    }

    val settingsFlow: Flow<LanternState> = context.lanternDataStore.data.map { prefs ->
        LanternState(
            host = prefs[HOST_NAME] ?: "10.0.0.1"
        )
    }

    suspend fun update(key: String, value: Any) {
        context.lanternDataStore.edit { prefs ->
            when (key) {
                "lantern_host_name" -> prefs[HOST_NAME] = value as String
            }
        }
    }

}