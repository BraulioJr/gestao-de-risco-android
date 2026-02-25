import java.io.File
import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	id("com.google.dagger.hilt.android")
	alias(libs.plugins.ksp)
	alias(libs.plugins.google.gms.services)
}

// --- Funções Auxiliares ---
fun getVersionCodeFromGit(): Int {
	return try {
		val process = ProcessBuilder("git", "rev-list", "--count", "HEAD").start()
		val reader = process.inputStream.bufferedReader()
		val versionCode = reader.readLine().trim().toInt()
		process.waitFor()
		versionCode
	} catch (e: Exception) { 1 }
}

fun stringToFile(content: String): File {
	val file = File(rootProject.rootDir, "git-hooks/pre-push")
	file.parentFile?.mkdirs()
	file.writeText(content)
	return file
}

// --- Tarefas de Qualidade ---
tasks.register("checkCodeQuality") {
	group = "verification"
}

// CORREÇÃO AQUI: Usando a função de permissão correta para o Gradle moderno
tasks.register<org.gradle.api.tasks.Copy>("installGitHook") {
	group = "git hooks"
	val hookScript = """
        #!/bin/sh
        ./gradlew :app:checkCodeQuality
    """.trimIndent()

	from(stringToFile(hookScript))
	into(File(rootProject.rootDir, ".git/hooks"))
	rename { "pre-push" }

	// Forma segura de dar permissão de execução
	filePermissions {
		user {
			read = true
			execute = true
		}
	}
}

afterEvaluate {
	tasks.named("preBuild") { dependsOn("installGitHook") }
}

android {
	namespace = "com.example.project_gestoderisco"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.example.project_gestoderisco"
		minSdk = 26
		targetSdk = 34
		versionCode = getVersionCodeFromGit()
		versionName = "1.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	buildFeatures { viewBinding = true }
}

// CORREÇÃO AQUI: Criando a configuração detektPlugins de forma segura
val detektPlugins: Configuration by configurations.creating

// --- Gerenciamento de Dependências ---

// Remova a criação manual da configuração detektPlugins por enquanto para isolar o erro
dependencies {
	// Core & UI
	implementation(libs.core.ktx)
	implementation(libs.core.splashscreen)
	implementation(libs.appcompat)
	implementation(libs.material)
	implementation(libs.constraintlayout)
	implementation(libs.recyclerview)
	implementation(libs.activity.ktx)

	// Lifecycle, Navigation e Coroutines
	implementation(libs.lifecycle.viewmodel.ktx)
	implementation(libs.lifecycle.livedata.ktx)
	implementation(libs.navigation.fragment.ktx)
	implementation(libs.navigation.ui.ktx)
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.coroutines.android)

	// Injeção de Dependência (Hilt)
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)

	// Firebase (Segurança e LGPD)
	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.auth.ktx)
	implementation(libs.firebase.firestore.ktx)

	// Testes
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.espresso.core)
}

// Remova ou comente qualquer menção a 'detektPlugins' fora daqui por enquanto

kotlin { jvmToolchain(17) }