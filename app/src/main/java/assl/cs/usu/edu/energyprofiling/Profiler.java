package assl.cs.usu.edu.energyprofiling;

import android.os.Process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;

/**
 * Created by ywkwon on 2/15/2017.
 */

public class Profiler {

    private static Profiler instance;

    private static boolean isTracking = false;
    private static long refreshRate = 500;
    private int pid;

    private long startTime;
    private long stopTime;

    private Profiler() {
    }

    public static Profiler getInstance() {
        if(instance == null) {
            instance = new Profiler();
        }
        return instance;
    }

    /*
    public void setpId(int inpid){
        this.pId = inpid;
    }
    */

    public void start(int inpid) {
        pid = inpid;
        isTracking = true;
        //calculate using readCPUUsage() periodically
        //TODO read CPU usages asynchronously
            readCPUUsage();
        startTime = getCpuTime();
    }

    public double stop() {
        //calculate energy consumption
        isTracking = false;
        stopTime = getCpuTime();
        //TODO calculate here

        double consumption = 0.0;
        long measuredTime = stopTime - startTime;
        for(long i = 0; i <= measuredTime; i += refreshRate) {
            double energy ;
                energy = MainActivity.cpu1Power + MainActivity.cpu2Power + MainActivity.cpu3Power + MainActivity. cpu4Power;
                energy *= (refreshRate * 1.0) / 1000.0;

            consumption += energy;
        }

        return consumption;
    }

    private double readCPUUsage() {
        //read current CPU frequency
        return getCpuFreq();
    }


    public long getCpuTime(){
        try {
            //CPU time for a specific process
            BufferedReader reader = new BufferedReader(new FileReader("/proc/" + pid + "/stat"));

            String[] sa = reader.readLine().split("[ ]+", 18);

            //utime + stime + cutime + cstime
            long cputime = Long.parseLong(sa[13]) + Long.parseLong(sa[14]) + Long.parseLong(sa[15]) + Long.parseLong(sa[16]);
            reader.close();

            System.out.println(cputime);
            return cputime;
        }catch (Exception e) {e.printStackTrace();}
        long failed = -1;
        return failed;
    }

    public double getCpuFreq(){
        try {
            //Runtime.getRuntime().exec("su -c \"echo 1234 > /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_cur_freq\"");

            String cpuFreq = "";
            double freqTotal = 0.0;
            RandomAccessFile reader;

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            freqTotal += (Double.parseDouble(cpuFreq));

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            freqTotal += (Double.parseDouble(cpuFreq));

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            freqTotal += (Double.parseDouble(cpuFreq));

            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq", "r");
            cpuFreq = reader.readLine();
            reader.close();
            freqTotal += (Double.parseDouble(cpuFreq));

            return freqTotal;
        }catch (Exception e) {e.printStackTrace();}
        double freqTotalFailed = -1.0;
        return freqTotalFailed;
    }
}
