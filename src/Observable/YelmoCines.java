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
        this("santa-cruz-tenerife");
    }

    public YelmoCines(String city){
        this.city = city;
        yelmo = new MovieScrapper("C:\\Users\\Dani\\Uni\\Libraries\\chromedriver-win64\\chromedriver.exe",baseUrl);
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
        for(Observer o : observers) o.update(fetchMovies());
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
    public void startAutoReload(int intervalMinutes) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Updating yelmo...");
                List<Movie> newMovies = fetchMovies();
                notifyObservers(); // Notifica a los observadores
                /*
                if (isUpdated(newMovies)) {
                    lastFetchedMovies = new ArrayList<>(newMovies); // Actualiza la lista de películas

                }

                 */
            } catch (Exception e) {
                e.printStackTrace(); // Maneja errores (ej. API caída)
            }
        }, 0, intervalMinutes, TimeUnit.SECONDS);
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