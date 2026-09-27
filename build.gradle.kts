plugins {
    id("com.android.application") version "8.6.1" apply false
    kotlin("android") version "2.0.21" apply false
    kotlin("plugin.serialization") version "2.0.21" apply false
    kotlin("plugin.compose") version "2.0.21" apply false
    // Room (Guardados, feature 3): genera el Dao en tiempo de compilacion.
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}
