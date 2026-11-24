# RoastLab — frontend

Interfaz de React y Vite para el proyecto RoastLab. La guía visual toma como referencia el prototipo Figma y las capturas del repositorio.

## Ejecutar en tu computadora

Desde PowerShell, entra a la carpeta `frontend` y ejecuta:

```powershell
npm install
npm run dev
```

Abre la dirección local que muestre Vite, normalmente `http://localhost:5173/`.

## Qué incluye

- Inicio de sesión y registro con validación de campos.
- Paneles de inicio con navegación por rol (administración, gerencia y tostador).
- Catálogos de cafés, lotes y perfiles con búsqueda, paginación, alta, edición y eliminación.
- Registro de tuestes, validación de pesos e inventario, historial y reportes.
- Diseño adaptable para pantallas pequeñas.
- Cliente Axios en `src/services/api.js`, preparado para enviar el JWT.

## Cómo está organizado React

```text
src/
├── main.jsx                # punto de entrada: monta App en el navegador
├── App.jsx                 # organiza la sesión, la navegación y los datos
├── pages/                  # pantallas completas
│   ├── Login.jsx
│   ├── Dashboard.jsx
│   ├── Cafes.jsx
│   ├── Lots.jsx
│   ├── Profiles.jsx
│   ├── RoastForm.jsx
│   ├── History.jsx
│   └── Reports.jsx
├── components/             # piezas reutilizadas por las pantallas
│   ├── Sidebar.jsx
│   ├── Topbar.jsx
│   ├── CatalogPage.jsx
│   ├── Icon.jsx
│   ├── EmptyState.jsx
│   └── Toast.jsx
├── services/api.js         # configuración de Axios y servicios
├── data/                   # datos de muestra y navegación/roles
├── assets/                 # imágenes
└── styles/global.css       # estilos generales y diseño adaptable
```

El recorrido es `main.jsx` → `App.jsx` → la pantalla de `pages` que corresponde. Las pantallas pueden usar componentes compartidos de `components`; por ejemplo, `Cafes.jsx`, `Lots.jsx` y `Profiles.jsx` reutilizan `CatalogPage.jsx` para mostrar sus tablas y formularios.

## Datos y backend

El backend actual contiene las entidades y migraciones de la base de datos, pero aún no define controladores ni rutas REST. Por eso, el login y el registro funcionan en modo local si no existe `VITE_API_URL`; los catálogos guardan sus cambios en el navegador y usan datos de ejemplo. Esta demostración no guarda los catálogos en MySQL.

Cuando el backend tenga rutas disponibles, copia `.env.example` como `.env`, ajusta `VITE_API_URL` y acuerda las rutas y el formato JSON con quien desarrolla el backend. El cliente Axios ya incluye timeout, encabezado JSON, token `Bearer` y manejo del mensaje de error. Las funciones `authService` y `resourceService` dejan el punto central para conectar esas rutas.

Para compilar la versión de entrega:

```powershell
npm run build
```

La configuración conserva la ruta `/t3_act8_eq02/` para despliegues en ese subdirectorio y usa `/` durante desarrollo local.
