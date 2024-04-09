import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AffichageEmprunts extends JFrame {

    private Connexion connexion;
    private int userID;

    public AffichageEmprunts(int userID) {
        super("Liste des emprunts");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.userID = userID;

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Barre de navigation
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
                new AffichageCompte();
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

        // Reste du code pour la fenêtre principale
        JPanel titleSearchPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Liste des emprunts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleSearchPanel.add(titleLabel, BorderLayout.NORTH);

        mainPanel.add(titleSearchPanel, BorderLayout.NORTH);

        JPanel empruntsListPanel = new JPanel(new GridLayout(0, 1));

        // Requête SQL pour récupérer les emprunts de l'utilisateur connecté
        String sql = "SELECT b.TITLE, a.SURNAME, a.NAME, br.LEASING_DATE, br.RETURN_DATE " +
                "FROM borrow br " +
                "JOIN books b ON br.BOOK_ID = b.ID " +
                "JOIN authors a ON b.AUTHOR_ID = a.ID " +
                "WHERE br.USER_ID = ?";
        try {
            PreparedStatement pst = connexion.nouvelleConnexion().prepareStatement(sql);
            pst.setInt(1, userID);
            ResultSet rs = pst.executeQuery();

            // Parcours des résultats et affichage des informations des emprunts
            while (rs.next()) {
                JPanel empruntPanel = new JPanel(new BorderLayout());
                empruntPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1)); // Panneau pour les informations de l'emprunt

                JLabel titleLabel2 = new JLabel("Titre du livre: " + rs.getString("TITLE"));
                JLabel authorLabel = new JLabel("Auteur: " + rs.getString("SURNAME") + " " + rs.getString("NAME"));
                JLabel leasingDateLabel = new JLabel("Date d'emprunt: " + rs.getDate("LEASING_DATE"));
                JLabel returnDateLabel = new JLabel("Date de retour: " + rs.getDate("RETURN_DATE"));

                infoPanel.add(titleLabel2);
                infoPanel.add(authorLabel);
                infoPanel.add(leasingDateLabel);
                infoPanel.add(returnDateLabel);

                empruntPanel.add(infoPanel, BorderLayout.CENTER);

                empruntsListPanel.add(empruntPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mainPanel.add(new JScrollPane(empruntsListPanel), BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        setSize(800, 600);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AffichageEmprunts(3)); // Utilisateur avec ID 3
    }
}
