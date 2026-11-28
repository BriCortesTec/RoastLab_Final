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

- Inicio de sesión y alta interna de usuarios con validación de campos.
- Paneles de inicio con navegación por rol (administración, gerencia y tostador).
- Catálogos de cafés, lotes y perfiles con búsqueda, paginación, alta, edición y eliminación.
- Registro de tuestes con cronómetro, lecturas manuales y curva que se actualiza al agregar cada temperatura.
- Historial para consultar las curvas guardadas y crear perfiles a partir de un tueste.
- Reportes de producción consultados desde la API.
- Cálculo del último RoR entre dos lecturas; todavía no se presenta una gráfica independiente de RoR.
- Validación de pesos y existencias. El backend descuenta el café al guardar el tueste y lo restituye si se elimina.
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
│   ├── TemperatureChart.jsx
│   ├── Icon.jsx
│   ├── EmptyState.jsx
│   └── Toast.jsx
├── services/api.js         # configuración de Axios y servicios
├── data/                   # datos de muestra y navegación/roles
├── assets/                 # imágenes
└── styles/
    ├── global.css          # tipografía, colores, botones y reglas compartidas
    ├── auth.css            # inicio de sesión
    ├── layout.css          # menú lateral y barra superior
    ├── dashboard.css       # panel de inicio
    ├── catalogs.css        # tablas, búsqueda y formularios de catálogo
    ├── roasts.css          # formulario de registro de tueste
    ├── temperatures.css    # gráfica y lecturas de temperatura
    └── reports.css         # historial y reportes
```

El recorrido es `main.jsx` → `App.jsx` → la pantalla de `pages` que corresponde. Las pantallas pueden usar componentes compartidos de `components`; por ejemplo, `Cafes.jsx`, `Lots.jsx` y `Profiles.jsx` reutilizan `CatalogPage.jsx` para mostrar sus tablas y formularios.

`main.jsx` importa `global.css`. Cada página o componente importa su hoja de estilos específica; por eso puedes trabajar en `auth.css` para cambiar el login sin buscar esas reglas entre los estilos del dashboard.

## Conexión y acceso

Vite usa `http://localhost:8080/api` en desarrollo y `/api` al compilar para producción. Puedes indicar otra dirección con `VITE_API_URL`. El backend autentica cada cuenta y devuelve su rol. En **Mi perfil** se consultan y actualizan los datos personales. En **Usuarios**, administración gestiona las cuentas y gerencia puede crear tostadores.

Para compilar la versión de entrega:

```powershell
npm run build
```

La configuración de Vite genera rutas desde `/`, que corresponde a la configuración de Nginx y al dominio `https://roastlab.space/`.
