import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.text.DecimalFormat;

public class Arayuz extends JFrame {
    private List<Makale> makaleler;
    private GrafPanel grafPanel;
    private JTextField txtSearch;
    private JTextField txtKValue; // K değeri için kutu

    public Arayuz(List<Makale> makaleler, int toplamReferans) {
        this.makaleler = makaleler;

        setTitle("Prolab 3 - Makale Graf Analiz Uygulaması");
        setSize(1400, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. İSTATİSTİKLER ---
        Makale enCokAtifAlan = makaleler.get(0);
        Makale enCokRefVeren = makaleler.get(0);

        for (Makale m : makaleler) {
            if (m.getCitationCount() > enCokAtifAlan.getCitationCount()) enCokAtifAlan = m;
            if (m.references.size() > enCokRefVeren.references.size()) enCokRefVeren = m;
        }

        JPanel topContainer = new JPanel(new BorderLayout());

        // A. İstatistikler
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        statsPanel.setBackground(new Color(245, 245, 245));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Genel Graf İstatistikleri [Madde 2.1]"));
        statsPanel.setPreferredSize(new Dimension(1350, 100));

        statsPanel.add(new JLabel(" Toplam Makale: " + makaleler.size()));
        statsPanel.add(new JLabel(" Toplam Ref (Siyah): " + toplamReferans));
        statsPanel.add(new JLabel(" Toplam Verilen: " + toplamReferans));
        statsPanel.add(new JLabel(" Toplam Alınan: " + toplamReferans));
        statsPanel.add(new JLabel(" En Çok Atıf ALAN: " + enCokAtifAlan.getId() + " (" + enCokAtifAlan.getCitationCount() + ")"));
        statsPanel.add(new JLabel(" En Çok Ref VEREN: " + enCokRefVeren.getId() + " (" + enCokRefVeren.references.size() + ")"));

        topContainer.add(statsPanel, BorderLayout.NORTH);

        // B. Kontrol Paneli (Arama + K-Core + Betweenness)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBorder(BorderFactory.createEtchedBorder());

        // B1. Arama Kısmı
        controlPanel.add(new JLabel("Makale ID:"));
        txtSearch = new JTextField(25);
        if (!makaleler.isEmpty()) txtSearch.setText(enCokAtifAlan.getId());
        JButton btnCiz = new JButton("Analiz Et ve Çiz");
        btnCiz.addActionListener(e -> cizimYap());

        controlPanel.add(txtSearch);
        controlPanel.add(btnCiz);

        // B2. Ayırıcı
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));

        // B3. K-Core Kısmı (Madde 2.3)
        controlPanel.add(new JLabel("K Değeri:"));
        txtKValue = new JTextField("2", 3);
        JButton btnKCore = new JButton("K-Core Analizi Yap");
        btnKCore.addActionListener(e -> kCoreIslemiYap());

        controlPanel.add(txtKValue);
        controlPanel.add(btnKCore);

        // B4. Betweenness Kısmı (Madde 2.3)
        JButton btnBetweenness = new JButton("Betweenness Centrality Hesapla");
        btnBetweenness.addActionListener(e -> betweennessIslemiYap());
        controlPanel.add(btnBetweenness);

        topContainer.add(controlPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // --- ORTA PANEL ---
        grafPanel = new GrafPanel();
        grafPanel.setNodeClickListener(new GrafPanel.NodeClickListener() {
            @Override
            public void onNodeClick(Makale tiklanan) {
                // Tıklanan düğüm detayları
                Hesaplama.Sonuc sonuc = Hesaplama.hIndexHesapla(tiklanan);

                // Betweenness değeri varsa onu da göster
                String betStr = String.format("%.2f", tiklanan.betweenness);

                String mesaj = "Makale: " + tiklanan.getTitle() + "\n" +
                        "H-Index: " + sonuc.hIndex + "\n" +
                        "Betweenness Score: " + betStr;

                JOptionPane.showMessageDialog(Arayuz.this, mesaj);

                // Genişlet
                List<Makale> eklenecekler = sonuc.hCoreList.isEmpty() ? tiklanan.citedBy : sonuc.hCoreList;
                tiklanan.isCenter = true;
                grafPanel.grafiGenislet(tiklanan, eklenecekler);
            }
        });

        add(grafPanel, BorderLayout.CENTER);
    }

    private void cizimYap() {
        String arananId = txtSearch.getText().trim();
        Makale bulunan = null;
        for (Makale m : makaleler) {
            if (m.getId().equals(arananId)) { bulunan = m; break; }
        }

        if (bulunan == null) {
            JOptionPane.showMessageDialog(this, "Makale bulunamadı!");
            return;
        }

        Hesaplama.Sonuc sonuc = Hesaplama.hIndexHesapla(bulunan);
        List<Makale> cizilecek = sonuc.hCoreList.isEmpty() ? bulunan.citedBy : sonuc.hCoreList;
        grafPanel.grafiSifirlaVeCiz(bulunan, cizilecek);
    }

    // --- MADDE 2.3 İŞLEMLERİ ---

    private void kCoreIslemiYap() {
        try {
            int k = Integer.parseInt(txtKValue.getText().trim());

            // Şu an ekranda olan graf üzerinde işlem yapıyoruz (Proje "nihai graf yapısı" diyor)
            List<Makale> ekrandakiGraf = grafPanel.getCizilecekMakaleler();

            if (ekrandakiGraf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Önce bir graf çizdiriniz!");
                return;
            }

            // Hesaplama
            Hesaplama.kCoreAnalizi(ekrandakiGraf, k);

            // Ekranı güncelle (Boyamalar değişecek)
            grafPanel.repaint();

            JOptionPane.showMessageDialog(this, "K-Core (k=" + k + ") analizi tamamlandı.\nMor renkli düğümler K-Core kümesidir.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı giriniz.");
        }
    }

    private void betweennessIslemiYap() {
        List<Makale> ekrandakiGraf = grafPanel.getCizilecekMakaleler();

        if (ekrandakiGraf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Önce bir graf çizdiriniz!");
            return;
        }

        // Hesapla
        Hesaplama.calculateBetweenness(ekrandakiGraf);

        // Sonuçları Göster (En yüksek skoru alanları listeleyelim)
        StringBuilder sb = new StringBuilder("Betweenness Centrality Sonuçları (Top 10):\n\n");
        ekrandakiGraf.sort((m1, m2) -> Double.compare(m2.betweenness, m1.betweenness));

        int count = 0;
        DecimalFormat df = new DecimalFormat("#.####");
        for(Makale m : ekrandakiGraf) {
            if (count++ >= 10) break;
            sb.append(m.getId().substring(Math.max(0, m.getId().length()-10)))
                    .append("... -> Score: ").append(df.format(m.betweenness)).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString());

        // Ayrıca üzerine gelince (tooltip) de güncellenmiş olacak
    }
}