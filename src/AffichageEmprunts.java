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

        // Reste du code pour la fenêtre principale
        JPanel titleSearchPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Liste de vos emprunts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleSearchPanel.add(titleLabel, BorderLayout.NORTH);

        mainPanel.add(titleSearchPanel, BorderLayout.NORTH);

        JPanel empruntsListPanel = new JPanel(new GridLayout(0, 1));

        // Requête SQL pour récupérer les emprunts de l'utilisateur connecté
        String sql = "SELECT b.ID, b.TITLE, a.SURNAME, a.NAME, br.LEASING_DATE, br.RETURN_DATE, br.GRADE " +
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
                final int bookID = rs.getInt("b.ID");

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

                // Ajout de Appréciation
                JLabel appreciationLabel = new JLabel("Appréciation");
                JPanel appreciationPanel = new JPanel();
                appreciationPanel.add(appreciationLabel);
                infoPanel.add(appreciationPanel);

                empruntPanel.add(infoPanel, BorderLayout.CENTER);

                // Bouton de vote
                JPanel votePanel = new JPanel();
                JButton likeButton = new JButton("\uD83D\uDC4D"); // Pouce levé
                JButton dislikeButton = new JButton("\uD83D\uDC4E"); // Pouce baissé
                int grade = rs.getInt("GRADE");
                if (grade == 0) {
                    dislikeButton.setEnabled(false);
                } else if (grade == 1) {
                    likeButton.setEnabled(false);
                }
                likeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateGrade(bookID, 1);
                        likeButton.setEnabled(false);
                        dislikeButton.setEnabled(true);
                    }
                });

                dislikeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        updateGrade(bookID, 0);
                        dislikeButton.setEnabled(false);
                        likeButton.setEnabled(true);
                    }
                });

                votePanel.add(likeButton);
                votePanel.add(dislikeButton);
                infoPanel.add(votePanel);

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

    // Pour mettre à jour la note dans la base de données
    private void updateGrade(int bookID, int grade) {
        String updateSql = "UPDATE borrow SET GRADE = ? WHERE USER_ID = ? AND BOOK_ID = ?";
        try {
            PreparedStatement pst = connexion.nouvelleConnexion().prepareStatement(updateSql);
            pst.setInt(1, grade);
            pst.setInt(2, userID);
            pst.setInt(3, bookID);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de la note.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AffichageEmprunts(3));
    }
}
