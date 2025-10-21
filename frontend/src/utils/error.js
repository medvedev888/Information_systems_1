
export function showErrorFromResponse(err, modal, fieldLabels) {

  if (typeof err === 'string') {
    modal.value.show(err);
    return;
  }
  if (err instanceof Error) {
    modal.value.show(err.message);
    return;
  }

  const data = err.response?.data;
  if (data?.errors) {
    const text = Object.entries(data.errors)
      .map(([f, m]) => `<li><strong>${fieldLabels[f] || f}:</strong> ${m}</li>`)
      .join('');
    modal.value.show(`<ul>${text}</ul>`);
  } else if (data?.message) {
    modal.value.show(data.message);
  } else {
    modal.value.show('Unknown error');
  }
}
