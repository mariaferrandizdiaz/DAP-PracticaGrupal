package Observer;

import Data.Movie;

import java.util.List;

public interface Observer {
    void update(List<Movie> movies);
    String getId();
}