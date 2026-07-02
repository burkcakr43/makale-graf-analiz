import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Swing arayüzünü güvenli modda başlat
        SwingUtilities.invokeLater(() -> {
            System.out.println("Veriler yükleniyor, lütfen bekleyin...");

            // 1. Verileri Oku (data.json dosyasından)
            List<Makale> makaleler = DosyaIslemleri.jsonOku("data.json");

            if (makaleler.isEmpty()) {
                System.err.println("HATA: Makale listesi boş! data.json dosyasını kontrol et.");
                return;
            }

            // 2. Graf Bağlantılarını Kur (Referansları eşleştir)
            GrafIslemleri.grafOlustur(makaleler);

            // 3. Toplam Referans Sayısını Hesapla (İstatistik için)
            int toplamReferans = 0;
            for(Makale m : makaleler) {
                toplamReferans += m.references.size(); // Siyah kenar sayısı
            }

            // 4. Arayüzü Başlat
            Arayuz ekran = new Arayuz(makaleler, toplamReferans);
            ekran.setVisible(true);
        });
    }
}