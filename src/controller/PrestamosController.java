package controller;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import model.Equipo;
import model.Prestamo;
import model.Usuario;
import persistencia.JsonManager;

public class PrestamosController {
    private static final String ARCHIVO_JSON = "prestamos.json";
    private final JsonManager jsonManager;

    private List<Prestamo> listaPrestamos;
    private Deque<Prestamo> historialUltimosPrestamos;
    private int idContador;

    private UsuarioController usuarioController;
    private EquipoController equipoController;

    public PrestamosController(UsuarioController usuarioController, EquipoController equipoController) {
        this.jsonManager = new JsonManager();
        this.historialUltimosPrestamos = new ArrayDeque<>();
        this.usuarioController = usuarioController;
        this.equipoController = equipoController;

        Type tipoLista = new TypeToken<LinkedList<Prestamo>>() {}.getType();
        List<Prestamo> cargados = jsonManager.cargarLista(ARCHIVO_JSON, tipoLista);
        this.listaPrestamos = (cargados != null) ? new LinkedList<>(cargados) : new LinkedList<>();

        int maxId = 0;
        for (Prestamo p : this.listaPrestamos) {
            this.historialUltimosPrestamos.push(p);
            if (p.getId() > maxId) {
                maxId = p.getId();
            }
        }
        this.idContador = maxId + 1;
    }

    /**
     * Registra un préstamo adaptado a la estructura exacta de la entidad Prestamo.
     */
    public String registrarPrestamo(String codigoInstitucional, String codigoInventario, LocalDateTime fechaHoraPactadaDevolucion, int encargadoEntregaId, String observaciones) {
        // 1. Validar existencia del usuario
        Usuario usuario = usuarioController.buscarPorCodigoInstitucional(codigoInstitucional);
        if (usuario == null) {
            return "ERROR: No existe un usuario con el Código Institucional: " + codigoInstitucional;
        }

        // 2. Validar estado del usuario
        if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
            return "ERROR: El usuario " + usuario.getNombre() + " no está ACTIVO. Estado actual: " + usuario.getEstado();
        }

        // 3. Validar existencia del equipo
        Equipo equipo = equipoController.buscarPorCodigoInventario(codigoInventario);
        if (equipo == null) {
            return "ERROR: No existe un equipo registrado con el código: " + codigoInventario;
        }

        // 4. Validar disponibilidad del equipo
        if (!"DISPONIBLE".equalsIgnoreCase(equipo.getEstado())) {
            return "ERROR: El equipo " + codigoInventario + " no está disponible. Estado actual: " + equipo.getEstado();
        }

        // 5. Crear e instanciar el préstamo según la entidad exacta
        Prestamo nuevoPrestamo = new Prestamo();
        nuevoPrestamo.setId(idContador++);
        nuevoPrestamo.setCodigoPrestamo("PRES-" + nuevoPrestamo.getId());
        nuevoPrestamo.setUsuarioId(usuario.getId());
        nuevoPrestamo.setEquipoId(equipo.getId());
        nuevoPrestamo.setEncargadoEntregaId(encargadoEntregaId);
        nuevoPrestamo.setFechaHoraSalida(LocalDateTime.now());
        nuevoPrestamo.setFechaHoraPactadaDevolucion(fechaHoraPactadaDevolucion);
        nuevoPrestamo.setEstadoPrestamo("ACTIVO");
        nuevoPrestamo.setObservacionesEntrega(observaciones);

        // Cambiar estado del equipo en el controlador
        equipoController.cambiarEstadoEquipo(codigoInventario, "PRESTADO");

        listaPrestamos.add(nuevoPrestamo);
        historialUltimosPrestamos.push(nuevoPrestamo);

        jsonManager.guardarLista(ARCHIVO_JSON, listaPrestamos);

        return "ÉXITO: Préstamo " + nuevoPrestamo.getCodigoPrestamo() + " registrado correctamente.";
    }

    /**
     * Registra la devolución utilizando LocalDateTime y estadoPrestamo.
     */
    public String registrarDevolucion(int idPrestamo, int encargadoRecepcionId, String observacionesDevolucion) {
        Prestamo prestamo = buscarPorId(idPrestamo);
        if (prestamo == null) {
            return "ERROR: No se encontró el préstamo con ID: " + idPrestamo;
        }

        if ("FINALIZADO".equalsIgnoreCase(prestamo.getEstadoPrestamo())) {
            return "ERROR: Este préstamo ya fue finalizado previamente.";
        }

        prestamo.setEncargadoRecepcionId(encargadoRecepcionId);
        prestamo.setFechaHoraDevolucionReal(LocalDateTime.now());
        prestamo.setEstadoPrestamo("FINALIZADO");
        prestamo.setObservacionesDevolucion(observacionesDevolucion);

        // Buscar equipo para devolverlo a estado DISPONIBLE
        Equipo equipo = equipoController.buscarPorId(prestamo.getEquipoId());
        if (equipo != null) {
            equipoController.cambiarEstadoEquipo(equipo.getCodigoInventario(), "DISPONIBLE");
        }

        // Atender siguiente en cola de espera si existe
        String siguienteEstudiante = equipoController.atenderSiguienteEnCola();
        String msjCola = (siguienteEstudiante != null) 
            ? " NOTA: El estudiante " + siguienteEstudiante + " es el siguiente en la cola."
            : "";

        jsonManager.guardarLista(ARCHIVO_JSON, listaPrestamos);

        return "ÉXITO: Devolución registrada correctamente." + msjCola;
    }

    public Prestamo buscarPorId(int id) {
        for (Prestamo p : listaPrestamos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Prestamo> listarPrestamos() {
        return listaPrestamos;
    }

    public Deque<Prestamo> getHistorialUltimosPrestamos() {
        return historialUltimosPrestamos;
    }
}