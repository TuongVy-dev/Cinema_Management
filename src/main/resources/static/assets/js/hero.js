document.addEventListener("DOMContentLoaded", () => {
  const slides = document.querySelectorAll(".hero__slide");
  const dots = document.querySelectorAll(".hero__dot");
  const prevBtn = document.querySelector(".hero__arrow--prev");
  const nextBtn = document.querySelector(".hero__arrow--next");

  let index = 0;
  const total = slides.length;

  function showSlide(i) {
    slides.forEach((s, n) => s.classList.toggle("is-active", n === i));
    dots.forEach((d, n) => d.classList.toggle("is-active", n === i));
    index = i;
  }

  function next() { showSlide((index + 1) % total); }
  function prev() { showSlide((index - 1 + total) % total); }

  nextBtn.addEventListener("click", next);
  prevBtn.addEventListener("click", prev);
  dots.forEach((dot, i) => dot.addEventListener("click", () => showSlide(i)));

  // Auto switch
  setInterval(next, 3000);
});
