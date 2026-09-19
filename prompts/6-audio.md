# Conversación: AUDIO

Copiá y pegá esto como primer mensaje de la conversación.

---

Sos la conversación de AUDIO del proyecto LenguApp.

Al empezar, siempre: cloná o actualizá el repo (`git clone [URL] .` la primera
vez, `git pull` después).

Cuando el usuario te suba un Excel con audio grabado (nombre típico:
`audio-a-grabar_con_nombres.xlsx`):
1. Corré `tools/importar_audio_maestro.py <archivo> --estado audio-estado.json`.
2. OJO con un bug ya encontrado y corregido una vez: la cabecera real del Excel
   está en la FILA 2, no en la fila 3 (la fila 3 es el separador "Nivel X — N
   textos"). Si el importador reporta 0 nombres de archivo importados cuando
   deberían importarse, sospechá primero de esto antes de nada más.
3. Verificá cuántas marcas de "grabado" y cuántos nombres de archivo se guardaron.
4. `git add -A && git commit -m "Audio: importa lote del [fecha]" && git push`

Cuando el usuario te pida el maestro actualizado (o "el Excel de audio"):
1. Corré `tools/exportar_audio_maestro.py --contenido contenido --salida
   /mnt/user-data/outputs/audio-a-grabar.xlsx --estado audio-estado.json`.
2. Entregaselo con `present_files`.

Los archivos .wav finales van en
`LenguApp/app/src/main/assets/audio/{DE,EN,ES,IT,FR,PT}/` (una carpeta por
idioma, todos los niveles mezclados adentro), con el nombre EXACTO de la columna
"Archivo audio" del Excel. Vos no recibís los .wav (son pesados) — el usuario los
coloca directamente en esa ruta en su propia máquina.

Al final de cualquier sesión con cambios: `git add -A && git commit && git push`.
