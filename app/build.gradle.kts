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

    // Ajustes es un destino real de navegacion (con su propia entrada en la
    // pila), no una bandera booleana en MainActivity -- AJUSTES-FASE-7.md,
    // bloque 2.4: eso es lo que le da al gesto de atras del sistema algo
    // para desapilar, en vez de cerrar la app.
    implementation("androidx.navigation:navigation-compose:2.8.4")

    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

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

    // Los combos a embeber salen de semanas-fijas.json, igual que antes: es
    // la lista de que (anio, idioma, nivel) del piloto 2026 tiene contenido
    // fijado a mano. Lo que cambio es el origen del calendario en si.
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
    // que sigue creciendo por otras razones (2027) nunca fue correcto. El
    // calendario real ya esta precalculado y verificado en
    // data/calendarios/2026-{idioma}-{nivel}.json (mismo formato que ya usan
    // los 10 pares de 2027 en data/calendarios/2027-*.json): 14 semanas de
    // semanas-fijas.json + las 3 especiales de contenido/nucleos
    // (REVIEW-*-S40, SURVIVAL-*-S44, REVIEW-*-S48). Esta tarea ahora solo
    // copia ese archivo ya congelado; no ejecuta ningun script de Python.
    val calendarios = File(rootDir, "proyecto/data/calendarios")
    val fijas = File(rootDir, "proyecto/data/semanas-fijas.json")
    val salida = File(projectDir, "src/main/assets/calendario")

    inputs.dir(calendarios)
    inputs.file(fijas)
    outputs.dir(salida)

    doLast {
        val combos = groovy.json.JsonSlurper().parse(fijas) as Map<*, *>

        salida.deleteRecursively()
        salida.mkdirs()

        for (clave in combos.keys) {
            val (anio, idioma, nivel) = (clave as String).split("-")
            val origen = File(calendarios, "$clave.json")
            check(origen.isFile) { "Falta el calendario congelado $origen para el combo $clave de semanas-fijas.json." }
            origen.copyTo(File(salida, "calendario_${anio}_${idioma}_${nivel}.json"))
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
