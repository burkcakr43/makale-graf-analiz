import java.util.*;

public class Hesaplama {

    public static class Sonuc {
        public int hIndex;
        public int hMedian;
        public List<Makale> hCoreList;

        public Sonuc(int hIndex, int hMedian, List<Makale> hCoreList) {
            this.hIndex = hIndex;
            this.hMedian = hMedian;
            this.hCoreList = hCoreList;
        }
    }

    // --- MEVCUT H-INDEX HESAPLAMA ---
    public static Sonuc hIndexHesapla(Makale makale) {
        List<Makale> atifYapanlar = new ArrayList<>(makale.citedBy);
        if (atifYapanlar.isEmpty()) return new Sonuc(0, 0, new ArrayList<>());

        Collections.sort(atifYapanlar, (m1, m2) -> Integer.compare(m2.getCitationCount(), m1.getCitationCount()));

        int h = 0;
        for (int i = 0; i < atifYapanlar.size(); i++) {
            if (atifYapanlar.get(i).getCitationCount() >= i + 1) h = i + 1;
            else break;
        }

        List<Makale> hCore = new ArrayList<>();
        for (int i = 0; i < h; i++) hCore.add(atifYapanlar.get(i));

        int median = 0;
        if (h > 0) {
            int orta = hCore.size() / 2;
            median = (hCore.size() % 2 == 1) ? hCore.get(orta).getCitationCount()
                    : (hCore.get(orta - 1).getCitationCount() + hCore.get(orta).getCitationCount()) / 2;
        }
        return new Sonuc(h, median, hCore);
    }

    // --- YENİ: K-CORE DECOMPOSITION ---
    public static void kCoreAnalizi(List<Makale> graphNodes, int k) {
        // Önce hepsini sıfırla
        for(Makale m : graphNodes) m.isKCore = false;

        // 1. Düğümleri ve derecelerini bir haritaya al (Sadece ekrandaki/listendeki düğümler için)
        Map<Makale, Integer> degrees = new HashMap<>();
        List<Makale> activeNodes = new ArrayList<>(graphNodes); // İşlenecek liste

        for (Makale m : activeNodes) {
            // Sadece bu listede (activeNodes) bulunan komşuları saymalıyız (Subgraph mantığı)
            int degree = 0;
            for (Makale neighbor : m.getUndirectedNeighbors()) {
                if (graphNodes.contains(neighbor)) {
                    degree++;
                }
            }
            degrees.put(m, degree);
        }

        // 2. Derecesi k'dan küçük olanları döngüsel olarak çıkar
        boolean changed = true;
        while (changed) {
            changed = false;
            // Silinecekleri belirle
            List<Makale> toRemove = new ArrayList<>();
            for (Makale m : activeNodes) {
                if (degrees.get(m) < k) {
                    toRemove.add(m);
                }
            }

            // Eğer silinecek varsa sil ve komşuların derecesini düşür
            if (!toRemove.isEmpty()) {
                changed = true;
                activeNodes.removeAll(toRemove);

                for (Makale removed : toRemove) {
                    // Silinenin komşularını bul ve derecelerini 1 azalt
                    for (Makale neighbor : removed.getUndirectedNeighbors()) {
                        if (activeNodes.contains(neighbor)) {
                            degrees.put(neighbor, degrees.get(neighbor) - 1);
                        }
                    }
                }
            }
        }

        // 3. Geriye kalanlar K-Core kümesidir, işaretle
        for (Makale m : activeNodes) {
            m.isKCore = true;
        }
    }

    // --- YENİ: BETWEENNESS CENTRALITY ---
    public static void calculateBetweenness(List<Makale> graphNodes) {
        // Hepsini sıfırla
        for(Makale m : graphNodes) m.betweenness = 0.0;

        // Her düğüm çifti arasındaki en kısa yolları bul (BFS ile)
        // Not: Brandes Algoritması daha hızlıdır ama burada anlaşılır olması için
        // her düğümden BFS başlatıp geçen yolları sayacağız.

        for (Makale s : graphNodes) {
            // BFS Değişkenleri
            Stack<Makale> stack = new Stack<>();
            Map<Makale, List<Makale>> predecessors = new HashMap<>();
            Map<Makale, Integer> sigma = new HashMap<>(); // Kısa yol sayısı
            Map<Makale, Integer> distance = new HashMap<>();

            for(Makale m : graphNodes) {
                predecessors.put(m, new ArrayList<>());
                sigma.put(m, 0);
                distance.put(m, -1);
            }

            sigma.put(s, 1);
            distance.put(s, 0);

            Queue<Makale> queue = new LinkedList<>();
            queue.add(s);

            while (!queue.isEmpty()) {
                Makale v = queue.poll();
                stack.push(v);

                // Yönsüz komşuları gez (Madde 2.3: Yönsüz kenarlara dönüştürülmeli)
                for (Makale w : v.getUndirectedNeighbors()) {
                    // Eğer komşu grafiğin (ekranın) bir parçası değilse atla
                    if (!graphNodes.contains(w)) continue;

                    // W ilk kez keşfedildiyse
                    if (distance.get(w) < 0) {
                        queue.add(w);
                        distance.put(w, distance.get(v) + 1);
                    }

                    // Kısa yol bulunduysa
                    if (distance.get(w) == distance.get(v) + 1) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        predecessors.get(w).add(v);
                    }
                }
            }

            // Dependency (Bağımlılık) Geri Yayılımı
            Map<Makale, Double> delta = new HashMap<>();
            for(Makale m : graphNodes) delta.put(m, 0.0);

            while (!stack.isEmpty()) {
                Makale w = stack.pop();
                for (Makale v : predecessors.get(w)) {
                    double c = ((double) sigma.get(v) / sigma.get(w)) * (1.0 + delta.get(w));
                    delta.put(v, delta.get(v) + c);
                }
                if (w != s) {
                    w.betweenness += delta.get(w);
                }
            }
        }

        // Yönsüz graf olduğu için skorları 2'ye bölüyoruz (A-B ve B-A aynı yol)
        for(Makale m : graphNodes) {
            m.betweenness /= 2.0;
        }
    }
}