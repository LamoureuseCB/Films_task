package films_hometask;

import java.time.LocalDate;

public class Film {
    private String title;
    private LocalDate release_date;
    private String url;

    public Film(String title, LocalDate release_date, String url) {
        this.title = title;
        this.release_date = release_date;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getRelease_date() {
        return release_date;
    }

    public String getUrl() {
        return url;
    }
}
