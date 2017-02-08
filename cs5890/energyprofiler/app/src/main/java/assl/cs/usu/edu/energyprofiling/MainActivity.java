package assl.cs.usu.edu.energyprofiling;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Process;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public TextView infoTextView;
    private ActivityManager am;
    private int memTotal, pId;
    private Debug.MemoryInfo[] amMI;
    private ActivityManager.MemoryInfo mi;

    private List<String> memUsed, memAvailable, memFree, cached, threshold;
    private List<Float> cpuTotal, cpuAM;
    private List<Integer> memoryAM;

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
            SortMethods();
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

    public void SortMethods(){

        Random generator = new Random();

        int[] list = new int[11];
        for(int i=0; i<list.length; i++)
        {
            list[i] = generator.nextInt(10);
        }

        System.out.println("Original Random array: ");
        printArray(list);

        bubbleSort(list);

        System.out.println("\nAfter bubble sort: ");
        printArray(list);}


    public static void bubbleSort(int[] list)
    {
        for(int i=0; i<list.length; i++)
        {
            for(int j=i + 1; j<list.length; j++)
            {
                if(list[i] > list[j])
                {
                    int temp = list[i];
                    list[i] = list[j];
                    list[j] = temp;
                }
            }

        }
    }

    public void printArray(int[] list)
    {
        for(int i=0; i<list.length; i++)
        {
            System.out.print(list[i] + ", ");
            infoTextView.append(list[i] + ", ");
        }
        infoTextView.append("\n");
    }


//    private String cmdCat(String f){
//
//        String[] command = {"cat", f};
//        StringBuilder cmdReturn = new StringBuilder();
//
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder(command);
//            Process process = processBuilder.start();
//
//            InputStream inputStream = process.getInputStream();
//            int c;
//
//            while ((c = inputStream.read()) != -1) {
//                cmdReturn.append((char) c);
//            }
//
//            return cmdReturn.toString();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "Something Wrong";
//        }
//
//    }

}
