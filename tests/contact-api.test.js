const assert = require('node:assert/strict');
const { test } = require('node:test');
const { createContactHandler } = require('../site/api/contact');

const verificationCalls = [];
let botVerdict = { isHuman: true, isBot: false };
let botError = null;
const contact = createContactHandler(async options => {
  verificationCalls.push(options);
  if (botError) throw botError;
  return botVerdict;
});

const ENV_KEYS = ['CONTACT_RECIPIENT', 'CONTACT_SENDER', 'RESEND_API_KEY'];
const originalEnv = Object.fromEntries(ENV_KEYS.map(key => [key, process.env[key]]));
const originalFetch = global.fetch;

function configure() {
  process.env.CONTACT_RECIPIENT = 'private@example.test';
  process.env.CONTACT_SENDER = 'Lostar <sender@example.test>';
  process.env.RESEND_API_KEY = 'test-key';
}

function reset() {
  for (const key of ENV_KEYS) {
    if (originalEnv[key] === undefined) delete process.env[key];
    else process.env[key] = originalEnv[key];
  }
  global.fetch = originalFetch;
  verificationCalls.length = 0;
  botVerdict = { isHuman: true, isBot: false };
  botError = null;
}

function request(method, body, headers = {}) {
  return {
    method,
    body,
    headers: {
      host: 'lostartechnology.com',
      origin: 'https://lostartechnology.com',
      'content-type': 'application/json',
      ...headers
    }
  };
}

async function call(input) {
  const headers = {};
  const response = {
    statusCode: 200,
    setHeader(name, value) { headers[name.toLowerCase()] = value; },
    end(value) { this.body = JSON.parse(value); }
  };
  await contact(input, response);
  return { status: response.statusCode, headers, body: response.body };
}

function validTechnical() {
  return {
    form: 'technical',
    fields: {
      servizio: 'quadri',
      nome: 'Ada Esempio',
      email: 'ada@example.test',
      azienda: 'Azienda Esempio',
      comune: 'Torino',
      progetto: 'Serve un quadro per un nuovo impianto.'
    },
    website: ''
  };
}

test('GET reports availability without exposing a destination', async () => {
  try {
    reset();
    let result = await call(request('GET'));
    assert.deepEqual(result.body, { available: false });
    configure();
    result = await call(request('GET'));
    assert.equal(result.status, 200);
    assert.deepEqual(result.body, { available: true });
    assert.equal(result.headers['cache-control'], 'no-store');
    assert.equal(verificationCalls.length, 0);
  } finally { reset(); }
});

test('POST stays copy-only when delivery is not configured', async () => {
  try {
    reset();
    for (const key of ENV_KEYS) delete process.env[key];
    const result = await call(request('POST', validTechnical()));
    assert.equal(result.status, 503);
    assert.deepEqual(result.body, { ok: false, error: 'unavailable' });
    assert.equal(verificationCalls.length, 0);
  } finally { reset(); }
});

test('valid technical request sends only the expected plain-text message', async () => {
  try {
    configure();
    let outbound;
    global.fetch = async (url, options) => {
      outbound = { url, options };
      return { ok: true };
    };
    const result = await call(request('POST', validTechnical()));
    assert.equal(result.status, 200);
    assert.deepEqual(result.body, { ok: true });
    assert.equal(outbound.url, 'https://api.resend.com/emails');
    assert.equal(outbound.options.headers.Authorization, 'Bearer test-key');
    const sent = JSON.parse(outbound.options.body);
    assert.deepEqual(sent.to, ['private@example.test']);
    assert.equal(sent.reply_to, 'ada@example.test');
    assert.equal(sent.from, 'Lostar <sender@example.test>');
    assert.match(sent.text, /Un quadro elettrico nuovo/);
    assert.match(sent.text, /Serve un quadro/);
    assert.equal(sent.html, undefined);
    assert.equal(sent.attachments, undefined);
    assert.doesNotMatch(JSON.stringify(result), /private@example\.test|test-key/);
    assert.equal(verificationCalls.length, 1);
    assert.equal(verificationCalls[0].advancedOptions.checkLevel, 'basic');
    assert.equal(verificationCalls[0].advancedOptions.headers.host, 'lostartechnology.com');
  } finally { reset(); }
});

test('House Core accepts its service labels and requires email and commune', async () => {
  try {
    configure();
    let sends = 0;
    global.fetch = async () => { sends++; return { ok: true }; };
    const body = validTechnical();
    body.form = 'house-core';
    body.fields.servizio = 'Hardware Lostar e installazione';
    assert.equal((await call(request('POST', body))).status, 200);
    delete body.fields.email;
    assert.equal((await call(request('POST', body))).status, 400);
    body.fields.email = 'ada@example.test';
    body.fields.comune = '';
    assert.equal((await call(request('POST', body))).status, 400);
    assert.equal(sends, 1);
    assert.equal(verificationCalls.length, 1);
  } finally { reset(); }
});

test('rejects unlisted services, invalid origins, media types, and oversized bodies', async () => {
  try {
    configure();
    global.fetch = async () => { throw new Error('Must not send'); };
    const body = validTechnical();
    body.fields.servizio = 'constructor';
    assert.equal((await call(request('POST', body))).status, 400);
    body.fields.servizio = 'quadri';
    assert.equal((await call(request('POST', body, { origin: 'https://other.example' }))).status, 403);
    assert.equal((await call(request('POST', body, { 'content-type': 'text/plain' }))).status, 415);
    assert.equal((await call(request('POST', body, { 'content-length': '9000' }))).status, 400);
    body.fields.progetto = 'x'.repeat(9000);
    assert.equal((await call(request('POST', body))).status, 400);
  } finally { reset(); }
});

test('honeypot quietly skips sending; upstream failure stays generic', async () => {
  try {
    configure();
    let sends = 0;
    global.fetch = async () => { sends++; return { ok: false, status: 422, body: 'private@example.test' }; };
    const body = validTechnical();
    body.website = 'bot';
    assert.deepEqual((await call(request('POST', body))).body, { ok: true });
    assert.equal(sends, 0);
    body.website = '';
    const result = await call(request('POST', body));
    assert.equal(result.status, 502);
    assert.deepEqual(result.body, { ok: false, error: 'send_failed' });
    assert.equal(sends, 1);
    assert.equal(verificationCalls.length, 1);
    assert.doesNotMatch(JSON.stringify(result), /private@example\.test/);
  } finally { reset(); }
});

test('BotID denial and verification outage fail closed before sending', async () => {
  try {
    configure();
    let sends = 0;
    global.fetch = async () => { sends++; return { ok: true }; };
    botVerdict = { isHuman: false, isBot: true };
    let result = await call(request('POST', validTechnical()));
    assert.equal(result.status, 403);
    assert.deepEqual(result.body, { ok: false, error: 'verification_failed' });
    assert.equal(sends, 0);

    botVerdict = {};
    result = await call(request('POST', validTechnical()));
    assert.equal(result.status, 403);
    assert.equal(sends, 0);

    botError = new Error('Provider error with private@example.test');
    result = await call(request('POST', validTechnical()));
    assert.equal(result.status, 503);
    assert.deepEqual(result.body, { ok: false, error: 'verification_unavailable' });
    assert.doesNotMatch(JSON.stringify(result), /private@example\.test/);
    assert.equal(verificationCalls.length, 3);
    assert.equal(sends, 0);
  } finally { reset(); }
});
