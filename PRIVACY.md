# Privacidad

Esta aplicación **no recoge ningún dato**.

## Qué lee del dispositivo

La fecha. Nada más.

## Qué envía

Nada. La aplicación **no declara el permiso `INTERNET`** en su manifiesto, así que el sistema operativo le impide abrir cualquier conexión de red. No es una promesa: es una restricción que Android aplica.

## Qué guarda

Nada sobre vos. No hay cuentas, ni perfil, ni historial, ni progreso guardado. La aplicación muestra el contenido de la semana en curso según la fecha, y eso es todo lo que hace.

## Terceros

No hay bibliotecas de analítica, ni informes de fallos, ni Google Play Services, ni ningún SDK de terceros que pueda recoger datos.

## Cómo verificarlo

No hace falta creernos:

- El código fuente es público y la aplicación se compila de forma reproducible.
- F-Droid compila desde el código y publica las **Anti-Features** de cada aplicación. Esta no debe tener ninguna.
- Exodus Privacy analiza el APK y lista los rastreadores que encuentra. El informe de esta aplicación debe dar cero.
- Cualquiera puede abrir el `AndroidManifest.xml` y comprobar que no está el permiso `INTERNET`.

## Audio

Los archivos de audio vienen dentro de la aplicación, generados antes de compilar. El dispositivo nunca los descarga ni los genera.
