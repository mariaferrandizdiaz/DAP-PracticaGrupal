package Observer;

import Data.Movie;

import javax.swing.*;
import java.util.List;
import UI.StreamingGUI;

public class User implements Observer {
    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(List<Movie> movies) {
        System.out.println("Recibidas nuevas películas para " + name);
        SwingUtilities.invokeLater(() -> StreamingGUI.updateMoviesForUser(name, movies));
    }

    @Override
    public String getId() {
        return name;
    }
}