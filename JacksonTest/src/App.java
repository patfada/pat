
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.apache.commons.io.FileUtils;

public class App {
	private static final String MAIN_PATH = "D:\\java\\workspace2\\JacksonTest\\src\\";

	public static void main(String[] args) throws IOException

	{

		File file1 = FileUtils.getFile(MAIN_PATH + "data.json");
		List<String> lines;
		try {
			lines = FileUtils.readLines(file1, "UTF-8");
			for (String line : lines) {
				System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File file2 = FileUtils.getFile(MAIN_PATH + "data.json");
		String json = FileUtils.readFileToString(file2, "UTF-8");
		ObjectMapper mapper = new ObjectMapper();
		List<MyEntity> myObjects = mapper.readValue(json, new TypeReference<List<MyEntity>>(){});
		
		myObjects.forEach(it -> {
			System.out.println(it.getStuff());
		});

		//use tis one
		List<MyEntity> myObjects2 = mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, MyEntity.class));
		myObjects.forEach(it -> {
			System.out.println(it.getId());
			System.out.println(it.getStuff());
		});

	}

}
