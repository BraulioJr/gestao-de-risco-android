import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gestaoderisco.worker.ArchiveWorker
import java.util.concurrent.TimeUnit

// ... dentro do onCreate ...

val archiveRequest = PeriodicWorkRequestBuilder<ArchiveWorker>(7, TimeUnit.DAYS) // Roda a cada 7 dias
    .build()

WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    "ArchiveOldData",
    ExistingPeriodicWorkPolicy.KEEP, // Mantém o agendamento existente se já houver um
    archiveRequest
)

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import android.view.View

// Cole este código dentro da sua classe MainActivity
// Ex: class MainActivity : AppCompatActivity() { ... AQUI DENTRO ...

// Registra o callback para o resultado do pedido de permissão.
private val requestPermissionLauncher =
	registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
		val view = findViewById<View>(android.R.id.content)
		if (isGranted) {
			Snackbar.make(view, "Permissão de notificação concedida!", Snackbar.LENGTH_SHORT).show()
		} else {
			Snackbar.make(
				view,
				"Permissão negada. Você não receberá notificações de risco.",
				Snackbar.LENGTH_LONG
			).show()
		}
	}

/**
 * Função que encapsula a lógica para pedir a permissão de notificação.
 */
private fun askNotificationPermission() {
	// Só é necessário para Android 13 (TIRAMISU) ou superior.
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		if (ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED
		) {
			// Solicita a permissão. O resultado será tratado pelo `requestPermissionLauncher`.
			requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
		}
	}
}
// Cole este código dentro da sua classe MainActivity
// Ex: class MainActivity : AppCompatActivity() { ... AQUI DENTRO ...

// Registra o callback para o resultado do pedido de permissão.
private val requestPermissionLauncher =
	registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
		val view = findViewById<View>(android.R.id.content)
		if (isGranted) {
			Snackbar.make(view, "Permissão de notificação concedida!", Snackbar.LENGTH_SHORT).show()
		} else {
			Snackbar.make(
				view,
				"Permissão negada. Você não receberá notificações de risco.",
				Snackbar.LENGTH_LONG
			).show()
		}
	}

/**
 * Função que encapsula a lógica para pedir a permissão de notificação.
 */
private fun askNotificationPermission() {
	// Só é necessário para Android 13 (TIRAMISU) ou superior.
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		if (ContextCompat.checkSelfPermission(
				this,
				Manifest.permission.POST_NOTIFICATIONS
			) != PackageManager.PERMISSION_GRANTED
		) {
			// Solicita a permissão. O resultado será tratado pelo `requestPermissionLauncher`.
			requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
		}
	}
}
