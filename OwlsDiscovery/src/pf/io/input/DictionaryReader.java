package pf.io.input;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DictionaryReader {

	/**
	 * Variable that represents the dictionary.
	 */
	private HashMap<String, ArrayList<String>> dictionary;

	/**
	 * Constructor of the class DictionaryReader.
	 * @param fileName - Name of the file that denotes the dictionary. 
	 */
	public DictionaryReader(String fileName) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String str;
			dictionary = new HashMap<String, ArrayList<String>>();
			String[] vectorWords;
			while ((str = in.readLine()) != null) {
				ArrayList<String> words = new ArrayList<String>();
				vectorWords = str.split(" ");
				for (int i = 1; i < vectorWords.length; i++) {
					words.add(vectorWords[i]);
				}
				dictionary.put(vectorWords[0], words);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method that calculates the Descriptive Similarity.
	 * @param keyRequest - Key from Request.
	 * @param keyService - Key from Service.
	 * @return - Return the degree of descriptive similarity.
	 */
	public double calculateDescriptiveSimilarity(String keyRequest,
			String keyService) {

		/*
		 * If they are the same, so the degree is the maximum allowed. 1.0
		 */
		if (keyService.equalsIgnoreCase(keyRequest)) {
			return 1.0;
		}

		ArrayList<String> words = (ArrayList<String>) dictionary
				.get(keyService);
		if (words != null) {
			for (int i = 0; i < words.size(); i++) {
				if (words.get(i).equalsIgnoreCase(keyRequest)) {
					
				/*
				 * This degree is returned when the words aren't same but the request one
				 * is in the service list. (synonymous)
				 */
				return 0.5;
				}
			}
		}
		/*
		 * This degree is returned when doesn't exist relationship
		 * between the variables 'keyRequest' and 'keyService'
		 */
		return 0.0;
	}

	/**
	 * @return the dictionary
	 */
	public HashMap<String, ArrayList<String>> getDictionary() {
		return dictionary;
	}

	/**
	 * @param dictionary the dictionary to set
	 */
	public void setDictionary(HashMap<String, ArrayList<String>> dictionary) {
		this.dictionary = dictionary;
	}
}