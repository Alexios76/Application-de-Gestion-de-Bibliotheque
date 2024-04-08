import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream; // Import pour InputStream
import java.io.IOException; // Import pour IOException
import javax.imageio.ImageIO; // Import pour ImageIO
import java.awt.image.BufferedImage; // Import pour BufferedImage

public class AffichageLivres extends JFrame {

    private Connexion connexion;
    private List<JPanel> livrePanels;
    private JCheckBox disponibleCheckBox; // Case à cocher
    private JPanel livresPanel;

    public AffichageLivres() {
        super("Livres disponibles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panneau pour le titre, le champ de recherche et la case à cocher Disponible
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

        // Case à cocher Disponible
        disponibleCheckBox = new JCheckBox("Disponible");
        disponibleCheckBox.setSelected(false); // Par défaut, non cochée
        disponibleCheckBox.addItemListener(new DisponibleCheckBoxListener());
        titleSearchPanel.add(disponibleCheckBox, BorderLayout.SOUTH);

        mainPanel.add(titleSearchPanel, BorderLayout.NORTH);

        livresPanel = new JPanel();
        livresPanel.setLayout(new BoxLayout(livresPanel, BoxLayout.Y_AXIS));

        livrePanels = new ArrayList<>();

        // Affichage initial des livres disponibles
        afficherLivresDisponibles();

        mainPanel.add(new JScrollPane(livresPanel), BorderLayout.CENTER);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        // Taille de la fenêtre
        setSize(400, 500);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Méthode pour afficher les livres disponibles
    private void afficherLivresDisponibles() {
        // Requête SQL pour récupérer les livres disponibles avec les informations sur les auteurs
        String sql = "SELECT b.TITLE, CONCAT(a.SURNAME, ' ', a.NAME) AS AUTHOR_NAME, a.SURNAME, a.NAME, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, a.ID AS AUTHOR_ID, b.IMAGE " +
                "FROM books b " +
                "INNER JOIN authors a ON b.AUTHOR_ID = a.ID ";

        if (disponibleCheckBox.isSelected()) {
            // Afficher uniquement les livres disponibles
            sql += "WHERE b.AVAILABILITY = 1";
        }

        try {
            ResultSet rs = connexion.query(sql);

            // Affichage des informations des livres
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
        gbc.insets = new Insets(5, 5, 5, 5); // Espacement entre les composants

        String authorFirstName = rs.getString("NAME");
        String authorLastName = rs.getString("SURNAME");
        String authorFullName = authorLastName + " " + authorFirstName;
        String imagePath = rs.getString("IMAGE");

        // JLabel pour afficher l'image
        if (imagePath != null && !imagePath.isEmpty()) {
            try (InputStream inputStream = getClass().getResourceAsStream("/images/" + imagePath)) {
                if (inputStream != null) {
                    BufferedImage originalImage = ImageIO.read(inputStream);

                    // Redimensionner l'image
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

                    // Créer une nouvelle image redimensionnée
                    Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    ImageIcon scaledIcon = new ImageIcon(scaledImage);
                    JLabel coverLabel = new JLabel(scaledIcon);
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    infoPanel.add(coverLabel, gbc);
                    gbc.gridy++; // Saut de ligne après l'image
                } else {
                    System.out.println("Error loading image: " + imagePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Si aucune image n'est disponible, déplacer la grille vers la ligne suivante
            gbc.gridy++;
        }

        // Titre du livre
        JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
        gbc.gridx = 0;
        infoPanel.add(titleLabel, gbc);

        // Autres champs du livre
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

        // Bouton Détails
        JButton detailsButton = new JButton("Détails de l'auteur");
        detailsButton.setPreferredSize(new Dimension(140, 25)); // Taille du bouton
        detailsButton.addActionListener(new DetailsButtonListener(rs.getInt("AUTHOR_ID")));

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        detailsPanel.add(detailsButton);

        // Ajoute le infoPanel en haut et detailsPanel en bas du livrePanel
        livrePanel.add(infoPanel, BorderLayout.NORTH);
        livrePanel.add(detailsPanel, BorderLayout.SOUTH);

        return livrePanel;
    }




    // Gestionnaire d'événements pour le bouton Détails
    class DetailsButtonListener implements ActionListener {
        private int authorId;

        public DetailsButtonListener(int authorId) {
            this.authorId = authorId;
        }

        public void actionPerformed(ActionEvent e) {
            // Ouvre la fenêtre de détails de l'auteur
            new AuteurDetailsWindow(authorId);
        }
    }

    // Gestionnaire d'événements pour la recherche
    class SearchActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchTerm = ((JTextField) e.getSource()).getText().trim().toLowerCase();

            for (JPanel panel : livrePanels) {
                String title = ((JLabel) ((JPanel) panel.getComponent(0)).getComponent(0)).getText().toLowerCase();
                String author = ((JLabel) ((JPanel) panel.getComponent(0)).getComponent(1)).getText().toLowerCase();
                String authorFirstName = ((JLabel) ((JPanel) panel.getComponent(0)).getComponent(4)).getText().toLowerCase();
                String authorLastName = ((JLabel) ((JPanel) panel.getComponent(0)).getComponent(5)).getText().toLowerCase();
                String genre = ((JLabel) ((JPanel) panel.getComponent(0)).getComponent(2)).getText().toLowerCase();

                if (title.contains(searchTerm) || author.contains(searchTerm) || genre.contains(searchTerm) || authorFirstName.contains(searchTerm) || authorLastName.contains(searchTerm)) {
                    panel.setVisible(true);
                } else {
                    panel.setVisible(false);
                }
            }
        }
    }

    // Gestionnaire d'événements pour la case à cocher "Disponible"
    class DisponibleCheckBoxListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            // Efface les livres actuellement affichés
            livrePanels.clear();
            livresPanel.removeAll();
            getContentPane().validate();
            getContentPane().repaint();

            // Affiche les livres disponibles
            afficherLivresDisponibles();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AffichageLivres::new);
    }
}
