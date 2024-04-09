import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminListeEmprunts extends JFrame {

    private Connexion connexion;

    public AdminListeEmprunts() {
        super("Liste des emprunts réalisés");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        // Barre de navigation
        JMenuBar menuBar = new JMenuBar();

        // Menu "Gestion des livres"
        JMenuItem gestionLivresItem = new JMenuItem("Gestion des livres");
        gestionLivresItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de gestion des livres
                dispose(); // Fermer la fenêtre actuelle
                new AdminGestionLivres(); // Ouvrir la fenêtre de gestion des livres
            }
        });
        menuBar.add(gestionLivresItem);

        // Menu "Gestion des auteurs"
        JMenuItem gestionAuteursItem = new JMenuItem("Gestion des auteurs");
        gestionAuteursItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de gestion des auteurs
                dispose(); // Fermer la fenêtre actuelle
                new AdminGestionAuteurs(); // Ouvrir la fenêtre de gestion des auteurs
            }
        });
        menuBar.add(gestionAuteursItem);

        // Menu "Gestion des emprunts"
        JMenuItem gestionEmpruntsItem = new JMenuItem("Gestion des emprunts");
        gestionEmpruntsItem.setEnabled(false); // Désactiver le menu actuel
        menuBar.add(gestionEmpruntsItem);

        // Menu "Gestion des utilisateurs"
        JMenuItem gestionUtilisateursItem = new JMenuItem("Gestion des utilisateurs");
        gestionUtilisateursItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de gestion des utilisateurs
                dispose(); // Fermer la fenêtre actuelle
                new AdminGestionUtilisateurs(); // Ouvrir la fenêtre de gestion des utilisateurs
            }
        });
        menuBar.add(gestionUtilisateursItem);

        // Menu "Déconnexion"
        JMenuItem deconnexionItem = new JMenuItem("Déconnexion");
        deconnexionItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de connexion
                dispose(); // Fermer la fenêtre actuelle
                new LoginFrame(); // Ouvrir la fenêtre de connexion
            }
        });
        menuBar.add(deconnexionItem);

        setJMenuBar(menuBar);

        JLabel title = new JLabel("Liste des emprunts réalisés");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title);

        String sql = "SELECT bo.USER_ID, u.SURNAME, u.NAME, bo.BOOK_ID, b.TITLE, bo.LEASING_DATE, bo.RETURN_DATE, bo.GRADE, bo.COMMENT " +
                "FROM borrow bo " +
                "INNER JOIN users u ON bo.USER_ID = u.ID " +
                "INNER JOIN books b ON bo.BOOK_ID = b.ID";
        try {
            PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("USER_ID");
                int bookId = rs.getInt("BOOK_ID");

                JPanel empruntPanel = new JPanel(new BorderLayout());
                empruntPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1));

                JLabel userLabel = new JLabel("Utilisateur: " + rs.getString("SURNAME") + " " + rs.getString("NAME"));
                JLabel bookLabel = new JLabel("Livre: " + rs.getString("TITLE"));
                JLabel leasingDateLabel = new JLabel("Date d'emprunt: " + rs.getString("LEASING_DATE"));
                JLabel returnDateLabel = new JLabel("Date de retour: " + rs.getString("RETURN_DATE"));
                JLabel gradeLabel = new JLabel("Note: " + rs.getString("GRADE"));
                JLabel commentLabel = new JLabel("Commentaires: " + rs.getString("COMMENT"));

                infoPanel.add(userLabel);
                infoPanel.add(bookLabel);
                infoPanel.add(leasingDateLabel);
                infoPanel.add(returnDateLabel);
                infoPanel.add(gradeLabel);
                infoPanel.add(commentLabel);

                empruntPanel.add(infoPanel, BorderLayout.CENTER);

                JButton deleteButton = new JButton("Supprimer");
                deleteButton.setPreferredSize(new Dimension(110, 25));
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        deleteBorrow(userId, bookId);
                    }
                });

                JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                deletePanel.add(deleteButton);
                empruntPanel.add(deletePanel, BorderLayout.SOUTH);

                mainPanel.add(empruntPanel);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        getContentPane().add(new JScrollPane(mainPanel));

        setSize(800, 600); // Taille ajustée pour correspondre à celle de MainPageFrame
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void deleteBorrow(int userId, int bookId) {
        String deleteSql = "DELETE FROM borrow WHERE USER_ID = ? AND BOOK_ID = ?";
        try {
            PreparedStatement deleteStatement = connexion.nouvelleConnexion().prepareStatement(deleteSql);
            deleteStatement.setInt(1, userId);
            deleteStatement.setInt(2, bookId);
            int rowsAffected = deleteStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Emprunt supprimé avec succès !");
                dispose();
                new AdminListeEmprunts();
            } else {
                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'emprunt !");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'emprunt !");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminListeEmprunts::new);
    }
}
