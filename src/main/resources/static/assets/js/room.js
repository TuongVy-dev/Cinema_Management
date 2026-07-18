// =============== PAGE INIT ===============
document.addEventListener('DOMContentLoaded', () => {
    // active menu
    document.querySelectorAll('.navbar-nav .nav-link').forEach(a => a.classList.remove('active'));
    const link = document.querySelector('.navbar-nav .nav-link[href="room_management.html"]');
    if (link) link.classList.add('active');

    // demo data
    rooms = [
        { id: 1, code: 'R1', name: 'Room 1', type: '3D', seats: 155, status: 'Active', notes: '', seatMapJson: '' },
        { id: 2, code: 'R2', name: 'Room 2', type: '2D', seats: 120, status: 'Inactive', notes: '', seatMapJson: '' }
    ];
    renderRooms();
    setTimeout(() => document.getElementById('spinner').classList.remove('show'), 300);
});

// =============== STATE (Demo) ===============
let rooms = [];
let editingId = null;
let currentBrush = 'available';
let seatCells = [];

// =============== DOM REFERENCES ===============
const roomCode = document.getElementById('roomCode');
const roomName = document.getElementById('roomName');
const roomType = document.getElementById('roomType');
const roomSeats = document.getElementById('roomSeats');
const roomStatus = document.getElementById('roomStatus');
const roomNotes = document.getElementById('roomNotes');
const seatRows = document.getElementById('seatRows');
const seatCols = document.getElementById('seatCols');
const seatMapJson = document.getElementById('seatMapJson');
const seatGrid = document.getElementById('seatGrid');
const seatEditorModal = document.getElementById('seatEditorModal');

const btnOpenSeatEditor = document.getElementById('btnOpenSeatEditor');
const btnClearAllSeats = document.getElementById('btnClearAllSeats');
const btnFillAllSeats = document.getElementById('btnFillAllSeats');
const btnSaveSeatMap = document.getElementById('btnSaveSeatMap');

