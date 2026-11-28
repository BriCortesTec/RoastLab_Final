const demoRoasts = [
  {
    id: 1, name: 'Tueste balanceado de ejemplo', date: '2026-11-21', lot: 'CH-24-018 · Finca La Esperanza', lotCode: 'CH-24-018', profile: 'Balanceado de la casa', duration: 9, input: 8, output: 6.72, maxTemp: 207, status: 'Completado', demo: true, observations: 'Notas de catación de ejemplo: cacao y caramelo.',
    measurements: [{ seconds: 0, temperature: 180, eventType: 'charge' }, { seconds: 45, temperature: 151 }, { seconds: 75, temperature: 136 }, { seconds: 90, temperature: 132, eventType: 'tp' }, { seconds: 120, temperature: 138 }, { seconds: 180, temperature: 148, eventType: 'dry_end' }, { seconds: 240, temperature: 163 }, { seconds: 300, temperature: 178 }, { seconds: 360, temperature: 190 }, { seconds: 420, temperature: 197, eventType: 'fc_start' }, { seconds: 480, temperature: 202 }, { seconds: 540, temperature: 207, eventType: 'drop' }],
    events: [{ type: 'charge', short: 'CARGA', label: 'Carga', seconds: 0, temperature: 180 }, { type: 'tp', short: 'TP', label: 'Punto de inflexión', seconds: 90, temperature: 132 }, { type: 'dry_end', short: 'FIN SECADO', label: 'Fin del secado', seconds: 180, temperature: 148 }, { type: 'fc_start', short: 'PRIMER CRACK', label: 'Inicio del primer crack', seconds: 420, temperature: 197 }, { type: 'drop', short: 'CAÍDA', label: 'Caída / descarga', seconds: 540, temperature: 207 }],
    adjustments: [{ id: 'demo-1-heat', type: 'heat', label: 'Calor / quemador', value: '65 %', seconds: 180, temperature: 148 }, { id: 'demo-1-air', type: 'airflow', label: 'Flujo de aire', value: '45 %', seconds: 300, temperature: 178 }],
  },
  {
    id: 2, name: 'Tueste claro de ejemplo', date: '2026-11-20', lot: 'VR-24-006 · El Mirador', lotCode: 'VR-24-006', profile: 'Claro para filtro', duration: 8.5, input: 5, output: 4.28, maxTemp: 204, status: 'Completado', demo: true, observations: 'Notas de catación de ejemplo: frutos y flores.',
    measurements: [{ seconds: 0, temperature: 182, eventType: 'charge' }, { seconds: 45, temperature: 150 }, { seconds: 75, temperature: 134 }, { seconds: 90, temperature: 130, eventType: 'tp' }, { seconds: 120, temperature: 137 }, { seconds: 180, temperature: 148, eventType: 'dry_end' }, { seconds: 240, temperature: 161 }, { seconds: 300, temperature: 175 }, { seconds: 360, temperature: 188 }, { seconds: 390, temperature: 194, eventType: 'fc_start' }, { seconds: 450, temperature: 200 }, { seconds: 510, temperature: 204, eventType: 'drop' }],
    events: [{ type: 'charge', short: 'CARGA', label: 'Carga', seconds: 0, temperature: 182 }, { type: 'tp', short: 'TP', label: 'Punto de inflexión', seconds: 90, temperature: 130 }, { type: 'dry_end', short: 'FIN SECADO', label: 'Fin del secado', seconds: 180, temperature: 148 }, { type: 'fc_start', short: 'PRIMER CRACK', label: 'Inicio del primer crack', seconds: 390, temperature: 194 }, { type: 'drop', short: 'CAÍDA', label: 'Caída / descarga', seconds: 510, temperature: 204 }],
    adjustments: [{ id: 'demo-2-heat', type: 'heat', label: 'Calor / quemador', value: '60 %', seconds: 180, temperature: 148 }, { id: 'demo-2-air', type: 'airflow', label: 'Flujo de aire', value: '55 %', seconds: 300, temperature: 175 }],
  },
  {
    id: 3, name: 'Tueste intenso de ejemplo', date: '2026-11-18', lot: 'OX-24-011 · Las Nubes', lotCode: 'OX-24-011', profile: 'Espresso intenso', duration: 10, input: 6, output: 4.92, maxTemp: 216, status: 'Completado', demo: true, observations: 'Notas de catación de ejemplo: chocolate y cuerpo.',
    measurements: [{ seconds: 0, temperature: 190, eventType: 'charge' }, { seconds: 45, temperature: 160 }, { seconds: 75, temperature: 145 }, { seconds: 90, temperature: 142, eventType: 'tp' }, { seconds: 120, temperature: 147 }, { seconds: 180, temperature: 155, eventType: 'dry_end' }, { seconds: 240, temperature: 169 }, { seconds: 300, temperature: 183 }, { seconds: 360, temperature: 194 }, { seconds: 420, temperature: 200, eventType: 'fc_start' }, { seconds: 480, temperature: 206 }, { seconds: 540, temperature: 211 }, { seconds: 600, temperature: 216, eventType: 'drop' }],
    events: [{ type: 'charge', short: 'CARGA', label: 'Carga', seconds: 0, temperature: 190 }, { type: 'tp', short: 'TP', label: 'Punto de inflexión', seconds: 90, temperature: 142 }, { type: 'dry_end', short: 'FIN SECADO', label: 'Fin del secado', seconds: 180, temperature: 155 }, { type: 'fc_start', short: 'PRIMER CRACK', label: 'Inicio del primer crack', seconds: 420, temperature: 200 }, { type: 'drop', short: 'CAÍDA', label: 'Caída / descarga', seconds: 600, temperature: 216 }],
    adjustments: [{ id: 'demo-3-heat', type: 'heat', label: 'Calor / quemador', value: '70 %', seconds: 180, temperature: 155 }, { id: 'demo-3-air', type: 'airflow', label: 'Flujo de aire', value: '50 %', seconds: 360, temperature: 194 }],
  },
];

