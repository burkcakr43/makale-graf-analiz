import java.util.ArrayList;
import java.util.List;

public class Makale {
    private String id;
    private String title;
    private List<String> authors;
    private int year;
    private List<String> referencedWorkIds;

    // Graf Bağlantıları
    public List<Makale> references = new ArrayList<>(); // Bizim referans verdiklerimiz (Giden Ok)
    public List<Makale> citedBy = new ArrayList<>();    // Bize referans verenler (Gelen Ok)

    // Yeşil kenar için (Linked List yapısı)
    public Makale siradakiMakale = null;

    // Çizim Koordinatları ve Renk Durumu
    public int x, y;
    public boolean isCenter = false; // Çizimde merkezdeki eleman mı?

    // --- YENİ EKLENENLER (2.3 ANALİZ İÇİN) ---
    public boolean isKCore = false;  // K-Core algoritması sonucu seçilenler
    public double betweenness = 0.0; // Betweenness skoru

    public Makale(String id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.authors = new ArrayList<>();
        this.referencedWorkIds = new ArrayList<>();
    }

    // Getter Metotları
    public String getId() { return id; }
    public String getTitle() { return title; }
    public List<String> getAuthors() { return authors; }
    public int getYear() { return year; }
    public List<String> getReferencedWorkIds() { return referencedWorkIds; }

    // Atıf Sayısı
    public int getCitationCount() { return citedBy.size(); }

    public void addAuthor(String author) { this.authors.add(author); }
    public void addReference(String refId) { this.referencedWorkIds.add(refId); }

    // Gelen referansı ekle
    public void addCitedBy(Makale m) { this.citedBy.add(m); }

    // Yönsüz Komşuları Getir (Hem giden hem gelen) - Madde 2.3 için gerekli
    public List<Makale> getUndirectedNeighbors() {
        List<Makale> komsular = new ArrayList<>(references);
        for (Makale m : citedBy) {
            if (!komsular.contains(m)) {
                komsular.add(m);
            }
        }
        return komsular;
    }

    @Override
    public String toString() { return title; }
}