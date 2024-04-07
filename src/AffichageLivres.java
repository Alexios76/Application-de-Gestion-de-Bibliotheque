import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AffichageLivres extends JFrame {

    private Connexion connexion;
    private List<JPanel> livrePanels;

    public AffichageLivres() {
        super("Livres disponibles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel pour le titre et le champ de recherche
        JPanel titleSearchPanel = new JPanel(new BorderLayout());

        // Titre "Livres disponibles"
        JLabel titleLabel = new JLabel("Livres disponibles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleSearchPanel.add(titleLabel, BorderLayout.NORTH);

        // Panel de recherche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel searchLabel = new JLabel("Recherche : ");
        JTextField searchField = new JTextField(20);
        searchField.addActionListener(new SearchActionListener());
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        titleSearchPanel.add(searchPanel, BorderLayout.CENTER);

        mainPanel.add(titleSearchPanel, BorderLayout.NORTH);

        JPanel livresPanel = new JPanel();
        livresPanel.setLayout(new BoxLayout(livresPanel, BoxLayout.Y_AXIS));

        livrePanels = new ArrayList<>();

        // Requête SQL pour récupérer les livres disponibles avec les informations sur les auteurs
        String sql = "SELECT b.TITLE, CONCAT(a.SURNAME, ' ', a.NAME) AS AUTHOR_NAME, a.SURNAME, a.NAME, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, a.ID AS AUTHOR_ID " +
                "FROM books b " +
                "INNER JOIN authors a ON b.AUTHOR_ID = a.ID " +
                "WHERE b.AVAILABILITY = 1";
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

        mainPanel.add(new JScrollPane(livresPanel), BorderLayout.CENTER);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        // Taille de la fenêtre
        setSize(400, 500);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLivrePanel(ResultSet rs) throws SQLException {
        JPanel livrePanel = new JPanel(new BorderLayout());
        livrePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel infoPanel = new JPanel(new GridLayout(0, 1)); // Panneau pour les informations du livre

        String authorFirstName = rs.getString("NAME");
        String authorLastName = rs.getString("SURNAME");
        String authorFullName = authorLastName + " " + authorFirstName;

        JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
        JLabel genreLabel = new JLabel("Genre: " + rs.getString("GENRE"));
        JLabel releaseDateLabel = new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE"));
        JLabel descriptionLabel = new JLabel("Description: " + rs.getString("DESCRIPTION"));
        JLabel nbPagesLabel = new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES"));
        JLabel authorLabel = new JLabel("Auteur: " + authorFullName);


        // Ajout des labels à infoPanel
        infoPanel.add(titleLabel);
        infoPanel.add(authorLabel);
        infoPanel.add(genreLabel);
        infoPanel.add(releaseDateLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(nbPagesLabel);

        // Bouton Détails
        infoPanel.add(authorLabel);

        // Pour afficher les détails de l'auteur
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
            // Ouvrir la fenêtre de détails de l'auteur avec l'ID de l'auteur
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AffichageLivres::new);
    }
}
