import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminGestionUtilisateurs extends JFrame {

    private Connexion connexion;
    private JPanel mainPanel;

    public AdminGestionUtilisateurs() {
        super("Gestion des utilisateurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        mainPanel = new JPanel(new BorderLayout());

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
        gestionEmpruntsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de gestion des emprunts
                dispose(); // Fermer la fenêtre actuelle
                new AdminListeEmprunts(); // Ouvrir la fenêtre de gestion des emprunts
            }
        });
        menuBar.add(gestionEmpruntsItem);

        // Menu "Gestion des utilisateurs" (déjà sélectionné)
        JMenuItem gestionUtilisateursItem = new JMenuItem("Gestion des utilisateurs");
        gestionUtilisateursItem.setEnabled(false); // Désactiver le menu actuel
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

        JLabel title = new JLabel("Gestion des utilisateurs");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel usersPanel = new JPanel(new GridLayout(0, 1));
        loadUsers(usersPanel);
        JScrollPane scrollPane = new JScrollPane(usersPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadUsers(JPanel usersPanel) {
        String sql = "SELECT * FROM users";
        try {
            ResultSet rs = connexion.query(sql);

            while (rs.next()) {
                // Stocker les valeurs du ResultSet dans des variables
                int userID = rs.getInt("ID");
                String surname = rs.getString("SURNAME");
                String name = rs.getString("NAME");
                String email = rs.getString("EMAIL");
                int adminStatus = rs.getInt("ADMIN");

                JPanel userPanel = new JPanel(new BorderLayout());
                userPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1));

                JLabel nameLabel = new JLabel("Nom: " + surname);
                JLabel surnameLabel = new JLabel("Prénom: " + name);
                JLabel emailLabel = new JLabel("Email: " + email);

                JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel statusLabel = new JLabel("Statut: ");
                JComboBox<String> statusComboBox = new JComboBox<>();
                statusComboBox.setPreferredSize(new Dimension(100, 20)); // Réduire la taille du JComboBox
                statusComboBox.addItem(adminStatus == 1 ? "admin" : "client");
                statusComboBox.addItem(adminStatus == 1 ? "client" : "admin");

                statusComboBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JComboBox comboBox = (JComboBox) e.getSource();
                        String selectedStatus = (String) comboBox.getSelectedItem();
                        int newStatus = selectedStatus.equals("admin") ? 1 : 0;
                        try {
                            updateStatus(userID, newStatus);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour du statut de l'utilisateur !");
                        }
                    }
                });

                JButton deleteButton = new JButton("Supprimer");
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cet utilisateur ?");
                        if (confirmation == JOptionPane.YES_OPTION) {
                            try {
                                deleteUser(userID);
                                usersPanel.removeAll();
                                loadUsers(usersPanel);
                                mainPanel.revalidate();
                                mainPanel.repaint();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'utilisateur !");
                            }
                        }
                    }
                });

                statusPanel.add(statusLabel);
                statusPanel.add(statusComboBox);

                infoPanel.add(nameLabel);
                infoPanel.add(surnameLabel);
                infoPanel.add(emailLabel);
                infoPanel.add(statusPanel);

                JPanel buttonPanel = new JPanel(new FlowLayout());
                buttonPanel.add(deleteButton);

                infoPanel.add(buttonPanel);

                userPanel.add(infoPanel, BorderLayout.CENTER);

                usersPanel.add(userPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void updateStatus(int userID, int newStatus) throws SQLException {
        String sql = "UPDATE users SET ADMIN = ? WHERE ID = ?";
        PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
        preparedStatement.setInt(1, newStatus);
        preparedStatement.setInt(2, userID);
        preparedStatement.executeUpdate();
    }

    private void deleteUser(int userID) throws SQLException {
        String sql = "DELETE FROM users WHERE ID=?";
        PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
        preparedStatement.setInt(1, userID);
        preparedStatement.executeUpdate();
        JOptionPane.showMessageDialog(null, "Utilisateur supprimé avec succès !");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminGestionUtilisateurs::new);
    }
}
