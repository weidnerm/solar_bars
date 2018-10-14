package com.example.mike.solarbars;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.net.DatagramSocket;

public class MainActivity extends AppCompatActivity {

    private BarChart mChart;
    private BarChart mChart2;
    private BarChart mChart3;

    ArrayList<String> xLabel1;
    ArrayList<String> xLabel2;
    ArrayList<String> xLabel3;

    Handler mHandler;
    Runnable mRunnable;

    DatagramSocket mDatagramSocket;
    DatagramPacket mTxDatagramPacket;
    DatagramPacket mRxDatagramPacket;

    NetworkTxHandler myNetworkTxHandler;
    NetworkRxHandler myNetworkRxHandler;

    String[] m_names = new String[10];
    int[] m_maxEnergy = new int[10];
    int[] m_cumulativeEnergy = new int[10];
    int[] m_current = new int[10];
    double[] m_voltage = new double[10];
    int[] m_todayCumulativeEnergy = new int[10];

    int m_panel_index = 0;
    int m_load_index = 0;
    int[] m_battery_index = new int[10];


    int[] m_barColors = new int[10*2];

    int tempval = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mChart = (BarChart) findViewById(R.id.bargraph);
        mChart.setMaxVisibleValueCount(40);  // not sure why

        mChart2 = (BarChart) findViewById(R.id.bargraph2);
        mChart2.setMaxVisibleValueCount(40);  // not sure why

        mChart3 = (BarChart) findViewById(R.id.bargraph3);
        mChart3.setMaxVisibleValueCount(40);  // not sure why

        setData(8);
        setData2(3);
        setData3(3);

        setupSocket();


