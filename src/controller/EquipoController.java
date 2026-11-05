package controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import model.Equipo;

public class EquipoController {
    // Estructuras de datos
    private List<Equipo> listaEquipos;
    private Map<String, Equipo> mapaPorCodigoInventario; // Búsqueda O(1)
    private Queue<String> colaEsperaEstudiantes;       // Cola FIFO para reservas cuando no hay equipos
    private int idContador;

    public EquipoController() {
        this.listaEquipos = new LinkedList<>();
        this.mapaPorCodigoInventario = new HashMap<>();
        this.colaEsperaEstudiantes = new LinkedList<>();
        this.idContador = 1;
    }

    /**
     * Registra un nuevo equipo en el inventario.
     */
    public String registrarEquipo(Equipo nuevoEquipo) {
        if (nuevoEquipo.getCodigoInventario() == null || nuevoEquipo.getCodigoInventario().trim().isEmpty() ||
            nuevoEquipo.getMarca() == null || nuevoEquipo.getMarca().trim().isEmpty()) {
            return "ERROR: El código de inventario y la marca son obligatorios.";
        }

        // Validación de duplicados mediante HashMap O(1)
        if (mapaPorCodigoInventario.containsKey(nuevoEquipo.getCodigoInventario())) {
            return "ERROR: Ya existe un equipo registrado con el código de inventario: " + nuevoEquipo.getCodigoInventario();
        }

        nuevoEquipo.setId(idContador++);
        if (nuevoEquipo.getEstado() == null || nuevoEquipo.getEstado().trim().isEmpty()) {
            nuevoEquipo.setEstado("DISPONIBLE"); // DISPONIBLE, PRESTADO, MANTENIMIENTO
        }

        listaEquipos.add(nuevoEquipo);
        mapaPorCodigoInventario.put(nuevoEquipo.getCodigoInventario(), nuevoEquipo);

        return "ÉXITO: Equipo registrado correctamente con ID: " + nuevoEquipo.getId();
    }

    /**
     * Búsqueda instantánea O(1) por código de inventario.
     */
    public Equipo buscarPorCodigoInventario(String codigoInventario) {
        return mapaPorCodigoInventario.get(codigoInventario);
    }

    public Equipo buscarPorId(int id) {
        for (Equipo e : listaEquipos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public List<Equipo> listarEquipos() {
        return listaEquipos;
    }

    /**
     * Cambia el estado del equipo (ej: DISPONIBLE, PRESTADO, MANTENIMIENTO).
     */
    public String cambiarEstadoEquipo(String codigoInventario, String nuevoEstado) {
        Equipo equipo = buscarPorCodigoInventario(codigoInventario);
        if (equipo == null) {
            return "ERROR: Equipo no encontrado.";
        }
        equipo.setEstado(nuevoEstado.toUpperCase());
        return "ÉXITO: Estado del equipo " + codigoInventario + " actualizado a " + equipo.getEstado();
    }

    // --- MANEJO DE COLA DE ESPERA (Estructura Queue) ---

    /**
     * Agrega un estudiante (por su código institucional) a la cola de espera FIFO.
     */
    public String agregarAColaEspera(String codigoInstitucional) {
        colaEsperaEstudiantes.add(codigoInstitucional);
        return "ÉXITO: Estudiante " + codigoInstitucional + " en posición #" + colaEsperaEstudiantes.size() + " de la cola de espera.";
    }

    /**
     * Obtiene y remueve al primer estudiante en turno de la cola.
     */
    public String atenderSiguienteEnCola() {
        if (colaEsperaEstudiantes.isEmpty()) {
            return null;
        }
        return colaEsperaEstudiantes.poll(); // Saca el primero (FIFO)
    }

    public Queue<String> getColaEsperaEstudiantes() {
        return colaEsperaEstudiantes;
    }
}
