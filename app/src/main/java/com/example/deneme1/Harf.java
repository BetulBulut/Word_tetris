package com.example.deneme1;

import android.content.Context;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Random;

public class Harf extends AppCompatButton {


    int tıklanabilirlik=1;
    boolean buz=false;
    boolean secili=false;//geri bırakabilmek için
    int cumleindex;

    public Harf(Context context,char[] harf,int rastgeleSayi)
    {

        super(context);


        setText(harf,rastgeleSayi,1);



    }

}
