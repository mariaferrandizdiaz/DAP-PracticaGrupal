package Data;

public class Movie {
    private final String titulo;
    private final String info;
    private final String imagenUrl;

    public Movie(String titulo, String info, String imagenUrl) {
        this.titulo = titulo;
        this.info = info;
        this.imagenUrl = imagenUrl;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getInfo() {
        return info;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + titulo + '\'' +
                ", Information='" + info + '\'' +
                ", imageUrl='" + imagenUrl + '\'' +
                '}';
    }
}