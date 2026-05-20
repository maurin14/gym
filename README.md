# SAM GYM FITNESS

## Rama de trabajo

Usar la rama `dev` para integracion del proyecto.

## Compilar

En Linux/macOS:

```bash
./mvnw clean compile
```

En Windows PowerShell:

```powershell
.\mvnw.cmd clean compile
```

## Ejecutar

En Linux/macOS:

```bash
./mvnw spring-boot:run
```

En Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Tambien se puede ejecutar desde NetBeans usando la clase principal `com.una.ac.cr.gym.GymApplication`.

## Configuracion local

El archivo `src/main/resources/application.properties` contiene configuracion local y credenciales, por lo que no debe subirse con secretos reales. Usar `src/main/resources/application.properties.example` como referencia para preparar la configuracion local.

## Antes de subir cambios

```bash
git checkout dev
git pull origin dev
git status
./mvnw clean compile
git add .
git commit -m "Descripcion clara del cambio"
git push origin dev
```

En Windows, usar `.\mvnw.cmd clean compile` para validar la compilacion.

## Trabajo en rama propia

```bash
git checkout dev
git pull origin dev
git checkout -b nombre-rama
git merge dev
git push origin nombre-rama
```

Antes de abrir o actualizar un pull request, ejecutar `git status` y validar que no se incluyan archivos generados como `target/`, `build/`, `dist/`, configuraciones privadas de NetBeans o archivos con credenciales.
