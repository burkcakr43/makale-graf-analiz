import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GrafPanel extends JPanel {
    // Ekranda o an çizili olan tüm makaleler
    private List<Makale> cizilecekMakaleler;

    // Tıklama olayını Arayüz'e bildirmek için arabirim
    public interface NodeClickListener {
        void onNodeClick(Makale makale);
    }

    private NodeClickListener listener;

    public GrafPanel() {
        this.cizilecekMakaleler = new ArrayList<>();
        this.setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Makale tiklanan = getMakaleAt(e.getX(), e.getY());
                if (tiklanan != null && listener != null) {
                    listener.onNodeClick(tiklanan);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Makale uzerindeki = getMakaleAt(e.getX(), e.getY());
                if (uzerindeki != null) {
                    // Tooltip Bilgisi
                    String authors = String.join(", ", uzerindeki.getAuthors());
                    // Yazarlar çok uzunsa kısalt
                    if (authors.length() > 50) authors = authors.substring(0, 50) + "...";

                    String tooltip = "<html>" +
                            "<b>ID:</b> " + uzerindeki.getId() + "<br>" +
                            "<b>Title:</b> " + uzerindeki.getTitle() + "<br>" +
                            "<b>Authors:</b> " + (authors.isEmpty() ? "Bilinmiyor" : authors) + "<br>" +
                            "<b>Year:</b> " + uzerindeki.getYear() + "<br>" +
                            "<b>Citation Count:</b> " + uzerindeki.getCitationCount() +
                            "</html>";
                    setToolTipText(tooltip);
                } else {
                    setToolTipText(null);
                }
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setNodeClickListener(NodeClickListener listener) {
        this.listener = listener;
    }

    // Ekranı temizleyip sıfırdan çizer
    public void grafiSifirlaVeCiz(Makale merkez, List<Makale> cevredekiler) {
        this.cizilecekMakaleler.clear();

        // Merkezi ekranın ortasına koy
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // İlk açılışta panel boyutu 0 olabilir, varsayılan değer verelim
        if (centerX == 0) { centerX = 600; centerY = 400; }

        merkez.x = centerX;
        merkez.y = centerY;
        merkez.isCenter = true;

        cizilecekMakaleler.add(merkez);

        // Çevredekileri ekle ve çakışmayı önle
        grafiGenislet(merkez, cevredekiler);
    }

    // Mevcut grafı bozmadan yeni düğümler ekler
    public void grafiGenislet(Makale merkez, List<Makale> yeniler) {
        // Yeni düğümleri merkezin etrafına rastgele saç
        int r = 100;

        for (Makale m : yeniler) {
            if (!cizilecekMakaleler.contains(m)) {
                // Rastgele bir açı ve mesafe belirle (İlk yerleşim)
                double angle = Math.random() * 2 * Math.PI;
                int mesafe = r + (int)(Math.random() * 100);

                m.x = merkez.x + (int)(Math.cos(angle) * mesafe);
                m.y = merkez.y + (int)(Math.sin(angle) * mesafe);

                m.isCenter = false;
                cizilecekMakaleler.add(m);
            }
        }

        // --- ÇAKIŞMA ÖNLEME ALGORİTMASI ---
        // Düğümler üst üste binmesin diye birbirlerini itmelerini sağlıyoruz
        cakismaOnle();

        repaint();
    }

    // Düğümlerin üst üste binmesini engelleyen basit fizik motoru
    private void cakismaOnle() {
        int iterasyonSayisi = 100; // Düzenleme kalitesi (Ne kadar yüksekse o kadar iyi ayrışır)
        int minMesafe = 60; // İki düğüm arasındaki minimum boşluk (Düğüm çapı 40 + 20 boşluk)

        int width = getWidth() > 0 ? getWidth() : 1200;
        int height = getHeight() > 0 ? getHeight() : 800;

        for (int k = 0; k < iterasyonSayisi; k++) {
            for (int i = 0; i < cizilecekMakaleler.size(); i++) {
                Makale m1 = cizilecekMakaleler.get(i);

                for (int j = i + 1; j < cizilecekMakaleler.size(); j++) {
                    Makale m2 = cizilecekMakaleler.get(j);

                    // İki düğüm arasındaki mesafeyi hesapla (Pisagor)
                    double dx = m1.x - m2.x;
                    double dy = m1.y - m2.y;
                    double mesafe = Math.sqrt(dx * dx + dy * dy);

                    // Eğer çok yakınlarsa (Çakışıyorlarsa)
                    if (mesafe < minMesafe) {
                        // Birbirlerini itmeleri gereken miktar
                        if (mesafe == 0) { dx = 1; dy = 1; mesafe = 1.41; } // Tam üst üsteyse hata vermesin

                        double itmeGucu = (minMesafe - mesafe) / 2.0; // Yarı yarıya itişelim
                        double offsetX = (dx / mesafe) * itmeGucu;
                        double offsetY = (dy / mesafe) * itmeGucu;

                        // m1'i sağa/yukarı it
                        m1.x += offsetX;
                        m1.y += offsetY;

                        // m2'yi sola/aşağı it
                        m2.x -= offsetX;
                        m2.y -= offsetY;
                    }
                }

                // Ekran sınırlarından taşmayı engelle
                m1.x = Math.max(30, Math.min(width - 50, m1.x));
                m1.y = Math.max(30, Math.min(height - 50, m1.y));
            }
        }
    }

    private Makale getMakaleAt(int x, int y) {
        for (Makale m : cizilecekMakaleler) {
            // Düğüm çapı 40 ise yarıçap 20
            if (Math.pow(x - (m.x + 20), 2) + Math.pow(y - (m.y + 20), 2) <= 20 * 20) {
                return m;
            }
        }
        return null;
    }

    // Arayüz sınıfı erişebilsin diye getter
    public List<Makale> getCizilecekMakaleler() {
        return cizilecekMakaleler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. KENARLARI ÇİZ

        // Yeşil Kenarlar (ID Sıralı)
        g2.setColor(new Color(0, 150, 0, 80));
        g2.setStroke(new BasicStroke(2.0f));
        for (Makale m : cizilecekMakaleler) {
            if (m.siradakiMakale != null && cizilecekMakaleler.contains(m.siradakiMakale)) {
                g2.drawLine(m.x + 20, m.y + 20, m.siradakiMakale.x + 20, m.siradakiMakale.y + 20);
            }
        }

        // Siyah ve K-Core Kenarlar
        g2.setStroke(new BasicStroke(1.0f));
        for (Makale kaynak : cizilecekMakaleler) {
            for (Makale hedef : kaynak.references) {
                if (cizilecekMakaleler.contains(hedef)) {
                    if (kaynak.isKCore && hedef.isKCore) {
                        g2.setColor(new Color(0, 0, 255)); // Mavi/Mor
                        g2.setStroke(new BasicStroke(2.0f));
                    } else {
                        g2.setColor(Color.BLACK);
                        g2.setStroke(new BasicStroke(1.0f));
                    }
                    g2.drawLine(kaynak.x + 20, kaynak.y + 20, hedef.x + 20, hedef.y + 20);
                }
            }
        }

        // 2. DÜĞÜMLERİ ÇİZ
        for (Makale m : cizilecekMakaleler) {
            if (m.isKCore) {
                g2.setColor(new Color(200, 180, 255)); // K-Core (Mor)
            } else if (m.isCenter) {
                g2.setColor(new Color(255, 100, 100)); // Merkez (Kırmızı)
            } else {
                g2.setColor(new Color(255, 255, 200)); // Normal (Sarı)
            }

            g2.fillOval(m.x, m.y, 40, 40);

            // Çerçeve
            if (m.isKCore) {
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2.0f));
            } else {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.0f));
            }
            g2.drawOval(m.x, m.y, 40, 40);
            g2.setStroke(new BasicStroke(1.0f));

            // Yazılar
            g2.setColor(Color.BLACK);
            String text = String.valueOf(m.getCitationCount());
            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(text);
            g2.drawString(text, m.x + (40 - w) / 2, m.y + 25);

            String idText = m.getId().length() > 5 ? m.getId().substring(m.getId().length() - 5) : m.getId();
            g2.drawString(idText, m.x, m.y + 55);
        }
    }
}