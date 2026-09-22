const technicalForm = document.querySelector('#technical-request');
const technicalStatus = document.querySelector('#technical-status');
let technicalEmail = '';
function technicalText() {
  const f = new FormData(technicalForm);
  return `Buongiorno Lostar,\n\nNome e azienda: ${f.get('nome')}\nEmail: ${f.get('email')}\nRichiesta: ${f.get('servizio')}\n\n${f.get('progetto')}\n\nAggiungo a questa email i documenti utili alla valutazione.\n\nGrazie.`;
}
async function initializeTechnicalContact() {
  try {
    const response = await fetch('./contact-config.json');
    const config = response.ok ? await response.json() : {};
    if (typeof config.email === 'string' && /^[^\s@<>]+@[^\s@<>]+\.[^\s@<>]+$/.test(config.email)) technicalEmail = config.email;
  } catch { /* Keep preparation available even if contact config cannot load. */ }
  const channel = document.querySelector('#technical-channel');
  if (technicalEmail) {
    const link=document.createElement('a');link.href=`mailto:${technicalEmail}`;link.textContent=technicalEmail;channel.replaceChildren(link);
  } else {
    channel.textContent='Anteprima: recapito Lostar in configurazione. Puoi preparare e copiare la richiesta; nessun messaggio viene inviato da questa pagina.';
    document.querySelector('#technical-mail').hidden=true;
  }
}
initializeTechnicalContact();
technicalForm.addEventListener('submit',event=>{
  event.preventDefault();
  if(!technicalForm.reportValidity()||!technicalEmail)return;
  window.location.href=`mailto:${technicalEmail}?subject=${encodeURIComponent('Lostar — richiesta tecnica')}&body=${encodeURIComponent(technicalText())}`;
  technicalStatus.textContent='Apri la bozza nella tua posta, allega i documenti e inviala. Se non si apre, copia la richiesta e scrivi all’indirizzo indicato.';
});
document.querySelector('#technical-copy').addEventListener('click',async()=>{
  if(!technicalForm.reportValidity())return;
  try{
    await navigator.clipboard.writeText(technicalText());
    technicalStatus.textContent='Richiesta copiata. Incollala nella tua email e allega i documenti.';
  }catch{
    let draft=document.querySelector('#technical-draft');
    if(!draft){const label=document.createElement('label');label.textContent='Testo da copiare';draft=document.createElement('textarea');draft.id='technical-draft';draft.readOnly=true;label.append(draft);technicalForm.append(label);}
    draft.value=technicalText();draft.focus();draft.select();
    technicalStatus.textContent='Copia il testo selezionato e incollalo nella tua email, poi allega i documenti.';
  }
});
