plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    namespace = "org.lenguapp.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.lenguapp.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation("junit:junit:4.13.2")
}

// assembleDebug debe fallar si el JSON de assets/ no valida contra el schema:
// mas barato fallar aca que en el telefono. ContenidoAssetsTest carga cada
// archivo de assets/contenido con los mismos modelos que usa la app en tiempo
// de ejecucion, asi que un JSON invalido (campo obligatorio faltante, enum
// fuera de rango, campo extra) hace fallar el test y, por esta dependencia,
// el build.
tasks.matching { it.name == "assembleDebug" }.configureEach {
    dependsOn("testDebugUnitTest")
}
