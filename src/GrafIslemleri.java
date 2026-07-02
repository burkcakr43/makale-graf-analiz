import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrafIslemleri {

    public static void grafOlustur(List<Makale> makaleler) {
        // 1. ID -> Makale Haritası (Hızlı erişim için)
        Map<String, Makale> makaleMap = new HashMap<>();
        for (Makale m : makaleler) {
            makaleMap.put(m.getId(), m);
        }

        // 2. SİYAH KENARLARI KUR (Referans İlişkisi)
        // Doküman Madde 2.1: Siyah kenar referans verenden verilene gider.
        for (Makale kaynak : makaleler) {
            for (String hedefId : kaynak.getReferencedWorkIds()) {
                Makale hedef = makaleMap.get(hedefId);
                if (hedef != null) {
                    kaynak.references.add(hedef); // Kaynak -> Hedef (Bizim referans verdiklerimiz)
                    hedef.addCitedBy(kaynak);     // Hedef <- Kaynak (Bize referans verenler - H-Index için lazım)
                }
            }
        }

        // 3. YEŞİL KENARLARI KUR (ID Sıralaması)
        // Doküman Madde 2.1: Yeşil kenarlar makaleleri artan id sırasına göre bağlar.

        // Listeyi ID'ye göre (String olarak) küçükten büyüğe sırala
        Collections.sort(makaleler, new Comparator<Makale>() {
            @Override
            public int compare(Makale m1, Makale m2) {
                return m1.getId().compareTo(m2.getId());
            }
        });

        // Her makaleyi bir sonrakine bağla (Linked List mantığı gibi)
        for (int i = 0; i < makaleler.size() - 1; i++) {
            makaleler.get(i).siradakiMakale = makaleler.get(i + 1);
        }

        System.out.println("Graf başarıyla oluşturuldu: Siyah (Referans) ve Yeşil (ID Sırası) kenarlar bağlandı.");
    }
}