/* ==========================================================================
   Application Logic (App.js)
   Handles all Javascript interactions for login, local storage, and dashboard SPA.
   ========================================================================== */

// --- UTILITIES ---

// Helper to get or initialize LocalStorage Arrays
const getStorage = (key, defaultVal = []) => {
    const data = localStorage.getItem(key);
    return data ? JSON.parse(data) : defaultVal;
};

// Helper to save to LocalStorage
const setStorage = (key, data) => {
    localStorage.setItem(key, JSON.stringify(data));
};

// Generates a simple pseudo-random ID
const generateId = () => Math.random().toString(36).substr(2, 9);


// --- AUTHENTICATION (login.html) ---

function initAuth() {
    const authForm = document.getElementById('auth-form');
    if (!authForm) return;

    let isLoginMode = true;
    const toggleLink = document.getElementById('toggle-auth-link');
    const toggleText = document.getElementById('toggle-auth-text');
    const nameGroup = document.getElementById('name-group');
    const authBtn = document.getElementById('auth-btn');
    const authSubtitle = document.getElementById('auth-subtitle');
    const errorAlert = document.getElementById('auth-error');

    // Check if user is already logged in (simulated auth check)
    if (localStorage.getItem('currentUser')) {
        window.location.href = 'dashboard.html';
        return;
    }

    // Handle Auth Mode Toggle (Login vs Register)
    if (toggleLink) {
        // Also check URL params if standard link forces register
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('register') === 'true') {
            switchMode(false);
        }

        toggleLink.addEventListener('click', (e) => {
            e.preventDefault();
            switchMode(isLoginMode); // if login, switch to false (register)
        });
    }

    function switchMode(toRegister) {
        isLoginMode = !toRegister;
        errorAlert.style.display = 'none';
        
        if (!isLoginMode) {
            // Switch to Register Mode
            nameGroup.style.display = 'block';
            document.getElementById('reg-name').required = true;
            authBtn.textContent = 'Crear Cuenta';
            authSubtitle.textContent = 'Regístrate para continuar';
            toggleText.innerHTML = '¿Ya tienes una cuenta? <a href="#" id="toggle-auth-link">Inicia sesión</a>';
        } else {
            // Switch to Login Mode
            nameGroup.style.display = 'none';
            document.getElementById('reg-name').required = false;
            authBtn.textContent = 'Acceder';
            authSubtitle.textContent = 'Inicia sesión en tu agenda';
            toggleText.innerHTML = '¿No tienes credenciales? <a href="#" id="toggle-auth-link">Regístrate aquí</a>';
        }
        
        // Re-attach event listener to new injected link
        document.getElementById('toggle-auth-link').addEventListener('click', (e) => {
            e.preventDefault();
            switchMode(isLoginMode);
        });
    }

    // Handle Form Submit
    authForm.addEventListener('submit', (e) => {
        e.preventDefault();
        
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;
        const name = document.getElementById('reg-name').value.trim();

        const users = getStorage('users');

        if (isLoginMode) {
            // Simulate Login validation
            const user = users.find(u => u.username === username && u.password === password);
            if (user) {
                // Success
                localStorage.setItem('currentUser', JSON.stringify(user));
                window.location.href = 'dashboard.html';
            } else {
                errorAlert.textContent = 'Credenciales incorrectas. (Si no tienes cuenta, usa el enlace de abajo)';
                errorAlert.style.display = 'block';
            }
        } else {
            // Simulate Registration
            if (users.find(u => u.username === username)) {
                errorAlert.textContent = 'El usuario/email ya existe.';
                errorAlert.style.display = 'block';
                return;
            }

            const newUser = { id: generateId(), name: name || username, username, password };
            users.push(newUser);
            setStorage('users', users);
            
            // Auto login after registration
            localStorage.setItem('currentUser', JSON.stringify(newUser));
            window.location.href = 'dashboard.html';
        }
    });
}


// --- DASHBOARD SPA & LOGIC (dashboard.html) ---

