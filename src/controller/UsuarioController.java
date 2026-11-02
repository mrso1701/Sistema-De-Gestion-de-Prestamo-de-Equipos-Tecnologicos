package controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import model.Usuario;

public class UsuarioController {
    private List<Usuario> listaUsuarios;
    private Map<String, Usuario> mapaPorDocumento;
    private Long idContador;

    public UsuarioController() {
        this.listaUsuarios = new LinkedList<>();
        this.mapaPorDocumento = new HashMap<>();
        this.idContador = 1L;
    }

    public String registrarUsuario(Usuario nuevoUsuario) {
        if (nuevoUsuario.getDocumentoIdentidad() == null || nuevoUsuario.getDocumentoIdentidad().trim().isEmpty() ||
            nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().trim().isEmpty() ||
            nuevoUsuario.getCorreoInstitucional() == null || nuevoUsuario.getCorreoInstitucional().trim().isEmpty()) {
            return "ERROR: Los campos Documento, Nombre y Correo son obligatorios.";
        }

        
        String correo = nuevoUsuario.getCorreoInstitucional().toLowerCase();
        if (!correo.endsWith("@universidad.edu.co")) {
            return "ERROR: El correo debe ser un correo institucional válido (@universidad.edu.co).";
        }

        if (mapaPorDocumento.containsKey(nuevoUsuario.getDocumentoIdentidad())) {
            return "ERROR: Ya existe un usuario registrado con este documento de identidad.";
        }

        for (Usuario u : listaUsuarios) {
            if (u.getCorreoInstitucional().equalsIgnoreCase(nuevoUsuario.getCorreoInstitucional())) {
                return "ERROR: Ya existe un usuario registrado con este correo institucional.";
            }
        }

        nuevoUsuario.setId(idContador++);
        if (nuevoUsuario.getEstado() == null || nuevoUsuario.getEstado().trim().isEmpty()) {
            nuevoUsuario.setEstado("ACTIVO");
        }

        
        listaUsuarios.add(nuevoUsuario);
        mapaPorDocumento.put(nuevoUsuario.getDocumentoIdentidad(), nuevoUsuario);

        return "ÉXITO: Usuario registrado correctamente con ID: " + nuevoUsuario.getId();
    }

    public Usuario buscarPorDocumento(String documento) {
        return mapaPorDocumento.get(documento);
    }

    public Usuario buscarPorId(Long id) {
        for (Usuario u : listaUsuarios) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarUsuarios() {
        return listaUsuarios;
    }

    public String cambiarEstadoUsuario(Long id, String nuevoEstado) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            return "ERROR: Usuario no encontrado.";
        }
        
        usuario.setEstado(nuevoEstado.toUpperCase());
        return "ÉXITO: Estado del usuario actualizado a " + usuario.getEstado();
    }
}
