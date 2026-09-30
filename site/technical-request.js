const technicalForm = document.querySelector('#technical-request');
const technicalStatus = document.querySelector('#technical-status');
const serviceSelect = technicalForm.elements.servizio;
const documentTitle = document.querySelector('#documents-title');
const documentList = document.querySelector('#document-list');
let contactAvailable = false;
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
  return `Buongiorno Lostar,\n\nNome: ${f.get('nome')}\nEmail: ${f.get('email')}\nAzienda: ${optional(f.get('azienda'))}\nComune: ${optional(f.get('comune'))}\nRichiesta: ${serviceSelect.selectedOptions[0].textContent}\n\n${f.get('progetto')}\n\nDocumenti: posso condividere il materiale disponibile dopo un primo contatto.\n\nGrazie.`;
}
async function initializeTechnicalContact() {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 5000);
  try {
    const response = await fetch('/api/contact', { signal: controller.signal });
    const config = response.ok ? await response.json() : {};
    contactAvailable = config.available === true && window.lostarBotIdReady === true;
  } catch { /* Preparation stays available if the recipient cannot be loaded. */ }
  finally { clearTimeout(timeout); }
  const channel = document.querySelector('#technical-channel');
  if (contactAvailable) {
    channel.textContent = 'Invia la richiesta a Lostar senza allegati. Ti ricontatteremo per i documenti utili al preventivo.';
    document.querySelector('#technical-mail').hidden = false;
  } else {
    channel.textContent = 'L’invio dal sito non è disponibile ora. Puoi compilare e copiare il testo; nessuna richiesta viene inviata.';
  }
}
initializeTechnicalContact();
technicalForm.addEventListener('submit', async event => {
  event.preventDefault();
  if (!technicalForm.reportValidity()) return;
  if (!contactAvailable) { technicalStatus.textContent = 'L’invio non è ancora disponibile. Usa Copia il testo per conservare la richiesta.'; return; }
  const button = document.querySelector('#technical-mail');
  button.disabled = true;
  technicalStatus.textContent = 'Invio in corso…';
  const fields = Object.fromEntries(new FormData(technicalForm));
  try {
    const response = await fetch('/api/contact', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ form: 'technical', fields, website: fields.website || '' })
    });
    if (response.status === 403 || response.status === 503) {
      contactAvailable = false;
      button.hidden = true;
      document.querySelector('#technical-channel').textContent = 'La richiesta non è stata recapitata a Lostar. Puoi copiarne il testo e conservarlo.';
      technicalStatus.textContent = 'Non siamo riusciti a verificare la richiesta. Usa Copia il testo per conservarla.';
      return;
    }
    const result = response.ok ? await response.json() : {};
    technicalStatus.textContent = result.ok === true
      ? 'Richiesta inviata. Ti ricontatteremo per gli eventuali documenti utili.'
      : 'Non siamo riusciti a inviare la richiesta. Riprova più tardi o copia il testo.';
  } catch {
    contactAvailable = false;
    button.hidden = true;
    document.querySelector('#technical-channel').textContent = 'Non possiamo confermare l’invio. Puoi copiare il testo e conservarlo.';
    technicalStatus.textContent = 'Non possiamo confermare l’invio. Usa Copia il testo per conservare la richiesta.';
  } finally { button.disabled = false; }
});
document.querySelector('#technical-copy').addEventListener('click', async () => {
  if (!technicalForm.reportValidity()) return;
  try {
    await navigator.clipboard.writeText(technicalText());
    technicalStatus.textContent = 'Testo copiato. Conservalo per quando l’invio sarà disponibile.';
  } catch {
    let draft = document.querySelector('#technical-draft');
    if (!draft) {
      const label = document.createElement('label'); label.textContent = 'Testo da copiare';
      draft = document.createElement('textarea'); draft.id = 'technical-draft'; draft.readOnly = true;
      label.append(draft); technicalForm.append(label);
    }
    draft.value = technicalText(); draft.focus(); draft.select();
    technicalStatus.textContent = 'Il browser non consente la copia automatica. Copia il testo selezionato e conservalo.';
  }
});
document.querySelector('#technical-fields').disabled = false;
