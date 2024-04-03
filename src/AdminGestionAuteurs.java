import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminGestionAuteurs extends JFrame {

    private Connexion connexion;
    private JPanel mainPanel;

    public AdminGestionAuteurs() {
        super("Gestion des auteurs");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        mainPanel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Gestion des auteurs");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        // Affichage de la liste des auteurs
        JPanel authorsPanel = new JPanel(new GridLayout(0, 1));
        loadAuthors(authorsPanel);
        JScrollPane scrollPane = new JScrollPane(authorsPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau pour les boutons en bas de la page
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton Actualiser
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authorsPanel.removeAll();
                loadAuthors(authorsPanel);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
        buttonPanel.add(refreshButton);

        // Bouton Ajouter un nouvel auteur
        JButton createButton = new JButton("Ajouter un nouvel auteur");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de création d'un nouvel auteur
                dispose(); // Fermer la fenêtre actuelle
                new AdminNouvAuteur(); // Ouvrir la fenêtre de création d'un nouvel auteur
            }
        });
        buttonPanel.add(createButton);

        // Ajout du panneau de boutons au panneau principal
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        // Taille de la fenêtre
        setSize(400, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Charger la liste des auteurs dans le panneau
    private void loadAuthors(JPanel authorsPanel) {
        String sql = "SELECT * FROM authors";
        try {
            ResultSet rs = connexion.query(sql);

            // Affichage des informations des auteurs avec leurs boutons respectifs
            while (rs.next()) {
                JPanel authorPanel = new JPanel(new BorderLayout());
                authorPanel.setBorder(BorderFactory.createEtchedBorder());

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

                // Boutons
                JButton deleteButton = new JButton("Supprimer");
                JButton editButton = new JButton("Modifier");

                // Récupération de l'ID de l'auteur
                int authorID = rs.getInt("ID");

                // Bouton Supprimer
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer cet auteur ?");
                        if (confirmation == JOptionPane.YES_OPTION) {
                            // Suppression de l'auteur dans la base de données
                            deleteAuthor(authorID);
                            // Rafraîchir la fenêtre
                            authorsPanel.removeAll();
                            loadAuthors(authorsPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                        }
                    }
                });

                // Bouton Modifier
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Ouvrir la fenêtre de modification de l'auteur
                        new AdminModifierAuteur(authorID);
                    }
                });

                infoPanel.add(deleteButton);
                infoPanel.add(editButton);

                authorPanel.add(infoPanel, BorderLayout.CENTER);

                authorsPanel.add(authorPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Suppression d'un auteur
    private void deleteAuthor(int authorID) {
        String sql = "DELETE FROM authors WHERE ID=?";
        try {
            PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
            preparedStatement.setInt(1, authorID);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Auteur supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression de l'auteur !");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminGestionAuteurs::new);
    }
}
