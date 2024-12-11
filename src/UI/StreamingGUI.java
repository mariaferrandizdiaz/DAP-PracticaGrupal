package UI;

import Data.Movie;
import Observable.DisneyPlus;
import Observable.Max;
import Observable.Netflix;
import Observable.YelmoCines;
import Observable.Observable;
import Observer.User;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingGUI {
    private static final Map<String, Observable> services = new HashMap<>();
    private static final Map<String, JFrame> userWindows = new HashMap<>();
    private static final Map<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        // Inicializar servicios
        services.put("Max", new Max());
        services.put("Netflix", new Netflix());
        services.put("DisneyPlus", new DisneyPlus());
        services.put("YelmoCines", new YelmoCines());

        // Iniciar auto-recarga de cada servicio
        services.values().forEach(service -> service.startAutoReload(30));

        SwingUtilities.invokeLater(StreamingGUI::showUserManager);
    }

    private static void showUserManager() {
        JFrame userManagerFrame = new JFrame("Gestión de Usuarios");
        userManagerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userManagerFrame.setSize(400, 300);

        JPanel panel = new JPanel(new BorderLayout());

        DefaultListModel<String> userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        panel.add(userScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addUserButton = new JButton("Añadir Usuario");
        addUserButton.addActionListener(e -> {
            String userName = JOptionPane.showInputDialog(userManagerFrame, "Introduce el nombre del usuario:");
            if (userName != null && !userName.trim().isEmpty()) {
                if (users.containsKey(userName)) {
                    JOptionPane.showMessageDialog(userManagerFrame, "El usuario ya existe.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    User newUser = new User(userName);
                    users.put(userName, newUser);
                    userListModel.addElement(userName);
                    System.out.println("Usuario creado: " + newUser.getId());
                }
            }
        });


        JButton openUserButton = new JButton("Abrir Usuario");
        openUserButton.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                openUserWindow(selectedUser);
            } else {
                JOptionPane.showMessageDialog(userManagerFrame, "Selecciona un usuario primero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(addUserButton);
        buttonPanel.add(openUserButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        userManagerFrame.getContentPane().add(panel);
        userManagerFrame.setVisible(true);
    }

    private static void openUserWindow(String userName) {
        if (userWindows.containsKey(userName)) {
            userWindows.get(userName).toFront();
            return;
        }

        JFrame userFrame = new JFrame("Panel de Usuario - " + userName);
        userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        userFrame.setSize(1200, 800);

        JPanel moviesPanel = createMoviesPanel();

        JPanel controlPanel = createControlPanel(userName, users.get(userName));

        userFrame.getContentPane().add(controlPanel, BorderLayout.NORTH);
        userFrame.getContentPane().add(new JScrollPane(moviesPanel), BorderLayout.CENTER);

        userFrame.setVisible(true);
        userWindows.put(userName, userFrame);

        userFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                userWindows.remove(userName);
            }
        });
    }


    private static JPanel createControlPanel(String userName, User user) {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel userLabel = new JLabel("Usuario: " + userName);
        panel.add(userLabel);

        services.forEach((name, service) -> {
            JCheckBox serviceCheckBox = new JCheckBox(name);
            panel.add(serviceCheckBox);

            serviceCheckBox.addActionListener(e -> {
                if (serviceCheckBox.isSelected()) {
                    System.out.println("Intentando suscribir: " + user.getId() + " al servicio " + name);
                    service.subscribe(user);
                    System.out.println("Usuarios suscritos en " + name + ": " + service.getSubscribers());
                    JOptionPane.showMessageDialog(null, "Suscrito a " + name);
                } else {
                    service.unsubscribe(user);
                    System.out.println("Desuscrito: " + user.getId() + " del servicio " + name);
                    System.out.println("Usuarios suscritos en " + name + ": " + service.getSubscribers());
                    JOptionPane.showMessageDialog(null, "Desuscrito de " + name);
                }
            });

        });

        return panel;
    }


    private static JPanel createMoviesPanel() {
        return new JPanel(new GridLayout(0, 4, 10, 10));
    }

    public static void updateMoviesForUser(String userName, List<Movie> movies) {
        JFrame userFrame = userWindows.get(userName);
        if (userFrame == null) {
            return;
        }

        JScrollPane scrollPane = (JScrollPane) userFrame.getContentPane().getComponent(1);
        JPanel moviesPanel = (JPanel) scrollPane.getViewport().getView();

        System.out.println("Número de películas para mostrar: " + movies.size());
        moviesPanel.removeAll();

        for (Movie movie : movies) {
            JPanel moviePanel = createMoviePanel(movie);
            moviesPanel.add(moviePanel);
        }

        moviesPanel.revalidate();
        moviesPanel.repaint();
    }


    private static JPanel createMoviePanel(Movie movie) {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            ImageIcon posterIcon = new ImageIcon(new URL(movie.getImagenUrl()));
            JLabel posterLabel = new JLabel(new ImageIcon(posterIcon.getImage().getScaledInstance(120, 180, Image.SCALE_SMOOTH)));
            panel.add(posterLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            panel.add(new JLabel("Sin Imagen"), BorderLayout.CENTER);
        }

        JLabel titleLabel = new JLabel("<html><center>" + movie.getTitulo() + "</center></html>", SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.SOUTH);

        return panel;
    }
}