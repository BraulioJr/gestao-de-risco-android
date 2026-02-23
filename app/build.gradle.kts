plugins {

	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	id("com.google.dagger.hilt.android")
	alias(libs.plugins.ksp)
	alias(libs.plugins.google.gms.services)

}

fun getVersionCodeFromGit(): Int {
    return try {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD").start()
        val reader = process.inputStream.bufferedReader()
        val versionCode = reader.readLine().trim().toInt()
        process.waitFor()
        versionCode
    } catch (e: Exception) {
        e.printStackTrace() // Loga o erro para facilitar a depuração, especialmente em ambientes de CI
        1
    }
}


import org.gradle.kotlin.dsl.Copy
import java.io.File

// Adicione este bloco no final do seu arquivo app/build.gradle.kts

// Tarefa que o Git Hook irá executar.
// Ela depende de outras duas: "ktlintFormat" para formatar o código e "lintDebug" para analisar.
tasks.register("checkCodeQuality") {
    group = "verification"
    description = "Runs ktlint and lint checks for code quality."
    dependsOn("ktlintFormat", "lintDebug")
}

// Tarefa para instalar o Git Hook no diretório .git/hooks
tasks.register("installGitHook", Copy::class) {
    group = "git hooks"
    description = "Copies the pre-push hook script into the .git/hooks directory."

    // Cria o script do hook em tempo de execução
    val hookScript = """
        #!/bin/sh

        echo "=============================="
        echo "  Running Pre-Push Hook..."
        echo "  Executing :app:checkCodeQuality"
        echo "=============================="

        # Executa a tarefa Gradle para checar a qualidade do código
        ./gradlew :app:checkCodeQuality

        # Pega o resultado do comando anterior
        RESULT=$?

        if [ $RESULT -ne 0 ]; then
            echo ""
            echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            echo "  CODE QUALITY CHECK FAILED. Push aborted."
            echo "  Please fix the issues reported above and try again."
            echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            exit 1
        fi

        echo "Code quality check passed. Proceeding with push."
        exit 0
    """.trimIndent()

    from(stringToFile(hookScript))
    into(File(rootProject.rootDir, ".git/hooks"))
    rename { "pre-push" }
    fileMode = 0b111_101_101 // Torna o arquivo executável (permissão 755)
}

// Funcao auxiliar para criar um arquivo temporario a partir de uma String
fun stringToFile(content: String): File {
    val file = File(rootProject.rootDir, "git-hooks/pre-push")
    file.parentFile?.mkdirs()
    file.writeText(content)
    return file
}
afterEvaluate {
/* Ensure installGitHook runs after other tasks */
   tasks.named("preBuild") {
         dependsOn("installGitHook")
    group = "git hooks"
    description = "Copies the pre-push hook script into the .git/hooks directory."
    dependsOn("checkCodeQuality")
    finalizedBy("installGitHook")
}
android {
	@Suppress("UnstableApiUsage")
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

	signingConfigs {
		create("release") {
			val propsFile = rootProject.file("keystore.properties")
			if (propsFile.exists()) {
				val props = java.util.Properties()
				props.load(propsFile.inputStream())
				keyAlias = props.getProperty("keyAlias")
				keyPassword = props.getProperty("keyPassword")
				storeFile = if (props.getProperty("storeFile") != null) rootProject.file(props.getProperty("storeFile")) else null
				storePassword = props.getProperty("storePassword")
			}
		}
	}


	buildTypes {
		release {
			isMinifyEnabled = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
			signingConfig = signingConfigs.getByName("release")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	buildFeatures {
		viewBinding = true
	}
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.1")
}

tasks.register("detekt") {
    group = "verification"
    description = "Runs detekt code analysis."
    dependsOn("ktlintFormat") // Formats the code first
    outputs.dir(File(rootProject.buildDir, "reports/detekt")) // Tells Gradle that any subsequent task can check if it needs to run after detekt
    doLast {
        // Configures detekt command-line arguments
        val configFile = file("detekt.yml") // Use this if you want to specify configuration
        val input = projectDir
        val output = File(rootProject.buildDir, "reports/detekt/detekt.xml")

        // Runs the detekt task
        commandLine(
            "detekt",
            "--input", input,
            "--report", "xml:$output",
            "--config", configFile
        )

        // Check if there were any detekt failures, and output it
        val detektReport = File(rootProject.buildDir, "reports/detekt/detekt.xml")
        if (detektReport.exists()) {
            val detektXml = detektReport.readText()
            if (detektXml.contains("<error") || detektXml.contains("<warning")) {
                println("Detekt found code quality issues. Check the report at ${detektReport.absolutePath}")
            } else {
                println("Detekt found no code quality issues.")
            }
        }
    }
}

dependencies {
	// Core & UI
	implementation(libs.core.ktx)
	implementation(libs.core.splashscreen)
	implementation(libs.appcompat)
	implementation(libs.material)
	implementation(libs.constraintlayout)
	implementation(libs.recyclerview)
	implementation(libs.activity.ktx)

	// Lifecycle & Navigation
	implementation(libs.lifecycle.viewmodel.ktx)
	implementation(libs.lifecycle.livedata.ktx)
	implementation(libs.navigation.fragment.ktx)
	implementation(libs.navigation.ui.ktx)

	// Coroutines
	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.kotlinx.coroutines.play.services)

	// Dependency Injection
	implementation(libs.hilt.android)
	ksp(libs.hilt.compiler)

	// Networking
	implementation(libs.retrofit)
	implementation(libs.converter.gson)

	// Firebase
	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.auth.ktx)
	implementation(libs.firebase.firestore.ktx)
	implementation(libs.firebase.messaging.ktx)
	implementation(libs.firebase.storage.ktx)
	implementation(libs.firebase.functions.ktx)

	// Utilities
	implementation(libs.mpandroidchart)
	implementation(libs.preference.ktx)

	// Unit Testing
	testImplementation(libs.junit)
	testImplementation(libs.androidx.test.core.ktx)
	testImplementation(libs.androidx.test.ext.junit)
	testImplementation(libs.robolectric)
	testImplementation(libs.androidx.room.testing)
	testImplementation(libs.kotlinx.coroutines.test)
	testImplementation(libs.mockk)
	testImplementation(libs.turbine)

	// Instrumented Testing
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.espresso.core)
}

kotlin {
	jvmToolchain(17)
}

tasks.named("check") {
    dependsOn("detekt")
}