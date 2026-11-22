
// =========================================================
// CLIENTE API (CONEXIÓN CON Vista.java EN PUERTO 8080)
// =========================================================
const API = {
    async obtenerUsuarios() {
        const res = await fetch("/api/usuarios");
        return await res.json();
    },

    async registrarUsuario(usuario) {
        const res = await fetch("/api/usuarios", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(usuario)
        });
        return await res.json();
    },

    // NUEVO: INICIAR SESIÓN
    async iniciarSesion(correo, contrasena) {
        const res = await fetch("/api/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                correo: correo,
                contrasena: contrasena
            })
        });
        return await res.json();
    },

    async obtenerEquipos() {
        const res = await fetch("/api/equipos");
        return await res.json();
    },

    async registrarEquipo(equipo) {
        const res = await fetch("/api/equipos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(equipo)
        });
        return await res.json();
    },

    async obtenerPrestamos() {
        const res = await fetch("/api/prestamos");
        return await res.json();
    },

    async registrarPrestamo(codigoInstitucional, codigoInventario, fechaDevolucion, observaciones) {
        const res = await fetch("/api/prestamos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                accion: "PRESTAR",
                codigoInstitucional,
                codigoInventario,
                fechaDevolucion,
                observaciones
            })
        });
        return await res.json();
    },

    async devolverEquipo(idPrestamo, observaciones) {
        const res = await fetch("/api/prestamos", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                accion: "DEVOLVER",
                idPrestamo: String(idPrestamo),
                observaciones
            })
        });
        return await res.json();
    },

    async obtenerSanciones() {
        const res = await fetch("/api/sanciones");
        return await res.json();
    },

    async aplicarSancion(codigoInstitucional, prestamoId, motivo, diasSancion, observaciones) {
        const res = await fetch("/api/sanciones", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                accion: "APLICAR",
                codigoInstitucional,
                prestamoId: Number(prestamoId || 0),
                motivo,
                diasSancion: Number(diasSancion),
                observaciones
            })
        });
        return await res.json();
    }
};


// =========================================================
// 1. INICIO DE SESIÓN (login.html)
// =========================================================
const formLogin = document.getElementById("formLogin");

if (formLogin) {
    formLogin.addEventListener("submit", async function (event) {
        event.preventDefault();

        const correo = document.getElementById("correo").value.trim().toLowerCase();
        const contrasena = document.getElementById("contrasena").value;

        try {
            const respuesta = await API.iniciarSesion(correo, contrasena);

            if (respuesta.usuario) {
                sessionStorage.setItem(
                    "usuarioActivo",
                    JSON.stringify(respuesta.usuario)
                );

                window.location.href = "inicio.html";
            } else {
                alert(respuesta.mensaje || "Correo o contraseña incorrectos.");
            }

        } catch (error) {
            console.error("Error al iniciar sesión:", error);
            alert("Error de conexión con el servidor Java.");
        }
    });
}


// =========================================================
// 2. GESTIÓN DE USUARIOS (usuarios.html)
// =========================================================
const formBuscarUsuario = document.getElementById("formBuscarUsuario");
const formRegistrarUsuario = document.getElementById("formRegistrarUsuario");

