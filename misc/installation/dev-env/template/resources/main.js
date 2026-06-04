'use strict';

// Custom cursor
const cursor    = document.querySelector('.cursor');
const cursorDot = document.querySelector('.cursor-dot');

document.addEventListener('mousemove', e => {
  cursor.style.left    = e.clientX + 'px';
  cursor.style.top     = e.clientY + 'px';
  cursorDot.style.left = e.clientX + 'px';
  cursorDot.style.top  = e.clientY + 'px';
});

document.addEventListener('mousedown', () => {
  cursor.style.transform = 'translate(-50%, -50%) scale(1.6)';
  cursor.style.opacity   = '0.5';
});
document.addEventListener('mouseup', () => {
  cursor.style.transform = 'translate(-50%, -50%) scale(1)';
  cursor.style.opacity   = '1';
});

// Clock in top bar
function updateClock() {
  const el = document.getElementById('clock');
  if (!el) return;
  const now = new Date();
  const pad = n => String(n).padStart(2, '0');
  el.textContent = `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
}
updateClock();
setInterval(updateClock, 1000);

// Boot sequence: reveal terminal after short delay
window.addEventListener('DOMContentLoaded', () => {
  const terminal = document.querySelector('.terminal');
  const bootLines = document.querySelectorAll('.boot-line');

  // Show each boot line with a stagger
  bootLines.forEach((line, i) => {
    setTimeout(() => {
      line.style.opacity = '1';
      line.style.transform = 'translateX(0)';
    }, i * 120);
  });

  // Then reveal the main terminal
  const delay = bootLines.length * 120 + 400;
  setTimeout(() => {
    terminal.classList.add('visible');
  }, delay);
});

// Random matrix-rain char flicker on the banner
function randomFlicker() {
  const banner = document.querySelector('pre.banner');
  if (!banner) return;

  // Wrap each char span (already done inline), just pick a random one and flash it
  const chars = banner.querySelectorAll('span.c');
  if (!chars.length) return;

  const idx  = Math.floor(Math.random() * chars.length);
  const el   = chars[idx];
  const orig = el.style.opacity;
  el.style.opacity = '0.2';
  el.style.color   = '#00ff41';
  setTimeout(() => {
    el.style.opacity = orig || '1';
  }, 80);
}

setInterval(randomFlicker, 60);

// Glitch trigger on hover
const terminal = document.querySelector('.terminal');
if (terminal) {
  terminal.addEventListener('mouseenter', () => {
    const banner = document.querySelector('pre.banner');
    if (banner) {
      banner.style.animation = 'none';
      void banner.offsetWidth; // reflow
      banner.style.animation = '';
    }
  });
}
