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
let contactEmail = '';
function requestText() {
  const fields = new FormData(form);
  return `Buongiorno Lostar,\n\nvorrei valutare il mio progetto House Core.\n\nNome: ${fields.get('nome')}\nComune dell'intervento: ${fields.get('comune')}\nRichiesta: ${fields.get('servizio')}\n\nIl progetto:\n${fields.get('progetto')}\n\nAllegati: aggiungo a questa email lo ZIP dei layer esportati da House Core.\n\nGrazie.`;
}
async function loadContact() {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 5000);
  try {
    const response = await fetch('./contact-config.json', { signal: controller.signal });
    if (!response.ok) throw new Error('config');
    const config = await response.json();
    if (typeof config.email === 'string' && /^[^\s@<>]+@[^\s@<>]+\.[^\s@<>]+$/.test(config.email)) contactEmail = config.email;
  } catch { /* The draft helper stays usable without a configured recipient. */ }
  finally { clearTimeout(timeout); }
  const channel = document.querySelector('#hc-contact-channel');
  if (contactEmail) {
    const link = document.createElement('a');
    link.href = `mailto:${contactEmail}`; link.textContent = contactEmail; link.className = 'hc-address hc-link';
    channel.replaceChildren(link);
    document.querySelector('#hc-mail-button').hidden = false;
  } else {
    channel.className = 'hc-pending';
    channel.textContent = 'Anteprima: il recapito Lostar per ricevere i layer è in configurazione. Puoi già preparare e copiare la richiesta; nessun file viene inviato da questa pagina.';
    document.querySelector('#hc-mail-button').hidden = true;
  }
}
loadContact();
form.addEventListener('submit', event => {
  event.preventDefault();
  if (!form.reportValidity()) return;
  if (!contactEmail) { status.textContent = 'Il recapito non è ancora configurato. Usa Copia richiesta per conservare il testo.'; return; }
  const subject = 'House Core — richiesta preventivo';
  window.location.href = `mailto:${contactEmail}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(requestText())}`;
  status.textContent = 'Apri la bozza nel tuo programma di posta e allega lo ZIP dei layer prima di inviare. Se la posta non si apre, copia la richiesta e scrivi all’indirizzo indicato.';
});
copyButton.addEventListener('click', async () => {
  if (!form.reportValidity()) return;
  try {
    await navigator.clipboard.writeText(requestText());
    status.textContent = 'Richiesta copiata. Incollala nella tua email e aggiungi lo ZIP con i layer.';
  } catch {
    // Visible, selectable fallback also works without Clipboard API permission.
    let draft = document.querySelector('#hc-draft');
    if (!draft) {
      const label = document.createElement('label'); label.textContent = 'Testo da copiare';
      draft = document.createElement('textarea'); draft.id = 'hc-draft'; draft.readOnly = true;
      label.append(draft); form.append(label);
    }
    draft.value = requestText(); draft.focus(); draft.select();
    status.textContent = 'Copia il testo selezionato e incollalo nella tua email, poi allega lo ZIP.';
  }
});

document.querySelector('#hc-fields').disabled = false;