async function cargarTablaUsuarios() {
    const tbody = document.querySelector("table tbody");
    if (!tbody || !formBuscarUsuario) return;

    try {
        const usuarios = await API.obtenerUsuarios();

        if (usuarios.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="vacio">No hay usuarios registrados todavía.</td></tr>`;
            return;
        }

        tbody.innerHTML = usuarios.map(u => `
            <tr>
                <td>${u.documentoIdentidad || u.codigoInstitucional || "-"}</td>
                <td>${u.nombre || ""} ${u.apellido || ""}</td>
                <td>${u.correoInstitucional || "-"}</td>
                <td>${u.tipoUsuario || "ESTUDIANTE"}</td>
                <td>${u.estado || "ACTIVO"}</td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Error al cargar tabla de usuarios:", error);
    }
}

if (formBuscarUsuario) {
    cargarTablaUsuarios();

    formBuscarUsuario.addEventListener("submit", async function (event) {
        event.preventDefault();

        const criterio = document.getElementById("documento").value.trim().toLowerCase();
        const resultado = document.getElementById("resultadoUsuario");

        try {
            const usuarios = await API.obtenerUsuarios();

            const encontrado = usuarios.find(
                u => (u.documentoIdentidad && u.documentoIdentidad.toLowerCase() === criterio) ||
                    (u.codigoInstitucional && u.codigoInstitucional.toLowerCase() === criterio)
            );

            if (encontrado) {
                resultado.textContent = `Encontrado: ${encontrado.nombre} ${encontrado.apellido || ""} | Cód: ${encontrado.codigoInstitucional} | Correo: ${encontrado.correoInstitucional} | Estado: ${encontrado.estado}`;
            } else {
                resultado.textContent = "No se encontró ningún usuario con ese documento o código.";
            }

        } catch (error) {
            resultado.textContent = "Error al consultar el servidor.";
        }
    });
}


// REGISTRAR USUARIO CON CONTRASEÑA
if (formRegistrarUsuario) {
    formRegistrarUsuario.addEventListener("submit", async function (event) {
        event.preventDefault();

        const contrasena = document.getElementById("regContrasena").value;
        const confirmarContrasena = document.getElementById("regConfirmarContrasena").value;

        // Comprobar que las contraseñas coincidan
        if (contrasena !== confirmarContrasena) {
            alert("Las contraseñas no coinciden.");
            return;
        }

        // Comprobar longitud mínima
        if (contrasena.length < 8) {
            alert("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        const nuevoUsuario = {
            codigoInstitucional: document.getElementById("regCodigo").value.trim(),
            documentoIdentidad: document.getElementById("regDocumento").value.trim(),
            nombre: document.getElementById("regNombre").value.trim(),
            apellido: document.getElementById("regApellido").value.trim(),
            correoInstitucional: document.getElementById("regCorreo").value.trim().toLowerCase(),
            telefono: document.getElementById("regTelefono").value.trim(),
            tipoUsuario: document.getElementById("regTipo").value,
            estado: "ACTIVO",

            // Java recibirá esta contraseña y generará su hash.
            contrasena: contrasena
        };

        try {
            const respuesta = await API.registrarUsuario(nuevoUsuario);

            alert(respuesta.mensaje || "No se recibió una respuesta válida del servidor.");

            if (respuesta.mensaje && respuesta.mensaje.startsWith("ÉXITO")) {
                formRegistrarUsuario.reset();
            }

        } catch (error) {
            console.error("Error al registrar usuario:", error);
            alert("Error al conectar con el servidor para registrar el usuario.");
        }
    });
}


// =========================================================
// 3. GESTIÓN DE EQUIPOS (equipos.html)
// =========================================================
const formRegistrarEquipo = document.getElementById("formRegistrarEquipo");

async function cargarTablaEquipos() {
    const tbody = document.querySelector("table tbody");
    const esVistaEquipos = formRegistrarEquipo || document.querySelector(".fila-titulo .boton");

    if (!tbody || !esVistaEquipos) return;

    try {
        const equipos = await API.obtenerEquipos();

        if (equipos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="vacio">No hay equipos registrados todavía.</td></tr>`;
            return;
        }

        tbody.innerHTML = equipos.map(eq => `
            <tr>
                <td>${eq.codigoInventario || "-"}</td>
                <td>${eq.marca || "-"}</td>
                <td>${eq.modelo || "-"}</td>
                <td>${eq.tipoEquipo || "-"}</td>
                <td>${eq.estado || "DISPONIBLE"}</td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Error al cargar tabla de equipos:", error);
    }
}

if (document.querySelector(".fila-titulo .boton") || formRegistrarEquipo) {
    cargarTablaEquipos();
}

if (formRegistrarEquipo) {
    formRegistrarEquipo.addEventListener("submit", async function (event) {
        event.preventDefault();

        const nuevoEquipo = {
            codigoInventario: document.getElementById("regCodigoInventario").value.trim(),
            marca: document.getElementById("regMarca").value.trim(),
            modelo: document.getElementById("regModelo").value.trim(),
            tipoEquipo: document.getElementById("regTipoEquipo").value.trim(),
            estado: "DISPONIBLE",
            observacionesEstado: document.getElementById("regObservacionesEquipo")?.value.trim() || "En buen estado"
        };

        try {
            const respuesta = await API.registrarEquipo(nuevoEquipo);

            alert(respuesta.mensaje);

            if (respuesta.mensaje.startsWith("ÉXITO")) {
                formRegistrarEquipo.reset();
                cargarTablaEquipos();
            }

        } catch (error) {
            alert("Error al conectar con el servidor para registrar el equipo.");
        }
    });
}


// Respaldo mientras tu compañera agrega el formulario HTML en equipos.html
async function mostrarAviso() {
    const codigoInventario = prompt("Código de inventario (ej. EQ-001):");
    if (!codigoInventario) return;

    const marca = prompt("Marca (ej. Dell, Lenovo):");
    if (!marca) return;

    const modelo = prompt("Modelo (ej. Latitude 5420):");
    if (!modelo) return;

    const tipoEquipo = prompt("Tipo de equipo (ej. Portátil, Proyector):") || "Portátil";

    const respuesta = await API.registrarEquipo({
        codigoInventario: codigoInventario.trim(),
        marca: marca.trim(),
        modelo: modelo.trim(),
        tipoEquipo: tipoEquipo.trim(),
        estado: "DISPONIBLE",
        observacionesEstado: "Sin novedades"
    });

    alert(respuesta.mensaje);

    if (respuesta.mensaje.startsWith("ÉXITO")) {
        cargarTablaEquipos();
    }
}


// =========================================================
// 4. GESTIÓN DE PRÉSTAMOS (prestamos.html)
// =========================================================
const formPrestamo = document.getElementById("formPrestamo");
const formDevolucion = document.getElementById("formDevolucion");

async function cargarTablaPrestamos() {
    const tbody = document.querySelector("table tbody");
    if (!tbody || !formPrestamo) return;

    try {
        const [prestamos, usuarios, equipos] = await Promise.all([
            API.obtenerPrestamos(),
            API.obtenerUsuarios(),
            API.obtenerEquipos()
        ]);

        if (prestamos.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="vacio">No hay préstamos registrados todavía.</td></tr>`;
            return;
        }

        tbody.innerHTML = prestamos.map(p => {
            const usr = usuarios.find(u => u.id === p.usuarioId);
            const eq = equipos.find(e => e.id === p.equipoId);

            const textoUsuario = usr ? `${usr.nombre} (${usr.codigoInstitucional})` : `ID #${p.usuarioId}`;
            const textoEquipo = eq ? `${eq.marca} ${eq.modelo} (${eq.codigoInventario})` : `ID #${p.equipoId}`;

            return `
                <tr>
                    <td>${p.codigoPrestamo || p.id || "-"}</td>
                    <td>${textoUsuario}</td>
                    <td>${textoEquipo}</td>
                    <td>${p.fechaHoraSalida ? p.fechaHoraSalida.replace("T", " ").substring(0, 16) : "-"}</td>
                    <td>${p.estadoPrestamo || "ACTIVO"}</td>
                </tr>
            `;
        }).join("");

    } catch (error) {
        console.error("Error al cargar tabla de préstamos:", error);
    }
}

if (formPrestamo) {
    cargarTablaPrestamos();

    formPrestamo.addEventListener("submit", async function (event) {
        event.preventDefault();

        const criterioUsuario = document.getElementById("documentoPrestamo").value.trim().toLowerCase();
        const codigoInventario = document.getElementById("codigoEquipo").value.trim();
        const fechaDevolucion = document.getElementById("fechaDevolucion").value;
        const observaciones = document.getElementById("observacionesEntrega").value.trim() || "Préstamo estándar";

        try {
            const usuarios = await API.obtenerUsuarios();

            const usr = usuarios.find(
                u => (u.documentoIdentidad && u.documentoIdentidad.toLowerCase() === criterioUsuario) ||
                    (u.codigoInstitucional && u.codigoInstitucional.toLowerCase() === criterioUsuario)
            );

            const codigoInstitucional = usr ? usr.codigoInstitucional : criterioUsuario;

            const respuesta = await API.registrarPrestamo(
                codigoInstitucional,
                codigoInventario,
                fechaDevolucion,
                observaciones
            );

            alert(respuesta.mensaje);

            if (respuesta.mensaje.startsWith("ÉXITO")) {
                formPrestamo.reset();
                cargarTablaPrestamos();
            }

        } catch (error) {
            alert("Error al conectar con el servidor para registrar el préstamo.");
        }
    });
}

if (formDevolucion) {
    formDevolucion.addEventListener("submit", async function (event) {
        event.preventDefault();

        const codigoIngresado = document.getElementById("codigoPrestamo").value.trim();
        const observaciones = document.getElementById("observacionesDevolucion").value.trim() || "Equipo devuelto sin novedad";

        try {
            const prestamos = await API.obtenerPrestamos();

            const prestamoEncontrado = prestamos.find(
                p => String(p.id) === codigoIngresado ||
                    (p.codigoPrestamo && p.codigoPrestamo.toLowerCase() === codigoIngresado.toLowerCase())
            );

            const idNumerico = prestamoEncontrado ? prestamoEncontrado.id : codigoIngresado;

            const respuesta = await API.devolverEquipo(idNumerico, observaciones);

            alert(respuesta.mensaje);

            if (respuesta.mensaje.startsWith("ÉXITO")) {
                formDevolucion.reset();
                cargarTablaPrestamos();
            }

        } catch (error) {
            alert("Error al conectar con el servidor para registrar la devolución.");
        }
    });
}


// =========================================================
// 5. GESTIÓN DE SANCIONES (sanciones.html)
// =========================================================
const formBuscarSancion = document.getElementById("formBuscarSancion");
const formRegistrarSancion = document.getElementById("formRegistrarSancion");

async function cargarTablaSanciones(filtroDocumento = "") {
    const tbody = document.querySelector("table tbody");
    if (!tbody || !formBuscarSancion) return;

    try {
        const [sanciones, usuarios] = await Promise.all([
            API.obtenerSanciones(),
            API.obtenerUsuarios()
        ]);

        let lista = sanciones;

        if (filtroDocumento) {
            const criterio = filtroDocumento.toLowerCase();

            const usr = usuarios.find(
                u => (u.documentoIdentidad && u.documentoIdentidad.toLowerCase() === criterio) ||
                    (u.codigoInstitucional && u.codigoInstitucional.toLowerCase() === criterio)
            );

            lista = usr ? sanciones.filter(s => s.usuarioId === usr.id) : [];
        }

        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="vacio">No hay sanciones registradas todavía.</td></tr>`;
            return;
        }

        tbody.innerHTML = lista.map(s => {
            const usr = usuarios.find(u => u.id === s.usuarioId);

            const textoUsuario = usr ? `${usr.nombre} (${usr.documentoIdentidad || usr.codigoInstitucional})` : `ID #${s.usuarioId}`;

            return `
                <tr>
                    <td>${textoUsuario}</td>
                    <td>${s.motivo || "-"}</td>
                    <td>${s.fechaInicio ? s.fechaInicio.replace("T", " ").substring(0, 16) : "-"}</td>
                    <td>${s.fechaFin ? s.fechaFin.replace("T", " ").substring(0, 16) : "-"}</td>
                    <td>${s.estado || "ACTIVA"}</td>
                </tr>
            `;
        }).join("");

    } catch (error) {
        console.error("Error al cargar tabla de sanciones:", error);
    }
}

