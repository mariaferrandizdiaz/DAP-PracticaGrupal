package Observable;

import Data.Movie;
import Observer.Observer;

import java.util.List;

public interface Observable {
    void subscribe(Observer observer);
    void unsubscribe(Observer observer);
    void notifyObservers();
    List<Movie> fetchMovies();
    void startAutoReload(int intervalSeconds);
    void stopAutoReload();
    boolean isUpdated(List<Movie> newMovies);
    String getSubscribers();
}