import { useState } from 'react';

export default function useFormErrors() {
  const [error, setGeneral] = useState('');
  const [fields, setFields] = useState({});
  function setError(message, field) {
    setGeneral(field ? '' : message);
    setFields(field ? {[field]: message} : {});
  }
  function fromServer(failure, editable) {
    const details = Object.entries(failure.fieldErrors || {});
    setFields(Object.fromEntries(details.filter(([key]) => editable.includes(key))));
    const other = details.filter(([key]) => !editable.includes(key)).map(([,message]) => message);
    setGeneral(details.length ? other.join(' ') : failure.message);
  }
  return {error, fields, setError, fromServer};
}
