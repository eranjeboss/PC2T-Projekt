package Projekt;
import java.util.ArrayList;

public abstract class Student {
    private int id;
    private String jmeno;
    private String prijmeni;
    private int rokNarozeni;
    private ArrayList<Integer> znamky = new ArrayList<>();
    
    public Student(int id, String jmeno, String prijmeni, int rokNarozeni) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
    }
    
   
    public void pridejZnamku(int znamka) {
        if (znamka >= 1 && znamka <= 5) {
            znamky.add(znamka);
        } else {
            System.out.println("Neplatná známka, musí být mezi 1 a 5");
        }
    }
    
  
    public double prumer() {
        if (znamky.isEmpty()) {
            return 0;
        }
        
        int sum = 0;
        for (int finZnamka : znamky) {
            sum += finZnamka;
        }
        
        return (double) sum / znamky.size();
    }
    
    
    public ArrayList<Integer> getZnamky() {
        return znamky;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJmeno() {
        return jmeno;
    }

    public void setJmeno(String jmeno) {
        this.jmeno = jmeno;
    }

    public String getPrijmeni() {
        return prijmeni;
    }

    public void setPrijmeni(String prijmeni) {
        this.prijmeni = prijmeni;
    }

    public int getRokNarozeni() {
        return rokNarozeni;
    }

    public void setRokNarozeni(int rokNarozeni) {
        this.rokNarozeni = rokNarozeni;
    }

    
    public abstract String schopnost();
    
    @Override
    public String toString() {
        return id + ": " + prijmeni + ", " + jmeno + " (" + rokNarozeni + "), Průměr: " + String.format("%.2f", prumer());  
    }
}
