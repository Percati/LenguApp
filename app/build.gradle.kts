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
        versionCode = 2
        versionName = "0.2.0"

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
    description = "Copia a assets/plantillas/ el prompt de voz y los textos fijos de la planilla del profesor."
    from(File(rootDir, "proyecto/contenido/plantillas")) { include("prompt-voz.json", "planilla-profesor.json") }
    into(File(projectDir, "src/main/assets/plantillas"))
}

// Nombres legibles de los 14 topics (T01-T14): viven en proyecto/data/banco.json
// (`topicNombresI18n`: {T01: {es, en, de, fr, it, pt}, ...}), que NO se embebe entero
// en assets/ -- pesa mucho mas de lo que la app necesita. Se extrae solo esa tabla a
// assets/temas/topic-nombres.json en cada build (no se versiona la copia). La usan la
// Biblioteca y Guardados (idioma de app) y la planilla del profesor (idioma que se
// aprende). El `topicNombres` solo en espanol es para las herramientas, no para la app.
val generarTopicNombresAssets by tasks.registering {
    group = "build"
    description = "Extrae topicNombresI18n de banco.json a assets/temas/topic-nombres.json."
    val banco = File(rootDir, "proyecto/data/banco.json")
    val salida = File(projectDir, "src/main/assets/temas/topic-nombres.json")
    inputs.file(banco)
    outputs.file(salida)
    doLast {
        val raiz = groovy.json.JsonSlurper().parse(banco) as Map<*, *>
        val nombres = raiz["topicNombresI18n"] as? Map<*, *> ?: error("banco.json no tiene topicNombresI18n")
        salida.parentFile.mkdirs()
        salida.writeText(groovy.json.JsonOutput.toJson(nombres), Charsets.UTF_8)
    }
}

// Lista cerrada de categoriasUso (funciones comunicativas + patrones gramaticales):
// la Biblioteca filtra las expresiones por ella. Se copia a assets/temas/ en cada
// build (no se versiona la copia).
val copiarCategoriasUsoAssets by tasks.registering(Copy::class) {
    group = "build"
    description = "Copia categorias-uso.json a assets/temas/."
    from(File(rootDir, "proyecto/data")) { include("categorias-uso.json") }
    into(File(projectDir, "src/main/assets/temas"))
}

// Red de seguridad para que assets/contenido/ nunca vuelva a desfasarse de
// nucleos/packs/ocurrencias en silencio (paso en limpio, oct 2026): esos
// JSON SI se versionan (a diferencia de calendario/plantillas/temas, que se
// copian frescos en cada build), porque son el contenido real de la app --
// pero por eso mismo nadie los regeneraba en cada build, y nada avisaba
// cuando alguien tocaba un nucleo/pack/ocurrencia y se olvidaba de correr
// componer.py y copiar el resultado a assets/.
//
// Esta tarea recompone con tools/componer.py en un directorio de build (no
// se toca assets/) y compara archivo por archivo contra lo embebido. Si
// difieren -- en el set de archivos o en el contenido -- el build falla con
// el comando exacto para regenerar. Elegido en vez de "documentar el paso a
// mano" porque una regla dura que depende de que alguien se acuerde de
// correrla es exactamente el bug que la origino.
val verificarAssetsContenidoActualizados by tasks.registering {
    group = "verification"
    description = "Falla si assets/contenido/ esta desfasado de proyecto/contenido/ (nucleos/packs/ocurrencias)."

    val raizProyecto = File(rootDir, "proyecto")
    val contenidoFuente = File(raizProyecto, "contenido")
    val schema = File(raizProyecto, "schema/ficha.schema.json")
    val assetsContenido = File(projectDir, "src/main/assets/contenido")
    val salidaRecompuesta = layout.buildDirectory.dir("contenido-recompuesto").get().asFile

    inputs.dir(contenidoFuente)
    inputs.file(schema)
    inputs.dir(assetsContenido)
    outputs.dir(salidaRecompuesta)

    doLast {
        salidaRecompuesta.deleteRecursively()
        salidaRecompuesta.mkdirs()

        val salidaProceso = ByteArrayOutputStream()
        val resultado = exec {
            commandLine(
                "python3", File(raizProyecto, "tools/componer.py").absolutePath,
                "--contenido", contenidoFuente.absolutePath,
                "--salida", salidaRecompuesta.absolutePath,
                "--schema", schema.absolutePath,
            )
            standardOutput = salidaProceso
            errorOutput = salidaProceso
            isIgnoreExitValue = true
        }
        check(resultado.exitValue == 0) {
            "componer.py fallo al recomponer el contenido:\n${salidaProceso.toString(Charsets.UTF_8)}"
        }

        val nombresFuente = salidaRecompuesta.listFiles { f -> f.extension == "json" }!!.map { it.name }.toSet()
        val nombresAssets = assetsContenido.listFiles { f -> f.extension == "json" }!!.map { it.name }.toSet()

        val faltanEnAssets = nombresFuente - nombresAssets
        val sobranEnAssets = nombresAssets - nombresFuente
        val desfasados = (nombresFuente intersect nombresAssets).filter { nombre ->
            File(salidaRecompuesta, nombre).readText(Charsets.UTF_8) != File(assetsContenido, nombre).readText(Charsets.UTF_8)
        }

        if (faltanEnAssets.isNotEmpty() || sobranEnAssets.isNotEmpty() || desfasados.isNotEmpty()) {
            throw GradleException(
                "assets/contenido/ esta desfasado de proyecto/contenido/ (nucleos/packs/ocurrencias). " +
                    "Regeneralo con:\n" +
                    "  python3 proyecto/tools/componer.py --contenido proyecto/contenido --salida app/src/main/assets/contenido --schema proyecto/schema/ficha.schema.json\n" +
                    "Faltan en assets (${faltanEnAssets.size}): ${faltanEnAssets.take(10)}\n" +
                    "Sobran en assets (${sobranEnAssets.size}): ${sobranEnAssets.take(10)}\n" +
                    "Con contenido distinto (${desfasados.size}): ${desfasados.take(10)}",
            )
        }
    }
}

