// The destination and sending credentials exist only in the Vercel Function environment.
const { checkBotId } = require('botid/server');
const MAX_BODY_BYTES = 8192;
const EMAIL_PATTERN = /^[^\s@<>]+@[^\s@<>]+\.[^\s@<>]+$/;
const TECHNICAL_SERVICES = {
  quadri: 'Un quadro elettrico nuovo',
  automazione: 'Quadro o cablaggio bordo macchina',
  collaudo: 'Modifiche o verifiche su un quadro',
  'house-hardware': 'House Core: fornitura hardware',
  'house-installazione': 'House Core: hardware e installazione'
};
const HOUSE_CORE_SERVICES = new Set([
  'Hardware Lostar',
  'Hardware Lostar e installazione',
  'Un confronto sul progetto'
]);

function reply(response, status, body) {
  response.statusCode = status;
  response.setHeader('Content-Type', 'application/json; charset=utf-8');
  response.setHeader('Cache-Control', 'no-store');
  response.setHeader('X-Content-Type-Options', 'nosniff');
  response.end(JSON.stringify(body));
}

function configured() {
  const recipient = process.env.CONTACT_RECIPIENT?.trim();
  const sender = process.env.CONTACT_SENDER?.trim();
  const key = process.env.RESEND_API_KEY?.trim();
  const senderAddress = sender?.match(/<([^<>]+)>$/)?.[1] || sender;
  return Boolean(recipient && EMAIL_PATTERN.test(recipient) &&
    senderAddress && EMAIL_PATTERN.test(senderAddress) && key);
}

function sameHostOrigin(request) {
  const origin = request.headers.origin;
  const host = request.headers.host;
  if (typeof origin !== 'string' || typeof host !== 'string') return false;
  try {
    const parsed = new URL(origin);
    return (parsed.protocol === 'https:' || parsed.protocol === 'http:') &&
      parsed.origin === origin && parsed.host.toLowerCase() === host.toLowerCase();
  } catch {
    return false;
  }
}

