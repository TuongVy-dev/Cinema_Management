(function () {
    const MAX_SELECTION = 8;
    const khung = document.querySelector('.seat-map__grid');
    const list_chon = document.querySelector('.selected-seats__list');
    const hehehe = document.querySelector('a.btn.btn--large[href="checkout.html"]');
    const hintP = document.querySelector('.selected-seats p');
    let tinnhanhetthoigiancho = null;

    if (!khung || !list_chon) return;

    const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));
    const getSelectedSeats = () => qsa('.seat--selected', khung).map(s => s.dataset.seat);

    function announce(msg) {
        if (!hintP) return;
        hintP.textContent = msg;
        clearTimeout(tinnhanhetthoigiancho);
        tinnhanhetthoigiancho = setTimeout(() => {
            hintP.textContent = `Click on seats to select them. You can select up to ${MAX_SELECTION} seats.`;
        }, 2500);
    }

    function renderSelectedChips() {
        const seats = getSelectedSeats();
        list_chon.innerHTML = '';
        if (seats.length === 0) {
            list_chon.style.minHeight = '0';
            return;
        }
        seats.forEach(code => {
            const chip = document.createElement('span');
            chip.className = 'selected-seat';
            chip.dataset.seat = code;
            chip.textContent = code;
            chip.style.cssText = 'background-color: var(--primary); color: #fff; padding: .5rem 1rem; border-radius: 4px;';
            chip.setAttribute('role', 'button');
            chip.title = 'Remove';
            chip.addEventListener('click', () => {
                const seatEl = khung.querySelector(`.seat[data-seat="${code}"]`);
                if (seatEl) toggleSeat(seatEl, { force: false });
            });
            list_chon.appendChild(chip);
        });
    }

    function updateProceedState() {
        const seats = getSelectedSeats();
        const booking = {
            movie: 'The Last Guardian',
            cinema: 'CGV Vincom Center',
            showtime: '2024-12-15T19:30:00',
            seats
        };
        try {
            localStorage.setItem('booking.selectedSeats', JSON.stringify(booking));
        } catch (_) {}
    }

    function toggleSeat(el, options = {}) {
        if (!el || !el.classList.contains('seat')) return;
        if (el.classList.contains('seat--booked')) return;
        const willSelect = options.force === undefined ? !el.classList.contains('seat--selected') : !!options.force;
        if (willSelect) {
            const current = getSelectedSeats().length;
            if (current >= MAX_SELECTION) {
                announce(`You can select up to ${MAX_SELECTION} seats.`);
                return;
            }
        }
        el.classList.toggle('seat--selected', willSelect);
        el.setAttribute('aria-selected', willSelect ? 'true' : 'false');
        renderSelectedChips();
        updateProceedState();
    }

    khung.addEventListener('click', (e) => { const seat = e.target.closest('.seat');
        if (seat) toggleSeat(seat);
    });

    khung.addEventListener('keydown', (e) => {
        const seat = e.target.closest('.seat');
        if (!seat || seat.classList.contains('seat--booked')) return;
        if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            toggleSeat(seat);
        }
    });

    qsa('.seat', khung).forEach(seat => {
        if (seat.classList.contains('seat--booked')) {
            seat.setAttribute('aria-disabled', 'true');
            seat.removeAttribute('tabindex');
        } else {
            if (!seat.hasAttribute('tabindex')) seat.setAttribute('tabindex', '0');
            if (!seat.hasAttribute('aria-selected')) seat.setAttribute('aria-selected', 'false');
        }
    });

    renderSelectedChips();
    updateProceedState();

    if (hehehe) {
        hehehe.addEventListener('click', (e) => {
            const count = getSelectedSeats().length;
            if (count === 0) {
                e.preventDefault();
                announce('Please select at least 1 seat to continue.');
            }
        });
    }
})();