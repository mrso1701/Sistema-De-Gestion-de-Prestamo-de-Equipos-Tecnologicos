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
        List<Usuario> cargados = jsonManager.cargarLista(ARCHIVO_JSON, tipoLista);
        this.listaUsuarios = (cargados != null) ? new LinkedList<>(cargados) : new LinkedList<>();

        // Reconstruir el mapa O(1) y calcular el siguiente ID disponible
        int maxId = 0;
        for (Usuario u : this.listaUsuarios) {
            if (u.getCodigoInstitucional() != null) {
                this.mapaPorCodigoInstitucional.put(u.getCodigoInstitucional(), u);
            }
            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }
        this.idContador = maxId + 1;
    }

    public String registrarUsuario(Usuario nuevoUsuario) {
        if (nuevoUsuario.getCodigoInstitucional() == null || nuevoUsuario.getCodigoInstitucional().trim().isEmpty() ||
            nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().trim().isEmpty() ||
            nuevoUsuario.getCorreoInstitucional() == null || nuevoUsuario.getCorreoInstitucional().trim().isEmpty()) {
            return "ERROR: El Código Institucional (ID Universitario), Nombre y Correo son obligatorios.";
        }

        String correo = nuevoUsuario.getCorreoInstitucional().toLowerCase();
        if (!correo.endsWith("@unicolombo.edu.co") && !correo.endsWith("@unicartagena.edu.co")) {
            return "ERROR: Debe ingresar un correo institucional válido (@unicolombo.edu.co o @unicartagena.edu.co).";
        }

        // Búsqueda O(1) con la clave del Código Institucional
        if (mapaPorCodigoInstitucional.containsKey(nuevoUsuario.getCodigoInstitucional())) {
            return "ERROR: Ya existe un estudiante o usuario registrado con este Código Institucional.";
        }

        nuevoUsuario.setId(idContador++);
        if (nuevoUsuario.getEstado() == null || nuevoUsuario.getEstado().trim().isEmpty()) {
            nuevoUsuario.setEstado("ACTIVO");
        }

        listaUsuarios.add(nuevoUsuario);
        mapaPorCodigoInstitucional.put(nuevoUsuario.getCodigoInstitucional(), nuevoUsuario);

        // Guardar cambios en JSON
        jsonManager.guardarLista(ARCHIVO_JSON, listaUsuarios);

        return "ÉXITO: Usuario registrado correctamente con ID de sistema: " + nuevoUsuario.getId();
    }

    public Usuario buscarPorCodigoInstitucional(String codigoInstitucional) {
        return mapaPorCodigoInstitucional.get(codigoInstitucional);
    }

    public Usuario buscarPorId(int id) {
        for (Usuario u : listaUsuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return listaUsuarios;
    }

    public String cambiarEstadoUsuario(int id, String nuevoEstado) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            return "ERROR: Usuario no encontrado.";
        }
        
        usuario.setEstado(nuevoEstado.toUpperCase());

        // Guardar cambios en JSON
        jsonManager.guardarLista(ARCHIVO_JSON, listaUsuarios);

        return "ÉXITO: Estado del usuario actualizado a " + usuario.getEstado();
    }
}