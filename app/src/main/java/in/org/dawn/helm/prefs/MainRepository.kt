package `in`.org.dawn.helm.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class MainState(
    val isDynamic: Boolean = true, val themeMode: String = "Auto",
)

private val Context.mainDataStore by preferencesDataStore(name = "main_settings")

class MainRepository(private val context: Context) {

    companion object {
        private val IS_DYNAMIC = booleanPreferencesKey("main_is_dynamic")
        private val THEME_MODE = stringPreferencesKey("main_theme_mode")
    }

    val settingsFlow: Flow<MainState> = context.mainDataStore.data.map { prefs ->
        MainState(
            isDynamic = prefs[IS_DYNAMIC] ?: true, themeMode = prefs[THEME_MODE] ?: "Auto"
        )
    }

    suspend fun update(key: String, value: Any) {
        context.mainDataStore.edit { prefs ->
            when (key) {
                "main_is_dynamic" -> prefs[IS_DYNAMIC] = value as Boolean
                "main_theme_mode" -> {
                    if (value in arrayOf("Auto", "Light", "Dark")) prefs[THEME_MODE] =
                        value as String
                }
            }
        }
    }
}