        mHandler = new Handler();
        mRunnable = new Runnable(){
            @Override
            public void run() {

                if (mRxDatagramPacket != null)
                {
                    try{
                        int length;
                        String rxmessage = new String(mRxDatagramPacket.getData());
                        JSONObject overall = new JSONObject(rxmessage);

                        JSONArray names = overall.getJSONArray("names");
                        length = names.length();
                        m_names = new String[length];
                        for(int index=0; index<length; index++)
                        {
                            m_names[index] = names.getString(index);
                        }

                        JSONArray maxEnergy = overall.getJSONArray("maxEnergy");
                        length = maxEnergy.length();
                        m_maxEnergy = new int[length];
                        for(int index=0; index<length; index++)
                        {
                            m_maxEnergy[index] = maxEnergy.getInt(index);
                        }

                        JSONArray voltage = overall.getJSONArray("voltage");
                        length = voltage.length();
                        m_voltage = new double[length];
                        for(int index=0; index<length; index++)
                        {
                            m_voltage[index] = voltage.getDouble(index);
                        }

                        JSONArray current = overall.getJSONArray("current");
                        length = current.length();
                        m_current = new int[length];
                        for(int index=0; index<length; index++)
                        {
                            m_current[index] = current.getInt(index);
                        }

                        JSONArray cumulativeEnergy = overall.getJSONArray("cumulativeEnergy");
                        length = cumulativeEnergy.length();
                        m_cumulativeEnergy = new int[length];
                        for(int index=0; index<length; index++)
                        {
                            m_cumulativeEnergy[index] = cumulativeEnergy.getInt(index);
                        }

                        JSONArray todayCumulativeEnergy = overall.getJSONArray("todayCumulativeEnergy");
                        length = todayCumulativeEnergy.length();
                        m_todayCumulativeEnergy = new int[length];
                        for(int index=0; index<length; index++)
                        {
                            m_todayCumulativeEnergy[index] = todayCumulativeEnergy.getInt(index);
                        }
                        tempval = length;

                        m_battery_index = new int[m_names.length-2];
                        int battery_write_index = 0;
                        for(int index=0; index<m_names.length; index++)
                        {
                            if (m_names[index].equals("Panel"))
                            {
                                m_panel_index = index;
                            }
                            else if (m_names[index].equals("Load"))
                            {
                                m_load_index = index;
                            }
                            else if (m_names[index].substring(0,4).equals("Batt"))
                            {
                                m_battery_index[battery_write_index] = index;
                                battery_write_index++;
                            }
                        }

//                        int m_panel_index = 0;
//                        int m_load_index = 0;
//                        int[] m_battery_index = new int[10];

//                        Log.w("SolarBars", rxmessage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }



                setData(8);
                try {
                    myNetworkTxHandler = new NetworkTxHandler();
                    myNetworkTxHandler.execute();
                    myNetworkRxHandler = new NetworkRxHandler();
                    myNetworkRxHandler.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(mRunnable, 1000L);
            }
        };
        mRunnable.run();


    }

    private class NetworkTxHandler extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String subMessage = "sub";
            InetAddress destAddr;
            try {
                destAddr = InetAddress.getByName("192.168.86.44");
                mTxDatagramPacket = new DatagramPacket(subMessage.getBytes(), 0, subMessage.length(), destAddr, 29551);
//                mTxDatagramPacket = new DatagramPacket(subMessage.getBytes(), subMessage.length(), destAddr, 0);
                mDatagramSocket.send(mTxDatagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private class NetworkRxHandler extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            byte[] rxbuf = new byte[16384];
            mRxDatagramPacket = new DatagramPacket(rxbuf, rxbuf.length);

            InetAddress destAddr;
            try {
                mDatagramSocket.setSoTimeout(1);
                mDatagramSocket.receive(mRxDatagramPacket);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public void setupSocket()
    {
        try        {
            mDatagramSocket = new DatagramSocket(0);  // ephemeral port, hopefully
        }
        catch (Exception e)        {
            e.printStackTrace();
        }
    }

    public void setData(int count){
        ArrayList<BarEntry> yValues = new ArrayList<>();
        xLabel1 = new ArrayList<>();

        double accumActualBatFracMaxDrainRelative = 0;
        for(int i=0; i<count; i++){
            float val1 = (float)(Math.random()*60)+ 20;
            float val2 = 100-val1;
            int batt_index = m_battery_index[i];

            if ((m_names != null) && (batt_index<m_names.length)) {
                String label = String.format("%s\n%2.3f V\n%d mA\n%2.3f W",
                        m_names[batt_index],
                        m_voltage[batt_index],
                        m_current[batt_index],
                        m_voltage[batt_index] * m_current[batt_index] / 1000);
                xLabel1.add(label);

                if (m_current[batt_index] < -10) {
                    m_barColors[i*2] = Color.RED;
                } else if (m_current[batt_index] > 10) {
                    m_barColors[i*2] = Color.GREEN;
                } else {
                    m_barColors[i*2] = Color.YELLOW;
                }
                m_barColors[i*2+1] = R.color.grey1;

                int relBatLevel = m_maxEnergy[batt_index] - m_cumulativeEnergy[batt_index];
                int maxBatDrainAmount = 2000*3600;   // 2000 mAHr max usable amp hours for now.  computed in mA*Seconds

                double actualBatFracMaxDrainRelative = 1.0 - (double)relBatLevel/(double)maxBatDrainAmount;
                accumActualBatFracMaxDrainRelative = accumActualBatFracMaxDrainRelative + actualBatFracMaxDrainRelative;

                if (relBatLevel > maxBatDrainAmount) {
                    relBatLevel = maxBatDrainAmount;
                }
                double bar_1_frac = (double)relBatLevel/(double)maxBatDrainAmount * 100.0;
                double bar_2_frac = 100.0 - bar_1_frac;

                yValues.add(new BarEntry(i, new float[]{(float)bar_2_frac, (float)bar_1_frac}));

            }
        }

        BarDataSet set1;

        set1 = new BarDataSet(yValues, "Battery State");
        set1.setDrawIcons(false); // not sure why
        set1.setColors(m_barColors);
//        set1.setDrawValues(false);
        set1.setValueTextSize(10.0f);


        BarData data = new BarData(set1);
        data.setValueFormatter(new MyValueFormatter());

        mChart.setData(data);
        mChart.setFitBars(false);
        mChart.getDescription().setEnabled(false);
//        mChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel1.get((int)value);
            }
        });
        xAxis.setLabelCount(xLabel1.size());
        xAxis.setTextSize(8f);
        mChart.setXAxisRenderer(new CustomXAxisRenderer(mChart.getViewPortHandler(), mChart.getXAxis(), mChart.getTransformer(YAxis.AxisDependency.LEFT)));
        mChart.setExtraBottomOffset(30.0f);

        mChart.getLegend().setEnabled(false);
        mChart.getAxisLeft().setAxisMinimum(0f);
        mChart.getAxisLeft().setAxisMaximum(100f);
        mChart.getAxisRight().setAxisMinimum(0f);
        mChart.getAxisRight().setAxisMaximum(100f);

        mChart.invalidate();


    }
    public void setData2(int count){
        ArrayList<BarEntry> yValues = new ArrayList<>();
        xLabel2 = new ArrayList<>();

        for(int i=0; i<count; i++){
            float val1 = (float)(Math.random()*10)+ 2;

            yValues.add(new BarEntry(i, new float[]{val1}));
        }
        xLabel2.add("Panel\n\n\n9.9AH");
        xLabel2.add("Batt\n88%\n\n9.9AH");
        xLabel2.add("Load\n\n\n9.9AH");

        BarDataSet set1;

        set1 = new BarDataSet(yValues, "Energy");
        set1.setDrawIcons(false); // not sure why
//        set1.setStackLabels(new String[]{"children", "adults", "elders"});
        set1.setColors(new int[] {Color.GREEN, Color.RED, Color.YELLOW});
        set1.setValueTextSize(10.0f);


        BarData data = new BarData(set1);
//        data.setValueFormatter(new MyValueFormatter());

        mChart2.setData(data);
        mChart2.setFitBars(false);
        mChart2.getDescription().setEnabled(false);
//        mChart2.getAxisRight().setEnabled(false);

        XAxis xAxis = mChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel2.get((int)value);
            }
        });
        xAxis.setLabelCount(xLabel2.size());
        xAxis.setTextSize(8f);
        mChart2.setXAxisRenderer(new CustomXAxisRenderer(mChart2.getViewPortHandler(), mChart2.getXAxis(), mChart2.getTransformer(YAxis.AxisDependency.LEFT)));
        mChart2.setExtraBottomOffset(30.0f);
        mChart2.getLegend().setEnabled(false);
        mChart2.getAxisLeft().setAxisMinimum(0f);
        mChart2.getAxisLeft().setAxisMaximum(12.0f);
        mChart2.getAxisRight().setAxisMinimum(0f);
        mChart2.getAxisRight().setAxisMaximum(12.0f);

