package assl.cs.usu.edu.energyprofiling;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Process;
import android.os.Debug;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private TextView infoTextView;
    private ActivityManager am;
    private int memTotal, pId;
    private Debug.MemoryInfo[] amMI;
    private ActivityManager.MemoryInfo mi;

    private List<String> memUsed, memAvailable, memFree, cached, threshold;
    private List<Float> cpuTotal, cpuAM;
    private List<Integer> memoryAM;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = (TextView) findViewById(R.id.infoText);
        infoTextView.setMovementMethod(new ScrollingMovementMethod());

        try {
            Class<?> powerProfileClazz = Class.forName("com.android.internal.os.PowerProfile");

            //get constructor that takes a context object
            Class[] argTypes = {Context.class};
            Constructor constructor = powerProfileClazz
                    .getDeclaredConstructor(argTypes);
            Object[] arguments = {this};

            //Instantiate
            Object powerProInstance = constructor.newInstance(arguments);

            //define method
            Method batteryCap = powerProfileClazz.getMethod("getBatteryCapacity", null);
            Method averagePower = powerProfileClazz.getMethod("getAveragePower", new Class[]{String.class, int.class});
            Method averagePower_nolevel = powerProfileClazz.getMethod("getAveragePower", new Class[]{String.class});

            //call method
            infoTextView.append("CPU core 1: " + averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 0}).toString() + "\n");
            infoTextView.append("CPU core 2: " + averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 1}).toString() + "\n");
            infoTextView.append("CPU core 3: " + averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 2}).toString() + "\n");
            infoTextView.append("CPU core 4: " + averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 3}).toString() + "\n");

            infoTextView.append("WiFi on: " + averagePower.invoke(powerProInstance, new Object[]{"wifi.on", 0}).toString() + "\n");
            infoTextView.append("WiFi active: " + averagePower.invoke(powerProInstance, new Object[]{"wifi.active", 0}).toString() + "\n");
            infoTextView.append("Gps on: " + averagePower.invoke(powerProInstance, new Object[]{"gps.on", 0}).toString() + "\n");
            infoTextView.append("Screen: " + averagePower_nolevel.invoke(powerProInstance, new Object[]{"screen.full"}).toString() + "\n");

            cpuTotal = new ArrayList<Float>();
            cpuAM = new ArrayList<Float>();
            memoryAM = new ArrayList<Integer>();
            memUsed = new ArrayList<String>();
            memAvailable = new ArrayList<String>();
            memFree = new ArrayList<String>();

            pId = Process.myPid();

            am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            amMI = am.getProcessMemoryInfo(new int[]{ pId });
            mi = new ActivityManager.MemoryInfo();

            Log.d("Profiler", batteryCap.invoke(powerProInstance, null).toString());
            Log.d("Profiler", averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 1}).toString());
            Log.d("Profiler", averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 2}).toString());
            Log.d("Profiler", averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 3}).toString());

            Profiler.getInstance().start();

            //do something
            bubblesSort();

            double energy = Profiler.getInstance().stop();

            infoTextView.append("Energy used: " + energy + "\n");

            infoTextView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bubblesSort(){
        Random rng = new Random();
        int[] arr = new int[11];

        for(int i = 0; i < arr.length; i++) {
            arr[i] = rng.nextInt(10);
        }

        System.out.print("Pre sort: ");
        infoTextView.append("Pre sort: ");
        for(int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ", ");
            infoTextView.append(arr[i] + ", ");
        }
        infoTextView.append("\n");

        //write a bubble sort algorithm here
        for(int i = 0; i < arr.length; i++) {
            for(int j = 1; j < arr.length; j++) {
                if(arr[j] < arr[j-1]) {
                    int temp = arr[j-1];
                    arr[j-1] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        System.out.print("Post sort: ");
        infoTextView.append("Post sort: ");
        for(int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ", ");
            infoTextView.append(arr[i] + ", ");
        }
        infoTextView.append("\n");
    }
}
