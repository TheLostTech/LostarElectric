if (window.location.hash === "#house-core") window.location.replace("./house-core.html");
const pages = Array.from(document.querySelectorAll("[data-page]"));
const navLinks = Array.from(document.querySelectorAll("[data-nav]"));
const menuButton = document.querySelector(".menu-button");
const mainNav = document.querySelector(".main-nav");
const validRoutes = new Set(pages.map((page) => page.id));

function routeFromHash() {
  const hash = window.location.hash.replace("#", "");
  return validRoutes.has(hash) ? hash : "home";
}

function setRoute(route) {
  pages.forEach((page) => {
    page.classList.toggle("is-active", page.id === route);
  });

  navLinks.forEach((link) => {
    const target = link.getAttribute("href").replace("#", "");
    link.classList.toggle("is-active", target === route);
    if (target === route) link.setAttribute("aria-current", "page");
    else link.removeAttribute("aria-current");
  });

  if (mainNav.classList.contains("is-open")) {
    mainNav.classList.remove("is-open");
    menuButton.setAttribute("aria-expanded", "false");
  }

  window.scrollTo({ top: 0, behavior: "auto" });
}

window.addEventListener("hashchange", () => setRoute(routeFromHash()));
setRoute(routeFromHash());

menuButton.addEventListener("click", () => {
  const isOpen = mainNav.classList.toggle("is-open");
  menuButton.setAttribute("aria-expanded", String(isOpen));
});

document.querySelectorAll(".upload-box input").forEach((input) => {
  input.addEventListener("change", () => {
    const label = input.closest(".upload-box");
    const text = label.querySelector("span");
    const files = input.files.length;
    text.textContent = files ? `${files} file selezionati` : "Carica schema, distinta o foto";
  });
});

document.addEventListener("keydown", event => { if (event.key === "Escape" && mainNav.classList.contains("is-open")) { mainNav.classList.remove("is-open"); menuButton.setAttribute("aria-expanded", "false"); menuButton.focus(); } });
