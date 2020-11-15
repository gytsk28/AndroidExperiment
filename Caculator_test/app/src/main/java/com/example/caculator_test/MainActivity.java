package com.example.caculator_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.lang.Integer;

public class MainActivity extends AppCompatActivity {

    private TextView show = null;

    private TextView lenshow=null;
    private TextView outputlen=null;

    private TextView volshow=null;
    private TextView outputvol=null;

    private TextView sysshow=null;
    private TextView outputsys=null;

    private Button point=null;
    private Button land_point=null;
    private float num1=0;
    private float num2=0;
    private float land_num1=0;
    private float land_num2=0;
    private int land_num3=0;
    private String str="0";
    //进制标记
    private int flag_sys=0;
    //长度标记
    private int flag_len=0;
    //长度标记
    private int flag_vol=0;
    //小数点个数
    private int pointnum=0;
    //判断是否为第二个数字
    private int flag=0;
    //判定符号
    private int symbol=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        show = findViewById(R.id.show);
        lenshow=findViewById(R.id.lenth_show);
        outputlen=findViewById(R.id.output_len);
        volshow=findViewById(R.id.volume_show);
        outputvol=findViewById(R.id.output_volume);
        sysshow=findViewById(R.id.system_show);
        outputsys=findViewById(R.id.output_system);

