
import java.util.List;
import org.apache.commons.lang3.time.StopWatch;
import java.util.concurrent.TimeUnit;

public class Main {
	private Jobs jobs;

	public static void main(String[] args) {
		Main m = new Main();
		m.test("cook");
		m.test("Engineer");
		m.test("Builder");
		m.test("garden");
		m.test("house");
		
		

	}

	public Main() {
		jobs = new Jobs();
		System.out.println("dictionary size = " + jobs.size());
	}

	public void test(String query) {
		List<String> suggestions = jobs.ContainsExact(query);
		/*suggestions.forEach(it -> {
			System.out.println(it);
		});*/
		System.out.println("found " + suggestions.size());
	}

}
