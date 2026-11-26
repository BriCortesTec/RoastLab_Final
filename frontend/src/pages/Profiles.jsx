import { useEffect, useState } from 'react';
import CatalogPage from '../components/CatalogPage.jsx';
import { resourceService } from '../services/api.js';

const API_MODE = Boolean(import.meta.env.VITE_API_URL);

function profilePayload(profile) {
  return {
    name: profile.name,
    description: profile.description || '',
    flavorNotes: profile.flavorNotes || '',
    level: profile.level,
    initialTemp: Number(profile.initialTemp),
    finalTemp: Number(profile.finalTemp),
    duration: Number(profile.duration),
    measurements: (profile.measurements || []).map(({ seconds, temperature, eventType }) => ({ seconds, temperature, eventType: eventType || null })),
    adjustments: (profile.adjustments || []).map(({ type, label, value, unit, seconds, temperature }) => ({ type, label, value: Number(value), unit: unit || '', seconds, temperature: temperature ?? null })),
  };
}

export default function Profiles({ items, onSave, notify, navigate }) {
  const [profiles, setProfiles] = useState(items);

  useEffect(() => {
    if (!API_MODE) {
      setProfiles(items);
      return undefined;
    }
    let active = true;
    resourceService.list('perfiles', { page: 0, size: 100 })
      .then((result) => { if (active) setProfiles(result.content || []); })
      .catch((error) => { if (active) notify(`No se pudieron cargar los perfiles: ${error.message}`); });
    return () => { active = false; };
  }, []);

  const saveProfiles = async (nextProfiles) => {
    if (!API_MODE) {
      setProfiles(nextProfiles);
      onSave(nextProfiles);
      return;
    }
    const oldIds = new Set(profiles.map((profile) => profile.id));
    const nextIds = new Set(nextProfiles.map((profile) => profile.id));
    const requests = [];
    for (const profile of nextProfiles) {
      const payload = profilePayload(profile);
      if (!oldIds.has(profile.id)) requests.push(resourceService.create('perfiles', payload));
      else {
        const previous = profiles.find((item) => item.id === profile.id);
        if (JSON.stringify(profilePayload(previous)) !== JSON.stringify(payload)) {
          requests.push(resourceService.update('perfiles', profile.id, payload));
        }
      }
    }
    for (const profile of profiles) {
      if (!nextIds.has(profile.id)) requests.push(resourceService.remove('perfiles', profile.id));
    }
    await Promise.all(requests);
    const result = await resourceService.list('perfiles', { page: 0, size: 100 });
    setProfiles(result.content || []);
  };

  return <CatalogPage kind="profiles" items={profiles} onSave={saveProfiles} notify={notify} onNavigate={navigate} />;
}
