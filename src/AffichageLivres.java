import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class AffichageLivres extends JFrame {

    private Connexion connexion;
    private List<JPanel> livrePanels;
    private JCheckBox disponibleCheckBox;
    private JPanel livresPanel;
    private int userID;

    public AffichageLivres(int userID) {
        super("Livres disponibles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.userID = userID;

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();

        JMenuItem livresBibliothequeItem = new JMenuItem("Livres de la bibliothèque");
        livresBibliothequeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageLivres(userID);
            }
        });
        menuBar.add(livresBibliothequeItem);

        JMenuItem biographieAuteursItem = new JMenuItem("Biographie des auteurs");
        biographieAuteursItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageAuteurs(userID);
            }
        });
        menuBar.add(biographieAuteursItem);

        JMenuItem mesEmpruntsItem = new JMenuItem("Mes emprunts");
        mesEmpruntsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageEmprunts(userID);
            }
        });
        menuBar.add(mesEmpruntsItem);

        JMenuItem monCompteItem = new JMenuItem("Mon compte");
        monCompteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new AffichageCompte(userID);
            }
        });
        menuBar.add(monCompteItem);

        JMenuItem deconnexionItem = new JMenuItem("Déconnexion");
        deconnexionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });
        menuBar.add(deconnexionItem);

        setJMenuBar(menuBar);

        JPanel titleSearchPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Livres disponibles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleSearchPanel.add(titleLabel, BorderLayout.NORTH);


        // Panneau de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel searchLabel = new JLabel("Recherche : ");
        JTextField searchField = new JTextField(20);
        searchField.addActionListener(new SearchActionListener());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        titleSearchPanel.add(searchPanel, BorderLayout.CENTER);

        disponibleCheckBox = new JCheckBox("Disponible");
        disponibleCheckBox.setSelected(false);
        disponibleCheckBox.addItemListener(new DisponibleCheckBoxListener());
        titleSearchPanel.add(disponibleCheckBox, BorderLayout.SOUTH);

        mainPanel.add(titleSearchPanel, BorderLayout.NORTH);

        livresPanel = new JPanel();
        livresPanel.setLayout(new BoxLayout(livresPanel, BoxLayout.Y_AXIS));

        livrePanels = new ArrayList<>();

        // Affichage des livres disponibles
        afficherLivresDisponibles();

        mainPanel.add(new JScrollPane(livresPanel), BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        setSize(800,600);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void afficherLivresDisponibles() {
        String sql = "SELECT b.TITLE, CONCAT(a.SURNAME, ' ', a.NAME) AS AUTHOR_NAME, a.SURNAME, a.NAME, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, a.ID AS AUTHOR_ID, b.IMAGE " +
                "FROM books b " +
                "INNER JOIN authors a ON b.AUTHOR_ID = a.ID ";

        if (disponibleCheckBox.isSelected()) {
            sql += "WHERE b.AVAILABILITY = 1";
        }

        try {
            ResultSet rs = connexion.query(sql);

            while (rs.next()) {
                JPanel livrePanel = createLivrePanel(rs);
                livrePanels.add(livrePanel);
                livresPanel.add(livrePanel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JPanel createLivrePanel(ResultSet rs) throws SQLException {
        JPanel livrePanel = new JPanel(new BorderLayout());
        livrePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        String authorFirstName = rs.getString("NAME");
        String authorLastName = rs.getString("SURNAME");
        String authorFullName = authorLastName + " " + authorFirstName;
        String imagePath = rs.getString("IMAGE");

        if (imagePath != null && !imagePath.isEmpty()) {
            try (InputStream inputStream = getClass().getResourceAsStream("/images/" + imagePath)) {
                if (inputStream != null) {
                    BufferedImage originalImage = ImageIO.read(inputStream);
                    int maxSize = 150;
                    int width = originalImage.getWidth();
                    int height = originalImage.getHeight();
                    int newWidth = width;
                    int newHeight = height;

                    if (width > maxSize || height > maxSize) {
                        if (width > height) {
                            newWidth = maxSize;
                            newHeight = (newWidth * height) / width;
                        } else {
                            newHeight = maxSize;
                            newWidth = (newHeight * width) / height;
                        }
                    }

                    Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    JLabel coverLabel = new JLabel(scaledIcon);
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    infoPanel.add(coverLabel, gbc);
                    gbc.gridy++;
                } else {
                    System.out.println("Error loading image: " + imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            gbc.gridy++;
        }

        JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
        gbc.gridx = 0;
        infoPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel authorLabel = new JLabel("Auteur: " + authorFullName);
        infoPanel.add(authorLabel, gbc);

        gbc.gridy++;
        JLabel genreLabel = new JLabel("Genre: " + rs.getString("GENRE"));
        infoPanel.add(genreLabel, gbc);

        gbc.gridy++;
        JLabel releaseDateLabel = new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE"));
        infoPanel.add(releaseDateLabel, gbc);

        gbc.gridy++;
        JLabel descriptionLabel = new JLabel("Description: " + rs.getString("DESCRIPTION"));
        infoPanel.add(descriptionLabel, gbc);

        gbc.gridy++;
        JLabel nbPagesLabel = new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES"));
        infoPanel.add(nbPagesLabel, gbc);

        JButton detailsButton = new JButton("Détails de l'auteur");
        detailsButton.setPreferredSize(new Dimension(140, 25));
        detailsButton.addActionListener(new DetailsButtonListener(rs.getInt("AUTHOR_ID")));

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        detailsPanel.add(detailsButton);

        livrePanel.add(infoPanel, BorderLayout.NORTH);
        livrePanel.add(detailsPanel, BorderLayout.SOUTH);

        return livrePanel;
    }

    class DetailsButtonListener implements ActionListener {
        private int authorId;

        public DetailsButtonListener(int authorId) {
            this.authorId = authorId;
        }

        public void actionPerformed(ActionEvent e) {
            new AuteurDetailsWindow(authorId);
        }
    }

    class SearchActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = ((JTextField) e.getSource()).getText().trim().toLowerCase();

            for (JPanel panel : livrePanels) {
                JPanel infoPanel = (JPanel) panel.getComponent(0);

                if (infoPanel.getComponentCount() > 3 && infoPanel.getComponent(3) instanceof JLabel) {
                    JLabel genreComponent = (JLabel) infoPanel.getComponent(3);
                    String genreText = genreComponent.getText();
                    String[] parts = genreText.split(":");
                    if (parts.length > 1) {
                        String genre = parts[1].trim().toLowerCase();
                        if (genre.contains(searchTerm)) {
                            panel.setVisible(true);
                            continue;
                        }
                    }
                }

                if (infoPanel.getComponentCount() > 1 && infoPanel.getComponent(1) instanceof JLabel) {
                    JLabel authorComponent = (JLabel) infoPanel.getComponent(1);
                    String author = authorComponent.getText();
                    if (author != null && author.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                if (infoPanel.getComponentCount() > 2 && infoPanel.getComponent(2) instanceof JLabel) {
                    JLabel genreComponent = (JLabel) infoPanel.getComponent(2);
                    String genre = genreComponent.getText();
                    if (genre != null && genre.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                if (infoPanel.getComponentCount() > 4 && infoPanel.getComponent(4) instanceof JLabel) {
                    JLabel authorFirstNameComponent = (JLabel) infoPanel.getComponent(4);
                    String authorFirstName = authorFirstNameComponent.getText();
                    if (authorFirstName != null && authorFirstName.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                if (infoPanel.getComponentCount() > 5 && infoPanel.getComponent(5) instanceof JLabel) {
                    JLabel authorLastNameComponent = (JLabel) infoPanel.getComponent(5);
                    String authorLastName = authorLastNameComponent.getText();
                    if (authorLastName != null && authorLastName.toLowerCase().contains(searchTerm)) {
                        panel.setVisible(true);
                        continue;
                    }
                }

                panel.setVisible(false);
            }
        }
    }
    class DisponibleCheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            livrePanels.clear();
            livresPanel.removeAll();
            getContentPane().validate();
            getContentPane().repaint();
            afficherLivresDisponibles();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AffichageLivres(3)); // Utilisateur avec ID 3
    }
}
