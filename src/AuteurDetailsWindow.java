import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuteurDetailsWindow extends JFrame {

    private Connexion connexion;
    private int authorId;

    public AuteurDetailsWindow(int authorId) {
        super("Détails de l'auteur");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.authorId = authorId;

        // Initialisation de la connexion à la base de données
        connexion = new Connexion();
        connexion.nouvelleConnexion();

        // Création du panneau principal
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        // Ajout du titre "Fiche de l'auteur"
        JLabel titleLabel = new JLabel("Fiche de l'auteur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Police en gras, taille 20
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte
        mainPanel.add(titleLabel);

        // Requête SQL pour récupérer les détails de l'auteur
        String sql = "SELECT * FROM authors WHERE ID = " + authorId;
        try {
            ResultSet rs = connexion.query(sql);

            // Parcours des résultats et affichage des informations de l'auteur
            while (rs.next()) {
                JPanel auteurPanel = new JPanel(new BorderLayout());
                auteurPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1)); // Panneau pour les informations de l'auteur

                JLabel surnameLabel = new JLabel("Prénom: " + rs.getString("SURNAME"));
                JLabel nameLabel = new JLabel("Nom: " + rs.getString("NAME"));
                JLabel styleLabel = new JLabel("Style: " + rs.getString("STYLE"));
                JLabel biographyLabel = new JLabel("Biographie: " + rs.getString("BIOGRAPHY"));
                JLabel birthDateLabel = new JLabel("Date de naissance: " + rs.getInt("BIRTH_DATE"));
                JLabel deathDateLabel = new JLabel("Date de mort: " + rs.getInt("DEATH_DATE"));

                infoPanel.add(surnameLabel);
                infoPanel.add(nameLabel);
                infoPanel.add(styleLabel);
                infoPanel.add(biographyLabel);
                infoPanel.add(birthDateLabel);
                infoPanel.add(deathDateLabel);

                auteurPanel.add(infoPanel, BorderLayout.CENTER);

                mainPanel.add(auteurPanel);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AuteurDetailsWindow(1)); // Pour tester
    }
}
