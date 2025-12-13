package com.example.project_gestoderisco.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

object ThemeManager {

    private const val PREF_KEY_THEME = "theme" // Chave usada no root_preferences.xml

    // Enum para garantir segurança de tipo e clareza na escolha do tema.
    enum class ThemeMode(val value: Int, val key: String) {
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO, "light"),
        DARK(AppCompatDelegate.MODE_NIGHT_YES, "dark"),
        SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "system")
    }

    private lateinit var sharedPreferences: SharedPreferences

    // Inicializa o manager com o contexto do app.
    fun init(context: Context) {
        // Usa as SharedPreferences padrão para alinhar com o PreferenceFragmentCompat.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    // Salva a escolha do usuário e aplica o tema imediatamente.
    fun setTheme(themeMode: ThemeMode) {
        // Salva a chave string ("light", "dark", "system") nas SharedPreferences padrão.
        sharedPreferences.edit().putString(PREF_KEY_THEME, themeMode.key).apply()
        applyTheme(themeMode)
    }

    // Aplica o tema salvo na inicialização do app.
    fun applyThemeOnAppStart() {
        // Lê a chave string salva, com "system" como padrão.
        val themeKey = sharedPreferences.getString(PREF_KEY_THEME, ThemeMode.SYSTEM.key)
        val themeMode = ThemeMode.values().firstOrNull { it.key == themeKey } ?: ThemeMode.SYSTEM
        applyTheme(themeMode)
    }

    private fun applyTheme(themeMode: ThemeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode.value)
    }
}