// Reglas de prevencion (proyecto/REGLAS-PREVENCION.md, regla dura 14): el verificador de
// consistencia (A2/B1 bilingues incluido erroresContrastivos, nombres de topic y categoria
// en 6 idiomas, textos de la planilla, « » balanceados, categoriasUso en la lista cerrada)
// corre sobre la recomposicion que acaba de hacer verificarAssetsContenidoActualizados
// (componer.py sobre proyecto/contenido/), no sobre los nucleos crudos: ve lo mismo que la
// app. Si hay problemas, el build falla con la lista.
val verificarConsistenciaContenido by tasks.registering {
    group = "verification"
    description = "Recompone el contenido con componer.py y corre verificar_consistencia.py; falla si hay problemas."
    dependsOn(verificarAssetsContenidoActualizados)

    val raizProyecto = File(rootDir, "proyecto")
    val recompuesto = layout.buildDirectory.dir("contenido-recompuesto").get().asFile
    val marca = layout.buildDirectory.file("verificarConsistenciaContenido.ok").get().asFile

    inputs.dir(recompuesto)
    inputs.file(File(raizProyecto, "tools/verificar_consistencia.py"))
    inputs.dir(File(raizProyecto, "data"))
    inputs.dir(File(raizProyecto, "contenido/plantillas"))
    outputs.file(marca)

    doLast {
        marca.delete()
        val salidaProceso = ByteArrayOutputStream()
        val resultado = exec {
            commandLine(
                "python3", File(raizProyecto, "tools/verificar_consistencia.py").absolutePath,
                "--build", recompuesto.absolutePath,
            )
            standardOutput = salidaProceso
            errorOutput = salidaProceso
            isIgnoreExitValue = true
        }
        val texto = salidaProceso.toString(Charsets.UTF_8)
        if (resultado.exitValue != 0) {
            throw GradleException(
                "verificar_consistencia.py encontro problemas (reglas de prevencion, proyecto/REGLAS-PREVENCION.md):\n$texto\n" +
                    "Para correrlo a mano:\n" +
                    "  python3 proyecto/tools/componer.py --contenido proyecto/contenido --salida /tmp/b --schema proyecto/schema/ficha.schema.json\n" +
                    "  python3 proyecto/tools/verificar_consistencia.py --build /tmp/b",
            )
        }
        marca.writeText(texto, Charsets.UTF_8)
    }
}

// preBuild es dependencia transitiva tanto de compileDebugKotlin como de
// compileDebugUnitTestKotlin, asi que esto corre antes de compilar y antes
// de testear, en ambas variantes.
tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(generarCalendarioAssets)
    dependsOn(copiarPlantillasAssets)
    dependsOn(generarTopicNombresAssets)
    dependsOn(copiarCategoriasUsoAssets)
    dependsOn(verificarAssetsContenidoActualizados)
    dependsOn(verificarConsistenciaContenido)
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
