package Observable;

import Data.Movie;
import Data.TMDBApi.TMDBApi;
import Observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Netflix implements Observable {

    private List<Observer> observers;
    private List<Movie> lastFetchedMovies; // Lista de las últimas películas obtenidas
    private ScheduledExecutorService scheduler;

    public Netflix() {
        this.observers = new ArrayList<>();
        this.lastFetchedMovies = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(1); // Un único hilo programador
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
        return TMDBApi.fetchMovies("8");
    }

    @Override
    public void startAutoReload(int intervalMinutes) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Updating Netflix...");
                List<Movie> newMovies = fetchMovies();
                notifyObservers(); // Notifica a los observadores
                /*
                if (isUpdated(newMovies)) {
                    lastFetchedMovies = new ArrayList<>(newMovies); // Actualiza la lista de películas
                    notifyObservers(); // Notifica a los observadores
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