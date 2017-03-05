package assl.cs.usu.edu.energyprofiling;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView infoTextView;
    private ActivityManager am;
    private int memTotal, pId;
    private Debug.MemoryInfo[] amMI;
    private ActivityManager.MemoryInfo mi;

    private List<String> memUsed, memAvailable, memFree, cached, threshold;
    private List<Float> cpuTotal, cpuAM;
    private List<Integer> memoryAM;
    //Venky
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoTextView = (TextView) findViewById(R.id.infoText) ;


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

            getCpuTime();
            getCpuFreq();
            Log.d("Profiler", batteryCap.invoke(powerProInstance, null).toString());
            Log.d("Profiler", averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 1}).toString());
            Log.d("Profiler", averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 2}).toString());
            Log.d("Profiler", averagePower.invoke(powerProInstance, new Object[]{"cpu.active", 3}).toString());


            Profiler.getInstance().start();
            //do something
            bubblesSort();
            double energy = Profiler.getInstance().stop();

        } catch (Exception e) {e.printStackTrace();}
    }

    private void getCpuTime() {
        try {
            //CPU time for a specific process
            BufferedReader reader = new BufferedReader(new FileReader("/proc/" + pId + "/stat"));

            String[] sa = reader.readLine().split("[ ]+", 18);

            //utime + stime + cutime + cstime
            long cputime = Long.parseLong(sa[13]) + Long.parseLong(sa[14]) + Long.parseLong(sa[15]) + Long.parseLong(sa[16]);
            reader.close();

            System.out.println(cputime);
        }catch (Exception e) {e.printStackTrace();}
    }

    private void getCpuFreq() {
        try {
            //Runtime.getRuntime().exec("su -c \"echo 1234 > /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq\"");

            String cpuFreq = "";
            RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();

            //cpuFreq = cmdCat("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq");

            infoTextView.append("CPU frequency (core 0): " + cpuFreq + "\n");

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            infoTextView.append("CPU frequency (core 1): " + cpuFreq + "\n");

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            infoTextView.append("CPU frequency (core 2): " + cpuFreq + "\n");

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            infoTextView.append("CPU frequency (core 3): " + cpuFreq + "\n");
        }catch (Exception e) {e.printStackTrace();}
    }


    private void bubblesSort() {
        //write a bubble sort algorithm here
        for(int i = 0; i < 100; i++) {
            for(int j = 0; j < 100; j++) {
                //sort
            }
        }
    }

}