// =============== RENDER TABLE ===============
function renderRooms() {
    const tbody = document.getElementById('roomTbody');
    tbody.innerHTML = '';
    rooms.forEach(r => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${r.code}</td>
            <td>${r.name}</td>
            <td>${r.type}</td>
            <td>${r.seats}</td>
            <td>${r.status}</td>
            <td class="text-end">
                <button class="btn btn-outline-light btn-sm me-2" data-id="${r.id}" onclick="openEdit(${r.id})">
                    <i class="bi bi-pencil-square me-1"></i>Edit
                </button>
                <button class="btn btn-danger btn-sm" onclick="removeRoom(${r.id})">
                    <i class="bi bi-x-circle me-1"></i>Delete
                </button>
            </td>`;
        tbody.appendChild(tr);
    });
}

// =============== ADD / EDIT ===============
document.getElementById('btnAddRoom').addEventListener('click', () => {
    editingId = null;
    document.getElementById('roomModalTitle').textContent = 'Add room';
    document.getElementById('roomForm').reset();
    seatRows.value = 10;
    seatCols.value = 12;
    seatMapJson.value = '';
    new bootstrap.Modal(document.getElementById('roomModal')).show();
});

function openEdit(id) {
    const r = rooms.find(x => x.id === id);
    if (!r) return;
    editingId = id;
    document.getElementById('roomModalTitle').textContent = 'Edit room';
    roomCode.value = r.code;
    roomName.value = r.name;
    roomType.value = r.type;
    roomSeats.value = r.seats;
    roomStatus.value = r.status;
    roomNotes.value = r.notes || '';
    seatMapJson.value = r.seatMapJson || '';

    try {
        if (r.seatMapJson) {
            const m = JSON.parse(r.seatMapJson);
            seatRows.value = m.length;
            seatCols.value = m[0]?.length || 0;
        } else {
            seatRows.value = 10; seatCols.value = 12;
        }
    } catch (_) {
        seatRows.value = 10; seatCols.value = 12;
    }

    new bootstrap.Modal(document.getElementById('roomModal')).show();
}

document.getElementById('btnSaveRoom').addEventListener('click', () => {
    const payload = {
        code: roomCode.value.trim(),
        name: roomName.value.trim(),
        type: roomType.value,
        seats: parseInt(roomSeats.value || '0', 10),
        status: roomStatus.value,
        notes: roomNotes.value.trim(),
        seatMapJson: seatMapJson.value || ''
    };
    if (!payload.code || !payload.name || !payload.type || !payload.seats || !payload.status) {
        alert('Please fill in all required fields.');
        return;
    }

    if (editingId) {
        const idx = rooms.findIndex(x => x.id === editingId);
        rooms[idx] = { id: editingId, ...payload };
    } else {
        const newId = (rooms.at(-1)?.id || 0) + 1;
        rooms.push({ id: newId, ...payload });
    }
    renderRooms();
    bootstrap.Modal.getInstance(document.getElementById('roomModal')).hide();
});

function removeRoom(id) {
    if (confirm('Delete this room?')) {
        rooms = rooms.filter(x => x.id !== id);
        renderRooms();
    }
}

// =============== SEAT EDITOR ===============
function alpha(n) { return String.fromCharCode(65 + n); }

function buildSeatGrid(rows, cols, existing) {
    seatGrid.style.setProperty('--cols', cols);
    seatGrid.innerHTML = '';
    seatCells = [];

    for (let r = 0; r < rows; r++) {
        const lab = document.createElement('div');
        lab.className = 'seat-row-label';
        lab.textContent = alpha(r);
        seatGrid.appendChild(lab);

        const rowArr = [];
        for (let c = 0; c < cols; c++) {
            const cell = document.createElement('div');
            const label = alpha(r) + (c + 1);
            const state = existing?.[r]?.[c]?.state || 'available';
            cell.className = `seat ${state}`;
            cell.dataset.state = state;
            cell.dataset.r = r;
            cell.dataset.c = c;
            cell.textContent = label;
            cell.addEventListener('click', () => paintSeat(cell));
            seatGrid.appendChild(cell);
            rowArr.push({ r, c, label, state });
        }
        seatCells.push(rowArr);
    }
}

function paintSeat(cell) {
    const r = +cell.dataset.r, c = +cell.dataset.c;
    seatCells[r][c].state = currentBrush;
    cell.className = `seat ${currentBrush}`;
    cell.dataset.state = currentBrush;
}

// Brush buttons
document.querySelectorAll('.seat-tool').forEach(b => {
    b.addEventListener('click', () => {
        document.querySelectorAll('.seat-tool').forEach(x => x.classList.remove('active'));
        b.classList.add('active');
        currentBrush = b.dataset.tool;
    });
});
document.querySelector('.seat-tool[data-tool="available"]')?.classList.add('active');

// Open editor
btnOpenSeatEditor.addEventListener('click', () => {
    const rows = Math.max(1, Math.min(26, +seatRows.value || 10));
    const cols = Math.max(1, Math.min(30, +seatCols.value || 12));
    let existing = null;
    try {
        const json = seatMapJson.value;
        if (json) existing = JSON.parse(json);
    } catch (e) { existing = null; }
    buildSeatGrid(rows, cols, existing);
    new bootstrap.Modal(seatEditorModal).show();
});

// Clear / Fill
btnClearAllSeats.addEventListener('click', () => {
    seatCells.flat().forEach(s => {
        s.state = 'empty';
        const el = document.querySelector(`.seat[data-r="${s.r}"][data-c="${s.c}"]`);
        if (el) { el.className = 'seat empty'; el.dataset.state = 'empty'; }
    });
});

btnFillAllSeats.addEventListener('click', () => {
    seatCells.flat().forEach(s => {
        s.state = 'available';
        const el = document.querySelector(`.seat[data-r="${s.r}"][data-c="${s.c}"]`);
        if (el) { el.className = 'seat available'; el.dataset.state = 'available'; }
    });
});

// Save seat map
btnSaveSeatMap.addEventListener('click', () => {
    const payload = seatCells.map(row => row.map(({ r, c, label, state }) => ({ r, c, label, state })));
    seatMapJson.value = JSON.stringify(payload);

    const seatsCount = payload.flat().filter(s => s.state !== 'empty').length;
    roomSeats.value = seatsCount;

    bootstrap.Modal.getInstance(seatEditorModal).hide();
});
