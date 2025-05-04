package Projekt;

import java.util.HashMap;
import java.util.Map;

public class Student_Telekomunikace extends Student {

	public Student_Telekomunikace(int id, String jmeno, String prijmeni, int rokNarozeni) {
		super(id, jmeno, prijmeni, rokNarozeni);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String schopnost() {
		// TODO Auto-generated method stub
		return prepisNaMorseCode(getJmeno() + " " + getPrijmeni());
	}

	 private String prepisNaMorseCode(String text) {
	        Map<Character, String> morseMap = new HashMap<>();
	        morseMap.put('A', ".-");    morseMap.put('B', "-...");
	        morseMap.put('C', "-.-.");  morseMap.put('D', "-..");
	        morseMap.put('E', ".");     morseMap.put('F', "..-.");
	        morseMap.put('G', "--.");   morseMap.put('H', "....");
	        morseMap.put('I', "..");    morseMap.put('J', ".---");
	        morseMap.put('K', "-.-");   morseMap.put('L', ".-..");
	        morseMap.put('M', "--");    morseMap.put('N', "-.");
	        morseMap.put('O', "---");   morseMap.put('P', ".--.");
	        morseMap.put('Q', "--.-");  morseMap.put('R', ".-.");
	        morseMap.put('S', "...");   morseMap.put('T', "-");
	        morseMap.put('U', "..-");   morseMap.put('V', "...-");
	        morseMap.put('W', ".--");   morseMap.put('X', "-..-");
	        morseMap.put('Y', "-.--");  morseMap.put('Z', "--..");
	        morseMap.put(' ', "/");     
	        
	        
	        StringBuilder result = new StringBuilder();
	        for (char c : text.toUpperCase().toCharArray()) {
	            if (morseMap.containsKey(c)) {
	                result.append(morseMap.get(c)).append(" ");
	            } else if (c == ' ') {
	                result.append("/ ");
	            }
	        }
	        return result.toString();
	    }
	
}
