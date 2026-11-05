package controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import model.Sancion;
import model.Usuario;

public class SancionController {
   private List<Sancion> listaSanciones;
    private Map<Integer, Sancion> mapaSanciones;
    private int idContador;

    private UsuarioController usuarioController;

    public SancionController(UsuarioController usuarioController) {
        this.listaSanciones = new LinkedList<>();
        this.mapaSanciones = new HashMap<>();
        this.usuarioController = usuarioController;
        this.idContador = 1;
    }

    /**
     * Aplica una sanción en base al Código Institucional del usuario y la entidad Sancion.
     */
    public String aplicarSancion(String codigoInstitucional, int prestamoId, String motivo, int diasSancion, String observaciones) {
        Usuario usuario = usuarioController.buscarPorCodigoInstitucional(codigoInstitucional);
        if (usuario == null) {
            return "ERROR: Usuario con Código Institucional " + codigoInstitucional + " no existe.";
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaFin = ahora.plusDays(diasSancion);

        Sancion nuevaSancion = new Sancion();
        nuevaSancion.setId(idContador++);
        nuevaSancion.setPrestamoId(prestamoId);
        nuevaSancion.setUsuarioId(usuario.getId());
        nuevaSancion.setMotivo(motivo);
        nuevaSancion.setFechaInicio(ahora);
        nuevaSancion.setFechaFin(fechaFin);
        nuevaSancion.setEstado("ACTIVA");
        nuevaSancion.setObservaciones(observaciones);

        // Inhabilitar al usuario en el sistema automáticamente
        usuarioController.cambiarEstadoUsuario(usuario.getId(), "SANCIONADO");

        listaSanciones.add(nuevaSancion);
        mapaSanciones.put(nuevaSancion.getId(), nuevaSancion);

        return "ÉXITO: Sanción #" + nuevaSancion.getId() + " aplicada a " + usuario.getNombre() + " hasta " + fechaFin.toLocalDate();
    }

    /**
     * Levanta una sanción y reactiva al usuario.
     */
    public String levantarSancion(int idSancion) {
        Sancion sancion = mapaSanciones.get(idSancion);
        if (sancion == null) {
            return "ERROR: Sanción no encontrada.";
        }

        sancion.setEstado("INACTIVA");
        
        // Reactivar al usuario
        Usuario usuario = usuarioController.buscarPorId(sancion.getUsuarioId());
        if (usuario != null) {
            usuarioController.cambiarEstadoUsuario(usuario.getId(), "ACTIVO");
        }

        return "ÉXITO: Sanción #" + idSancion + " levantada correctamente.";
    }

    public Sancion buscarPorId(int id) {
        return mapaSanciones.get(id);
    }

    public List<Sancion> listarSanciones() {
        return listaSanciones;
    }
}
