import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
}

android {
    namespace = "io.github.percati.lenguapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.percati.lenguapp"
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

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets")
        }
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
    // Para testear la pantalla de Compose sin emulador: Robolectric la
    // renderiza en la JVM. Los tests en src/testDebug/ (ui-test-manifest es
    // debugImplementation, por eso no viven en src/test/) usan esto para
    // las 34 piezas de contenido y para capturar pantallazos de verificacion.
    testImplementation("org.robolectric:robolectric:4.13")
    testImplementation(composeBom)
    testImplementation("androidx.compose.ui:ui-test-junit4")
    testImplementation("androidx.compose.ui:ui-test")
    testImplementation("androidx.test:core:1.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// El calendario se precalcula y se embebe, nunca se genera en el
// dispositivo (CLAUDE.md, regla dura #7) — pero tampoco se copia a mano a
// assets/: esta tarea corre generar_calendario.py como paso de build, una
// vez por cada (anio, idioma, nivel) que proyecto/data/semanas-fijas.json
// tiene fijado. "--desde" sale del propio archivo (la semana mas chica
// fijada para ese combo), no esta hardcodeado: si el piloto de un anio
// futuro arranca en otra semana, esto lo sigue sin tocar Gradle.
val generarCalendarioAssets by tasks.registering {
    group = "build"
    description = "Genera assets/calendario/*.json con generar_calendario.py."

    val script = File(rootDir, "proyecto/tools/generar_calendario.py")
    val banco = File(rootDir, "proyecto/data/banco.json")
    val fijas = File(rootDir, "proyecto/data/semanas-fijas.json")
    val salida = File(projectDir, "src/main/assets/calendario")

    inputs.file(script)
    inputs.file(banco)
    inputs.file(fijas)
    outputs.dir(salida)

    doLast {
        // Escrito a un archivo y no pasado con "-c": los argumentos de linea
        // de comando con comillas embebidas se corrompen al invocar procesos
        // en Windows.
        val extractorCombos = File(temporaryDir, "extraer_combos.py").apply {
            writeText(
                """
                import json, sys
                d = json.load(open(sys.argv[1], encoding="utf-8"))
                for clave, semanas in d.items():
                    print(f"{clave}|{min(int(w) for w in semanas.keys())}")
                """.trimIndent(),
            )
        }

        val listado = ByteArrayOutputStream()
        exec {
            commandLine("python3", extractorCombos.absolutePath, fijas.absolutePath)
            standardOutput = listado
        }
        val combos = listado.toString(Charsets.UTF_8).lines().map { it.trim() }.filter { it.isNotEmpty() }

        salida.deleteRecursively()
        salida.mkdirs()

        for (combo in combos) {
            val (clave, desde) = combo.split("|")
            val (anio, idioma, nivel) = clave.split("-")
            File(salida, "calendario_${anio}_${idioma}_${nivel}.json").outputStream().use { destino ->
                exec {
                    commandLine(
                        "python3", script.absolutePath,
                        "--anio", anio, "--desde", desde,
                        "--idioma", idioma, "--nivel", nivel,
                        "--banco", banco.absolutePath, "--fijas", fijas.absolutePath,
                        "--formato", "json",
                    )
                    standardOutput = destino
                }
            }
        }
    }
}

// preBuild es dependencia transitiva tanto de compileDebugKotlin como de
// compileDebugUnitTestKotlin, asi que esto corre antes de compilar y antes
// de testear, en ambas variantes.
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(generarCalendarioAssets)
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
