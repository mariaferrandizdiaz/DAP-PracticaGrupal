package Observable;

import Data.Movie;
import Data.WebScraping.MovieScrapper;
import Observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class YelmoCines implements Observable {

    private List<Observer> observers;
    private List<Movie> lastFetchedMovies;
    private ScheduledExecutorService scheduler;
    final String baseUrl = "https://www.yelmocines.es/cartelera/";
    String city;
    MovieScrapper yelmo;

    public YelmoCines(){
        this("santa-cruz-tenerife/meridiano");
    }

    public YelmoCines(String city){
        this.city = city;
        yelmo = new MovieScrapper("resources/chromedriver-mac-x64/chromedriver",baseUrl);
        this.observers = new ArrayList<>();
        this.lastFetchedMovies = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers() {
        List<Movie> movies = fetchMovies();
        for(Observer o : observers) o.update(movies);
    }

    @Override
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public List<Movie> fetchMovies() {
        return yelmo.getSchedule(city);
    }

    @Override
    public void startAutoReload(int intervalSeconds) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println( "\u001B[34m" + "Updating yelmo..." + "\u001B[0m");
                if (!observers.isEmpty()) notifyObservers(); // Notifica a los observadores
                /*
                List<Movie> newMovies = fetchMovies();
                if (isUpdated(newMovies)) {
                    lastFetchedMovies = new ArrayList<>(newMovies); // Actualiza la lista de películas
                    notifyObservers(); // Notifica a los observadores
                }*/
            } catch (Exception e) {
                e.printStackTrace(); // Maneja errores (ej. API caída)
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    @Override
    public void stopAutoReload() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Override
    public boolean isUpdated(List<Movie> newMovies) {
        // Compara las listas para ver si hay cambios
        if (newMovies.size() != lastFetchedMovies.size()) return true;
        /*
        for (int i = 0; i < newMovies.size(); i++) {
            if (!newMovies.get(i).equals(lastFetchedMovies.get(i))) return true;
        }
         */
        return false;
    }

    @Override
    public String getSubscribers() {
        return observers.size() + "";
    }

}