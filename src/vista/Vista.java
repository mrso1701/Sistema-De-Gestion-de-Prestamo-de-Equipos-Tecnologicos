
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
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import controller.EquipoController;
import controller.PrestamosController;
import controller.SancionController;
import controller.UsuarioController;
import model.Equipo;
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

        // Archivos JSON
        private static final String ARCHIVO_USUARIOS = "usuarios.json";
        private static final String ARCHIVO_EQUIPOS = "equipos.json";
        private static final String ARCHIVO_PRESTAMOS = "prestamos.json";
        private static final String ARCHIVO_SANCIONES = "sanciones.json";

        // Gson con soporte para LocalDateTime
        private static final Gson gson = new GsonBuilder()
                        .registerTypeAdapter(
                                        LocalDateTime.class,
                                        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new JsonPrimitive(
                                                        src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                        .registerTypeAdapter(
                                        LocalDateTime.class,
                                        (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> LocalDateTime
                                                        .parse(
                                                                        json.getAsString(),
                                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .create();

        // --- INICIO DEL SERVIDOR ---

        public static void main(String[] args) throws IOException {
                inicializarDatosPorDefecto();

                int puerto = 8080;

                HttpServer server = HttpServer.create(
                                new InetSocketAddress(puerto), 0);

                server.createContext("/", Vista::manejarArchivosEstaticos);

                // Endpoints API
                server.createContext("/api/usuarios", Vista::manejarUsuarios);
                server.createContext("/api/login", Vista::manejarLogin);
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

        // Inicializar administradores
        private static void inicializarDatosPorDefecto() {
                // 1. Precargar los 2 administradores únicos
                if (usuarioController.buscarPorCodigoInstitucional("ADMIN-SALA") == null) {
                        Usuario encargado = new Usuario();
                        encargado.setCodigoInstitucional("ADMIN-SALA");
                        encargado.setDocumentoIdentidad("10000001");
                        encargado.setNombre("Encargado");
                        encargado.setApellido("Sala");
                        encargado.setCorreoInstitucional("sala@unicartagena.edu.co");
                        encargado.setTelefono("3000000001");
                        encargado.setTipoUsuario("ENCARGADO_SALA");
                        encargado.setEstado("ACTIVO");
                        usuarioController.registrarUsuario(encargado, "sala123");
                }

                if (usuarioController.buscarPorCodigoInstitucional("ADMIN-JEFE") == null) {
                        Usuario jefe = new Usuario();
                        jefe.setCodigoInstitucional("ADMIN-JEFE");
                        jefe.setDocumentoIdentidad("10000002");
                        jefe.setNombre("Jefe");
                        jefe.setApellido("Laboratorio");
                        jefe.setCorreoInstitucional("jefe@unicartagena.edu.co");
                        jefe.setTelefono("3000000002");
                        jefe.setTipoUsuario("JEFE_LABORATORIO");
                        jefe.setEstado("ACTIVO");
                        usuarioController.registrarUsuario(jefe, "jefe123");
                }

                // 2. Precargar equipos de ejemplo
                String[][] equiposIniciales = {
                                { "EQ-001", "Lenovo", "ThinkPad E14", "Portátil" },
                                { "EQ-002", "Dell", "Latitude 5430", "Portátil" },
                                { "EQ-003", "HP", "ProBook 450 G9", "Portátil" },
                                { "EQ-004", "Epson", "PowerLite X49", "Proyector" },
                                { "EQ-005", "Samsung", "Galaxy Tab S8", "Tablet" }
                };

                for (String[] datos : equiposIniciales) {
                        if (equipoController.buscarPorCodigoInventario(datos[0]) == null) {
                                Equipo eq = new Equipo();
                                eq.setCodigoInventario(datos[0]);
                                eq.setMarca(datos[1]);
                                eq.setModelo(datos[2]);
                                eq.setTipoEquipo(datos[3]);
                                eq.setEstado("DISPONIBLE");
                                eq.setObservacionesEstado("Equipo operativo en sala");
                                equipoController.registrarEquipo(eq);
                        }
                }
        }

        // --- USUARIOS: REGISTRO Y CONSULTA ---
        private static void manejarUsuarios(HttpExchange exchange) throws IOException {

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                        // Consultar usuarios sin exponer sus hashes
                        JsonArray usuariosJson = gson.toJsonTree(
                                        usuarioController.listarUsuarios()).getAsJsonArray();

                        for (int i = 0; i < usuariosJson.size(); i++) {
                                usuariosJson.get(i)
                                                .getAsJsonObject()
                                                .remove("contrasenaHash");
                        }

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(usuariosJson),
                                        "application/json");

                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                        JsonObject datos = gson.fromJson(
                                        leerBody(exchange),
                                        JsonObject.class);

                        if (datos == null) {
                                enviarRespuesta(
                                                exchange, 400,
                                                "Solicitud vacía",
                                                "text/plain");
                                return;
                        }

                        String contrasena = datos.has("contrasena")
                                        && !datos.get("contrasena").isJsonNull()
                                                        ? datos.get("contrasena").getAsString()
                                                        : null;

                        // La contraseña no se guarda directamente en Usuario
                        datos.remove("contrasena");

                        Usuario nuevo = gson.fromJson(datos, Usuario.class);

                        String mensaje = usuarioController.registrarUsuario(
                                        nuevo,
                                        contrasena);

                        if (mensaje.startsWith("ÉXITO")) {
                                guardarTodoEnJson();
                        }

                        enviarRespuesta(
                                        exchange,
                                        mensaje.startsWith("ÉXITO") ? 201 : 400,
                                        gson.toJson(Map.of("mensaje", mensaje)),
                                        "application/json");

                } else {

                        enviarRespuesta(
                                        exchange,
                                        405,
                                        "Método no permitido",
                                        "text/plain");
                }
        }

        // --- INICIO DE SESIÓN ---

        private static void manejarLogin(HttpExchange exchange) throws IOException {

                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                        enviarRespuesta(
                                        exchange,
                                        405,
                                        "Método no permitido",
                                        "text/plain");
                        return;
                }

                JsonObject datos = gson.fromJson(
                                leerBody(exchange),
                                JsonObject.class);

                if (datos == null) {
                        enviarRespuesta(
                                        exchange,
                                        400,
                                        "Solicitud vacía",
                                        "text/plain");
                        return;
                }

                String correo = datos.has("correo")
                                && !datos.get("correo").isJsonNull()
                                                ? datos.get("correo").getAsString()
                                                : null;

                String contrasena = datos.has("contrasena")
                                && !datos.get("contrasena").isJsonNull()
                                                ? datos.get("contrasena").getAsString()
                                                : null;

                Usuario usuario = usuarioController.iniciarSesion(
                                correo,
                                contrasena);

                if (usuario == null) {

                        enviarRespuesta(
                                        exchange,
                                        401,
                                        gson.toJson(Map.of(
                                                        "mensaje",
                                                        "Correo o contraseña incorrectos, o usuario inactivo.")),
                                        "application/json");

                        return;
                }

                // Respuesta sin incluir el hash de la contraseña
                JsonObject usuarioJson = gson.toJsonTree(usuario)
                                .getAsJsonObject();

                usuarioJson.remove("contrasenaHash");

                JsonObject respuesta = new JsonObject();

                respuesta.addProperty(
                                "mensaje",
                                "Inicio de sesión exitoso");

                respuesta.add("usuario", usuarioJson);

                enviarRespuesta(
                                exchange,
                                200,
                                gson.toJson(respuesta),
                                "application/json");
        }

        // --- GUARDAR INFORMACIÓN ---

        private static void guardarTodoEnJson() {

                jsonManager.guardarLista(
                                ARCHIVO_USUARIOS,
                                usuarioController.listarUsuarios());

                jsonManager.guardarLista(
                                ARCHIVO_EQUIPOS,
                                equipoController.listarEquipos());

                jsonManager.guardarLista(
                                ARCHIVO_PRESTAMOS,
                                prestamosController.listarPrestamos());

                jsonManager.guardarLista(
                                ARCHIVO_SANCIONES,
                                sancionController.listarSanciones());
        }

        // --- ARCHIVOS ESTÁTICOS ---

        private static void manejarArchivosEstaticos(
                        HttpExchange exchange) throws IOException {

                String ruta = exchange.getRequestURI().getPath();

                if (ruta.equals("/")) {
                        ruta = "/html/login.html";
                }

                File archivo = new File("src/vista" + ruta);

                if (!archivo.exists() || archivo.isDirectory()) {

                        enviarRespuesta(
                                        exchange,
                                        404,
                                        "Recurso no encontrado: " + ruta,
                                        "text/plain");
                        return;
                }

                byte[] bytes = Files.readAllBytes(archivo.toPath());

                exchange.getResponseHeaders().set(
                                "Content-Type",
                                obtenerContentType(ruta));

                exchange.sendResponseHeaders(200, bytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                }
        }

        // --- ENDPOINT EQUIPOS ---

        private static void manejarEquipos(HttpExchange exchange)
                        throws IOException {

                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(equipoController.listarEquipos()),
                                        "application/json");

                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {

                        Equipo nuevo = gson.fromJson(
                                        leerBody(exchange),
                                        Equipo.class);

                        String mensaje = equipoController.registrarEquipo(nuevo);

                        if (mensaje.startsWith("ÉXITO")) {
                                guardarTodoEnJson();
                        }

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(Map.of("mensaje", mensaje)),
                                        "application/json");
                }
        }

        // --- ENDPOINT PRÉSTAMOS ---

        private static void manejarPrestamos(HttpExchange exchange)
                        throws IOException {

                String metodo = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(metodo)) {

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(prestamosController.listarPrestamos()),
                                        "application/json");

                } else if ("POST".equalsIgnoreCase(metodo)) {

                        @SuppressWarnings("unchecked")
                        Map<String, String> datos = gson.fromJson(
                                        leerBody(exchange),
                                        Map.class);

                        String accion = datos.getOrDefault("accion", "PRESTAR");
                        String mensaje;

                        if ("DEVOLVER".equalsIgnoreCase(accion)) {
                                try {
                                        int idPrestamo = Integer.parseInt(datos.get("idPrestamo"));
                                        mensaje = prestamosController.registrarDevolucion(
                                                        idPrestamo,
                                                        1,
                                                        datos.getOrDefault("observaciones", "Devuelto sin novedad"));
                                } catch (NumberFormatException e) {
                                        mensaje = "ERROR: El código del préstamo debe ser numérico.";
                                }
                        } else {
                                LocalDateTime fechaPactada = LocalDateTime.now().plusHours(4);
                                String fechaTexto = datos.get("fechaDevolucion");
                                if (fechaTexto != null && !fechaTexto.trim().isEmpty()) {
                                        try {
                                                fechaPactada = LocalDateTime.parse(fechaTexto.trim());
                                        } catch (Exception ignored) {
                                        }
                                }

                                mensaje = prestamosController.registrarPrestamo(
                                                datos.get("codigoInstitucional"),
                                                datos.get("codigoInventario"),
                                                fechaPactada,
                                                1,
                                                datos.getOrDefault("observaciones", "Préstamo estándar"));
                        }

                        if (mensaje.startsWith("ÉXITO")) {
                                guardarTodoEnJson();
                        }

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(Map.of("mensaje", mensaje)),
                                        "application/json");
                }
        }

        // --- ENDPOINT SANCIONES ---

        private static void manejarSanciones(HttpExchange exchange)
                        throws IOException {

                String metodo = exchange.getRequestMethod();

                if ("GET".equalsIgnoreCase(metodo)) {

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(sancionController.listarSanciones()),
                                        "application/json");

                } else if ("POST".equalsIgnoreCase(metodo)) {

                        @SuppressWarnings("unchecked")
                        Map<String, Object> datos = gson.fromJson(
                                        leerBody(exchange),
                                        Map.class);

                        String accion = (String) datos.getOrDefault(
                                        "accion",
                                        "APLICAR");

                        String mensaje;

                        if ("LEVANTAR".equalsIgnoreCase(accion)) {

                                int idSancion = ((Double) datos.get("idSancion"))
                                                .intValue();

                                mensaje = sancionController.levantarSancion(idSancion);

                        } else {

                                mensaje = sancionController.aplicarSancion(
                                                (String) datos.get("codigoInstitucional"),
                                                ((Double) datos.getOrDefault(
                                                                "prestamoId", 0.0)).intValue(),
                                                (String) datos.get("motivo"),
                                                ((Double) datos.get("diasSancion")).intValue(),
                                                (String) datos.getOrDefault(
                                                                "observaciones",
                                                                ""));
                        }

                        if (mensaje.startsWith("ÉXITO")) {
                                guardarTodoEnJson();
                        }

                        enviarRespuesta(
                                        exchange,
                                        200,
                                        gson.toJson(Map.of("mensaje", mensaje)),
                                        "application/json");
                }
        }

        // --- UTILIDADES HTTP ---

        private static String leerBody(HttpExchange exchange)
                        throws IOException {

                try (InputStream is = exchange.getRequestBody()) {
                        return new String(
                                        is.readAllBytes(),
                                        StandardCharsets.UTF_8);
                }
        }

        private static void enviarRespuesta(
                        HttpExchange exchange,
                        int status,
                        String respuesta,
                        String contentType) throws IOException {

                byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().set(
                                "Content-Type",
                                contentType + "; charset=UTF-8");

                exchange.sendResponseHeaders(status, bytes.length);

                try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                }
        }

        private static String obtenerContentType(String ruta) {

                if (ruta.endsWith(".html")) {
                        return "text/html";
                }

                if (ruta.endsWith(".css")) {
                        return "text/css";
                }

                if (ruta.endsWith(".js")) {
                        return "application/javascript";
                }

                if (ruta.endsWith(".png")) {
                        return "image/png";
                }

                if (ruta.endsWith(".jpg") || ruta.endsWith(".jpeg")) {
                        return "image/jpeg";
                }

                return "application/octet-stream";
        }
}
