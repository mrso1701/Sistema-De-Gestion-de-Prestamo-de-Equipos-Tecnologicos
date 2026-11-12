
function mostrarAviso() {
    alert("Esta función estará disponible cuando conectemos el backend.");
}

// Inicio de sesión
const formLogin = document.getElementById("formLogin");

if (formLogin) {
    formLogin.addEventListener("submit", function(event) {
        event.preventDefault();

        alert("El inicio de sesión estará disponible cuando conectemos el backend.");
    });
}

// Búsqueda de usuarios
const formBuscarUsuario = document.getElementById("formBuscarUsuario");

if (formBuscarUsuario) {
    formBuscarUsuario.addEventListener("submit", function(event) {
        event.preventDefault();

        document.getElementById("resultadoUsuario").textContent =
            "La consulta estará disponible cuando conectemos el backend.";
    });
}

// Registrar préstamo
const formPrestamo = document.getElementById("formPrestamo");

if (formPrestamo) {
    formPrestamo.addEventListener("submit", function(event) {
        event.preventDefault();

        alert("El registro del préstamo estará disponible cuando conectemos el backend.");
    });
}

// Registrar devolución
const formDevolucion = document.getElementById("formDevolucion");

if (formDevolucion) {
    formDevolucion.addEventListener("submit", function(event) {
        event.preventDefault();

        alert("El registro de la devolución estará disponible cuando conectemos el backend.");
    });
}

// Buscar sanciones
const formBuscarSancion = document.getElementById("formBuscarSancion");

if (formBuscarSancion) {
    formBuscarSancion.addEventListener("submit", function(event) {
        event.preventDefault();

        alert("La consulta de sanciones estará disponible cuando conectemos el backend.");
    });
}
