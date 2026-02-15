package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


data class LanternState(
    val host: String = "10.0.0.1",
    val token: String = "",
    val secure: Boolean = false,
    val delay: Long = 16,
    val power: Int = 100,
    val ctrl7: String = "α,β,γ,δ",
    val ctrl8: String = "W,X,Y,Z"
)

private val Context.lanternDataStore by preferencesDataStore(name = "lantern_settings")

class LanternRepository(private val context: Context) {

    companion object {
        private val HOST_NAME = stringPreferencesKey("lantern_host_name")
        private val TOKEN = stringPreferencesKey("lantern_token")
        private val SECURE = booleanPreferencesKey("lantern_secure")
        private val DELAY = longPreferencesKey("lantern_delay")
        private val POWER = intPreferencesKey("lantern_power")
        private val CTRL7 = stringPreferencesKey("lantern_ctrl7")
        private val CTRL8 = stringPreferencesKey("lantern_ctrl8")
    }

    val settingsFlow: Flow<LanternState> = context.lanternDataStore.data.map { prefs ->
        LanternState(
            host = prefs[HOST_NAME] ?: "10.0.0.1",
            token = prefs[TOKEN] ?: "",
            secure = prefs[SECURE] ?: true,
            delay = prefs[DELAY] ?: 500,
            power = prefs[POWER] ?: 100,
            ctrl7 = prefs[CTRL7] ?: "α,β,γ,δ",
            ctrl8 = prefs[CTRL8] ?: "W,X,Y,Z"

        )
    }

    suspend fun update(key: String, value: Any) {
        context.lanternDataStore.edit { prefs ->
            when (key) {
                "lantern_host_name" -> prefs[HOST_NAME] = value as String
                "lantern_token" -> prefs[TOKEN] = value as String
                "lantern_secure" -> prefs[SECURE] = value as Boolean
                "lantern_delay" -> prefs[DELAY] = value as Long
                "lantern_power" -> prefs[POWER] = value as Int
                "lantern_ctrl7" -> prefs[CTRL7] = value as String
                "lantern_ctrl8" -> prefs[CTRL8] = value as String
            }
        }
    }

}