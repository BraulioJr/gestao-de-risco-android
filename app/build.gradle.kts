dor novo
		plugins {

			alias(libs.plugins.android.application)
			alias(libs.plugins.kotlin.android)
			alias(libs.plugins.hilt)
			alias(libs.plugins.ksp)
			alias(libs.plugins.google.gms.services)
			alias(libs.plugins.kotlin.parcelize)
		}

fun getVersionCodeFromGit(): Int {
    return try {
        val process = ProcessBuilder("git", "rev-list", "--count", "HEAD").start()
        val reader = process.inputStream.bufferedReader()
        val versionCode = reader.readLine().trim().toInt()
        process.waitFor()
        versionCode
    } catch (e: Exception) {
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
		multiDexEnabled = true
	}

	signingConfigs {
		create("release") {
			val propsFile = rootProject.file("keystore.properties")
			if (propsFile.exists()) {
				val props = java.util.Properties()
				props.load(propsFile.inputStream())
				keyAlias = props.getProperty("keyAlias")
				keyPassword = props.getProperty("keyPassword")
				storeFile = if (props.getProperty("storeFile") != null) file(props.getProperty("storeFile")) else null
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
	testImplementation("junit:junit:4.13.2")
	testImplementation("androidx.test.ext:junit:1.1.5")
	testImplementation("androidx.test:core-ktx:1.5.0")
	testImplementation("org.robolectric:robolectric:4.11.1")
	testImplementation("androidx.room:room-testing:2.6.1")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

	implementation(libs.core.splashscreen)
	implementation(libs.core.ktx)
	implementation(libs.appcompat)
	implementation(libs.material)
	implementation(libs.constraintlayout)
	implementation(libs.navigation.fragment.ktx)
	implementation(libs.navigation.ui.ktx)
	implementation(libs.recyclerview)
	implementation(libs.activity.ktx)

	implementation(libs.lifecycle.viewmodel.ktx)
	implementation(libs.lifecycle.livedata.ktx)

	implementation(libs.hilt.android)
	implementation(libs.mpandroidchart)
	ksp(libs.hilt.compiler)

	implementation(libs.preference.ktx)

	implementation(libs.kotlinx.coroutines.core)
	implementation(libs.kotlinx.coroutines.android)
	implementation(libs.kotlinx.coroutines.play.services)

	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.auth.ktx)
	implementation(libs.firebase.firestore.ktx)
	implementation(libs.firebase.messaging.ktx)
	implementation(libs.firebase.storage.ktx)
	implementation(libs.firebase.functions.ktx)

	implementation(libs.retrofit)
	implementation(libs.converter.gson)

	testImplementation(libs.mockk)
	testImplementation(libs.turbine)


	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.espresso.core)
}

kotlin {
	jvmToolchain(17)
}