import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AffichageLivres extends JFrame {

    private Connexion connexion;

    public AffichageLivres() {
        super("Livres disponibles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialisation de la connexion à la base de données
        connexion = new Connexion();
        connexion.nouvelleConnexion();

        // Création du panneau principal
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        // Ajout du titre "Livres disponibles"
        JLabel title = new JLabel("Livres disponibles");
        title.setFont(new Font("Arial", Font.BOLD, 24)); // Police en gras, taille 24
        title.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte
        mainPanel.add(title);

        // Requête SQL pour récupérer les livres disponibles avec les informations sur les auteurs
        String sql = "SELECT b.TITLE, CONCAT(a.NAME, ' ', a.SURNAME) AS AUTHOR_NAME, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, a.ID AS AUTHOR_ID " +
                "FROM books b " +
                "INNER JOIN authors a ON b.AUTHOR_ID = a.ID " +
                "WHERE b.AVAILABILITY = 1";
        try {
            ResultSet rs = connexion.query(sql);

            // Parcours des résultats et affichage des informations des livres
            while (rs.next()) {
                JPanel livrePanel = new JPanel(new BorderLayout());
                livrePanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1)); // Panneau pour les informations du livre

                JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
                JLabel authorLabel = new JLabel("Auteur: " + rs.getString("AUTHOR_NAME")); // Utilisation du nom de l'auteur
                JLabel genreLabel = new JLabel("Genre: " + rs.getString("GENRE"));
                JLabel releaseDateLabel = new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE"));
                JLabel descriptionLabel = new JLabel("Description: " + rs.getString("DESCRIPTION"));
                JLabel nbPagesLabel = new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES"));

                // Ajout des labels à infoPanel
                infoPanel.add(new JLabel("Titre: " + rs.getString("TITLE")));
                infoPanel.add(new JLabel("Genre: " + rs.getString("GENRE")));
                infoPanel.add(new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE")));
                infoPanel.add(new JLabel("Description: " + rs.getString("DESCRIPTION")));
                infoPanel.add(new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES")));

                // Ajout du bouton "Détails" après le label de l'auteur
                infoPanel.add(authorLabel);

                // Bouton "Détails" pour afficher les détails de l'auteur
                JButton detailsButton = new JButton("Détails de l'auteur");
                detailsButton.addActionListener(new DetailsButtonListener(rs.getInt("AUTHOR_ID"))); // Ajoute un gestionnaire d'événements

                // Ajout du bouton à infoPanel
                infoPanel.add(detailsButton);

                livrePanel.add(infoPanel, BorderLayout.CENTER);

                mainPanel.add(livrePanel);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(new JScrollPane(mainPanel));

        // Taille de la fenêtre
        setSize(400, 500); // Taille (largeur/hauteur)

        setLocationRelativeTo(null); // Pour centrer la fenêtre
        setVisible(true);
    }

    // Gestionnaire d'événements pour le bouton "Détails"
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AffichageLivres::new);
    }
}
