import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DosyaIslemleri {

    public static List<Makale> jsonOku(String dosyaYolu) {
        List<Makale> makaleler = new ArrayList<>();
        String jsonContent = "";

        try {
            jsonContent = Files.readString(Paths.get(dosyaYolu), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("HATA: Dosya bulunamadı! data.json dosyasını proje klasörüne (src yanına) attığından emin ol.");
            return makaleler;
        }

        // Basit regex ile nesneleri ayır
        Pattern objectPattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
        Matcher objectMatcher = objectPattern.matcher(jsonContent);

        while (objectMatcher.find()) {
            Makale makale = parseMakale(objectMatcher.group());
            if (makale != null) makaleler.add(makale);
        }
        return makaleler;
    }

    private static Makale parseMakale(String block) {
        String id = extractString(block, "\"id\":\\s*\"(.*?)\"");
        String title = extractString(block, "\"title\":\\s*\"(.*?)\"");
        int year = extractInt(block, "\"year\":\\s*(\\d+)");

        if (id.equals("Bilinmiyor")) return null;

        Makale makale = new Makale(id, title, year);

        List<String> authors = extractList(block, "\"authors\":\\s*\\[(.*?)\\]");
        for (String auth : authors) makale.addAuthor(auth.replace("\"", "").trim());

        List<String> refs = extractList(block, "\"referenced_works\":\\s*\\[(.*?)\\]");
        for (String ref : refs) makale.addReference(ref.replace("\"", "").trim());

        return makale;
    }

    private static String extractString(String source, String patternStr) {
        Matcher matcher = Pattern.compile(patternStr).matcher(source);
        return matcher.find() ? matcher.group(1) : "Bilinmiyor";
    }

    private static int extractInt(String source, String patternStr) {
        Matcher matcher = Pattern.compile(patternStr).matcher(source);
        try { return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0; }
        catch (Exception e) { return 0; }
    }

    private static List<String> extractList(String source, String patternStr) {
        List<String> list = new ArrayList<>();
        Matcher matcher = Pattern.compile(patternStr, Pattern.DOTALL).matcher(source);
        if (matcher.find()) {
            String content = matcher.group(1);
            if (content != null && !content.trim().isEmpty()) {
                for (String item : content.split(",")) {
                    if (!item.trim().isEmpty()) list.add(item.trim());
                }
            }
        }
        return list;
    }
}