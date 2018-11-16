import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

//author xiao wang at stevens cwid:10427141

public class ParseText {
	static HashMap<String, BigramMap> countMap = new HashMap<String, BigramMap>();

	public static void main(String argus[]) {
		ArrayList<String> fileNames = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(argus[0]))) {
			for (Path path : directoryStream) {

				try {

					String content = new String(Files.readAllBytes(path), "UTF-8");
					String[] words = content.replaceAll("[\\¡°\\¡±\"\\.\\,\\?\\!\\;\\:]", " ").replaceAll("\\¡¯", "'")
							.toLowerCase().split("\\s+");

					ArrayList<String> commonwords = readCommonwords();

					for (int i = 0; i < words.length; i++) {
						String word = words[i];
						if (commonwords.contains(word)) {
							BigramMap bm;
							if (countMap.containsKey(word)) {
								bm = countMap.get(word);
							} else {
								bm = new BigramMap();
								countMap.put(word, bm);
							}
							if (i > 0) {
								bm.addtoPre(words[i - 1]);
							}
							if (i < words.length - 1) {
								bm.addtoAfter(words[i + 1]);
							}
						}
					}

				} catch (IOException e) {
					System.out.println(e);
				}
			}
		} catch (IOException ex) {
		}

		for (String key : countMap.keySet()) {
			
			BigramMap bm = countMap.get(key);
			
			for(String preWord:bm.preMap.keySet()) {
				if(bm.preMap.get(preWord)>50) {
					System.out.println(preWord+" "+key+" "+bm.preMap.get(preWord));
				}
			}
			for(String afterWord:bm.afterMap.keySet()) {
				if(bm.afterMap.get(afterWord)>50) {
					System.out.println(key+" "+afterWord+" "+bm.afterMap.get(afterWord));
				}
			}
		}
	}

	static ArrayList<String> readCommonwords() {
		ArrayList<String> commonwords = new ArrayList<>();
		String content;
		try {
			content = new String(Files.readAllBytes(Paths.get("shortwords.txt")), "UTF-8");
			String[] words = content.split(System.getProperty("line.separator"));
			for (String word : words) {
				commonwords.add(word);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return commonwords;
	}
}

class BigramMap {
	HashMap<String, Integer> preMap = new HashMap<String, Integer>();
	HashMap<String, Integer> afterMap = new HashMap<String, Integer>();

	void addtoPre(String word) {
		if (preMap.containsKey(word)) {
			preMap.put(word, preMap.get(word) + 1);
		} else {
			preMap.put(word, 1);
		}
	}

	void addtoAfter(String word) {

		if (afterMap.containsKey(word)) {
			afterMap.put(word, afterMap.get(word) + 1);
		} else {
			afterMap.put(word, 1);
		}
	}
}