        mChart2.invalidate();

    }
    public void setData3(int count){
        ArrayList<BarEntry> yValues = new ArrayList<>();
        xLabel3 = new ArrayList<>();

        for(int i=0; i<count; i++){
            float val1 = (float)(Math.random()*4)+ 2;

            yValues.add(new BarEntry(i, new float[]{val1}));
        }
        xLabel3.add("Panel\n13.248 V\n452 mA\n6.149W");
        xLabel3.add("Batt\n\n452 mA\n6.149W");
        xLabel3.add("Load\n13.248 V\n452 mA\n6.149W");

        BarDataSet set1;

        set1 = new BarDataSet(yValues, "Power");
        set1.setDrawIcons(false); // not sure why
//        set1.setStackLabels(new String[]{"children", "adults", "elders"});
        set1.setColors(new int[] {Color.GREEN, Color.RED, Color.YELLOW});
        set1.setValueTextSize(10.0f);


        BarData data = new BarData(set1);
//        data.setValueFormatter(new MyValueFormatter());

        mChart3.setData(data);
        mChart3.setFitBars(false);
        mChart3.getDescription().setEnabled(false);

        XAxis xAxis = mChart3.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabel3.get((int)value);
            }
        });
        xAxis.setLabelCount(xLabel3.size());
        xAxis.setTextSize(8f);
        mChart3.setXAxisRenderer(new CustomXAxisRenderer(mChart3.getViewPortHandler(), mChart3.getXAxis(), mChart3.getTransformer(YAxis.AxisDependency.LEFT)));
        mChart3.setExtraBottomOffset(30f);
        mChart3.getLegend().setEnabled(false);
        mChart3.getAxisLeft().setAxisMinimum(0f);
        mChart3.getAxisLeft().setAxisMaximum(6.4f);
        mChart3.getAxisRight().setAxisMinimum(0f);
        mChart3.getAxisRight().setAxisMaximum(6.4f);

        mChart3.invalidate();
    }

}
