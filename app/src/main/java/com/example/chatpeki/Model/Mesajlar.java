package com.example.chatpeki.Model;

public class Mesajlar {
    private String Kimden,Kime,MesajID,Mesaj,Tipi,Zaman,Isim;

    public Mesajlar() {
    }

    public Mesajlar(String kimden, String kime, String mesajID, String mesaj, String tipi, String zaman, String isim) {
        Kimden = kimden;
        Kime = kime;
        MesajID = mesajID;
        Mesaj = mesaj;
        Tipi = tipi;
        Zaman = zaman;
        Isim = isim;
    }

    public String getKimden() {
        return Kimden;
    }

    public void setKimden(String kimden) {
        Kimden = kimden;
    }

    public String getKime() {
        return Kime;
    }

    public void setKime(String kime) {
        Kime = kime;
    }

    public String getMesajID() {
        return MesajID;
    }

    public void setMesajID(String mesajID) {
        MesajID = mesajID;
    }

    public String getMesaj() {
        return Mesaj;
    }

    public void setMesaj(String mesaj) {
        Mesaj = mesaj;
    }

    public String getTipi() {
        return Tipi;
    }

    public void setTipi(String tipi) {
        Tipi = tipi;
    }

    public String getZaman() {
        return Zaman;
    }

    public void setZaman(String zaman) {
        Zaman = zaman;
    }

    public String getIsim() {
        return Isim;
    }

    public void setIsim(String isim) {
        Isim = isim;
    }
}
