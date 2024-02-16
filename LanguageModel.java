import java.util.HashMap;
import java.util.Random;
import java.util.random.RandomGenerator;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {

        In newReader = new In(fileName);

        String text = newReader.readAll();

        for (int i = 0; i < text.length() - windowLength ; ++i) {
            String key = text.substring(i, i + windowLength);
            if (CharDataMap.get(key) == null) {
                CharDataMap.put(key, new List());
            }
            CharDataMap.get(key).update(text.charAt(i + windowLength));
            calculateProbabilities(CharDataMap.get(key));
        }
	}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        int totalCharCount = 0;

        ListIterator lIterator = probs.listIterator(0);
        while (lIterator != null && lIterator.hasNext()) {
            totalCharCount += lIterator.next().count;
        }

        double cp = 0.0;
        for (int i = 0; i < probs.getSize(); ++i) {
            CharData currentCharData = probs.get(i);
            currentCharData.p = (double)currentCharData.count / totalCharCount;
            currentCharData.cp = (double)cp + currentCharData.p;
            cp += currentCharData.p;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
        double cpValue = randomGenerator.nextDouble();
        CharData[] charDataArray = probs.toArray();

        for (CharData cd : charDataArray) {
            if (cd.cp > cpValue) {
                return cd.chr;
            }
        }
        // will never get here
        return ' '; 
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(initialText);

        while (stringBuilder.length() < textLength) {
            String key = stringBuilder.substring(stringBuilder.length() - windowLength);
            stringBuilder.append(getRandomChar(CharDataMap.get(key)));
        }
        return stringBuilder.toString();
    }

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
