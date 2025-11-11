/**
*
* @author Hasan KOÇ  hasan.koc7@ogr.sakarya.edu.tr
* @since 26.04.2025
* <p>
* Çıkış-varış gezegenleri arasında seyahat eden, yolcu listesini ve uçuş durum geçişlerini yöneten araçtır.
* </p>
*/


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Bir uzay aracını temsil eder.
// • Çıkış gezegeninden varış gezegenine belirli saatlik mesafe kat eder.
// • Yolcular (Kisi) içerir; tüm yolcular ölürse araç İMHA olur.
// • Hedefe varacağı tarih program başında doğru hesaplanır ve sabit kalır.
public class UzayAraci {

    // Araç durumları
    public enum Durum {
        BEKLIYOR,   // Çıkış gezegeninde, çıkış tarihini bekliyor
        YOLDA,      // Uzayda hareket halinde
        VARDI,      // Hedef gezegene ulaştı
        IMHA        // Yolcuların hepsi öldü (yoldayken veya beklerken)
    }

    private final String ad;                 // Araç adı
    private final Gezegen cikisGezegeni;     // Çıkış gezegeni
    private final Gezegen varisGezegeni;     // Varış gezegeni
    private final Zaman   cikisTarihi;       // Çıkış tarihi (gezegen takvimine göre, saat 00:00 varsayılır)
    private final int     toplamMesafeSaat;  // Uçuş süresi (saat)

    private final Zaman   hedefVarisTarihi;  // Program başında hesaplanan varış tarihi (varış gezegeni takvimi)

    private int   kalanMesafeSaat;           // Kalan uçuş süresi (saat)
    private Durum durum;                     // Güncel durum
    private final List<Kisi> yolcular;       // Canlı veya ölmüş tüm yolcular

    // Kurucu
    public UzayAraci(String ad,
                     Gezegen cikisGezegeni,
                     Gezegen varisGezegeni,
                     Zaman   cikisTarihi,
                     int     toplamMesafeSaat)
    {
        this.ad   = Objects.requireNonNull(ad, "ad null olamaz");
        this.cikisGezegeni = Objects.requireNonNull(cikisGezegeni, "cikisGezegeni null olamaz");
        this.varisGezegeni = Objects.requireNonNull(varisGezegeni, "varisGezegeni null olamaz");
        this.cikisTarihi   = Objects.requireNonNull(cikisTarihi, "cikisTarihi null olamaz");
        if (toplamMesafeSaat < 0) throw new IllegalArgumentException("toplamMesafeSaat negatif olamaz");
        this.toplamMesafeSaat = toplamMesafeSaat;
        this.kalanMesafeSaat  = toplamMesafeSaat;
        this.durum            = Durum.BEKLIYOR;
        this.yolcular         = new ArrayList<>();

        // ► Hedef varış tarihini program başında hesaplıyoruz ◄
        // 1) Program başlangıcındaki mutlak saat sayısı = 0 kabul edilir.
        // 2) Çıkış gezegeninin başlangıç zamanından çıkış tarihine kadar geçen saat → "cikisBeklemeSaat".
        long cikisBeklemeSaat = cikisGezegeni.getZaman()
                .kacSaatSonra(cikisTarihi, cikisGezegeni.getGundekiSaatSayisi());

        // 3) Toplam ilerleyecek saat = bekleme + uçuş mesafesi
        long toplamSaatProgramBasindan = cikisBeklemeSaat + toplamMesafeSaat;

        // 4) Varış gezegeninin başlangıç zamanına yukarıdaki toplam saati ekle → hedef tarih (sabit)
        this.hedefVarisTarihi = varisGezegeni.getZaman()
                .saatEkle(toplamSaatProgramBasindan, varisGezegeni.getGundekiSaatSayisi());
    }

    // BEKLIYOR → YOLDA geçişini kontrol eder
    public boolean cikisAninaGeldiMi() {
        return durum == Durum.BEKLIYOR && cikisGezegeni.getZaman().compareTo(cikisTarihi) >= 0;
    }

    // Çıkış yapar
    public void cikisYap() {
        if (durum == Durum.BEKLIYOR) {
            durum = Durum.YOLDA;
        }
    }

    // Bir saatlik simülasyon adımı
    public void birSaatIlerle() {
        if (durum == Durum.YOLDA) {
            if (kalanMesafeSaat > 0) {
                kalanMesafeSaat--; // uçuş süresi azalır
            }
            if (kalanMesafeSaat == 0) {
                durum = Durum.VARDI;
            }
        }
    }

    // Yolcuların ömürlerini kontrol eder, ölenleri çıkarır
    public void olumKontroluYap() {
        yolcular.removeIf(Kisi::olduMu);
        if (yolcular.isEmpty() && durum != Durum.VARDI) {
            durum = Durum.IMHA;
        }
    }

    // Yolcu ekler
    public void yolcuEkle(Kisi k) {
        yolcular.add(Objects.requireNonNull(k));
    }

    /* ---------------- GETTER’LAR ---------------- */

    public String getAd()                     { return ad; }
    public Gezegen getCikisGezegeni()         { return cikisGezegeni; }
    public Gezegen getVarisGezegeni()         { return varisGezegeni; }
    public Zaman   getCikisTarihi()           { return cikisTarihi; }
    public int     getToplamMesafeSaat()      { return toplamMesafeSaat; }
    public int     getKalanMesafeSaat()       { return kalanMesafeSaat; }
    public Durum   getDurum()                 { return durum; }
    public List<Kisi> getYolcular()           { return yolcular; }
    public Zaman   getHedefVarisTarihi()      { return hedefVarisTarihi; }

    @Override
    public String toString() {
        return String.format("%s (%s → %s)", ad, cikisGezegeni.getAd(), varisGezegeni.getAd());
    }
}
