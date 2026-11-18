package vista;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import controller.EquipoController;
import controller.PrestamosController;
import controller.SancionController;
import controller.UsuarioController;
import model.Equipo;
import model.Prestamo;
import model.Sancion;
import model.Usuario;
import persistencia.JsonManager;

public class Vista {
    // Instancia de persistencia y controladores
    private static final JsonManager jsonManager = new JsonManager();
    private static final UsuarioController usuarioController = new UsuarioController();
    private static final EquipoController equipoController = new EquipoController();
    private static final PrestamosController prestamosController = new PrestamosController(usuarioController,
            equipoController);
    private static final SancionController sancionController = new SancionController(usuarioController);

    // Solo el nombre del archivo (JsonManager ya agrega "data/")
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final String ARCHIVO_EQUIPOS = "equipos.json";
    private static final String ARCHIVO_PRESTAMOS = "prestamos.json";
    private static final String ARCHIVO_SANCIONES = "sanciones.json";

    // Gson para las peticiones/respuestas HTTP con soporte de LocalDateTime
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, typeOfSrc,
                            context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime
                            .parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    public static void main(String[] args) throws IOException {
        cargarDatosDesdeJson();

        int puerto = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);

        // Archivos estáticos (HTML, CSS, JS, fotos)
        server.createContext("/", Vista::manejarArchivosEstaticos);

        // Endpoints API
        server.createContext("/api/usuarios", Vista::manejarUsuarios);
        server.createContext("/api/equipos", Vista::manejarEquipos);
        server.createContext("/api/prestamos", Vista::manejarPrestamos);
        server.createContext("/api/sanciones", Vista::manejarSanciones);

        server.setExecutor(null);
        server.start();

