
import java.util.List;
import org.apache.commons.lang3.time.StopWatch;
import java.util.concurrent.TimeUnit;

public class Main {
	private Jobs jobs;

	public static void main(String[] args) {
		Main m = new Main();
		m.testContainsExact("cook");
		m.testContainsExact("Engineer");
		m.testContainsExact("Builder");
		m.testContainsExact("garden");
		m.testContainsExact("house");
		
		m.testContainsSoundsLike("cook");
		m.testContainsSoundsLike("Engineer");
		m.testContainsSoundsLike("Builder");
		m.testContainsSoundsLike("garden");
		m.testContainsSoundsLike("house");
		
		m.testContainsSoundsLike2("cook");
		m.testContainsSoundsLike2("Engineer");
		m.testContainsSoundsLike2("Builder");
		m.testContainsSoundsLike2("garden");
		m.testContainsSoundsLike2("house");

	}

	public Main() {
		jobs = new Jobs();
		System.out.println("dictionary size = " + jobs.size());
	}

	public void testContainsSoundsLike(String query) {
		List<String> suggestions = jobs.ContainsSoundsLike(query);
		/*suggestions.forEach(it -> {
			System.out.println(it);
		});*/
		System.out.println("found " + suggestions.size());
	}
	
	public void testContainsSoundsLike2(String query) {
		List<String> suggestions = jobs.ContainsSoundsLike2(query);
		/*suggestions.forEach(it -> {
			System.out.println(it);
		});*/
		System.out.println("found " + suggestions.size());
	}
	
	public void testContainsExact(String query) {
		List<String> suggestions = jobs.ContainsExact(query);
		/*suggestions.forEach(it -> {
			System.out.println(it);
		});*/
		System.out.println("found " + suggestions.size());
	}


}
