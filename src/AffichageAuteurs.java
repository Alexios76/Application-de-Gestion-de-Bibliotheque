import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AffichageAuteurs extends JFrame {

    private Connexion connexion;

    public AffichageAuteurs() {
        super("Liste des auteurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialisation de la connexion à la base de données
        connexion = new Connexion();
        connexion.nouvelleConnexion();

        // Création du panneau principal
        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        // Ajout du titre "Liste des auteurs"
        JLabel title = new JLabel("Liste des auteurs");
        title.setFont(new Font("Arial", Font.BOLD, 24)); // Police en gras, taille 24
        title.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte
        mainPanel.add(title);

        // Requête SQL pour récupérer les auteurs
        String sql = "SELECT * FROM authors";
        try {
            ResultSet rs = connexion.query(sql);

            // Parcours des résultats et affichage des informations des auteurs
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
        SwingUtilities.invokeLater(AffichageAuteurs::new);
    }
}
