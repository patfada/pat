
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JobsSuggesterCli {

	private static Suggester js;

	public static void main(String[] args) {

		// js = new Suggester(new JobTitles().getJobTitlesList());
		List<String> test = new ArrayList<String>();
		test.add("karitane gardener");
		test.add("fitter Welder");
		test.add("software Engineer");
		test.add("vege gardener");
		js = new Suggester(test);

		String query;
		System.out.println("dictionary size = " + js.size());
		Scanner scanner = new Scanner(System.in);
		do {
			System.out.print("Enter partial job name or q to quit : ");
			query = scanner.nextLine();
			List<String> suggestions = js.suggest(query);
			suggestions.forEach(it -> {
				System.out.println(it);
			});
			System.out.println();
		} while (query != "q");
		scanner.close();
	}

}
