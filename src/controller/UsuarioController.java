
package controller;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Usuario;
import persistencia.JsonManager;

public class UsuarioController {

    private static final String ARCHIVO_JSON = "usuarios.json";

    private final JsonManager jsonManager;

    private List<Usuario> listaUsuarios;
    private Map<String, Usuario> mapaPorCodigoInstitucional;
    private int idContador;

    public UsuarioController() {

        this.jsonManager = new JsonManager();
        this.mapaPorCodigoInstitucional = new HashMap<>();

        // Cargar usuarios persistidos en data/usuarios.json
        Type tipoLista = new TypeToken<LinkedList<Usuario>>() {}.getType();

        List<Usuario> cargados = jsonManager.cargarLista(
                ARCHIVO_JSON,
                tipoLista
        );

        this.listaUsuarios = (cargados != null)
                ? new LinkedList<>(cargados)
                : new LinkedList<>();

        // Reconstruir el mapa y calcular el siguiente ID
        int maxId = 0;

        for (Usuario u : this.listaUsuarios) {

            if (u.getCodigoInstitucional() != null) {
                this.mapaPorCodigoInstitucional.put(
                        u.getCodigoInstitucional(),
                        u
                );
            }

            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }

        this.idContador = maxId + 1;
    }

    // --- REGISTRAR USUARIO ---

    public String registrarUsuario(
            Usuario nuevoUsuario,
            String contrasena
    ) {

        if (nuevoUsuario == null) {
            return "ERROR: Los datos del usuario son obligatorios.";
        }

        if (nuevoUsuario.getCodigoInstitucional() == null
                || nuevoUsuario.getCodigoInstitucional().trim().isEmpty()
                || nuevoUsuario.getNombre() == null
                || nuevoUsuario.getNombre().trim().isEmpty()
                || nuevoUsuario.getCorreoInstitucional() == null
                || nuevoUsuario.getCorreoInstitucional().trim().isEmpty()
                || contrasena == null
                || contrasena.trim().isEmpty()) {

            return "ERROR: El código institucional, nombre, correo y contraseña son obligatorios.";
        }

        String codigo = nuevoUsuario.getCodigoInstitucional().trim();

        String correo = nuevoUsuario.getCorreoInstitucional()
                .trim()
                .toLowerCase();

        // Validar correo institucional
        if (!correo.endsWith("@unicolombo.edu.co")
                && !correo.endsWith("@unicartagena.edu.co")) {

            return "ERROR: Debe ingresar un correo institucional válido.";
        }

        // Comprobar código institucional duplicado
        if (mapaPorCodigoInstitucional.containsKey(codigo)) {
            return "ERROR: Ya existe un usuario con este código institucional.";
        }

        // Comprobar correo duplicado
        for (Usuario usuario : listaUsuarios) {

            if (usuario.getCorreoInstitucional() != null
                    && usuario.getCorreoInstitucional()
                            .trim()
                            .equalsIgnoreCase(correo)) {

                return "ERROR: Este correo institucional ya está registrado.";
            }
        }

        // Guardar los datos normalizados
        nuevoUsuario.setCodigoInstitucional(codigo);
        nuevoUsuario.setCorreoInstitucional(correo);

        // Generar hash de la contraseña
        String hash = PasswordUtil.generarHash(contrasena);

        nuevoUsuario.setContrasenaHash(hash);

        // Asignar ID de sistema
        nuevoUsuario.setId(idContador++);

        // Estado inicial del usuario
        if (nuevoUsuario.getEstado() == null
                || nuevoUsuario.getEstado().trim().isEmpty()) {

            nuevoUsuario.setEstado("ACTIVO");
        }

        // Agregar usuario a la lista y al mapa
        listaUsuarios.add(nuevoUsuario);

        mapaPorCodigoInstitucional.put(
                codigo,
                nuevoUsuario
        );

        // Guardar cambios en JSON
        jsonManager.guardarLista(
                ARCHIVO_JSON,
                listaUsuarios
        );

        return "ÉXITO: Usuario registrado correctamente con ID de sistema: "
                + nuevoUsuario.getId();
    }

    // --- INICIAR SESIÓN ---

    public Usuario iniciarSesion(
            String correo,
            String contrasena
    ) {

        if (correo == null
                || contrasena == null
                || correo.trim().isEmpty()
                || contrasena.isEmpty()) {

            return null;
        }

        // Buscar usuario por correo
        for (Usuario usuario : listaUsuarios) {

            if (usuario.getCorreoInstitucional() != null
                    && usuario.getCorreoInstitucional()
                            .equalsIgnoreCase(correo.trim())) {

                // Comprobar que el usuario esté activo
                if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
                    return null;
                }

                // Comprobar que tenga una contraseña registrada
                if (usuario.getContrasenaHash() == null) {
                    return null;
                }

                // Verificar la contraseña
                boolean contrasenaCorrecta =
                        PasswordUtil.verificarPassword(
                                contrasena,
                                usuario.getContrasenaHash()
                        );

                if (contrasenaCorrecta) {
                    return usuario;
                }

                return null;
            }
        }

        return null;
    }

    // --- BUSCAR POR CÓDIGO INSTITUCIONAL ---

    public Usuario buscarPorCodigoInstitucional(
            String codigoInstitucional
    ) {

        return mapaPorCodigoInstitucional.get(codigoInstitucional);
    }

    // --- BUSCAR POR ID ---

    public Usuario buscarPorId(int id) {

        for (Usuario u : listaUsuarios) {

            if (u.getId() == id) {
                return u;
            }
        }

        return null;
    }

    // --- LISTAR USUARIOS ---

    public List<Usuario> listarUsuarios() {

        return listaUsuarios;
    }

    // --- CAMBIAR ESTADO DEL USUARIO ---

    public String cambiarEstadoUsuario(
            int id,
            String nuevoEstado
    ) {

        Usuario usuario = buscarPorId(id);

        if (usuario == null) {
            return "ERROR: Usuario no encontrado.";
        }

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return "ERROR: El nuevo estado es obligatorio.";
        }

        usuario.setEstado(nuevoEstado.trim().toUpperCase());

        // Guardar cambios en JSON
        jsonManager.guardarLista(
                ARCHIVO_JSON,
                listaUsuarios
        );

        return "ÉXITO: Estado del usuario actualizado a "
                + usuario.getEstado();
    }
}
