
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.lang3.time.StopWatch;

public class Suggester {

	private List<String> dictionary;
	private List<String> dictionarySoundsLike;
	private StopWatch stopWatch;
	private DoubleMetaphone soundsLike;

	public Suggester(List<String> dictionary) {
		this.dictionary = dictionary;
		this.stopWatch = new StopWatch();
		this.soundsLike = new DoubleMetaphone();
		dictionarySoundsLike = new ArrayList<String>();
		dictionary.forEach(it -> {
			dictionarySoundsLike.add(soundsLike.doubleMetaphone(it));
		});
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
		String q = soundsLike.doubleMetaphone(queryStr);
		for (int i = 0 ;i <dictionarySoundsLike.size(); i++) {
			if (isMatched(q, dictionarySoundsLike.get(i))) 
				result.add(dictionary.get(i));
		}
		
		stopWatch.stop();
		System.out.println("ContainsSoundsLike time: " + stopWatch.getTime(TimeUnit.MILLISECONDS) + "ms");
		return result;
	}

	private static boolean isMatched(String query, String text) {
		return text.toLowerCase().contains(query.toLowerCase());
	}


	public int size() {
		return dictionary.size();
	}

	}
