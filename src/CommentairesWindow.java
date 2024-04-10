import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommentairesWindow extends JFrame {

    private Connexion connexion;
    public CommentairesWindow(int bookId, String bookName) {
        super("Commentaires - "+ Utilisateur.getName() + " " + Utilisateur.getSurname());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connexion = new Connexion();
        connexion.nouvelleConnexion();

        JPanel mainPanel = new JPanel(new GridLayout(0, 1));

        JLabel titleLabel = new JLabel("Commentaires");
        JLabel livreLabel = new JLabel(bookName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Police en gras, taille 20
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Centrer le texte
        mainPanel.add(titleLabel);

        // Requête SQL pour récupérer les détails de l'auteur
        String sql = "SELECT borrow.ID, borrow.USER_ID, borrow.LEASING_DATE, borrow.RETURN_DATE, borrow.GRADE, borrow.COMMENT, " +
                "users.SURNAME, users.NAME, users.EMAIL " +
                "FROM borrow JOIN users ON borrow.USER_ID = users.ID " +
                "WHERE borrow.BOOK_ID = " + bookId + " AND borrow.COMMENT != ''" +
                "ORDER BY borrow.RETURN_DATE DESC";
        try {
            ResultSet rs = connexion.query(sql);

            // Affichage des commentaires
            while (rs.next()) {
                JPanel listCommentsPanel = new JPanel(new BorderLayout());
                listCommentsPanel.setBorder(BorderFactory.createEtchedBorder());

                JPanel commentPanel = new JPanel(new GridLayout(0, 1));

                JLabel nameLabel = new JLabel(rs.getString("SURNAME") + " "
                        + rs.getString("NAME") + " | " + rs.getDate("RETURN_DATE"));
                JLabel commentLabel = new JLabel(rs.getString("COMMENT"));
                JLabel gradeLabel = new JLabel("Note : " + rs.getString("GRADE") + " sur 5");

                commentPanel.add(nameLabel);
                commentPanel.add(commentLabel);
                commentPanel.add(gradeLabel);

                listCommentsPanel.add(commentPanel, BorderLayout.CENTER);

                mainPanel.add(listCommentsPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Bouton Retour
        JButton retourButton = new JButton("Retour");
        retourButton.setPreferredSize(new Dimension(110, 25)); // Taille du bouton
        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Panel pour le bouton Retour
        JPanel retourPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Alignement au centre
        retourPanel.add(retourButton);
        mainPanel.add(retourPanel, BorderLayout.SOUTH); // Ajouter le panneau du bouton sous le panneau d'informations


        // Ajout du panneau principal à la fenêtre
        getContentPane().add(new JScrollPane(mainPanel));

        // Taille de la fenêtre
        setSize(400, 500);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CommentairesWindow(1, "L'Etranger"));
    }
}
