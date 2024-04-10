import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class commentaireForm extends JFrame {
    private Connexion connexion;
    public commentaireForm(int borrowID) {
        super("Commentaire");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        connexion = new Connexion();
        Connection cn = connexion.nouvelleConnexion();
        String sql = "SELECT * FROM borrow WHERE ID = " + borrowID;

        try {
            ResultSet rs = connexion.query(sql);

            JPanel mainPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            if (rs.next()) {
                ;
            }
            boolean nouveau = Objects.equals(rs.getString("COMMENT"), "");
            JLabel titleLabel = new JLabel(nouveau ? "Nouveau commentaire" : "Modifier le commentaire");

            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(titleLabel);
            JTextField gradeField = new JTextField(20);
            JTextArea commentArea = new JTextArea(5, 20);
            gradeField.setText("" + rs.getInt("GRADE"));
            commentArea.setText(rs.getString("COMMENT"));
            JScrollPane commentScrollPane = new JScrollPane(commentArea);

            // Bouton pour enregistrer
            JButton saveButton = new JButton("Enregistrer");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Requête SQL
                    String sql = "UPDATE `borrow` SET `GRADE` = ?, `COMMENT` = ? WHERE `borrow`.`ID` = " + borrowID;
                    boolean succes = false;
                    try {
                        int grade = Integer.parseInt(gradeField.getText());
                        if ((0<=grade && grade<=5) && !Objects.equals(commentArea.getText(), "")) try {
                            PreparedStatement preparedStatement = cn.prepareStatement(sql);
                            preparedStatement.setInt(1, grade);
                            preparedStatement.setString(2, commentArea.getText());
                            preparedStatement.executeUpdate();

                            JOptionPane.showMessageDialog(null,
                                    nouveau ? "Commentaire ajouté avec succès !": "Commentaire modifié avec succès !");
                                    succes = true;
                            dispose();

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    if (!succes) {
                        JOptionPane.showMessageDialog(null, "Erreur lors de l'ajout du commentaire !");
                    }
                }
            });

            // Champs de saisie
            mainPanel.add(createLabelAndTextField("Note :", gradeField));
            mainPanel.add(createLabelAndTextArea("Commentaire :", commentScrollPane));
            mainPanel.add(saveButton);

            // Ajout du panneau principal à la fenêtre
            getContentPane().add(new JScrollPane(mainPanel));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        setSize(400, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLabelAndTextField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    // Méthode utilitaire pour créer un label et un champ de texte associé
    private JPanel createLabelAndTextArea(String labelText, JScrollPane scrollPane) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
//        SwingUtilities.invokeLater(commentaireForm::new);
    }
}
