import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("com.google.devtools.ksp")
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

    // Ajustes es un destino real de navegacion (con su propia entrada en la
    // pila), no una bandera booleana en MainActivity -- AJUSTES-FASE-7.md,
    // bloque 2.4: eso es lo que le da al gesto de atras del sistema algo
    // para desapilar, en vez de cerrar la app.
    implementation("androidx.navigation:navigation-compose:2.8.4")

    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // Guardados (feature 3): la unica persistencia de datos de usuario de la
    // app, y es una excepcion documentada a CLAUDE.md regla dura #4 -- son
    // marcadores de referencia, no progreso pedagogico. Ver CLAUDE.md.
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.compose.material:material-icons-extended")

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
    description = "Copia a assets/calendario/ los calendarios congelados de data/calendarios/."

    // Los combos a embeber se derivan de que archivos 2026-*.json existen en
    // data/calendarios/ (regla dura #10: se deriva del contenido, nunca se
    // escribe a mano) -- NO de 2027-*.json, que todavia no se embebe (las
    // semanas de repaso 2027 no tienen dueno, ver FALTANTES.md seccion 8).
    // Hoy son 10: los 3 del piloto (de-B2, en-B2, en-C1, congelados de
    // semanas-fijas.json) mas los 7 parciales nuevos (de-A2/B1/C1/C2,
    // en-A2/B1/C2, semanas 41-53) que generar_calendario.py --parcial
    // escribio ahi. El piloto no se toca: sigue siendo el mismo archivo,
    // copiado igual que antes.
    //
    // Antes esta tarea invocaba generar_calendario.py contra el banco.json
    // vivo en cada build. Eso dejo de funcionar cuando banco.json crecio a
    // 33-37 skills por nivel para cubrir los 10 pares de 2027: el algoritmo
    // de generar_calendario.py toma TODOS los skills del (idioma, nivel), no
    // solo los que le faltan a las semanas fijas, y ya no entran en las
    // pocas semanas libres que deja semanas-fijas.json. Ademas REVIEW esta
    // hardcodeado en generar_calendario.py con el patron nuevo de 2027
    // ({10,22,34,46}), que no es el patron real del piloto (S40/S44/S48) --
    // aunque el cupo de skills no hubiera reventado, las semanas especiales
    // habrian salido mal.
    //
    // El piloto 2026 esta congelado (CLAUDE.md, regla dura #11: "no se
    // regenera"), asi que recalcularlo en cada build contra un banco.json
    // que sigue creciendo por otras razones (2027) nunca fue correcto. Esta
    // tarea ahora solo copia calendarios ya congelados; no ejecuta ningun
    // script de Python.
    val calendarios = File(rootDir, "proyecto/data/calendarios")
    val salida = File(projectDir, "src/main/assets/calendario")
    val patronNombre = Regex("""^2026-([a-z]{2})-([A-Z]\d)\.json$""")

    inputs.dir(calendarios)
    outputs.dir(salida)

    doLast {
        salida.deleteRecursively()
        salida.mkdirs()

        val archivos2026 = calendarios.listFiles { f -> patronNombre.matches(f.name) }
            ?: error("No se encontro $calendarios")
        check(archivos2026.isNotEmpty()) { "Ningun calendario 2026-*.json en $calendarios." }

        for (origen in archivos2026) {
            val (idioma, nivel) = patronNombre.matchEntire(origen.name)!!.destructured
            origen.copyTo(File(salida, "calendario_2026_${idioma}_${nivel}.json"))
        }
    }
}

// Plantillas de prompt de voz (proyecto/contenido/plantillas/prompt-voz.json):
// Fer las itera en ese JSON, asi que viven alli y se copian a assets/ en cada
// build, igual que el calendario -- no se versiona la copia.
val copiarPlantillasAssets by tasks.registering(Copy::class) {
    group = "build"
    description = "Copia a assets/plantillas/ las plantillas de prompt de voz."
    from(File(rootDir, "proyecto/contenido/plantillas")) { include("prompt-voz.json") }
    into(File(projectDir, "src/main/assets/plantillas"))
}

// preBuild es dependencia transitiva tanto de compileDebugKotlin como de
// compileDebugUnitTestKotlin, asi que esto corre antes de compilar y antes
// de testear, en ambas variantes.
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(generarCalendarioAssets)
    dependsOn(copiarPlantillasAssets)
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

// Lista blanca de permisos aceptados en el APK -- ver CLAUDE.md, regla dura
// #1. Cualquier otro permiso hace fallar el build: Glance demostro que una
// dependencia de AndroidX puede meter WAKE_LOCK, RECEIVE_BOOT_COMPLETED,
// FOREGROUND_SERVICE y ACCESS_NETWORK_STATE sin avisar, y depender de que
// alguien se acuerde de correr `aapt dump permissions` a mano no escala.
val permisosPermitidos = setOf(
    "io.github.percati.lenguapp.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION",
)

val verificarPermisosApk by tasks.registering {
    group = "verification"
    description = "Falla si el APK tiene un permiso fuera de la lista blanca de CLAUDE.md."

    val apkDebug = layout.buildDirectory.file("outputs/apk/debug/app-debug.apk")
    val sdkDir = android.sdkDirectory
    inputs.file(apkDebug)

    doLast {
        val apk = apkDebug.get().asFile
        check(apk.isFile) { "No se encontro el APK en $apk; esta tarea corre despues de assembleDebug." }

        // aapt (no aapt2: mismo binario que se uso para descubrir y
        // verificar el problema de Glance/WorkManager, ver CLAUDE.md).
        val buildToolsDir = File(sdkDir, "build-tools")
        val versionMasNueva = buildToolsDir.listFiles { f -> f.isDirectory }
            ?.maxByOrNull { it.name }
            ?: error("No se encontro ninguna build-tools instalada en $buildToolsDir")
        val esWindows = System.getProperty("os.name").lowercase().contains("win")
        val aapt = File(versionMasNueva, if (esWindows) "aapt.exe" else "aapt")
        check(aapt.isFile) { "No se encontro aapt en $aapt" }

        val salida = ByteArrayOutputStream()
        exec {
            commandLine(aapt.absolutePath, "dump", "permissions", apk.absolutePath)
            standardOutput = salida
        }

        val permisosEnElApk = Regex("""uses-permission: name='([^']+)'""")
            .findAll(salida.toString(Charsets.UTF_8))
            .map { it.groupValues[1] }
            .toSet()
        val fueraDeLaListaBlanca = permisosEnElApk - permisosPermitidos

        if (fueraDeLaListaBlanca.isNotEmpty()) {
            throw GradleException(
                "El APK tiene permisos fuera de la lista blanca de CLAUDE.md: $fueraDeLaListaBlanca. " +
                    "Ver CLAUDE.md, regla dura #1, antes de agregarlos a permisosPermitidos.",
            )
        }
    }
}

// finalizedBy, no dependsOn: esta tarea necesita que el APK ya exista, asi
// que tiene que correr despues de assembleDebug, no antes. Si falla, el
// build de todos modos se reporta como fallido.
tasks.matching { it.name == "assembleDebug" }.configureEach {
    finalizedBy(verificarPermisosApk)
}
