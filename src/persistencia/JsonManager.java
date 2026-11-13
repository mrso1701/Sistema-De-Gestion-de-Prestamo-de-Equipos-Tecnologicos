package persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class JsonManager {

    private static final String CARPETA_DATA = "data/";
    private final Gson gson;

    public JsonManager() {
        File directorio = new File(CARPETA_DATA);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public <T> void guardarLista(String nombreArchivo, List<T> lista) {
        try (FileWriter writer = new FileWriter(CARPETA_DATA + nombreArchivo)) {
            gson.toJson(lista, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo " + nombreArchivo + ": " + e.getMessage());
        }
    }

    public <T> List<T> cargarLista(String nombreArchivo, Type tipoLista) {
        File archivo = new File(CARPETA_DATA + nombreArchivo);
        if (!archivo.exists()) {
            return new LinkedList<>();
        }

        try (FileReader reader = new FileReader(archivo)) {
            List<T> lista = gson.fromJson(reader, tipoLista);
            return (lista != null) ? lista : new LinkedList<>();
        } catch (IOException e) {
            System.err.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
            return new LinkedList<>();
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime fecha, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(fecha.format(FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), FORMATTER);
        }
    }
}