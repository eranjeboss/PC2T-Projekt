package Projekt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Student_Kyberbezpecnost extends Student {

    public Student_Kyberbezpecnost(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public String schopnost() {
        return hashName(getJmeno() + " " + getPrijmeni());
    }
    
    private String hashName(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "Hashovani selhalo";
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " (Kyberbezpečnost)";
    }
}