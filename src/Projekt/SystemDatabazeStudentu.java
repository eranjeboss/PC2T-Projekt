package Projekt;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SystemDatabazeStudentu {
    private DatabazeStudentu databaze;
    private Scanner scanner;
    
    public SystemDatabazeStudentu() {
        databaze = new DatabazeStudentu();
        scanner = new Scanner(System.in);
    }
    
    public void start() {
    
        databaze.nactiZDatabaze();
        
        boolean beh = true;
        while (beh) {
            vypisMenu();
            int volba = getIntInput("Volba: ");
            
            switch (volba) {
                case 1: pridatStudenta(); break;
                case 2: pridatZnamku(); break;
                case 3: odstranitStudenta(); break;
                case 4: vyhledatStudenta(); break;
                case 5: spustitSchopnost(); break;
                case 6: vypsatVsechny(); break;
                case 7: oborPrumery(); break;
                case 8: poctyStudentu(); break;
                case 9: ulozitDoSouboru(); break;
                case 10: nacistZeSouboru(); break;
                case 0: beh = false; break;
                default: System.out.println("Neplatná volba!");
            }
        }
        
        databaze.ulozDoDatabaze();
        scanner.close();
        System.out.println("Program byl ukončen.");
    }
    
    private void vypisMenu() {
        System.out.println("\n=== Databáze studentů ===");
        System.out.println("1. Přidat studenta");
        System.out.println("2. Přidat známku");
        System.out.println("3. Odstranit studenta");
        System.out.println("4. Vyhledat studenta");
        System.out.println("5. Speciální schopnost");
        System.out.println("6. Výpis všech studentů");
        System.out.println("7. Průměry oborů");
        System.out.println("8. Počty studentů");
        System.out.println("9. Uložit do souboru");
        System.out.println("10. Načíst ze souboru");
        System.out.println("0. Konec");
    }
    
    private void pridatStudenta() {
        System.out.println("\n--- Přidání studenta ---");
        System.out.print("Obor (T=Telekomunikace, K=Kyberbezpečnost): ");
        boolean jeTele = scanner.nextLine().toUpperCase().startsWith("T");
        
        String jmeno = getStringInput("Jméno: ");
        String prijmeni = getStringInput("Příjmení: ");
        int rokNarozeni = getIntInput("Rok narození: ");
        
        Student s = databaze.pridejStudenta(jeTele, jmeno, prijmeni, rokNarozeni);
        System.out.println("Vytvořen student s ID: " + s.getId());
    }
    
    private void pridatZnamku() {
        System.out.println("\n--- Přidání známky ---");
        int id = getIntInput("ID studenta: ");
        Student student = databaze.najdiPodleID(id);
        
        if (student == null) {
            System.out.println("Student nebyl nalezen!");
            return;
        }
        
        int znamka = getIntInput("Známka (1-5): ");
        if (znamka < 1 || znamka > 5) {
            System.out.println("Neplatná známka, musí být v rozmezí 1-5");
            return;
        }
        
        student.pridejZnamku(znamka);
        System.out.println("Známka byla úspěšně přidána.");
    }
    
    private void odstranitStudenta() {
        System.out.println("\n--- Odstranění studenta ---");
        int id = getIntInput("ID studenta pro odstranění: ");
        
        boolean success = databaze.odstranitStudenta(id);
        if (success) {
            System.out.println("Student byl úspěšně odstraněn.");
        } else {
            System.out.println("Student nebyl nalezen!");
        }
    }
    
    private void vyhledatStudenta() {
        System.out.println("\n--- Vyhledání studenta ---");
        int id = getIntInput("ID studenta: ");
        
        Student student = databaze.najdiPodleID(id);
        if (student != null) {
            System.out.println("\nNalezený student:");
            System.out.println("ID: " + student.getId());
            System.out.println("Jméno: " + student.getJmeno());
            System.out.println("Příjmení: " + student.getPrijmeni());
            System.out.println("Rok narození: " + student.getRokNarozeni());
            System.out.println("Studijní průměr: " + String.format("%.2f", student.prumer()));
            System.out.println("Obor: " + (student instanceof Student_Telekomunikace ? "Telekomunikace" : "Kyberbezpečnost"));
        } else {
            System.out.println("Student nebyl nalezen!");
        }
    }
    
    private void spustitSchopnost() {
        System.out.println("\n--- Speciální schopnost ---");
        int id = getIntInput("ID studenta: ");
        
        Student student = databaze.najdiPodleID(id);
        if (student != null) {
            System.out.println("Jméno: " + student.getJmeno() + " " + student.getPrijmeni());
            System.out.println("Speciální schopnost:");
            System.out.println(student.schopnost());
        } else {
            System.out.println("Student nebyl nalezen!");
        }
    }
    
    private void vypsatVsechny() {
        System.out.println("\n--- Výpis všech studentů ---");
        
        System.out.println("Studenti Telekomunikace:");
        List<Student> teleStudents = databaze.seraditPodleOboru(true);
        Collections.sort(teleStudents, (s1, s2) -> s1.getPrijmeni().compareTo(s2.getPrijmeni()));
        if (teleStudents.isEmpty()) {
            System.out.println("  Žádní studenti");
        } else {
            for (Student s : teleStudents) {
                System.out.println("  " + s);
            }
        }
        
        System.out.println("\nStudenti Kyberbezpečnost:");
        List<Student> kybStudents = databaze.seraditPodleOboru(false);
        Collections.sort(kybStudents, (s1, s2) -> s1.getPrijmeni().compareTo(s2.getPrijmeni()));
        if (kybStudents.isEmpty()) {
            System.out.println("  Žádní studenti");
        } else {
            for (Student s : kybStudents) {
                System.out.println("  " + s);
            }
        }
    }
    
    private void oborPrumery() {
        System.out.println("\n--- Průměry oborů ---");
        
        double telePrumer = databaze.prumerOboru(true);
        double kybPrumer = databaze.prumerOboru(false);
        
        System.out.println("Průměr oboru Telekomunikace: " + String.format("%.2f", telePrumer));
        System.out.println("Průměr oboru Kyberbezpečnost: " + String.format("%.2f", kybPrumer));
    }
    
    private void poctyStudentu() {
        System.out.println("\n--- Počty studentů ---");
        
        int pocetTele = databaze.pocetStudentuObor(true);
        int pocetKyb = databaze.pocetStudentuObor(false);
        
        System.out.println("Počet studentů oboru Telekomunikace: " + pocetTele);
        System.out.println("Počet studentů oboru Kyberbezpečnost: " + pocetKyb);
        System.out.println("Celkový počet studentů: " + (pocetTele + pocetKyb));
    }
    
    private void ulozitDoSouboru() {
        System.out.println("\n--- Uložení studenta do souboru ---");
        int id = getIntInput("ID studenta pro uložení: ");
        
        Student student = databaze.najdiPodleID(id);
        if (student == null) {
            System.out.println("Student nebyl nalezen!");
            return;
        }
        
        String fileName = getStringInput("Zadejte název souboru: ");
        if (!fileName.endsWith(".dat")) {
            fileName += ".dat";
        }
        
        try {
            databaze.ulozDoSouboru(id, fileName);
            System.out.println("Student byl úspěšně uložen do souboru " + fileName);
        } catch (IOException e) {
            System.out.println("Chyba při ukládání do souboru: " + e.getMessage());
        }
    }
    
    private void nacistZeSouboru() {
        System.out.println("\n--- Načtení studenta ze souboru ---");
        String fileName = getStringInput("Zadejte název souboru: ");
        if (!fileName.endsWith(".dat")) {
            fileName += ".dat";
        }
        
        try {
            Student student = databaze.nactiZeSouboru(fileName);
            System.out.println("Student byl úspěšně načten ze souboru:");
            System.out.println(student);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Chyba při načítání ze souboru: " + e.getMessage());
        }
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Neplatný vstup! Zadejte celé číslo.");
            }
        }
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    public static void main(String[] args) {
        SystemDatabazeStudentu system = new SystemDatabazeStudentu();
        system.start();
    }
}