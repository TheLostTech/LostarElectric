async function loadPlan() {
  try {
    const response = await fetch('./assets/house-demo/house-demo.svg');
    if (!response.ok) return;
    const parsed = new DOMParser().parseFromString(await response.text(), 'image/svg+xml');
    const svg = parsed.documentElement;
    if (svg.localName !== 'svg' || parsed.querySelector('parsererror')) return;
    // This is a versioned, first-party diagram, never a visitor-uploaded document.
    svg.setAttribute('role', 'img');
    svg.setAttribute('aria-label', 'Casa dimostrativa di 48 metri quadrati: soggiorno, camera e bagno.');
    document.querySelector('#hc-plan-stage').replaceChildren(document.importNode(svg, true));
    const controls = document.querySelector('.hc-layers');
    const names = { 'demo-arredi': 'Arredi', 'demo-luci': 'Luci', 'demo-percorsi': 'Percorsi' };
    function update() {
      const visible = [];
      controls.querySelectorAll('input').forEach(input => {
        const group = document.getElementById(input.dataset.layer);
        if (group) group.style.display = input.checked ? '' : 'none';
        if (input.checked) visible.push(names[input.dataset.layer]);
      });
      document.querySelector('#hc-plan-status').textContent = visible.length ? `${visible.join(', ')}: livelli visibili` : 'Solo planimetria: tutti i layer nascosti';
    }
    controls.hidden = false;
    controls.addEventListener('change', update);
    update();
  } catch { /* Static image remains visible when enhancement is unavailable. */ }
}
loadPlan();

const form = document.querySelector('#hc-request');
const status = document.querySelector('#hc-form-status');
const copyButton = document.querySelector('#hc-copy-request');
let contactAvailable = false;
function requestText() {
  const fields = new FormData(form);
  return `Buongiorno Lostar,\n\nvorrei valutare il mio progetto House Core.\n\nNome: ${fields.get('nome')}\nEmail: ${fields.get('email')}\nComune dell'intervento: ${fields.get('comune')}\nRichiesta: ${fields.get('servizio')}\n\nIl progetto:\n${fields.get('progetto')}\n\nDocumenti: posso condividere lo ZIP dei layer dopo un primo contatto.\n\nGrazie.`;
}
async function loadContact() {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 5000);
  try {
    const response = await fetch('/api/contact', { signal: controller.signal });
    if (!response.ok) throw new Error('config');
    const config = await response.json();
    contactAvailable = config.available === true;
  } catch { /* The draft helper stays usable without a configured recipient. */ }
  finally { clearTimeout(timeout); }
  const channel = document.querySelector('#hc-contact-channel');
  if (contactAvailable) {
    channel.textContent = 'Invia il riepilogo del progetto a Lostar. Ti ricontatteremo per ricevere lo ZIP dei layer.';
    document.querySelector('#hc-mail-button').hidden = false;
  } else {
    channel.className = 'hc-pending';
    channel.textContent = 'L’invio dal sito non è ancora disponibile. Puoi preparare e copiare la richiesta; nessun dato o file viene inviato.';
    document.querySelector('#hc-mail-button').hidden = true;
  }
}
loadContact();
form.addEventListener('submit', async event => {
  event.preventDefault();
  if (!form.reportValidity()) return;
  if (!contactAvailable) { status.textContent = 'L’invio non è ancora disponibile. Usa Copia richiesta per conservare il testo.'; return; }
  const button = document.querySelector('#hc-mail-button');
  button.disabled = true;
  status.textContent = 'Invio in corso…';
  const fields = Object.fromEntries(new FormData(form));
  try {
    const response = await fetch('/api/contact', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ form: 'house-core', fields, website: fields.website || '' })
    });
    const result = response.ok ? await response.json() : {};
    status.textContent = result.ok === true
      ? 'Richiesta inviata. Ti ricontatteremo per ricevere lo ZIP dei layer.'
      : 'Non siamo riusciti a inviare la richiesta. Riprova più tardi o copia il testo.';
  } catch {
    status.textContent = 'Non siamo riusciti a inviare la richiesta. Riprova più tardi o copia il testo.';
  } finally { button.disabled = false; }
});
copyButton.addEventListener('click', async () => {
  if (!form.reportValidity()) return;
  try {
    await navigator.clipboard.writeText(requestText());
    status.textContent = 'Richiesta copiata. Conservala per quando l’invio sarà disponibile.';
  } catch {
    // Visible, selectable fallback also works without Clipboard API permission.
    let draft = document.querySelector('#hc-draft');
    if (!draft) {
      const label = document.createElement('label'); label.textContent = 'Testo da copiare';
      draft = document.createElement('textarea'); draft.id = 'hc-draft'; draft.readOnly = true;
      label.append(draft); form.append(label);
    }
    draft.value = requestText(); draft.focus(); draft.select();
    status.textContent = 'Copia il testo selezionato e conservalo.';
  }
});

document.querySelector('#hc-fields').disabled = false;
