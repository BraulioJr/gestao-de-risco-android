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