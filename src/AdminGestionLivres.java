import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminGestionLivres extends JFrame {

    private Connexion connexion;
    private JPanel mainPanel;

    public AdminGestionLivres() {
        super("Gestion des livres");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        mainPanel = new JPanel(new BorderLayout());

        // Barre de navigation
        JMenuBar menuBar = new JMenuBar();

        // Menu "Gestion des livres" (déjà sélectionné)
        JMenuItem gestionLivresItem = new JMenuItem("Gestion des livres");
        gestionLivresItem.setEnabled(false); // Désactiver le lien de redirection vers la même page
        menuBar.add(gestionLivresItem);

        // Menu "Gestion des auteurs"
        JMenuItem gestionAuteursItem = new JMenuItem("Gestion des auteurs");
        gestionAuteursItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de gestion des auteurs
                dispose(); // Fermer la fenêtre actuelle
                new AdminGestionAuteurs();
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
                new AdminListeEmprunts();
            }
        });
        menuBar.add(gestionEmpruntsItem);


        // Menu "Gestion des utilisateurs"
        JMenuItem gestionUtilisateursItem = new JMenuItem("Gestion des utilisateurs");
        gestionUtilisateursItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de gestion des utilisateurs
                dispose(); // Fermer la fenêtre actuelle
                new AdminGestionUtilisateurs();
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
                new LoginFrame().setVisible(true);
            }
        });
        menuBar.add(deconnexionItem);

        setJMenuBar(menuBar);

        JLabel title = new JLabel("Gestion des livres");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        // Affichage de la liste des livres
        JPanel booksPanel = new JPanel(new GridLayout(0, 1));
        loadBooks(booksPanel);
        JScrollPane scrollPane = new JScrollPane(booksPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Panneau pour les boutons en bas de la page
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Bouton Actualiser
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                booksPanel.removeAll();
                loadBooks(booksPanel);
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        });
        buttonPanel.add(refreshButton);

        // Bouton Ajouter un nouveau livre
        JButton createButton = new JButton("Ajouter un nouveau livre");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Redirection vers la page de création d'un nouveau livre
                dispose(); // Fermer la fenêtre actuelle
                new AdminNouvLivre();
            }
        });
        buttonPanel.add(createButton);

        // Ajout du panneau de boutons au panneau principal
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajout du panneau principal à la fenêtre
        getContentPane().add(mainPanel);

        // Taille de la fenêtre
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Charger la liste des livres dans le panneau
    private void loadBooks(JPanel booksPanel) {
        String sql = "SELECT b.ID, b.TITLE, CONCAT(a.NAME, ' ', a.SURNAME) AS AUTHOR_NAME, b.GENRE, b.RELEASE_DATE, b.DESCRIPTION, b.NB_PAGES, b.availability " +
                "FROM books b " +
                "INNER JOIN authors a ON b.AUTHOR_ID = a.ID";
        try {
            ResultSet rs = connexion.query(sql);

            // Affichage des informations des livres avec leurs boutons respectifs
            while (rs.next()) {
                JPanel livrePanel = new JPanel(new BorderLayout());
                livrePanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel infoPanel = new JPanel(new GridLayout(0, 1)); // Panneau pour les informations du livre

                JLabel titleLabel = new JLabel("Titre: " + rs.getString("TITLE"));
                JLabel authorLabel = new JLabel("Auteur: " + rs.getString("AUTHOR_NAME"));
                JLabel genreLabel = new JLabel("Genre: " + rs.getString("GENRE"));
                JLabel releaseDateLabel = new JLabel("Date de sortie: " + rs.getInt("RELEASE_DATE"));
                JLabel descriptionLabel = new JLabel("Description: " + rs.getString("DESCRIPTION"));
                JLabel nbPagesLabel = new JLabel("Nombre de pages: " + rs.getInt("NB_PAGES"));
                JLabel availabilityLabel = new JLabel("Disponibilité: " + (rs.getInt("availability") == 1 ? "Disponible" : "Non disponible"));

                infoPanel.add(titleLabel);
                infoPanel.add(authorLabel);
                infoPanel.add(genreLabel);
                infoPanel.add(releaseDateLabel);
                infoPanel.add(descriptionLabel);
                infoPanel.add(nbPagesLabel);
                infoPanel.add(availabilityLabel); // Ajout de l'étiquette de disponibilité

                // Boutons
                JButton deleteButton = new JButton("Supprimer");
                JButton editButton = new JButton("Modifier");
                JButton borrowButton = new JButton(rs.getInt("availability") == 1 ? "Emprunter ce livre" : "Retourner ce livre");


                // Panneau pour les boutons
                JPanel buttonPanel = new JPanel(new FlowLayout());

                // Récupération de l'ID du livre
                int bookID = rs.getInt("ID");

                // Bouton Supprimer
                deleteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int confirmation = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ce livre ?");
                        if (confirmation == JOptionPane.YES_OPTION) {
                            // Suppression du livre dans la base de données
                            deleteBook(bookID);
                            // Rafraîchir la fenêtre
                            booksPanel.removeAll();
                            loadBooks(booksPanel);
                            mainPanel.revalidate();
                            mainPanel.repaint();
                        }
                    }
                });


                // Bouton Modifier
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // Ouvrir la fenêtre de modification du livre
                        new AdminModifierLivre(bookID);
                    }
                });


                // Bouton Emprunter/Retourner
                borrowButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleBorrowAction(bookID, booksPanel, mainPanel);
                    }
                });


                // Ajout des boutons au panneau des boutons
                buttonPanel.add(deleteButton);
                buttonPanel.add(editButton);
                buttonPanel.add(borrowButton);

                // Ajout du panneau des boutons au panneau d'informations du livre
                infoPanel.add(buttonPanel);

                livrePanel.add(infoPanel, BorderLayout.CENTER);

                booksPanel.add(livrePanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Suppression d'un livre
    private void deleteBook(int bookID) {
        String sql = "DELETE FROM books WHERE ID=?";
        try {
            PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
            preparedStatement.setInt(1, bookID);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "Livre supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la suppression du livre !");
        }
    }


    // Mettre à jour la disponibilité d'un livre
    private void updateBookAvailability(int bookID, int newAvailability) throws SQLException {
        String sql = "UPDATE books SET availability = ? WHERE ID = ?";
        try (PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql)) {
            preparedStatement.setInt(1, newAvailability);
            preparedStatement.setInt(2, bookID);
            preparedStatement.executeUpdate();
        }
    }

    // Déclarer une nouvelle méthode pour gérer l'action et l'exception SQLException
    private void handleBorrowAction(int bookID, JPanel booksPanel, JPanel mainPanel) {
        try {
            String sql = "SELECT availability, TITLE FROM books WHERE ID =?";
            PreparedStatement preparedStatement = connexion.nouvelleConnexion().prepareStatement(sql);
            preparedStatement.setInt(1, bookID);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int availability = rs.getInt("availability");
                String bookTitle = rs.getString("TITLE");
                if (availability == 1) {
                    // Afficher la fenêtre d'emprunt
                    new EmpruntLivre(bookID, bookTitle, connexion);
                } else {
                    int confirmation = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment retourner ce livre ?");
                    if (confirmation == JOptionPane.YES_OPTION) {
                        int newAvailability = 1; // Mettre à jour la disponibilité du livre à disponible (1)
                        updateBookAvailability(bookID, newAvailability); // Mettre à jour la disponibilité du livre
                        // Rafraîchir la fenêtre
                        booksPanel.removeAll();
                        loadBooks(booksPanel);
                        mainPanel.revalidate();
                        mainPanel.repaint();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Livre non trouvé !");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la mise à jour de la disponibilité du livre !");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminGestionLivres::new);
    }
}