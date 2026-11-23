
const menuButton = document.getElementById("menu-toggle");
const menu = document.getElementById("nav-menu");
const yearText = document.getElementById("year");


function setMenu(isOpen) {
  menu.classList.toggle("is-open", isOpen);
  menuButton.setAttribute("aria-expanded", isOpen);
  menuButton.setAttribute("aria-label", isOpen ? "Close menu" : "Open menu");
}

menuButton.addEventListener("click", function () {
  setMenu(!menu.classList.contains("is-open"));
});

menu.querySelectorAll("a").forEach(function (link) {
  link.addEventListener("click", function () {
    setMenu(false);
  });
});

document.addEventListener("keydown", function (event) {
  if (event.key === "Escape") {
    setMenu(false);
  }
});


yearText.textContent = new Date().getFullYear();
