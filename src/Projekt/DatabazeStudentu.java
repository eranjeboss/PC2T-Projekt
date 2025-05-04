package Projekt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatabazeStudentu implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Student> students = new ArrayList<>();
    private int nextId = 1;
    
    
    public Student pridejStudenta(boolean isTele, String jmeno, String prijmeni, int rokNarozeni) {
        Student newStudent;
        if (isTele) {
            newStudent = new Student_Telekomunikace(nextId, jmeno, prijmeni, rokNarozeni);
        } else {
            newStudent = new Student_Kyberbezpecnost(nextId, jmeno, prijmeni, rokNarozeni);
        }
        students.add(newStudent);
        nextId++;
        return newStudent;
    }
    
    
    public Student najdiPodleID(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }
        return null;
    }
    
   
    public boolean odstranitStudenta(int id) {
        Student student = najdiPodleID(id);
        if (student != null) {
            students.remove(student);
            return true;
        }
        return false;
    }
    
   
    public List<Student> seraditPodlePrijmeni() {
        List<Student> sortedList = new ArrayList<>(students);
        Collections.sort(sortedList, Comparator.comparing(Student::getPrijmeni));
        return sortedList;
    }
    
   
    public List<Student> seraditPodleOboru(boolean isTele) {
        List<Student> result = new ArrayList<>();
        for (Student student : students) {
            if ((isTele && student instanceof Student_Telekomunikace) || 
                (!isTele && student instanceof Student_Kyberbezpecnost)) {
                result.add(student);
            }
        }
        return result;
    }
    
   
    public double prumerOboru(boolean isTele) {
        List<Student> departmentStudents = seraditPodleOboru(isTele);
        if (departmentStudents.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        for (Student student : departmentStudents) {
            sum += student.prumer();
        }
        
        return sum / departmentStudents.size();
    }
    
   
    public int pocetStudentuObor(boolean isTele) {
        return seraditPodleOboru(isTele).size();
    }
    
   
    public void ulozDoSouboru(int id, String filename) throws IOException {
        Student student = najdiPodleID(id);
        if (student == null) {
            throw new IllegalArgumentException("Student nebyl nalezen");
        }
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(student);
        }
    }
    
  
    public Student nactiZeSouboru(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            Student student = (Student) in.readObject();
            Student existing = najdiPodleID(student.getId());
            if (existing != null) {
                students.remove(existing);
            }
            students.add(student);
            
          
            if (student.getId() >= nextId) {
                nextId = student.getId() + 1;
            }
            
            return student;
        }
    }
    
 
    public void ulozDoDatabaze() {
       
        String url = "jdbc:sqlite:studenti.db";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            
            vytvorTabulky(conn);
            
            
            vymazTabulky(conn);
            
            
            ulozDalsiId(conn);
            
            
            for (Student student : students) {
                ulozStudenta(conn, student);
            }
            
            System.out.println("Data byla úspěšně uložena do databáze.");
        } catch (SQLException e) {
            System.out.println("Chyba při ukládání do databáze: " + e.getMessage());
        }
    }
    

    public void nactiZDatabaze() {
        String url = "jdbc:sqlite:studenti.db";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            
            vytvorTabulky(conn);
            
            
            students.clear();
            
            
            nactiDalsiId(conn);
            
            
            nactiStudenta(conn);
            
            System.out.println("Data byla úspěšně načtena z databáze.");
        } catch (SQLException e) {
            System.out.println("Chyba při načítání z databáze: " + e.getMessage());
        }
    }
    
    private void vytvorTabulky(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
           
            stmt.execute("PRAGMA foreign_keys = ON");
            
           
            stmt.execute("CREATE TABLE IF NOT EXISTS counter (" +
                         "id INTEGER PRIMARY KEY, " +
                         "next_id INTEGER NOT NULL)");
            
            
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                         "id INTEGER PRIMARY KEY, " +
                         "jmeno TEXT NOT NULL, " +
                         "prijmeni TEXT NOT NULL, " +
                         "rok_narozeni INTEGER NOT NULL, " +
                         "je_telekomunikace INTEGER NOT NULL)");
            
            
            stmt.execute("CREATE TABLE IF NOT EXISTS znamky (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                         "student_id INTEGER NOT NULL, " +
                         "znamka INTEGER NOT NULL, " +
                         "FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE)");
        }
    }
    
    private void vymazTabulky(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM znamky");
            stmt.execute("DELETE FROM students");
            stmt.execute("DELETE FROM counter");
        }
    }
    
    private void ulozDalsiId(Connection conn) throws SQLException {
        String sql = "INSERT INTO counter (id, next_id) VALUES (1, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nextId);
            pstmt.executeUpdate();
        }
    }
    
    private void nactiDalsiId(Connection conn) throws SQLException {
        String sql = "SELECT next_id FROM counter WHERE id = 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                nextId = rs.getInt("next_id");
            } else {
                nextId = 1;
            }
        }
    }
    
    private void ulozStudenta(Connection conn, Student student) throws SQLException {
        String sql = "INSERT INTO students (id, jmeno, prijmeni, rok_narozeni, je_telekomunikace) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            pstmt.setString(2, student.getJmeno());
            pstmt.setString(3, student.getPrijmeni());
            pstmt.setInt(4, student.getRokNarozeni());
            pstmt.setInt(5, student instanceof Student_Telekomunikace ? 1 : 0);
            pstmt.executeUpdate();
        }
        
        for (int znamka : student.getZnamky()) {
            sql = "INSERT INTO znamky (student_id, znamka) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, student.getId());
                pstmt.setInt(2, znamka);
                pstmt.executeUpdate();
            }
        }
    }
    
    private void nactiStudenta(Connection conn) throws SQLException {
        String sql = "SELECT * FROM students";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String jmeno = rs.getString("jmeno");
                String prijmeni = rs.getString("prijmeni");
                int rokNarozeni = rs.getInt("rok_narozeni");
                boolean jeTele = rs.getInt("je_telekomunikace") == 1;
                
                Student student;
                if (jeTele) {
                    student = new Student_Telekomunikace(id, jmeno, prijmeni, rokNarozeni);
                } else {
                    student = new Student_Kyberbezpecnost(id, jmeno, prijmeni, rokNarozeni);
                }
               
                nactiZnamky(conn, student);
                
                students.add(student);
            }
        }
    }
    
    private void nactiZnamky(Connection conn, Student student) throws SQLException {
        String sql = "SELECT znamka FROM znamky WHERE student_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, student.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int znamka = rs.getInt("znamka");
                    student.pridejZnamku(znamka);
                }
            }
        }
    }
}