package controller;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import model.Equipo;
import persistencia.JsonManager;

public class EquipoController {
    private static final String ARCHIVO_JSON = "equipos.json";
    private static final String ARCHIVO_COLA_JSON = "cola_espera.json";
    private final JsonManager jsonManager;

    // Estructuras de datos
    private List<Equipo> listaEquipos;
    private Map<String, Equipo> mapaPorCodigoInventario; // Búsqueda O(1)
    private Queue<String> colaEsperaEstudiantes;       // Cola FIFO para reservas cuando no hay equipos
    private int idContador;

    public EquipoController() {
        this.jsonManager = new JsonManager();
        this.mapaPorCodigoInventario = new HashMap<>();

        Type tipoListaEquipos = new TypeToken<LinkedList<Equipo>>() {}.getType();
        List<Equipo> cargados = jsonManager.cargarLista(ARCHIVO_JSON, tipoListaEquipos);
        this.listaEquipos = (cargados != null) ? new LinkedList<>(cargados) : new LinkedList<>();

        Type tipoCola = new TypeToken<LinkedList<String>>() {}.getType();
        List<String> colaCargada = jsonManager.cargarLista(ARCHIVO_COLA_JSON, tipoCola);
        this.colaEsperaEstudiantes = (colaCargada != null) ? new LinkedList<>(colaCargada) : new LinkedList<>();

        int maxId = 0;
        for (Equipo e : this.listaEquipos) {
            if (e.getCodigoInventario() != null) {
                this.mapaPorCodigoInventario.put(e.getCodigoInventario(), e);
            }
            if (e.getId() > maxId) {
                maxId = e.getId();
            }
        }
        this.idContador = maxId + 1;
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

        jsonManager.guardarLista(ARCHIVO_JSON, listaEquipos);

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

        jsonManager.guardarLista(ARCHIVO_JSON, listaEquipos);

        return "ÉXITO: Estado del equipo " + codigoInventario + " actualizado a " + equipo.getEstado();
    }

    // --- MANEJO DE COLA DE ESPERA (Estructura Queue) ---

    /**
     * Agrega un estudiante (por su código institucional) a la cola de espera FIFO.
     */
    public String agregarAColaEspera(String codigoInstitucional) {
        colaEsperaEstudiantes.add(codigoInstitucional);
        jsonManager.guardarLista(ARCHIVO_COLA_JSON, new LinkedList<>(colaEsperaEstudiantes));
        return "ÉXITO: Estudiante " + codigoInstitucional + " en posición #" + colaEsperaEstudiantes.size() + " de la cola de espera.";
    }

    /**
     * Obtiene y remueve al primer estudiante en turno de la cola.
     */
    public String atenderSiguienteEnCola() {
        if (colaEsperaEstudiantes.isEmpty()) {
            return null;
        }
        String siguiente = colaEsperaEstudiantes.poll(); // Saca el primero (FIFO)
        jsonManager.guardarLista(ARCHIVO_COLA_JSON, new LinkedList<>(colaEsperaEstudiantes));
        return siguiente;
    }

    public Queue<String> getColaEsperaEstudiantes() {
        return colaEsperaEstudiantes;
    }
}