function initDashboard() {
    // 1. Session Validation
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    if (!currentUser) {
        // Not logged in -> redirect
        window.location.href = 'login.html';
        return;
    }

    // Set user info
    const userNameDisplay = document.getElementById('user-display-name');
    if (userNameDisplay) userNameDisplay.textContent = currentUser.name;
    const avatar = document.querySelector('.avatar');
    if (avatar) avatar.textContent = currentUser.name.charAt(0).toUpperCase();

    // Logout
    document.getElementById('logout-btn').addEventListener('click', () => {
        localStorage.removeItem('currentUser');
        window.location.href = 'login.html';
    });

    // 2. SPA Navigation System
    const navItems = document.querySelectorAll('.nav-item');
    const views = document.querySelectorAll('.view-section');

    function switchView(targetId) {
        // Update Nav Menu UI
        navItems.forEach(nav => nav.classList.remove('active'));
        const activeNav = document.querySelector(`[data-target="${targetId}"]`);
        if(activeNav) activeNav.classList.add('active');

        // Toggle Views
        views.forEach(view => {
            view.classList.remove('active');
            if (view.id === targetId) {
                view.classList.add('active');
            }
        });

        // specific actions on view load
        if (targetId === 'view-list') renderContacts();
        if (targetId === 'view-search') {
            document.getElementById('search-input').focus();
            renderSearch();
        }
        
        // Hide sidebar on mobile after clicking
        document.getElementById('sidebar').classList.remove('open');
    }

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            switchView(item.getAttribute('data-target'));
        });
    });

    // Mobile menu toggle
    const menuBtn = document.getElementById('menu-toggle');
    if(menuBtn) {
        menuBtn.addEventListener('click', () => {
            document.getElementById('sidebar').classList.toggle('open');
        });
    }

    // 3. Contact CRUD Logic
    let contacts = getStorage(`contacts_${currentUser.id}`); // User-specific contacts

    // Render Function
    function renderContacts(containerId = 'contacts-container', filterStr = '') {
        const container = document.getElementById(containerId);
        const emptyState = document.getElementById('empty-state');
        if (!container) return;

        container.innerHTML = '';
        
        let displayContacts = contacts;
        
        // Apply filter if searching
        if (filterStr) {
            const lowerFilter = filterStr.toLowerCase();
            displayContacts = contacts.filter(c => 
                c.name.toLowerCase().includes(lowerFilter) || 
                c.phone.toLowerCase().includes(lowerFilter) ||
                (c.email && c.email.toLowerCase().includes(lowerFilter)) ||
                c.group.toLowerCase().includes(lowerFilter)
            );
        }

        // Handle Empty state displaying only in the main list view
        if (containerId === 'contacts-container') {
             if (displayContacts.length === 0) {
                 emptyState.style.display = 'block';
             } else {
                 emptyState.style.display = 'none';
             }
        }

        displayContacts.forEach(contact => {
             const card = document.createElement('div');
             card.className = 'contact-card';
             
             // Determine group color class
             let groupBg = '#E5E7EB';
             if (contact.group === 'Familia') groupBg = '#DBEAFE'; // blue
             if (contact.group === 'Trabajo') groupBg = '#FEF3C7'; // yellow
             if (contact.group === 'Amigos') groupBg = '#D1FAE5'; // green

             card.innerHTML = `
                <div class="contact-header">
                    <div class="contact-avatar">${contact.name.charAt(0).toUpperCase()}</div>
                    <div>
                        <h3 class="contact-name">${contact.name}</h3>
                        <span class="contact-group-badge" style="background-color: ${groupBg}">${contact.group}</span>
                    </div>
                </div>
                <div class="contact-info">
                    <p title="Teléfono">📞 ${contact.phone}</p>
                    <p title="Email">✉️ ${contact.email || 'Sin correo'}</p>
                </div>
                <div class="contact-actions">
                    <button class="btn btn-primary-outline btn-sm edit-btn" data-id="${contact.id}" title="Editar la información de ${contact.name}">Editar</button>
                    <button class="btn btn-danger btn-sm delete-btn" data-id="${contact.id}" title="Eliminar a ${contact.name} de la agenda">Eliminar</button>
                </div>
             `;
             container.appendChild(card);
        });

        // Attach action listeners
        container.querySelectorAll('.edit-btn').forEach(btn => {
            btn.addEventListener('click', (e) => loadContactIntoForm(e.target.dataset.id));
        });
        container.querySelectorAll('.delete-btn').forEach(btn => {
            btn.addEventListener('click', (e) => deleteContact(e.target.dataset.id));
        });
    }

    // Add / Update logic
    const contactForm = document.getElementById('contact-form');
    if (contactForm) {
        contactForm.addEventListener('submit', (e) => {
            e.preventDefault();
            
            const idInput = document.getElementById('contact-id').value;
            const newContact = {
                id: idInput ? idInput : generateId(),
                name: document.getElementById('contact-name').value.trim(),
                phone: document.getElementById('contact-phone').value.trim(),
                email: document.getElementById('contact-email').value.trim(),
                group: document.getElementById('contact-group').value
            };

            if (idInput) {
                // Update existing
                const index = contacts.findIndex(c => c.id === idInput);
                if(index > -1) contacts[index] = newContact;
            } else {
                // Add new
                contacts.push(newContact);
            }

            // Save and clean UI
            setStorage(`contacts_${currentUser.id}`, contacts);
            contactForm.reset();
            document.getElementById('contact-id').value = '';
            
            // Switch back to list view
            switchView('view-list');
        });

        // Cancel Edit behavior
        document.getElementById('cancel-edit-btn').addEventListener('click', () => {
            contactForm.reset();
            document.getElementById('contact-id').value = '';
            document.getElementById('form-title').textContent = 'Añadir Nuevo Contacto';
            document.getElementById('cancel-edit-btn').style.display = 'none';
            switchView('view-list');
        });
    }

    function loadContactIntoForm(id) {
        const c = contacts.find(c => c.id === id);
        if (!c) return;

        document.getElementById('contact-id').value = c.id;
        document.getElementById('contact-name').value = c.name;
        document.getElementById('contact-phone').value = c.phone;
        document.getElementById('contact-email').value = c.email || '';
        document.getElementById('contact-group').value = c.group;

        document.getElementById('form-title').textContent = 'Editar Contacto';
        document.getElementById('cancel-edit-btn').style.display = 'inline-block';
        
        switchView('view-add');
    }

    function deleteContact(id) {
        if (confirm('¿Estás seguro de que deseas eliminar este contacto?')) {
            contacts = contacts.filter(c => c.id !== id);
            setStorage(`contacts_${currentUser.id}`, contacts);
            
            // Re-render current view
            const activeViewId = document.querySelector('.view-section.active').id;
            if (activeViewId === 'view-search') {
                renderSearch();
            } else {
                renderContacts();
            }
        }
    }

    // Search logic
    const searchInput = document.getElementById('search-input');
    if(searchInput) {
        searchInput.addEventListener('input', () => {
             renderSearch();
        });
    }

    function renderSearch() {
        const filterStr = document.getElementById('search-input').value;
        const searchContainer = document.getElementById('search-results-container');
        
        if (contacts.length === 0) {
            searchContainer.innerHTML = '<p class="text-secondary">No tienes contactos creados aún.</p>';
            return;
        }

        if (filterStr.trim() === '') {
            searchContainer.innerHTML = '<p class="text-secondary">Escribe algo arriba para comenzar a buscar.</p>';
        } else {
            renderContacts('search-results-container', filterStr);
            if (searchContainer.childElementCount === 0) {
                 searchContainer.innerHTML = '<p class="text-secondary">No se encontraron resultados que coincidan con la búsqueda.</p>';
            }
        }
    }

    // Initial render
    renderContacts();
    
    // Auto-check URL query parameters for action intents (e.g. from index.html links to login)
    // If a user clicks "Añadir contacto" on index and isn't logged in, they hit login.html?action=add
    // When login is successful, we didn't save the action intent (for simplicity in this prototype),
    // but we can route them properly next time or just default to list.
}
