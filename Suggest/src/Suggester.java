
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class Suggester {

	private List<SoundsLike> dictionarySoundsLike;
	private StopWatch stopWatch;
	private DoubleMetaphone soundsLike;
	private LevenshteinDistance distance;
	// extra percentage bonus to give matches which are at the start of the
	// string
	private static final int PREFIX_PERCENT_BONUS = 5;

	public Suggester(List<String> dictionary) {
		this.stopWatch = new StopWatch();
		this.soundsLike = new DoubleMetaphone();
		this.distance = new LevenshteinDistance();
		createSoundsLikeDictionary(dictionary);

		System.out.println("garden=" + soundsLike.doubleMetaphone("garden"));
		System.out.println("karitane=" + soundsLike.doubleMetaphone("karitane"));
		System.out.println("curtain=" + soundsLike.doubleMetaphone("curtain"));
	}

	// create a soundex representation of the dictionary
	private void createSoundsLikeDictionary(List<String> dictionary) {
		
		dictionarySoundsLike = new ArrayList<SoundsLike>();
		dictionary.forEach(it -> {
			String[] words = it.trim().split("\\s+");
			dictionarySoundsLike.add(new SoundsLike(it,createSoundsLikePhrase(words));
		});
	}

	// returns a new string consisting of the soundex for each word in the input
	// text, separated by spaces
	private String createSoundsLikePhrase(String[] words) {
		// split each entry into individual words
		String soundsLikePhrase = "";
		for (int i = 0; i < words.length; i++) {
			soundsLikePhrase += (" " + soundsLike.doubleMetaphone(words[i]) + " ");
		}
		return soundsLikePhrase;

	}

	public List<String> suggest(String query) {

		String[] words = query.trim().split("\\s+");

		stopWatch.reset();
		stopWatch.start();
		// List<String> result = new ArrayList<String>();
		Map<Integer, String> scoredMatches = new TreeMap<Integer, String>();

		String q = createSoundsLikePhrase(words);
		for (int i = 0; i < dictionarySoundsLike.size(); i++) {
			String text = dictionarySoundsLike.get(i).getText();
			for (int j = 0; j < words.length; j++) {
				if (isMatched(words[j], dictionarySoundsLike.get(i).soundex))
					scoredMatches.put(score(text, query), text);
			}
		}
		// now look for exact prefixes or contained substrings
		dictionary.forEach(std -> {
			if (isMatched(query, std)) {
				scoredMatches.put(score(std, query), std);
			}

		});

		stopWatch.stop();
		System.out.println("suggest time: " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");

		return new ArrayList<String>(scoredMatches.values());

	}

	private Integer score(String text, String query) {

		int base = text.length();
		int dist = distance.apply(query, text);
		float percentage = 100 * (Math.abs(base - dist) / (float) base);
		int result = Math.round(percentage);
		// score higher if match is at start of string
		if (text.startsWith(query)) {
			result = Math.max(100, result + 5);
		}
		System.out.println(text + ":" + Math.round(percentage));
		return new Integer(Math.round(percentage));
	}

	// order the suggestions list based on how close the suggestions are to the
	// original query
	public List<String> orderSuggestions(List<String> suggestions, String query) {
		suggestions.sort(new SuggestionsComparitor(query));
		return suggestions;
	}

	private static boolean isMatched(String query, String text) {
		return text.toLowerCase().contains(query.toLowerCase());
	}

	public int size() {
		return dictionary.size();
	}

	private class SuggestionsComparitor implements Comparator<String> {

		private String query;

		public SuggestionsComparitor(String query) {
			this.query = query;
		}

		@Override
		public int compare(String o1, String o2) {
			Integer d1 = distance.apply(query, o1);
			Integer d2 = distance.apply(query, o2);
			System.out.println("query : " + query + " o1:" + o1 + "(" + d1 + ")  + o2:" + o2 + "(" + d2 + ")");
			return d1 - d2;
		}

	}

	private class SoundsLike {

		private String text;
		private String soundex;

		public SoundsLike(String text, String soundex) {
			super();
			this.text = text;
			this.soundex = soundex;
		}

		public String getText() {
			return text;
		}

		public String getSoundex() {
			return soundex;
		}		

	}
}
