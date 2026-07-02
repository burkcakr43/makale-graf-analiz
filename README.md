# 📊 Makale Graf Analiz Uygulaması 

Akademik literatürdeki makaleler arasındaki atıf ilişkilerini modelleyen, analiz eden ve görselleştiren **Java Swing** tabanlı masaüstü uygulaması. JSON formatındaki ham atıf verilerini yönlü bir graf yapısına dönüştürür ve bu graf üzerinde graf teorisi metriklerini uygular.

> Kocaeli Üniversitesi – Bilgisayar Mühendisliği – Programlama Laboratuvarı 3

---

## 🚀 Özellikler

- **JSON Ayrıştırma:** Yapılandırılmamış/yarı yapılandırılmış `data.json` verilerinin işlenmesi
- **Graf Oluşturma:** Referans ilişkilerine göre yönlü graf kurulumu
  - ⚫ **Siyah kenarlar:** Referans (atıf) ilişkileri
  - 🟢 **Yeşil kenarlar:** ID sırasına göre bağlı liste (linked list) yapısı
- **H-Index / H-Core / H-Median** bibliyometrik hesaplamaları
- **K-Core Decomposition:** Ağdaki en yoğun bağlı çekirdek topluluğun tespiti ("soğan kabuğu" mantığı)
- **Betweenness Centrality:** BFS tabanlı, ağdaki "köprü" niteliğindeki kritik düğümlerin bulunması
- **Force-Directed Yerleşim:** Düğümlerin üst üste binmesini önleyen basit fizik motoru (çakışma önleme)
- **İnteraktif Arayüz:** Tıklanabilir düğümler, tooltip ile makale detayları, arama ve dinamik graf genişletme

---

## 🖼️ Ekran Görüntüleri

Uygulama ana ekranı üst panelde genel istatistikleri (toplam makale, toplam referans, en çok atıf alan/veren) gösterir. Seçilen makale kırmızı merkez olarak, H-Core kümesi sarı düğümlerle, K-Core kümesi ise mor düğümler ve kalın mavi çizgilerle vurgulanır.

> _İsterseniz `docs/` klasörüne ekran görüntüleri ekleyip buraya `![Ana Ekran](docs/ekran1.png)` şeklinde bağlayabilirsiniz._

---

## 🛠️ Kullanılan Teknolojiler

| Teknoloji | Amaç |
|-----------|------|
| **Java (JDK 8+)** | Ana programlama dili |
| **Java Swing** | Grafik kullanıcı arayüzü (GUI) |
| **Graphics2D** | Düğüm ve kenar çizimi |
| **HashMap** | ID → Makale eşleşmesi için O(1) erişim |
| **ArrayList** | Komşuluk listesi (Adjacency List) |
| **Regex** | JSON ayrıştırma |

---

## 📁 Proje Yapısı

```
Yazar/
├── src/
│   ├── Main.java              # Uygulama giriş noktası
│   ├── Makale.java           # Veri modeli (düğüm) sınıfı
│   ├── DosyaIslemleri.java   # JSON okuma ve ayrıştırma
│   ├── GrafIslemleri.java    # Graf bağlantılarının kurulması
│   ├── Hesaplama.java        # H-Index, K-Core, Betweenness algoritmaları
│   ├── GrafPanel.java        # Çizim ve force-directed yerleşim
│   └── Arayuz.java           # Ana pencere ve kullanıcı etkileşimi
├── data.json                 # Makale veri seti (~1000 makale)
└── README.md
```

---

## ⚙️ Kurulum ve Çalıştırma

### Gereksinimler
- Java JDK 8 veya üzeri

### IntelliJ IDEA ile
1. Projeyi klonlayın:
   ```bash
   git clone https://github.com/KULLANICI_ADIN/REPO_ADI.git
   ```
2. Projeyi IntelliJ IDEA'da açın.
3. `data.json` dosyasının proje kök dizininde (kaynak klasörünün yanında) olduğundan emin olun.
4. `Main.java` dosyasını çalıştırın.

### Komut satırı ile
```bash
# Derle
javac -d out src/*.java

# Çalıştır (data.json çalışma dizininde olmalı)
java -cp out Main
```

> ⚠️ **Not:** Uygulama `data.json` dosyasını çalışma dizininden okur. Dosya bulunamazsa program hata mesajı verir.

---

## 🧮 Algoritmalar

### H-Index & H-Core
Makaleye atıf yapan yayınlar atıf sayılarına göre büyükten küçüğe sıralanır. Atıf sayısının sıra numarasına eşit veya büyük olduğu son nokta H-Index'tir. Bu noktaya kadar olan makaleler H-Core kümesini oluşturur.

### K-Core Decomposition
Derecesi `k`'dan küçük olan düğümler iteratif olarak silinir. Silme işlemi komşuların derecesini düşürdüğü için işlem, silinecek düğüm kalmayana kadar tekrarlanır. Geriye kalanlar K-Core kümesidir.

### Betweenness Centrality
Yönsüz grafa dönüştürülen yapı üzerinde her düğümden BFS başlatılır. Bir düğümden geçen en kısa yolların oranı biriktirilerek merkezilik skoru hesaplanır (Brandes algoritması mantığı).

---

## 👤 Yazar

**Burak Çakır**
Bilgisayar Mühendisliği – Kocaeli Üniversitesi


---

## 📄 Lisans

Bu proje eğitim amaçlı geliştirilmiştir.
