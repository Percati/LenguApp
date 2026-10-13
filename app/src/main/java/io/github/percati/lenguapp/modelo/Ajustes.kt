package io.github.percati.lenguapp.modelo

/**
 * Preferencias del usuario, no progreso: cambiar de idioma o nivel no mueve
 * ningun puntero de semana, la semana la sigue mandando la fecha (modo
 * revista).
 *
 * `idiomasAprendidos` reemplaza el par singular `idiomaAprendido` + `nivel`
 * de las Fases 3-5 (ver AJUSTES-FASE-6.md, bloque B): varios idiomas en
 * paralelo, cada uno con su propio nivel. Una pestaña por entrada en la
 * pantalla principal.
 *
 * `idiomaBase` (baseLanguage) e `idiomaInterfaz` (uiLanguage) son dos campos
 * distintos en los datos aunque la pantalla de ajustes los presente como un
 * solo "Idioma de la aplicación" -- CLAUDE.md, regla dura #6: el dia que
 * alguien quiera interfaz en un idioma y glosas en otro, se desdobla el
 * ajuste en pantalla sin migrar datos, porque el modelo ya los tenia
 * separados. `idiomaSegunSistema`: si esta activo, el idioma efectivo de la
 * aplicacion se resuelve contra el idioma del sistema en tiempo de uso (ver
 * presentacion/IdiomaAplicacion.kt), no se persiste un valor recalculado
 * encima de `idiomaBase`/`idiomaInterfaz` -- esos dos siguen siendo el
 * ultimo valor elegido a mano, y quedan como respaldo si el idioma del
 * sistema no es ninguno de los soportados.
 */
data class Ajustes(
    val idiomasAprendidos: Map<Idioma, Nivel> = mapOf(Idioma.DE to Nivel.B2),
    val idiomaBase: Idioma = Idioma.ES,
    val idiomaInterfaz: Idioma = Idioma.ES,
    val idiomaSegunSistema: Boolean = false,
    val familiaTema: FamiliaTema = FamiliaTema.PAPEL,
    val modoTema: ModoTema = ModoTema.SEGUN_SISTEMA,
    // Tipografia (Ronda G): independiente del tema de color.
    val fuente: FuenteTema = FuenteTema.SISTEMA,
    // Pantalla de bienvenida (Ronda N): es un AJUSTE (regla dura 4: ajustes si, progreso no), no viaja en la copia de
    // seguridad de Guardados. El valor por omision del modelo es true para que las pantallas y los tests que arman un
    // Ajustes() no abran la bienvenida; lo que decide el primer arranque es el repositorio: sin clave guardada, false.
    val bienvenidaCompletada: Boolean = true,
    // Velocidad del audio (Ronda O): preferencia de uso, no progreso (regla dura 4). No viaja en la copia de seguridad.
    val velocidadAudio: VelocidadAudio = VelocidadAudio.NORMAL,
    // Tutorial con foco (Ronda Q): que tours ya se vieron y si se salto todo. Son ajustes, no progreso (regla dura 4), y no viajan
    // en la copia de seguridad. El valor por omision del MODELO es «todos vistos» (las pantallas y los tests que arman un Ajustes() no
    // lanzan tours); lo que decide el primer arranque es el repositorio: sin clave guardada, ninguno visto.
    val toursVistos: Set<Tour> = Tour.entries.toSet(),
    val toursSalteados: Boolean = false,
)

/** Las partes del tutorial: cada una se lanza sola la primera vez que se entra a su seccion (docs/tutorial-guion.md). */
enum class Tour { FICHA, BIBLIOTECA, GUARDADOS, AJUSTES, DESAFIO }

/**
 * Los cuatro pasos de la velocidad del audio: 60, 75, 90 y 100 %. Pasos fijos y no un deslizador: se toca una vez y se
 * lee de un vistazo. `factor` es lo que recibe el reproductor (1,0 = normal; el tono se conserva).
 */
enum class VelocidadAudio(val porcentaje: Int) {
    LENTA(60), MEDIA(75), CASI_NORMAL(90), NORMAL(100);

    val factor: Float get() = porcentaje / 100f
}

/**
 * Ronda G: cuatro temas de color + SISTEMA (color dinamico de Material 3, solo API 31+). Familia y modo
 * (claro/oscuro/sistema) son dos ajustes independientes. Los valores guardados por versiones anteriores
 * (ACADEMIA, EDITORIAL) se migran al cargar: ver datos/RepositorioAjustes.kt (migrarFamiliaTema).
 */
enum class FamiliaTema { PAPEL, TERRACOTA, MAR, GRAFITO, SISTEMA }

/**
 * `SEGUN_SISTEMA` lee el modo claro/oscuro del dispositivo -- misma clase de
 * lectura que el idioma del sistema, ya enmendada en CLAUDE.md regla dura
 * #2 (AJUSTES-FASE-6.md, bloque E): configuracion local, nada sale del
 * dispositivo.
 */
enum class ModoTema { CLARO, OSCURO, SEGUN_SISTEMA }

/**
 * Tres tipografias seleccionables (Ronda G): SISTEMA (la del dispositivo, por defecto), LECTURA (Literata,
 * serifa) y CLARA (Atkinson Hyperlegible Next, sans de alta legibilidad). Las dos ultimas vienen embebidas
 * (res/font/, licencia OFL en LICENSES/): sin descarga ni proveedor en la nube.
 */
enum class FuenteTema { SISTEMA, LECTURA, CLARA }