const demoProfiles = [
  { id: 1, name: 'Balanceado de la casa', description: 'Ejemplo de perfil para comparar curvas', flavorNotes: 'Notas de ejemplo: cacao y caramelo', initialTemp: 180, finalTemp: 207, duration: 9, level: 'Medio', createdAt: '2026-11-21T16:30:00', referenceLotId: 1, referenceLotCode: 'CH-24-018', referenceInputKg: 8, referenceOutputKg: 6.72, demo: true, sourceRoastId: 1, measurements: demoRoasts[0].measurements, events: demoRoasts[0].events, adjustments: demoRoasts[0].adjustments },
  { id: 2, name: 'Claro para filtro', description: 'Ejemplo de perfil para comparar curvas', flavorNotes: 'Notas de ejemplo: frutos y flores', initialTemp: 182, finalTemp: 204, duration: 8.5, level: 'Claro', createdAt: '2026-11-20T14:15:00', referenceLotId: 2, referenceLotCode: 'VR-24-006', referenceInputKg: 5, referenceOutputKg: 4.28, demo: true, sourceRoastId: 2, measurements: demoRoasts[1].measurements, events: demoRoasts[1].events, adjustments: demoRoasts[1].adjustments },
  { id: 3, name: 'Espresso intenso', description: 'Ejemplo de perfil para comparar curvas', flavorNotes: 'Notas de ejemplo: chocolate y cuerpo', initialTemp: 190, finalTemp: 216, duration: 10, level: 'Oscuro', createdAt: '2026-11-18T11:45:00', referenceLotId: 3, referenceLotCode: 'OX-24-011', referenceInputKg: 6, referenceOutputKg: 4.92, demo: true, sourceRoastId: 3, measurements: demoRoasts[2].measurements, events: demoRoasts[2].events, adjustments: demoRoasts[2].adjustments },
];

