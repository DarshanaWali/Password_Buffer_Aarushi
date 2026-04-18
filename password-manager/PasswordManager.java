import java.io.*;
import java.util.*;

public class PasswordManager {

    private static final String USER_FILE = "users.txt";
    private static final String PASSWORD_FILE = "passwords.txt";

    // ------------------ REGISTER ------------------
    public static void register(String email, String password) throws Exception {

        String salt = HashUtil.generateSalt();
        String hash = HashUtil.hash(password, salt);

        FileWriter fw = new FileWriter(USER_FILE, true);
        fw.write(email + "," + salt + "," + hash + "\n");
        fw.close();
    }

    // ------------------ LOGIN ------------------
    public static boolean login(String email, String password) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(USER_FILE));
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts[0].equals(email)) {
                String salt = parts[1];
                String storedHash = parts[2];

                String hash = HashUtil.hash(password, salt);

                return hash.equals(storedHash);
            }
        }
        return false;
    }

    // ------------------ SAVE PASSWORD ------------------
    public static void savePassword(String email, String website, String username, String password, String key) throws Exception {

        String encrypted = EncryptionUtil.encrypt(password, key);
        String strength = PasswordStrength.check(password);

        PasswordEntry entry = new PasswordEntry(website, username, encrypted, strength);

        FileWriter fw = new FileWriter(PASSWORD_FILE, true);
        fw.write(email + "," + entry.toFileString() + "\n");
        fw.close();
    }

    // ------------------ GET PASSWORDS ------------------
    public static List<PasswordEntry> getPasswords(String email) throws Exception {

        List<PasswordEntry> list = new ArrayList<>();

        File file = new File(PASSWORD_FILE);
        if (!file.exists()) return list;

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {

            String[] parts = line.split(",", 2);

            if (parts[0].equals(email)) {
                PasswordEntry entry = PasswordEntry.fromFileString(parts[1]);
                list.add(entry);
            }
        }

        br.close();
        return list;
    }

     public static String getSalt(String email) throws Exception {

     BufferedReader br = new BufferedReader(new FileReader(USER_FILE));
     String line;

     while ((line = br.readLine()) != null) {
        String[] parts = line.split(",");

        if (parts[0].equals(email)) {
            return parts[1]; // salt
        }
     }
     return null;
    }
}