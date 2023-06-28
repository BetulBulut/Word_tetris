package com.example.deneme1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;

import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatDrawableManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;
import org.w3c.dom.TypeInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class game_area extends AppCompatActivity {
    TextView cevapText;
    GridLayout gl;
    ArrayList<Point> positions = new ArrayList<>();

    List<Integer> maxes =new ArrayList<>();

    List<Point> Indexes= new ArrayList<>();

    List<Point> Silinecek=new ArrayList<>();

    int puan=0;
    int hata=0;
    int second=5;
    int skor2;
    boolean bulundu=false;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDocRef;
    private HashMap<String,Integer> skorlar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_area);

        Intent i = getIntent();

        //burada cancel yapıldıktan sonra butonları eski haline döndürmek için
        //default buton özelliklerini aldım
        Button button = new Button(game_area.this);
        Drawable d2 = button.getBackground();
        mFirestore= FirebaseFirestore.getInstance();
        for(int s=0;s<8;s++){
            maxes.add(9);
        }


        gl = (GridLayout) findViewById(R.id.gridLayout);
        ImageButton checkButton = (ImageButton) findViewById(R.id.check_button);
        ImageButton cancelButton = (ImageButton) findViewById(R.id.cancel_button);
        cevapText = (TextView) findViewById(R.id.cevap_text);
        TextView skor=(TextView)findViewById(R.id.skor);
        positions.clear();
        Indexes.clear();
        //sesli sessiz harf oranı dengeli olsun diye yaptım
        String Harfler = "A A A B C Ç D E E E F G Ğ H I I I İ İ J K L M N O O Ö Ö P R S Ş T U U Ü Ü V Y Z";
        String[] str = Harfler.split(" ");
        char[] harfler = new char[40];

        for (int j = 0; j < str.length; j++) {
            harfler[j] = str[j].charAt(0);
        }
        Random random = new Random();


        //burası başlangıçta 24 tane harfin gelmesini sağlayan kısım
        //?sesli sessiz harfler eşit mi olmalı(Belli bir oranda olmalı deniyor)
        int count = 0;
        for (int k = 0; k < 10; k++) {
            for (int l = 0; l < 8; l++) {
               if (count>= 24) {
                  break;
               }else {
                   GridLayout.LayoutParams lpGl = new GridLayout.LayoutParams();
                   lpGl.height = 140;
                   lpGl.width = 130;
                   lpGl.columnSpec = GridLayout.spec(7 - l);
                   lpGl.rowSpec = GridLayout.spec(9 - k);
                   int HarfRandom = random.nextInt(40);
                   Harf harf = HarfOlustur(harfler, HarfRandom,7-l,9-k);
                   if (maxes.get(7 - l) == null) {
                       maxes.set(7 - l, 9 - k);
                   } else if (maxes.get(7 - l) > 9 - k) {
                       maxes.set(7 - l, 9 - k);
                   }
                   harf.setLayoutParams(lpGl);
                   gl.addView(harf);
                   Point p= new Point();
                   p.x=7-l;
                   p.y=9-k;
                   p.index=gl.indexOfChild(harf);
                   Indexes.add(p);
               }

                count++;
            }

        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cevapText.setText("");
                for (int j = 0; j < positions.size(); j++) {

                    Harf Harf2 = (Harf) gl.getChildAt(positions.get(j).index);

                    Harf2.setBackground(d2);
                    Harf2.secili=false;
                    if(Harf2.buz){
                        Harf2.tıklanabilirlik++;
                    }
                }
                positions.clear();


            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String kelime = cevapText.getText().toString();
                cevapText.setText("");
                bulundu=false;
                String line="";

                InputStream in=getResources().openRawResource(R.raw.kelimeler);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                try {
                    while ((line = br.readLine()) != null) {

                        if (line.equals(kelime.toLowerCase()))
                        {
                            bulundu=true;
                            gridEdit(gl, positions);
                            break;
                        }
                    }

                    in.close();
                    if(bulundu) {
                        skor2 = puanHesapla(kelime);
                        if(skor2==100)
                        {
                            second=4;

                        }
                        else if(skor2==200)
                        {
                            second=3;
                        }
                        else if(skor2==300)
                        {
                            second=2;
                        }
                        else if(skor2==400)
                        {
                            second=1;
                        }

                    }

                    else {

                        hata++;


                        cevapText.setText("");
                        for (int j = 0; j < positions.size(); j++) {

                            Harf Harf2 = (Harf) gl.getChildAt(positions.get(j).index);

                            Harf2.setBackground(d2);
                            Harf2.secili=false;
                            if(Harf2.buz){
                                Harf2.tıklanabilirlik++;
                            }
                        }
                        positions.clear();
                    }



                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }




            }



        });




        new CountDownTimer(280000,second*1000){

            @Override
            public void onTick(long l){
                for(int x=0;x<8;x++){
                    if(maxes.get(x)==0) {
                        //oyun bitecek yeni intente geçecek
                        //Buraya game over gelecek---Beyza
                        cancel();
                        System.out.println(skor2);
                        skorlar=new HashMap<>();
                        skorlar.put("Puan",skor2);

                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());

                        mFirestore.collection("Puanlar").document(formatter.format(date).toString()).set(skorlar);





                        Intent j=new Intent(game_area.this,game_end.class);
                        j.putExtra("Skor",skor2);
                        startActivity(j);


                    }
                }
                if (hata == 3) {
                    hata=0;
                    for(int s=0;s<8;s++){
                        GridLayout.LayoutParams lpGl = new GridLayout.LayoutParams();
                        lpGl.height = 140;
                        lpGl.width = 130;
                        int colonrandom= random.nextInt(7);
                        lpGl.columnSpec = GridLayout.spec(7 -s);
                        lpGl.rowSpec = GridLayout.spec(0);
                        int HarfRandom = random.nextInt(40);
                        Harf Harf = HarfOlustur(harfler, HarfRandom,7-s,maxes.get(7-s)-1);
                        Harf.setLayoutParams(lpGl);
                        gl.addView(Harf);



                        int i;


                        for(i=1;i<maxes.get(7-s)-1;i++){

                            gl.removeView(gl.getChildAt(gl.indexOfChild(Harf)));
                            gl.removeView(Harf);
                            char[] chars=new char[1];
                            chars[0]=Harf.getText().charAt(0);
                            GridLayout.LayoutParams lpGl2 = new GridLayout.LayoutParams();
                            lpGl2.height = 140;
                            lpGl2.width = 130;
                            lpGl2.columnSpec = GridLayout.spec(7-s);
                            lpGl2.rowSpec = GridLayout.spec(i);
                            Harf.setLayoutParams(lpGl2);

                            gl.addView(Harf);

                        }
                        if(maxes.get(7-s)==1){
                            i=0;
                        }

                        gl.removeView(gl.getChildAt(gl.indexOfChild(Harf)));
                        gl.removeView(Harf);
                        char[] chars=new char[1];
                        chars[0]=Harf.getText().charAt(0);
                        GridLayout.LayoutParams lpGl2 = new GridLayout.LayoutParams();
                        lpGl2.height = 140;
                        lpGl2.width = 130;
                        lpGl2.columnSpec = GridLayout.spec(7-s);
                        lpGl2.rowSpec = GridLayout.spec(i);
                        Harf.setLayoutParams(lpGl2);
                        int sayac=0;
                        for (int k = 0; k < Silinecek.size(); k++) {
                            if (Silinecek.get(k).x == 7-s && Silinecek.get(k).y == i) {
                                gl.removeView(gl.getChildAt(Silinecek.get(k).index));
                                Point p= new Point();
                                p.x=7-s;
                                p.y=i;
                                gl.addView(Harf, Silinecek.get(k).index);
                                p.index=Silinecek.get(k).index;
                                Indexes.add(p);
                                maxes.set(7-s,maxes.get(7-s)-1);
                                Silinecek.remove(Silinecek.get(k));
                                sayac=1;
                            }
                        }
                        if(sayac==0){
                            Point p= new Point();
                            p.x=7-s;
                            p.y=i;
                            gl.addView(Harf);
                            p.index=gl.indexOfChild(Harf);
                            Indexes.add(p);
                            maxes.set(7-s,maxes.get(7-s)-1);


                        }





                    }





                }
                else{
                    GridLayout.LayoutParams lpGl = new GridLayout.LayoutParams();
                    lpGl.height = 140;
                    lpGl.width = 130;
                    int colonrandom= random.nextInt(7);
                    lpGl.columnSpec = GridLayout.spec(7 -colonrandom);
                    lpGl.rowSpec = GridLayout.spec(0);
                    int HarfRandom = random.nextInt(40);
                    Harf Harf = HarfOlustur(harfler, HarfRandom,7-colonrandom,maxes.get(7-colonrandom)-1);
                    Harf.setLayoutParams(lpGl);
                    gl.addView(Harf);




                    int i;
                    for(i=1;i<maxes.get(7-colonrandom)-1;i++){



                        gl.removeView(gl.getChildAt(gl.indexOfChild(Harf)));
                        gl.removeView(Harf);
                        char[] chars=new char[1];
                        chars[0]=Harf.getText().charAt(0);
                        GridLayout.LayoutParams lpGl2 = new GridLayout.LayoutParams();
                        lpGl2.height = 140;
                        lpGl2.width = 130;
                        lpGl2.columnSpec = GridLayout.spec(7-colonrandom);
                        lpGl2.rowSpec = GridLayout.spec(i);
                        Harf.setLayoutParams(lpGl2);

                        gl.addView(Harf);



                    }
                    if(maxes.get(7-colonrandom)==1){
                        i=0;
                    }

                    gl.removeView(gl.getChildAt(gl.indexOfChild(Harf)));
                    gl.removeView(Harf);
                    char[] chars=new char[1];
                    chars[0]=Harf.getText().charAt(0);
                    GridLayout.LayoutParams lpGl2 = new GridLayout.LayoutParams();
                    lpGl2.height = 140;
                    lpGl2.width = 130;
                    lpGl2.columnSpec = GridLayout.spec(7-colonrandom);
                    lpGl2.rowSpec = GridLayout.spec(i);
                    Harf.setLayoutParams(lpGl2);

                    int sayac=0;
                    for (int s = 0; s < Silinecek.size(); s++) {
                        if (Silinecek.get(s).x == 7-colonrandom && Silinecek.get(s).y == i) {
                            gl.removeView(gl.getChildAt(Silinecek.get(s).index));

                            Point p= new Point();
                            p.x=7-colonrandom;
                            p.y=i;
                            gl.addView(Harf, Silinecek.get(s).index);
                            p.index=Silinecek.get(s).index;
                            Indexes.add(p);
                            maxes.set(7-colonrandom,maxes.get(7-colonrandom)-1);
                            Silinecek.remove(Silinecek.get(s));
                            sayac=1;
                        }
                    }
                    if(sayac==0){


                        Point p= new Point();
                        p.x=7-colonrandom;
                        p.y=i;
                        gl.addView(Harf);
                        p.index=gl.indexOfChild(Harf);
                        Indexes.add(p);
                        maxes.set(7-colonrandom,maxes.get(7-colonrandom)-1);
                    }





                }





            }

            @Override
            public void onFinish(){

            }
        }.start();






    }

    //Bu kısımda çalışıyorum
    public void gridEdit(GridLayout gl, List<Point> positions) {



        int n = positions.size();
        for (int i = 0; i < n - 1; i++){
            for (int k = 0; k < n - i - 1; k++){
                if (positions.get(k).y > positions.get(k+1).y){
                    int temp = positions.get(k).y;
                    Point p=positions.get(k);
                    p.y=positions.get(k+1).y;
                    positions.set(k,p);
                    Point m=positions.get(k+1);
                    m.y=temp;
                    positions.set(k+1,m);
                }
            }
        }





        for(int j=0;j<n;j++){
            System.out.println(j+". dönüş"+positions.get(j).x +" "+positions.get(j).y);


                int x= positions.get(j).x;
                int y = positions.get(j).y;


                for(int i=0;i<Indexes.size();i++) {
                    if(Indexes.get(i).x==x && Indexes.get(i).y==y) {

                        System.out.println("sildim"+positions.get(j).y);
                        Harf harf = (Harf) gl.getChildAt(positions.get(j).index);
                        gl.removeView(harf);


                        TextView textView = new TextView(game_area.this);
                        GridLayout.LayoutParams lpGl = new GridLayout.LayoutParams();
                        lpGl.height = 100;
                        lpGl.width = 130;
                        lpGl.columnSpec = GridLayout.spec(positions.get(j).x);
                        lpGl.rowSpec = GridLayout.spec(positions.get(j).y);
                        textView.setLayoutParams(lpGl);
                        textView.setText(" ");
                        gl.addView(textView,positions.get(j).index);
                        Point p= new Point();
                        p.x=x;
                        p.y=y;
                        p.index=positions.get(j).index;
                        Silinecek.add(p);

                        Indexes.remove(Indexes.get(i));
                        maxes.set(x,maxes.get(x)+1);




                    }
                }

                int control=0;
                for (int s = 0; s < Silinecek.size(); s++) {
                    if (Silinecek.get(s).x == x && Silinecek.get(s).y == y-1) {
                        control=1;
                    }
                }



                 if(control==1){


                 }else{
                     for (int s = 0; s < Indexes.size(); s++) {
                         if (Indexes.get(s).x == x && Indexes.get(s).y == y - 1) {

                             char[] c=new char[1];
                             c[0]=' ';
                             Harf harf=HarfOlustur(c,0,x,y);
                             try{
                                 harf = (Harf) gl.getChildAt(Indexes.get(s).index);
                             }catch(ClassCastException e){
                                 System.out.println("exception");


                             }


                             char[] chars=new char[1];
                             chars[0]=harf.getText().charAt(0);
                             Harf newHarf= HarfOlustur(chars,0,x,y);
                             newHarf.setText(harf.getText());

                             GridLayout.LayoutParams lpGl = new GridLayout.LayoutParams();
                             lpGl.height = 140;
                             lpGl.width = 130;
                             lpGl.columnSpec = GridLayout.spec(x);
                             lpGl.rowSpec = GridLayout.spec(y);
                             newHarf.setLayoutParams(lpGl);

                             TextView textView2 = new TextView(game_area.this);
                             GridLayout.LayoutParams lpGl2 = new GridLayout.LayoutParams();
                             lpGl2.height = 100;
                             lpGl2.width = 130;
                             lpGl2.columnSpec = GridLayout.spec(x);
                             lpGl2.rowSpec = GridLayout.spec(y - 1);
                             textView2.setLayoutParams(lpGl2);
                             textView2.setText(" ");



                             gl.removeView(gl.getChildAt(Indexes.get(s).index));
                             gl.removeView(harf);
                             gl.addView(textView2, Indexes.get(s).index);
                             gl.removeView(gl.getChildAt(positions.get(j).index));

                             for (int d = 0; d < Silinecek.size(); d++) {
                                 if (Silinecek.get(d).x == x && Silinecek.get(d).y == y) {
                                     Silinecek.remove(Silinecek.get(d));
                                     break;
                                 }
                             }




                             Point m= new Point();
                             m.x=x;
                             m.y=y-1;
                             m.index=Indexes.get(s).index;
                             Silinecek.add(m);
                             gl.addView(newHarf, positions.get(j).index);




                             positions.add(j+1,Indexes.get(s));
                             n++;





                             Point p = new Point();
                             p.x = x;
                             p.y = y;
                             p.index = positions.get(j).index;
                             Indexes.remove(Indexes.get(s));
                             Indexes.add(p);
                             break;




                         }
                     }
                 }




        }



        positions.clear();


    }



    public Harf HarfOlustur(char[] harfler, int HarfRandom,int col, int row) {
        Harf harf = new Harf(game_area.this, harfler, HarfRandom);

        Random random = new Random();
        int rand=random.nextInt(40);
        if(rand%39==0){
            harf.tıklanabilirlik=2;
            harf.buz=true;
            @SuppressLint("RestrictedApi") Drawable d = AppCompatDrawableManager.get().getDrawable(game_area.this, R.drawable.blue);
            harf.setBackground(d);
        }


        harf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Harf Harf1 = (Harf) view;


                int pos = gl.indexOfChild(Harf1);
                Point p= new Point();
                if(harf.secili!=true){

                    //ilk tıklama

                    p.x=col;
                    p.y=row;
                    p.index=pos;



                    cevapText.append(Harf1.getText());
                    if(harf.tıklanabilirlik!=2){
                        harf.secili=true;
                        positions.add(p);


                        @SuppressLint("RestrictedApi") Drawable d = AppCompatDrawableManager.get().getDrawable(game_area.this, R.drawable.gray);
                        Harf1.setBackground(d);
                    }else{
                        harf.tıklanabilirlik=1;
                    }

                    harf.cumleindex=cevapText.length()-1;//cümledeki yeri
                }else{
                    //ikinci tıklama
                    harf.secili=false;
                    StringBuffer sBuffer = new StringBuffer();
                    String cevap;
                    sBuffer.append(cevapText.getText().toString());
                    sBuffer.deleteCharAt(harf.cumleindex);
                    cevap = sBuffer.toString();
                    cevapText.setText(cevap);
                    positions.remove(p);

                    Button button = new Button(game_area.this);
                    Drawable d2 = button.getBackground();

                    Harf1.setBackground(d2);
                }

            }
        });
        return  harf;
    }

    public int puanHesapla(String kelime)
    {
        char[] harf=kelime.toLowerCase().toCharArray();
        for(int i=0;i<harf.length;i++)
        {
            if(harf[i]=='a' || harf[i]=='e' || harf[i]=='i' || harf[i]=='k' || harf[i]=='l' ||
                    harf[i]=='n' || harf[i]=='r' ||harf[i]=='t')
            {
                puan+=1;
            }
            else if (harf[i]=='ı' || harf[i]=='m' || harf[i]=='o' || harf[i]=='s' || harf[i]=='u')
            {
                puan+=2;
            }
            else if(harf[i]=='b' || harf[i]=='d' || harf[i]=='ü' || harf[i]=='y')
            {
                puan+=3;

            }
            else if(harf[i]=='c' || harf[i]=='ç' || harf[i]=='ş' || harf[i]=='z')
            {
                puan+=4;
            }
            else if(harf[i]=='g' || harf[i]=='h' || harf[i]=='p')
            {
                puan+=5;
            }
            else if(harf[i]=='f' || harf[i]=='ö' || harf[i]=='v')
            {
                puan+=7;
            }
            else if(harf[i]=='ğ')
            {
                puan+=8;
            }
            else if(harf[i]=='j')
            {
                puan+=10;
            }
        }
        //System.out.println(puan);
        return puan;

    }
}