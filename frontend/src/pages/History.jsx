import HistoryReports from './HistoryReports.jsx';

export default function History({ data, onSaveProfile, notify, canManageProfiles }) {
  return <HistoryReports data={data} page="history" onSaveProfile={onSaveProfile} notify={notify} canManageProfiles={canManageProfiles} />;
}
