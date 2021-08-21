package com.example.chatpeki.Model;

public interface IKullanici {
     void arkadaslariGetir(IKisilerEklendiListener listener);
     void arkadaslaraBildir(Bildirimler bildirim);
}