        point=(Button)findViewById(R.id.point);
        land_point=(Button)findViewById(R.id.land_point);


    }

    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.CE:
                pointnum=0;
                point.setClickable(true);
                show.setText("");
                break;
            case R.id.zero:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"0");
                break;
            case R.id.one:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"1");
                break;
            case R.id.two:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"2");
                break;
            case R.id.three:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"3");
                break;
            case R.id.four:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"4");
                break;
            case R.id.five:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"5");
                break;
            case R.id.six:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"6");
                break;
            case R.id.seven:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"7");
                break;
            case R.id.eight:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"8");
                break;
            case R.id.nine:
                if(flag==1) {
                    show.setText("");
                    flag=0;
                }
                show.setText(show.getText().toString()+"9");
                break;
            case R.id.point:
                if(pointnum==0){
                    pointnum=1;
                    point.setClickable(false);
                }
                show.setText(show.getText().toString()+".");
                break;
            case R.id.add:
                num1=Float.parseFloat(show.getText().toString());
                point.setClickable(true);
                pointnum=0;
                symbol=1;
                flag=1;
                break;
            case R.id.subtraction:
                num1=Float.parseFloat(show.getText().toString());
                point.setClickable(true);
                pointnum=0;
                symbol=2;
                flag=1;
                break;
            case R.id.multiply:
                num1=Float.parseFloat(show.getText().toString());
                point.setClickable(true);
                pointnum=0;
                symbol=3;
                flag=1;
                break;
            case R.id.divide:
                num1=Float.parseFloat(show.getText().toString());
                point.setClickable(true);
                pointnum=0;
                symbol=4;
                flag=1;
                break;
            case R.id.mod:
                num1=Float.parseFloat(show.getText().toString());
                point.setClickable(true);
                pointnum=0;
                symbol=5;
                flag=1;
                break;
            case R.id.power:
                num1=Float.parseFloat(show.getText().toString());
                point.setClickable(true);
                pointnum=0;
                symbol=6;
                flag=1;
                break;
            case R.id.sin:
                point.setClickable(true);
                pointnum=0;
                symbol=7;
                flag=1;
                break;
            case R.id.cos:
                point.setClickable(true);
                pointnum=0;
                symbol=8;
                flag=1;
                break;
            case R.id.tan:
                point.setClickable(true);
                pointnum=0;
                symbol=9;
                flag=1;
                break;
            case R.id.countBack:
                num1=Float.parseFloat(show.getText().toString());
                show.setText(1/num1+"");
                break;
            case R.id.root:
                num1=Float.parseFloat(show.getText().toString());
                show.setText(Math.sqrt(num1)+"");
                break;
            case R.id.percent:
                num1=Float.parseFloat(show.getText().toString());
                show.setText(num1/100+"");
                break;
            case R.id.equal:
                num2=Float.parseFloat(show.getText().toString());
                switch (symbol){
                    case 1:
                        num1=num1+num2;
                        show.setText(num1+"");
                        break;
                    case 2:
                        num1=num1-num2;
                        show.setText(num1+"");
                        break;
                    case 3:
                        num1=num1*num2;
                        show.setText(num1+"");
                        break;
                    case 4:
                        num1=num1/num2;
                        show.setText(num1+"");
                        break;
                    case 5:
                        num1=num1%num2;
                        show.setText(num1+"");
                        break;
                    case 6:
                        int temp=1;
                        for(int i=0;i<num2;i++)
                            temp*=num1;
                        num1=temp;
                        show.setText(num1+"");
                        break;
                    case 7:
                        show.setText(Math.sin(Double.parseDouble(show.getText().toString()))+"");
                        break;
                    case 8:
                        show.setText(Math.cos(Double.parseDouble(show.getText().toString()))+"");
                        break;
                    case 9:
                        show.setText(Math.tan(Double.parseDouble(show.getText().toString()))+"");
                        break;
                }
                flag=1;
                point.setClickable(true);
                pointnum=0;
                break;



                //右侧按键land_*
            case R.id.land_CE:
                pointnum=0;
                land_point.setClickable(true);
                lenshow.setText("");
                volshow.setText("");
                sysshow.setText("");
                break;
            case R.id.land_zero:
                lenshow.setText(lenshow.getText().toString()+"0");
                volshow.setText(volshow.getText().toString()+"0");
                sysshow.setText(sysshow.getText().toString()+"0");
                break;
            case R.id.land_one:
                lenshow.setText(lenshow.getText().toString()+"1");
                volshow.setText(volshow.getText().toString()+"1");
                sysshow.setText(sysshow.getText().toString()+"1");
                break;
            case R.id.land_two:
                lenshow.setText(lenshow.getText().toString()+"2");
                volshow.setText(volshow.getText().toString()+"2");
                sysshow.setText(sysshow.getText().toString()+"2");
                break;
            case R.id.land_three:
                lenshow.setText(lenshow.getText().toString()+"3");
                volshow.setText(volshow.getText().toString()+"3");
                sysshow.setText(sysshow.getText().toString()+"3");
                break;
            case R.id.land_four:
                lenshow.setText(lenshow.getText().toString()+"4");
                volshow.setText(volshow.getText().toString()+"4");
                sysshow.setText(sysshow.getText().toString()+"4");
                break;
            case R.id.land_five:
                lenshow.setText(lenshow.getText().toString()+"5");
                volshow.setText(volshow.getText().toString()+"5");
                sysshow.setText(sysshow.getText().toString()+"5");
                break;
            case R.id.land_six:
                lenshow.setText(lenshow.getText().toString()+"6");
                volshow.setText(volshow.getText().toString()+"6");
                sysshow.setText(sysshow.getText().toString()+"6");
                break;
            case R.id.land_seven:
                lenshow.setText(lenshow.getText().toString()+"7");
                volshow.setText(volshow.getText().toString()+"7");
                sysshow.setText(sysshow.getText().toString()+"7");
                break;
            case R.id.land_eight:
                lenshow.setText(lenshow.getText().toString()+"8");
                volshow.setText(volshow.getText().toString()+"8");
                sysshow.setText(sysshow.getText().toString()+"8");
                break;
            case R.id.land_nine:
                lenshow.setText(lenshow.getText().toString()+"9");
                volshow.setText(volshow.getText().toString()+"9");
                sysshow.setText(sysshow.getText().toString()+"9");
                break;
            case R.id.land_point:
                if(pointnum==0){
                    pointnum=1;
                    land_point.setClickable(false);
                }
                lenshow.setText(lenshow.getText().toString()+".");
                volshow.setText(volshow.getText().toString()+".");
                sysshow.setText(sysshow.getText().toString()+".");
                break;

            case R.id.fir_mm:
                land_num1=Float.parseFloat(lenshow.getText().toString());
                lenshow.setText(lenshow.getText().toString()+"mm");
                flag_len=1;
                break;
            case R.id.fir_cm:
                land_num1=Float.parseFloat(lenshow.getText().toString());
                lenshow.setText(lenshow.getText().toString()+"cm");
                flag_len=2;
                break;
            case R.id.fir_m:
                land_num1=Float.parseFloat(lenshow.getText().toString());
                lenshow.setText(lenshow.getText().toString()+"m");
                flag_len=3;
                break;
            case R.id.sec_mm:
                switch (flag_len){
                    case 1:
                        outputlen.setText(land_num1+"mm");
                        break;
                    case 2:
                        outputlen.setText(land_num1*10+"mm");
                        break;
                    case 3:
                        outputlen.setText(land_num1*1000+"mm");
                        break;
                }
                break;
            case R.id.sec_cm:
                switch (flag_len){
                    case 1:
                        outputlen.setText(land_num1/10+"cm");
                        break;
                    case 2:
                        outputlen.setText(land_num1+"cm");
                        break;
                    case 3:
                        outputlen.setText(land_num1*100+"cm");
                        break;
                }
                break;
            case R.id.sec_m:
                switch (flag_len){
                    case 1:
                        outputlen.setText(land_num1/1000+"m");
                        break;
                    case 2:
                        outputlen.setText(land_num1/100+"m");
                        break;
                    case 3:
                        outputlen.setText(land_num1+"m");
                        break;
                }
                break;

            case R.id.fir_ccm:
                land_num2=Float.parseFloat(volshow.getText().toString());
                volshow.setText(volshow.getText().toString()+"cm^2");
                flag_vol=1;
                break;
            case R.id.fir_stere:
                land_num2=Float.parseFloat(volshow.getText().toString());
                volshow.setText(volshow.getText().toString()+"m^2");
                flag_vol=2;
                break;
            case R.id.fir_litre:
                land_num2=Float.parseFloat(volshow.getText().toString());
                volshow.setText(volshow.getText().toString()+"L");
                flag_vol=3;
                break;
            case R.id.sec_ccm:
                switch (flag_vol){
                    case 1:
                        outputvol.setText(land_num2+"cm^2");
                        break;
                    case 2:
                        outputvol.setText(land_num2*1000000+"cm^2");
                        break;
                    case 3:
                        outputvol.setText(land_num2*1000+"cm^2");
                        break;
                }
                break;
            case R.id.sec_stere:
                switch (flag_vol){
                    case 1:
                        outputvol.setText(land_num2/1000000+"m^2");
                        break;
                    case 2:
                        outputvol.setText(land_num2+"m^2");
                        break;
                    case 3:
                        outputvol.setText(land_num2/1000+"m^2");
                        break;
                }
                break;
            case R.id.sec_litre:
                switch (flag_vol){
                    case 1:
                        outputvol.setText(land_num2/1000+"L");
                        break;
                    case 2:
                        outputvol.setText(land_num2*1000+"L");
                        break;
                    case 3:
                        outputvol.setText(land_num2+"L");
                        break;
                }
                break;
            case R.id.fir_sys2:
                str=sysshow.getText().toString();
                land_num3=Integer.parseInt(sysshow.getText().toString());
                flag_sys=1;
                break;
            case R.id.fir_sys8:
                str=sysshow.getText().toString();
                land_num3=Integer.parseInt(sysshow.getText().toString());
                flag_sys=2;
                break;
            case R.id.fir_sys10:
                str=sysshow.getText().toString();
                land_num3=Integer.parseInt(sysshow.getText().toString());
                flag_sys=3;
                break;
            case R.id.sec_sys2:
                switch (flag_sys){
                    case 1:
                        outputsys.setText(str);
                        break;
                    case 2:
                        outputsys.setText(Integer.toBinaryString(Integer.valueOf(str , 8)));
                        break;
                    case 3:
                        outputsys.setText(Integer.toBinaryString(land_num3));
                        break;
                }
                break;
            case R.id.sec_sys8:
                switch (flag_sys){
                    case 1:
                        outputsys.setText(Integer.toOctalString(Integer.valueOf(str,2)));
                        break;
                    case 2:
                        outputsys.setText(land_num3+"");
                        break;
                    case 3:
                        outputsys.setText(Integer.toOctalString(land_num3));
                        break;
                }
                break;
            case R.id.sec_sys10:
                switch (flag_sys){
                    case 1:
                        outputsys.setText(Integer.valueOf(str,2).toString());
                        break;
                    case 2:
                        outputsys.setText(Integer.valueOf(str,8).toString());
                        break;
                    case 3:
                        outputsys.setText(land_num3+"");
                        break;
                }
                break;
        }

    }
}