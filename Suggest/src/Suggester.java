
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class Suggester {

	private List<String> dictionary;
	private List<String> dictionarySoundsLike;
	private StopWatch stopWatch;
	private DoubleMetaphone soundsLike;
	private  LevenshteinDistance distance;

	public Suggester(List<String> dictionary) {
		this.dictionary = dictionary;
		this.stopWatch = new StopWatch();
		this.soundsLike = new DoubleMetaphone();
		this.distance = new  LevenshteinDistance();
		createSoundsLikeDictionary();
		
		System.out.println("garden=" + soundsLike.doubleMetaphone("garden"));
		System.out.println("karitane=" + soundsLike.doubleMetaphone("karitane"));
		System.out.println("curtain=" + soundsLike.doubleMetaphone("curtain"));
	}

	//create a soundex representation of the dictionary
	private void createSoundsLikeDictionary() {
		dictionarySoundsLike = new ArrayList<String>();
		dictionary.forEach(it -> {
			dictionarySoundsLike.add(CreateSoundsLikePhrase(it));
		});
		
	}
	
	//returns a new string consisting of the soundex for each word in the input text, separated by spaces
	private String CreateSoundsLikePhrase(String text) {
		//split each entry into individual words
		String soundsLikePhrase = "";
		String[] words = text.trim().split("\\s+");
		for (int i=0; i < words.length;i++) {
			soundsLikePhrase += (" " + soundsLike.doubleMetaphone(words[i]));
		}
		return soundsLikePhrase;
		
	}
	
	
	public List<String> ContainsExact(String queryStr) {
		stopWatch.reset();
		stopWatch.start();
		List<String> result = new ArrayList<String>();
		dictionary.forEach(std -> {
			if (isMatched(queryStr, std)) {
				result.add(std);
			}

		});
		stopWatch.stop();
		System.out.println("ContainsExact time: " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
		return result;
	}


	public List<String> ContainsSoundsLike(String queryStr) {
		stopWatch.reset();
		stopWatch.start();
		List<String> result = new ArrayList<String>();
		String q = CreateSoundsLikePhrase(queryStr);
		for (int i = 0 ;i <dictionarySoundsLike.size(); i++) {
			if (isMatched(q, dictionarySoundsLike.get(i))) 
				result.add(dictionary.get(i));
		}
		
		stopWatch.stop();
		System.out.println("ContainsSoundsLike time: " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
		return result;
	}
	
	//order the suggestions list based on how close the suggestions are to the original query
	public List<String> orderSuggestions(List<String> suggestions, String query) {
		suggestions.sort(new SuggestionsComparitor( query));
		return suggestions;	
	}

	private static boolean isMatched(String query, String text) {
		return text.toLowerCase().contains(query.toLowerCase());
	}


	public int size() {
		return dictionary.size();
	}
	
	private class SuggestionsComparitor implements Comparator<String>{
		
		private String query;
		public SuggestionsComparitor(String query) {
			this.query = query;
		}


		@Override
		public int compare(String o1, String o2) {
			Integer d1 = distance.apply(query, o1);
			Integer d2 = distance.apply(query, o2);
			System.out.println("query : " + query  + " o1:" + o1 +  "(" + d1  +")  + o2:" + o2 +"(" + d2 +")");
			return d1-d2;
		}
		
	}

	}
    