export const INITIAL_DATA = {
  cafes: [
    { id: 1, name: 'Finca La Esperanza', origin: 'Chiapas, México', variety: 'Typica', process: 'Lavado', notes: 'Cítrico, cacao y piloncillo' },
    { id: 2, name: 'El Mirador', origin: 'Veracruz, México', variety: 'Caturra', process: 'Natural', notes: 'Frutos rojos y chocolate' },
    { id: 3, name: 'Las Nubes', origin: 'Oaxaca, México', variety: 'Bourbon', process: 'Honey', notes: 'Miel y caramelo' },
  ],
  lots: [
    { id: 1, code: 'CH-24-018', coffee: 'Finca La Esperanza', origin: 'Chiapas, México', variety: 'Typica', process: 'Lavado', density: 724, humidity: 11.2, altitude: 1450, harvest: '2026-09-12', weight: 42.5 },
    { id: 2, code: 'VR-24-006', coffee: 'El Mirador', origin: 'Veracruz, México', variety: 'Caturra', process: 'Natural', density: 690, humidity: 10.8, altitude: 1650, harvest: '2026-08-28', weight: 28 },
    { id: 3, code: 'OX-24-011', coffee: 'Las Nubes', origin: 'Oaxaca, México', variety: 'Bourbon', process: 'Honey', density: 735, humidity: 11.5, altitude: 1850, harvest: '2026-10-02', weight: 16.75 },
  ],
  profiles: demoProfiles,
  roasts: demoRoasts,
};

export const MODULES = {
  cafes: { label: 'Cafés', singular: 'café', icon: 'bean', description: 'Orígenes y variedades de café', columns: [['name', 'Nombre'], ['origin', 'Origen'], ['variety', 'Variedad'], ['process', 'Proceso'], ['notes', 'Notas']], fields: [['name', 'Nombre del café', 'text', true], ['origin', 'Origen', 'text', true], ['variety', 'Variedad', 'text', true], ['process', 'Proceso', 'select', true, ['Lavado', 'Natural', 'Honey', 'Anaeróbico']], ['notes', 'Notas de cata', 'text', false]] },
  lots: { label: 'Lotes', singular: 'lote', icon: 'layers', description: 'Inventario de café verde', columns: [['code', 'Código'], ['coffee', 'Café'], ['density', 'Densidad'], ['humidity', 'Humedad'], ['altitude', 'Altitud'], ['weight', 'Disponible (kg)'], ['harvest', 'Cosecha']], fields: [['code', 'Código de lote', 'text', true], ['coffee', 'Café', 'select', true], ['density', 'Densidad (g/L)', 'number', true], ['humidity', 'Humedad (%)', 'number', true], ['altitude', 'Altitud (msnm)', 'number', true], ['harvest', 'Fecha de cosecha', 'date', false], ['weight', 'Peso disponible (kg)', 'number', true]] },
  profiles: { label: 'Perfiles de tueste', singular: 'perfil', icon: 'sliders', description: 'Curvas y ajustes guardados para repetir tuestes', columns: [['name', 'Perfil'], ['level', 'Nivel'], ['initialTemp', 'Temp. inicial'], ['finalTemp', 'Temp. final'], ['duration', 'Duración objetivo'], ['createdAt', 'Fecha y hora'], ['curveStatus', 'Curva objetivo']], fields: [['name', 'Nombre del perfil', 'text', true], ['description', 'Descripción', 'text', false], ['flavorNotes', 'Notas de sabor', 'text', false], ['level', 'Nivel de tueste', 'select', true, ['Claro', 'Medio', 'Oscuro']], ['initialTemp', 'Temperatura inicial (°C)', 'number', true], ['finalTemp', 'Temperatura final (°C)', 'number', true], ['duration', 'Duración objetivo (min)', 'number', true]] },
};
