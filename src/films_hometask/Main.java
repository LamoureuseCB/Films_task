package films_hometask;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String filmUrl = "https://swapi.dev/api/films";
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI uri = URI.create(filmUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("results").getAsJsonArray();

        try (Writer writer = new FileWriter("src/films.csv")) {
            writer.write("title,release_date,url\n");
            for (JsonElement element : jsonArray) {
                JsonObject objectOfFilm = element.getAsJsonObject();
                String title = objectOfFilm.get("title").getAsString();
                LocalDate release_date = LocalDate.parse(objectOfFilm.get("release_date").getAsString());
                String url = objectOfFilm.get("url").getAsString();
                writer.write("%s,%s,%s\n".formatted(title, release_date, url));
            }
        }

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/films", new FilmsHandler());
        httpServer.start();
        System.out.println("Cервер с фильмами запущен на порту 8080");
    }


    private static List<String> brFromCSV() throws IOException {
        List<String> films = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/films.csv"))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                films.add(line);
            }
        }
        return films;
    }


//    чтобы по заданию в конце все фильмы вывести в формате JSON создала доп класс Фильм

    static class Film{
        String title;
        String year;
        String url;

        public Film(String title, String year, String url) {
            this.title = title;
            this.year = year;
            this.url = url;
        }
    }
    public static class FilmsHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            List<String> filmsFromFileCSV = brFromCSV();
            List<Film> films = new ArrayList<>();
            for (String line : filmsFromFileCSV) {
                String[] words = line.split(",");
                films.add(new Film(words[0], words[1], words[2]));
            }
            //       на данном сайте нашла способ как сделать вид JSON https://howtodoinjava.com/gson/pretty-print-json-output/
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String filmJSON = gson.toJson(films);
            exchange.sendResponseHeaders(200, filmJSON.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(filmJSON.getBytes());
            }
        }
    }
}