async function readBody(request) {
  const statedLength = Number(request.headers['content-length']);
  if (Number.isFinite(statedLength) && statedLength > MAX_BODY_BYTES) return null;

  let raw;
  if (request.body !== undefined) {
    raw = typeof request.body === 'string' || Buffer.isBuffer(request.body)
      ? request.body.toString()
      : JSON.stringify(request.body);
  } else {
    const chunks = [];
    let length = 0;
    for await (const chunk of request) {
      length += chunk.length;
      if (length > MAX_BODY_BYTES) return null;
      chunks.push(chunk);
    }
    raw = Buffer.concat(chunks).toString('utf8');
  }
  if (Buffer.byteLength(raw, 'utf8') > MAX_BODY_BYTES) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

function singleLine(value, maxLength, required = true) {
  if (typeof value !== 'string') return null;
  const cleaned = value.trim();
  if ((required && !cleaned) || cleaned.length > maxLength || /[\x00-\x1f\x7f]/.test(cleaned)) return null;
  return cleaned;
}

function validate(body) {
  if (!body || typeof body !== 'object' || Array.isArray(body) ||
      !['technical', 'house-core'].includes(body.form) ||
      !body.fields || typeof body.fields !== 'object' || Array.isArray(body.fields)) return null;
  const fields = body.fields;
  const allowed = new Set(['servizio', 'nome', 'email', 'azienda', 'comune', 'progetto', 'website']);
  if (Object.keys(fields).some(key => !allowed.has(key))) return null;
  if (body.website !== undefined && typeof body.website !== 'string') return null;
  if (fields.website !== undefined && typeof fields.website !== 'string') return null;
  if ((body.website || fields.website || '').trim()) return { spam: true };

  const servizio = singleLine(fields.servizio, 80);
  const nome = singleLine(fields.nome, 120);
  const email = singleLine(fields.email, 160);
  const azienda = singleLine(fields.azienda ?? '', 160, false);
  const comune = singleLine(fields.comune ?? '', 120, false);
  const progetto = typeof fields.progetto === 'string' ? fields.progetto.trim() : '';
  if (!servizio || !nome || !email || !EMAIL_PATTERN.test(email) ||
      azienda === null || comune === null || !progetto || progetto.length > 1800 ||
      /[\x00-\x08\x0b\x0c\x0e-\x1f\x7f]/.test(progetto)) return null;
  if (body.form === 'technical' && !Object.hasOwn(TECHNICAL_SERVICES, servizio)) return null;
  if (body.form === 'house-core' && !HOUSE_CORE_SERVICES.has(servizio)) return null;
  if ((body.form === 'house-core' || servizio === 'house-installazione') && !comune) return null;
  return { form: body.form, servizio, nome, email, azienda, comune, progetto };
}

function emailText(data) {
  const service = data.form === 'technical' ? TECHNICAL_SERVICES[data.servizio] : data.servizio;
  return [
    'Nuova richiesta dal sito Lostar',
    '',
    `Modulo: ${data.form === 'technical' ? 'Preventivo tecnico' : 'House Core'}`,
    `Servizio: ${service}`,
    `Nome: ${data.nome}`,
    `Email: ${data.email}`,
    `Azienda: ${data.azienda || 'Non indicata'}`,
    `Comune dell’intervento: ${data.comune || 'Non indicato'}`,
    '',
    'Progetto:',
    data.progetto,
    '',
    'Gli eventuali documenti saranno condivisi dopo un primo contatto.'
  ].join('\n');
}

function createContactHandler(verifyBotId = checkBotId) {
  return async function contact(request, response) {
  if (request.method === 'GET') {
    reply(response, 200, { available: configured() });
    return;
  }
  if (request.method !== 'POST') {
    response.setHeader('Allow', 'GET, POST');
    reply(response, 405, { ok: false, error: 'method_not_allowed' });
    return;
  }
  if (!sameHostOrigin(request)) {
    reply(response, 403, { ok: false, error: 'invalid_request' });
    return;
  }
  if (!/^application\/json(?:\s*;|$)/i.test(request.headers['content-type'] || '')) {
    reply(response, 415, { ok: false, error: 'invalid_request' });
    return;
  }
  if (!configured()) {
    reply(response, 503, { ok: false, error: 'unavailable' });
    return;
  }

  let body;
  try {
    body = await readBody(request);
  } catch {
    reply(response, 400, { ok: false, error: 'invalid_request' });
    return;
  }
  const data = validate(body);
  if (!data) {
    reply(response, 400, { ok: false, error: 'invalid_request' });
    return;
  }
  if (data.spam) {
    reply(response, 200, { ok: true });
    return;
  }

  // BotID Basic is required for every valid request before the mail provider is called.
  try {
    const verification = await verifyBotId({
      advancedOptions: { checkLevel: 'basic', headers: request.headers }
    });
    if (verification.isHuman !== true || verification.isBot !== false) {
      reply(response, 403, { ok: false, error: 'verification_failed' });
      return;
    }
  } catch {
    // Fail closed if BotID, its Vercel request context, or OIDC is unavailable.
    reply(response, 503, { ok: false, error: 'verification_unavailable' });
    return;
  }

  try {
    const sent = await fetch('https://api.resend.com/emails', {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${process.env.RESEND_API_KEY.trim()}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        from: process.env.CONTACT_SENDER.trim(),
        to: [process.env.CONTACT_RECIPIENT.trim()],
        reply_to: data.email,
        subject: data.form === 'technical' ? 'Lostar — richiesta di preventivo' : 'House Core — richiesta preventivo',
        text: emailText(data)
      }),
      signal: AbortSignal.timeout(10000)
    });
    if (!sent.ok) throw new Error('Send failed');
    reply(response, 200, { ok: true });
  } catch {
    // Never forward or log provider errors: they may contain a private address.
    reply(response, 502, { ok: false, error: 'send_failed' });
  }
  };
}

module.exports = createContactHandler();
module.exports.createContactHandler = createContactHandler;
