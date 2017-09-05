
import java.util.List;
import java.util.Scanner;

public class JobsSuggesterCli {

	private static Suggester js;

	public static void main(String[] args) {
		
		js = new Suggester(new JobTitles().getJobTitlesList());
/*
		testContainsExact("cook");
		testContainsExact("Engineer");
		testContainsExact("Builder");
		testContainsExact("garden");
		testContainsExact("house");

		testContainsSoundsLike("cook");
		testContainsSoundsLike("Engineer");
		testContainsSoundsLike("Builder");
		testContainsSoundsLike("garden");
		testContainsSoundsLike("house");

*/
		String query;
		System.out.println("dictionary size = " + js.size());
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.print("Enter partial job name orq to quit : ");
			query = scanner.nextLine();
			testContainsExact(query);
			testContainsSoundsLike(query);
		} while (query != "q");
		scanner.close();
	}

	public static void testContainsSoundsLike(String query) {
		List<String> suggestions = js.ContainsSoundsLike(query);
		
		suggestions.forEach(it -> {
			System.out.println(it);
			
		});
		//System.out.println("found " + suggestions.size());
	}

	

	public static void testContainsExact(String query) {
		
		List<String> suggestions = js.ContainsExact(query);
		suggestions.forEach(it -> { System.out.println(it); });
		//System.out.println("found " + suggestions.size());
	}

}
