const technicalForm = document.querySelector('#technical-request');
const technicalStatus = document.querySelector('#technical-status');
const serviceSelect = technicalForm.elements.servizio;
const documentTitle = document.querySelector('#documents-title');
const documentList = document.querySelector('#document-list');
let technicalEmail = '';
const requestGuides = {
  quadri: { title: 'Documenti utili per un quadro nuovo', items: ['Schema elettrico', 'Distinta componenti o capitolato', 'Ingombri e ambiente di installazione', 'Descrizione delle utenze e tempi desiderati'] },
  automazione: { title: 'Documenti utili per il bordo macchina', items: ['Schema elettrico della macchina', 'Elenco di ingressi e uscite, sensori e attuatori', 'Distinta e componenti da utilizzare', 'Attività richieste sul quadro e in campo'] },
  collaudo: { title: 'Informazioni sul quadro esistente', items: ['Schema disponibile e foto già in tuo possesso', 'Problema riscontrato o modifica richiesta', 'Componenti e utenze interessati', 'Vincoli di accesso e disponibilità dell’impianto'] },
  'house-hardware': { title: 'Materiale del progetto House Core', items: ['ZIP con i livelli PDF selezionati', 'Computo generale, se disponibile', 'Funzioni domotiche desiderate', 'Hardware già presente, se previsto nel progetto'] },
  'house-installazione': { title: 'Per hardware e installazione', items: ['ZIP dei livelli PDF e computo, se disponibile', 'Comune e stato dei lavori', 'Funzioni domotiche da realizzare', 'Professionisti già coinvolti e tempi desiderati'] }
};
const requestedService = new URLSearchParams(location.search).get('servizio');
if (Object.hasOwn(requestGuides, requestedService)) serviceSelect.value = requestedService;
function updateGuide() {
  const selected = requestGuides[serviceSelect.value] || requestGuides.quadri;
  documentTitle.textContent = selected.title;
  documentList.replaceChildren(...selected.items.map(text => {
    const li = document.createElement('li'); li.textContent = text; return li;
  }));
  const installation = serviceSelect.value === 'house-installazione';
  technicalForm.elements.comune.required = installation;
  document.querySelector('#locality-hint').textContent = installation ? '(richiesto per valutare l’installazione)' : '(facoltativo per la sola fornitura)';
}
serviceSelect.addEventListener('change', updateGuide);
updateGuide();
function technicalText() {
  const f = new FormData(technicalForm);
  const optional = (value) => String(value || '').trim() || 'Non indicato';
  return `Buongiorno Lostar,\n\nNome: ${f.get('nome')}\nEmail: ${f.get('email')}\nAzienda: ${optional(f.get('azienda'))}\nComune: ${optional(f.get('comune'))}\nRichiesta: ${serviceSelect.selectedOptions[0].textContent}\n\n${f.get('progetto')}\n\nDocumenti: aggiungo a questa email il materiale disponibile.\n\nGrazie.`;
}
async function initializeTechnicalContact() {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 5000);
  try {
    const response = await fetch('./contact-config.json', { signal: controller.signal });
    const config = response.ok ? await response.json() : {};
    if (typeof config.email === 'string' && /^[^\s@<>]+@[^\s@<>]+\.[^\s@<>]+$/.test(config.email)) technicalEmail = config.email;
  } catch { /* Preparation stays available if the recipient cannot be loaded. */ }
  finally { clearTimeout(timeout); }
  const channel = document.querySelector('#technical-channel');
  if (technicalEmail) {
    const link = document.createElement('a'); link.href = `mailto:${technicalEmail}`; link.textContent = technicalEmail; link.className = 'text-link'; channel.replaceChildren(link);
    document.querySelector('#technical-mail').hidden = false;
  } else {
    channel.textContent = 'Il recapito per l’invio non è ancora disponibile in questa anteprima. Puoi compilare e copiare il testo; nessuna richiesta viene inviata.';
  }
}
initializeTechnicalContact();
technicalForm.addEventListener('submit', event => {
  event.preventDefault();
  if (!technicalForm.reportValidity()) return;
  if (!technicalEmail) { technicalStatus.textContent = 'Il recapito non è ancora disponibile. Usa Copia il testo per conservare la richiesta.'; return; }
  window.location.href = `mailto:${technicalEmail}?subject=${encodeURIComponent('Lostar — richiesta di preventivo')}&body=${encodeURIComponent(technicalText())}`;
  technicalStatus.textContent = 'La bozza si apre nel tuo programma di posta. Aggiungi gli allegati e invia da lì. Se non si apre, copia il testo e usa l’indirizzo indicato.';
});
document.querySelector('#technical-copy').addEventListener('click', async () => {
  if (!technicalForm.reportValidity()) return;
  try {
    await navigator.clipboard.writeText(technicalText());
    technicalStatus.textContent = 'Testo copiato. Incollalo nella tua email e aggiungi i documenti disponibili.';
  } catch {
    let draft = document.querySelector('#technical-draft');
    if (!draft) {
      const label = document.createElement('label'); label.textContent = 'Testo da copiare';
      draft = document.createElement('textarea'); draft.id = 'technical-draft'; draft.readOnly = true;
      label.append(draft); technicalForm.append(label);
    }
    draft.value = technicalText(); draft.focus(); draft.select();
    technicalStatus.textContent = 'Il browser non consente la copia automatica. Copia il testo selezionato e incollalo nella tua email.';
  }
});
document.querySelector('#technical-fields').disabled = false;
