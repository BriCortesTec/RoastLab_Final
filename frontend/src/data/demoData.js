export const INITIAL_DATA = {
  cafes: [
    { id: 1, name: 'Finca La Esperanza', origin: 'Chiapas, México', variety: 'Typica', process: 'Lavado', notes: 'Cítrico, cacao y piloncillo' },
    { id: 2, name: 'El Mirador', origin: 'Veracruz, México', variety: 'Caturra', process: 'Natural', notes: 'Frutos rojos y chocolate' },
    { id: 3, name: 'Las Nubes', origin: 'Oaxaca, México', variety: 'Bourbon', process: 'Honey', notes: 'Miel y caramelo' },
  ],
  lots: [
    { id: 1, code: 'CH-24-018', coffee: 'Finca La Esperanza', origin: 'Chiapas, México', variety: 'Typica', process: 'Lavado', harvest: '2026-09-12', weight: 42.5 },
    { id: 2, code: 'VR-24-006', coffee: 'El Mirador', origin: 'Veracruz, México', variety: 'Caturra', process: 'Natural', harvest: '2026-08-28', weight: 28 },
    { id: 3, code: 'OX-24-011', coffee: 'Las Nubes', origin: 'Oaxaca, México', variety: 'Bourbon', process: 'Honey', harvest: '2026-10-02', weight: 16.75 },
  ],
  profiles: [
    { id: 1, name: 'Balanceado de la casa', description: 'Dulzor y cuerpo equilibrados', initialTemp: 185, finalTemp: 205, duration: 11, level: 'Medio' },
    { id: 2, name: 'Claro para filtro', description: 'Acidez brillante y notas florales', initialTemp: 178, finalTemp: 198, duration: 9, level: 'Claro' },
    { id: 3, name: 'Espresso intenso', description: 'Cuerpo alto y chocolate', initialTemp: 190, finalTemp: 212, duration: 13, level: 'Oscuro' },
  ],
  roasts: [
    { id: 1, name: 'Tueste matutino #024', date: '2026-11-21', lot: 'CH-24-018 · Finca La Esperanza', profile: 'Balanceado de la casa', duration: 11.2, input: 8, output: 6.72, maxTemp: 205, status: 'Completado' },
    { id: 2, name: 'Lote de prueba #023', date: '2026-11-20', lot: 'VR-24-006 · El Mirador', profile: 'Claro para filtro', duration: 9.4, input: 5, output: 4.28, maxTemp: 198, status: 'Completado' },
    { id: 3, name: 'Prueba espresso #022', date: '2026-11-18', lot: 'OX-24-011 · Las Nubes', profile: 'Espresso intenso', duration: 13, input: 6, output: 4.92, maxTemp: 212, status: 'Completado' },
  ],
};

export const MODULES = {
  cafes: { label: 'Cafés', singular: 'café', icon: 'bean', description: 'Orígenes y variedades de café', columns: [['name', 'Nombre'], ['origin', 'Origen'], ['variety', 'Variedad'], ['process', 'Proceso'], ['notes', 'Notas']], fields: [['name', 'Nombre del café', 'text', true], ['origin', 'Origen', 'text', true], ['variety', 'Variedad', 'text', true], ['process', 'Proceso', 'select', true, ['Lavado', 'Natural', 'Honey', 'Anaeróbico']], ['notes', 'Notas de cata', 'text', false]] },
  lots: { label: 'Lotes', singular: 'lote', icon: 'layers', description: 'Inventario de café verde', columns: [['code', 'Código'], ['coffee', 'Café'], ['origin', 'Origen'], ['process', 'Proceso'], ['weight', 'Disponible (kg)'], ['harvest', 'Cosecha']], fields: [['code', 'Código de lote', 'text', true], ['coffee', 'Café', 'select', true], ['origin', 'Origen', 'text', true], ['variety', 'Variedad', 'text', true], ['process', 'Proceso', 'select', true, ['Lavado', 'Natural', 'Honey', 'Anaeróbico']], ['harvest', 'Fecha de cosecha', 'date', false], ['weight', 'Peso disponible (kg)', 'number', true]] },
  profiles: { label: 'Perfiles de tueste', singular: 'perfil', icon: 'sliders', description: 'Recetas para tus próximos tuestes', columns: [['name', 'Perfil'], ['level', 'Nivel'], ['initialTemp', 'Temp. inicial'], ['finalTemp', 'Temp. final'], ['duration', 'Duración objetivo']], fields: [['name', 'Nombre del perfil', 'text', true], ['description', 'Descripción', 'text', false], ['level', 'Nivel de tueste', 'select', true, ['Claro', 'Medio', 'Oscuro']], ['initialTemp', 'Temperatura inicial (°C)', 'number', true], ['finalTemp', 'Temperatura final (°C)', 'number', true], ['duration', 'Duración objetivo (min)', 'number', true]] },
};
