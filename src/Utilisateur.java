import jdk.jshell.execution.Util;

public class Utilisateur {
    private static int id;
    private static String surname;
    private static String name;
    private static String email;
    private static String password;
    private static int admin;
    private static float debt;

    public static void connexion(int id, String surname, String name, String email, int admin, float debt, String password) {
        Utilisateur.id = id;
        Utilisateur.surname = surname;
        Utilisateur.name = name;
        Utilisateur.email = email;
        Utilisateur.admin = admin;
        Utilisateur.debt = debt;
        Utilisateur.password = password;
    }

    public static int getId() {
        return id;
    }
    public static String getSurname() {
        return surname;
    }
    public static String getName() {
        return name;
    }
    public static String getEmail() {
        return email;
    }
    public static boolean getAdmin() {
        return (admin == 1);
    }
    public static void modification(String surname, String name, String email, String password) {
        Utilisateur.surname = surname;
        Utilisateur.name = name;
        Utilisateur.email = email;
        Utilisateur.password = password;
    }
    public static float getDebt() {
        return debt;
    }
    public static void deconnexion(){
        id = 0;
        surname = null;
        name = null;
        email = null;
        admin = 0;
        debt = 0;
    }
    public static void print(){  // débuggage
        System.out.println("id: " + id +
                " surname : " + surname +
                " name : " + name +
                " email : " + email +
                " admin : " + admin +
                " debt : " + debt);
    }
    public static void main(String args[]){
        print();
    }
}

