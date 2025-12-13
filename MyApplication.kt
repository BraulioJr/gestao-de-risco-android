import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.gestaoderisco.R
import com.example.gestaoderisco.view.OcorrenciasSalvasActivity
import com.example.gestaoderisco.view.SettingsActivity

// ... dentro da sua classe MainActivity

/**
 * Este método é chamado uma vez para criar o menu de opções na barra de ferramentas.
 * Ele infla o layout do menu que definimos em 'res/menu/main_menu.xml'.
 */
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
}

/**
 * Este método é chamado sempre que um item do menu é selecionado.
 * Ele identifica qual item foi clicado e executa a ação correspondente.
 */
override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
        // Caso o item "Ocorrências Salvas" (ícone de histórico) seja clicado
        R.id.action_ocorrencias_salvas -> {
            val intent = Intent(this, OcorrenciasSalvasActivity::class.java)
            startActivity(intent)
            true // Indica que o evento de clique foi tratado
        }

        // Caso o item "Configurações" seja clicado
        R.id.action_settings -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        // Caso o item "Sair" seja clicado
        R.id.action_logout -> {
            // TODO: Adicione sua lógica de logout aqui (ex: Firebase.auth.signOut())
            Toast.makeText(this, "Funcionalidade de logout a ser implementada.", Toast.LENGTH_SHORT).show()
            true
        }

        // Se o item clicado não for um dos nossos, deixa o sistema tratar
        else -> super.onOptionsItemSelected(item)
    }
}
