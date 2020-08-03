package com.example.client;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener{

    SensorManager mSensorManager;
    Sensor mAccelerometerSensor;
    Sensor mMagneticFieldSensor;
    TextView mForceValueText;
    TextView mXValueText;
    TextView mYValueText;
    TextView mZValueText;
    float[] coordinats = new float[2];//массив для координат получаемых аксилирометра


    String serIpAddress;        // адрес сервера
    int port = 10000;           // порт
    String msg;                 // Сообщение
    String mCoord ="";          // Строка для передачи уровня наклона
    boolean isMove = false;     // флаг
    final byte codeMsg = 1;     // Оправить сообщение
    final byte codeRotate = 2;  // Повернуть экран
    final byte codePoff = 3;    // Выключить компьютер
    final byte codeVolumeUp = 4;// Повысить громкость
    final byte codeVolumeDown=5;// Понизить громкость
    final byte codeBriUp = 6;   //Повысить яркость экрана
    final byte codeBriDown = 7; //Понизить яркость экрана
    final byte codePrevSlide=8; //Предыдущий слайд
    final byte codeNextSlide=9; //Следующий слайд
    final byte codeLKM=10;      //Левая кнопка мыши
    final byte codePKM=11;      //Правая кнопка мыши
    final byte codeMouse=12;    //Включение управления мышкой !!!!!!!
    final byte codePlayFirst=13;//Начало презентации с первого слайда
    final byte codePlay=14;     //Начало презентации с текущего слайда
    final byte codeStopPres=15; //Выход из режима презентации
    final byte codeStopShow=16; //временное сокрытие слайда черным экраном
    byte codeCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        if(sensors.size() > 0){
            for(Sensor sensor: sensors){
                switch (sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
                        if(mAccelerometerSensor == null)
                            mAccelerometerSensor = sensor;
                        break;
                    default:
                        break;
                }
            }
        }

        //while (isMove){
        //            SenderThread sender = new SenderThread();
        //            codeCommand = codeMouse;
        //            sender.execute();
        //            }
    }

    @Override
    public void onPause(){
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        float[] values = event.values;
        switch (event.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:{
                coordinats[0]= event.values[SensorManager.DATA_X];
                coordinats[1]= event.values[SensorManager.DATA_Y];
                //coordinats[2]= event.values[SensorManager.DATA_Z];
            }
            break;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //////////////////////////////////////////
    public void onClick (View v)
    {
        EditText etIPaddress = (EditText)findViewById(R.id.edIPaddress);
        serIpAddress = etIPaddress.getText().toString();
        if (serIpAddress.isEmpty()){
            Toast msgToast = Toast.makeText(this, "Введите ip адрес", Toast.LENGTH_SHORT);
            msgToast.show();
            return;
        }
        EditText etMsg = (EditText) findViewById(R.id.etMsg);
        msg = etMsg.getText().toString();
        //ToggleButton tgl = findViewById(R.id.toggleButton);
        SenderThread sender = new SenderThread();
        switch (v.getId())
        {
            case R.id.btnSMsg:
                if (!msg.isEmpty()) {
                    codeCommand = codeMsg;
                    sender.execute();
                }
                else {
                    Toast msgToast = Toast.makeText(this, "Введите сообщение", Toast.LENGTH_SHORT);
                    msgToast.show();
                }
                break;
            //case R.id.btnRotate:
            //    codeCommand = codeRotate;
            //    sender.execute();
            //    break;
            case R.id.btnPowerOff:
                codeCommand = codePoff;
                sender.execute();
                break;
            case R.id.btnUpVol:
                codeCommand = codeVolumeUp;
                sender.execute();
                break;
            case R.id.btnDownVol:
                codeCommand = codeVolumeDown;
                sender.execute();
                break;
            case R.id.btnUpBri:
                codeCommand = codeBriUp;
                sender.execute();
                break;
            case R.id.btnDownBri:
                codeCommand = codeBriDown;
                sender.execute();
                break;
            case R.id.btnPrev:
                codeCommand = codePrevSlide;
                sender.execute();
                break;
            case R.id.btnNxt:
                codeCommand = codeNextSlide;
                sender.execute();
                break;
            case R.id.btnLKM:
                codeCommand = codeLKM;
                sender.execute();
                break;
            case R.id.btnPKM:
                codeCommand = codePKM;
                sender.execute();
                break;
            //case R.id.toggleButton:
            //    while (tgl.isChecked() == true){
            //    codeCommand = codeMouse;
            //    sender.execute();
            //    }
            //   break;
            case R.id.btnFirstPl:
                codeCommand = codePlayFirst;
                sender.execute();
                break;
            case R.id.btnPl:
                codeCommand = codePlay;
                sender.execute();
                break;
            case R.id.btnStopPres:
                codeCommand = codeStopPres;
                sender.execute();
                break;
            case R.id.btnStopShow:
                codeCommand = codeStopShow;
                sender.execute();
                break;
        }
    }

    public void onToggleClicked(View v) {

        ToggleButton tgl = findViewById(R.id.toggleButton);
        SenderThread sender = new SenderThread();
        // включена ли кнопка
        boolean on = tgl.isChecked();
        if (serIpAddress.isEmpty()){
            Toast msgToast = Toast.makeText(this, "Введите ip адрес", Toast.LENGTH_SHORT);
            msgToast.show();
            return;
        }

        if (on) {
            // действия если включена
            Toast.makeText(this, "Управление включено", Toast.LENGTH_SHORT).show();
            codeCommand = codeMouse;
            isMove = true;
            //while (tgl.isChecked() == true){
            //        codeCommand = codeMouse;
            //        sender.execute();
            //        }
            sender.execute();
        } else {
            // действия, если выключена
            Toast.makeText(this, "Управление выключено!", Toast.LENGTH_SHORT).show();
            isMove = false;
        }
    }

    public void ReSend(){
        SenderThread sender = new SenderThread();
        codeCommand = codeMouse;
        sender.execute();
    }

    //потенциально функиця обрабатывающая длинное нажатия кнопок для вывода подсказок, сейчас же просто лишние строки кода
    public void onLongClick (View v)
    {
        switch (v.getId())
        {
            case R.id.btnSMsg:
                Toast msgToast = Toast.makeText(this, "Введите сообщение для отпраки на ПК", Toast.LENGTH_LONG);
                msgToast.show();
                break;
            //case R.id.btnRotate:
            //    Toast msgToast1 = Toast.makeText(this, "Поворот экрана", Toast.LENGTH_SHORT);
            //    msgToast1.show();
            //    break;
            case R.id.btnPowerOff:
                Toast msgToast2 = Toast.makeText(this, "Включение/выключение функций мышки", Toast.LENGTH_LONG);
                msgToast2.show();
                break;
            case R.id.btnUpVol:
                Toast msgToast3 = Toast.makeText(this, "Повышение громкости", Toast.LENGTH_LONG);
                msgToast3.show();
                break;
            case R.id.btnDownVol:
                Toast msgToast4 = Toast.makeText(this, "Понижение громкости", Toast.LENGTH_LONG);
                msgToast4.show();
                break;
            case R.id.btnUpBri:
                Toast msgToast5 = Toast.makeText(this, "Повышение яркости", Toast.LENGTH_LONG);
                msgToast5.show();
                break;
            case R.id.btnDownBri:
                Toast msgToast6 = Toast.makeText(this, "Понижение яркости", Toast.LENGTH_LONG);
                msgToast6.show();
                break;
            case R.id.btnPrev:
                Toast msgToast7 = Toast.makeText(this, "Предыдущий слайд", Toast.LENGTH_LONG);
                msgToast7.show();
                break;
            case R.id.btnNxt:
                Toast msgToast8 = Toast.makeText(this, "Следующий слайд", Toast.LENGTH_LONG);
                msgToast8.show();
                break;
            case R.id.btnLKM:
                Toast msgToast9 = Toast.makeText(this, "Введите сообщение", Toast.LENGTH_LONG);
                msgToast9.show();
                break;
            case R.id.btnPKM:
                Toast msgToast10 = Toast.makeText(this, "Введите сообщение", Toast.LENGTH_LONG);
                msgToast10.show();
                break;
        }
    }

    class SenderThread extends AsyncTask <Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                InetAddress ipAddress = InetAddress.getByName(serIpAddress);
                Socket socket = new Socket(ipAddress, port);
                //InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream out = new DataOutputStream(outputStream);
                if(isMove){
                    ReSend();
                }

                switch (codeCommand) {
                    case codeMsg:
                        out.write(codeMsg);
                        //     out.flush();
                        byte[] outMsg = msg.getBytes("UTF8");
                        out.write(outMsg);
                        out.flush();
                        break;
                    //case codeRotate:
                    //    out.write(codeRotate);
                        //   out.flush();
                    //    break;
                    case codePoff:
                        out.write(codePoff);
                        // out.flush();
                        break;
                    case codeVolumeUp:
                        out.write(codeVolumeUp);
                        // out.flush();
                        break;
                    case codeVolumeDown:
                        out.write(codeVolumeDown);
                        // out.flush();
                        break;
                    case codeBriUp:
                        out.write(codeBriUp);
                        // out.flush();
                        break;
                    case codeBriDown:
                        out.write(codeBriDown);
                        // out.flush();
                        break;
                    case codePrevSlide:
                        out.write(codePrevSlide);
                        // out.flush();
                        break;
                    case codeNextSlide:
                        out.write(codeNextSlide);
                        // out.flush();
                        break;
                    case codeLKM:
                        out.write(codeLKM);
                        // out.flush();
                        break;
                    case codePKM:
                        out.write(codePKM);
                        // out.flush();
                        break;
                    case codeMouse:
                        while (isMove){
                            out.write(codeMouse);
                            //for (int i = 0; i < 2; i++) {
                            //    mCoord = mCoord + coordinats[i]+"/";
                            //}
                            mCoord = coordinats[0]+"/"+coordinats[1];
                            Thread.sleep(1);
                            byte[] outCoord = mCoord.getBytes("UTF8");
                            out.write(outCoord);
                            out.flush();
                            //oos.writeObject(coordinats);
                        }
                        break;
                    case codePlayFirst:
                        out.write(codePlayFirst);
                        break;
                    case codePlay:
                        out.write(codePlay);
                        break;
                    case codeStopPres:
                        out.write(codeStopPres);
                        break;
                    case codeStopShow:
                        out.write(codeStopShow);
                        break;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