        System.out.println("=========================================================");
        System.out.println("Sistema Universitario Iniciado Correctamente");
        System.out.println("Abre en tu navegador: http://localhost:" + puerto + "/html/login.html");
        System.out.println("=========================================================");
    }

    // --- PERSISTENCIA JSON ---
    private static void cargarDatosDesdeJson() {
        List<Usuario> usuariosGuardados = jsonManager.cargarLista(
                ARCHIVO_USUARIOS, new TypeToken<List<Usuario>>() {
                }.getType());
        for (Usuario u : usuariosGuardados) {
            usuarioController.registrarUsuario(u);
        }

        List<Equipo> equiposGuardados = jsonManager.cargarLista(
                ARCHIVO_EQUIPOS, new TypeToken<List<Equipo>>() {
                }.getType());
        for (Equipo e : equiposGuardados) {
            equipoController.registrarEquipo(e);
        }

        List<Prestamo> prestamosGuardados = jsonManager.cargarLista(
                ARCHIVO_PRESTAMOS, new TypeToken<List<Prestamo>>() {
                }.getType());
        prestamosController.listarPrestamos().addAll(prestamosGuardados);

        List<Sancion> sancionesGuardadas = jsonManager.cargarLista(
                ARCHIVO_SANCIONES, new TypeToken<List<Sancion>>() {
                }.getType());
        sancionController.listarSanciones().addAll(sancionesGuardadas);
    }

    private static void guardarTodoEnJson() {
        jsonManager.guardarLista(ARCHIVO_USUARIOS, usuarioController.listarUsuarios());
        jsonManager.guardarLista(ARCHIVO_EQUIPOS, equipoController.listarEquipos());
        jsonManager.guardarLista(ARCHIVO_PRESTAMOS, prestamosController.listarPrestamos());
        jsonManager.guardarLista(ARCHIVO_SANCIONES, sancionController.listarSanciones());
    }

    // --- ARCHIVOS ESTÁTICOS ---
    private static void manejarArchivosEstaticos(HttpExchange exchange) throws IOException {
        String ruta = exchange.getRequestURI().getPath();
        if (ruta.equals("/")) {
            ruta = "/html/login.html";
        }

        File archivo = new File("src/vista" + ruta);
        if (!archivo.exists() || archivo.isDirectory()) {
            enviarRespuesta(exchange, 404, "Recurso no encontrado: " + ruta, "text/plain");
            return;
        }

        byte[] bytes = Files.readAllBytes(archivo.toPath());
        exchange.getResponseHeaders().set("Content-Type", obtenerContentType(ruta));
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // --- ENDPOINTS API ---
    private static void manejarUsuarios(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            enviarRespuesta(exchange, 200, gson.toJson(usuarioController.listarUsuarios()), "application/json");
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Usuario nuevo = gson.fromJson(leerBody(exchange), Usuario.class);
            String mensaje = usuarioController.registrarUsuario(nuevo);
            if (mensaje.startsWith("ÉXITO"))
                guardarTodoEnJson();
            enviarRespuesta(exchange, 200, gson.toJson(Map.of("mensaje", mensaje)), "application/json");
        }
    }

    private static void manejarEquipos(HttpExchange exchange) throws IOException {
        if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            enviarRespuesta(exchange, 200, gson.toJson(equipoController.listarEquipos()), "application/json");
        } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            Equipo nuevo = gson.fromJson(leerBody(exchange), Equipo.class);
            String mensaje = equipoController.registrarEquipo(nuevo);
            if (mensaje.startsWith("ÉXITO"))
                guardarTodoEnJson();
            enviarRespuesta(exchange, 200, gson.toJson(Map.of("mensaje", mensaje)), "application/json");
        }
    }

    private static void manejarPrestamos(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(metodo)) {
            enviarRespuesta(exchange, 200, gson.toJson(prestamosController.listarPrestamos()), "application/json");
        } else if ("POST".equalsIgnoreCase(metodo)) {
            @SuppressWarnings("unchecked")
            Map<String, String> datos = gson.fromJson(leerBody(exchange), Map.class);
            String accion = datos.getOrDefault("accion", "PRESTAR");

            String mensaje;
            if ("DEVOLVER".equalsIgnoreCase(accion)) {
                int idPrestamo = Integer.parseInt(datos.get("idPrestamo"));
                mensaje = prestamosController.registrarDevolucion(
                        idPrestamo, 1, datos.getOrDefault("observaciones", "Devuelto sin novedad"));
            } else {
                mensaje = prestamosController.registrarPrestamo(
                        datos.get("codigoInstitucional"),
                        datos.get("codigoInventario"),
                        LocalDateTime.now().plusHours(4),
                        1,
                        datos.getOrDefault("observaciones", "Préstamo estándar"));
            }

            if (mensaje.startsWith("ÉXITO"))
                guardarTodoEnJson();
            enviarRespuesta(exchange, 200, gson.toJson(Map.of("mensaje", mensaje)), "application/json");
        }
    }

    private static void manejarSanciones(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(metodo)) {
            enviarRespuesta(exchange, 200, gson.toJson(sancionController.listarSanciones()), "application/json");
        } else if ("POST".equalsIgnoreCase(metodo)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> datos = gson.fromJson(leerBody(exchange), Map.class);
            String accion = (String) datos.getOrDefault("accion", "APLICAR");

            String mensaje;
            if ("LEVANTAR".equalsIgnoreCase(accion)) {
                int idSancion = ((Double) datos.get("idSancion")).intValue();
                mensaje = sancionController.levantarSancion(idSancion);
            } else {
                mensaje = sancionController.aplicarSancion(
                        (String) datos.get("codigoInstitucional"),
                        ((Double) datos.getOrDefault("prestamoId", 0.0)).intValue(),
                        (String) datos.get("motivo"),
                        ((Double) datos.get("diasSancion")).intValue(),
                        (String) datos.getOrDefault("observaciones", ""));
            }

            if (mensaje.startsWith("ÉXITO"))
                guardarTodoEnJson();
            enviarRespuesta(exchange, 200, gson.toJson(Map.of("mensaje", mensaje)), "application/json");
        }
    }

    // --- UTILIDADES HTTP ---
    private static String leerBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static void enviarRespuesta(HttpExchange exchange, int status, String respuesta, String contentType)
            throws IOException {
        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String obtenerContentType(String ruta) {
        if (ruta.endsWith(".html"))
            return "text/html";
        if (ruta.endsWith(".css"))
            return "text/css";
        if (ruta.endsWith(".js"))
            return "application/javascript";
        if (ruta.endsWith(".png"))
            return "image/png";
        if (ruta.endsWith(".jpg") || ruta.endsWith(".jpeg"))
            return "image/jpeg";
        return "application/octet-stream";
    }
}