if (formBuscarSancion) {
    cargarTablaSanciones();

    formBuscarSancion.addEventListener("submit", function (event) {
        event.preventDefault();

        const documento = document.getElementById("documentoSancion").value.trim();
        cargarTablaSanciones(documento);
    });
}

if (formRegistrarSancion) {
    formRegistrarSancion.addEventListener("submit", async function (event) {
        event.preventDefault();

        const criterioUsuario = document.getElementById("regDocumentoSancion").value.trim().toLowerCase();
        const prestamoId = document.getElementById("regPrestamoId")?.value.trim() || 0;
        const motivo = document.getElementById("regMotivoSancion").value.trim();
        const diasSancion = document.getElementById("regDiasSancion").value.trim();
        const observaciones = document.getElementById("regObservacionesSancion")?.value.trim() || "";

        try {
            const usuarios = await API.obtenerUsuarios();

            const usr = usuarios.find(
                u => (u.documentoIdentidad && u.documentoIdentidad.toLowerCase() === criterioUsuario) ||
                    (u.codigoInstitucional && u.codigoInstitucional.toLowerCase() === criterioUsuario)
            );

            const codigoInstitucional = usr ? usr.codigoInstitucional : criterioUsuario;

            const respuesta = await API.aplicarSancion(
                codigoInstitucional,
                prestamoId,
                motivo,
                diasSancion,
                observaciones
            );

            alert(respuesta.mensaje);

            if (respuesta.mensaje.startsWith("ÉXITO")) {
                formRegistrarSancion.reset();
                cargarTablaSanciones();
            }

        } catch (error) {
            alert("Error al conectar con el servidor para aplicar la sanción.");
        }
    });
}
