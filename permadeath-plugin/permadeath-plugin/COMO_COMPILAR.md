# Cómo compilar el plugin PermaDeath (sin instalar nada)

Como no has programado antes, la forma más fácil de convertir este código en el archivo
`.jar` que necesita Aternos es usando **GitHub Codespaces** (gratis, funciona desde el navegador).

## Paso 1: Sube el proyecto a GitHub
1. Crea una cuenta gratis en https://github.com si no tienes.
2. Crea un repositorio nuevo (botón verde "New").
3. Sube TODA la carpeta `permadeath-plugin` (arrastra los archivos a la web de GitHub,
   o usa el botón "Add file" > "Upload files").

## Paso 2: Abre un Codespace
1. En tu repositorio, pulsa el botón verde "Code" > pestaña "Codespaces" > "Create codespace on main".
2. Se abrirá un editor en el navegador (parecido a VS Code) con una terminal abajo. Espera a que cargue.

## Paso 3: Compila
En la terminal de abajo, escribe exactamente esto y pulsa Enter:

```
mvn clean package
```

Tarda 1-2 minutos la primera vez (descarga las librerías de Minecraft). Al terminar,
verás un mensaje `BUILD SUCCESS`.

## Paso 4: Descarga el .jar
1. En el panel de archivos de la izquierda, ve a la carpeta `target`.
2. Verás un archivo llamado `PermaDeath.jar`.
3. Haz clic derecho sobre él > "Download".

## Paso 5: Súbelo a Aternos
1. Entra a tu servidor en https://aternos.org
2. Ve a la sección "Plugins" (asegúrate de que el software del servidor sea Paper o Spigot,
   no "Vanilla" — si no, cámbialo en Configuración > Software antes de este paso).
3. Pulsa "Subir plugin" y selecciona el archivo `PermaDeath.jar` que descargaste.
4. Reinicia el servidor.

¡Listo! Cuando un jugador muera, verá el mensaje de eliminado y será expulsado sin poder
volver a entrar. Si algún día quieres perdonar a alguien, como op escribe en el chat del
servidor: `/revivir NombreDelJugador`
