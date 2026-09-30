import { initBotId } from 'botid/client/core';

// Initialize before either form's script: BotID attaches challenge headers to fetch.
initBotId({
  protect: [{
    path: '/api/contact',
    method: 'POST',
    advancedOptions: { checkLevel: 'basic' }
  }]
});

window.lostarBotIdReady = true;
