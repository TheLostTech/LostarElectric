const menuButton = document.querySelector('.menu-button');
const mainNav = document.querySelector('.main-nav');
function closeMenu(restoreFocus = false) {
  mainNav.classList.remove('is-open');
  menuButton.setAttribute('aria-expanded', 'false');
  menuButton.setAttribute('aria-label', 'Apri menu');
  if (restoreFocus) menuButton.focus();
}
menuButton.addEventListener('click', () => {
  const open = mainNav.classList.toggle('is-open');
  menuButton.setAttribute('aria-expanded', String(open));
  menuButton.setAttribute('aria-label', open ? 'Chiudi menu' : 'Apri menu');
});
mainNav.addEventListener('click', event => { if (event.target.closest('a')) closeMenu(); });
document.addEventListener('keydown', event => { if(event.key === 'Escape' && mainNav.classList.contains('is-open')) closeMenu(true); });
document.addEventListener('click', event => { if(!event.target.closest('.site-header')) closeMenu(); });
const wideViewport = matchMedia('(min-width: 861px)');
wideViewport.addEventListener('change', () => closeMenu());
// Keep the URLs shared by the old one-document site working.
function redirectLegacyHash() {
  const onHome = /\/(?:index\.html)?$/.test(location.pathname);
  const route = location.hash.slice(1);
  if (onHome && ['quadri','automazione','collaudo','house-core','contatti'].includes(route)) {
    location.replace(`./${route}.html`);
  }
}
redirectLegacyHash();
window.addEventListener('hashchange', redirectLegacyHash);
