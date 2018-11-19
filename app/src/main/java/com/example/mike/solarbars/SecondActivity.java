package com.example.mike.solarbars;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by For on 4/14/2017.
 */

public class SecondActivity extends Activity {

    private CombinedChart mChart;

    Handler mRxHandler;
    Runnable mRxRunnable;
    Handler mTxHandler;
    Runnable mTxRunnable;

    DatagramSocket mDatagramSocket;
    DatagramPacket mTxDatagramPacket;
    DatagramPacket mRxDatagramPacket;

    SecondActivity.NetworkTxHandler myNetworkTxHandler;
    SecondActivity.NetworkRxHandler myNetworkRxHandler;

    HistoricalData [] myHistoricalData;
    final int MAX_HISTORICAL_DATA_ENTRIES = 20;
    int myHistoricalData_count = 0;

    String [] m_source_names = {"Load", "Panel", "Batt 1", "Batt 2", "Batt 3", "Batt 4", "Batt 5", "Batt 6", "Batt 7", "Batt 8"};
    int currentSourceIndex = 0;
//    String currentSource = "Load";
    String currentParm = "Volts";
    int currentDayIndex = 0;

    int m_hist_index = -1;

    int pale_green = Color.rgb(128, 255, 128);
    int green      = Color.rgb(0, 255, 0);
    int pale_red   = Color.rgb(255, 128, 128);
    int red        = Color.rgb(255, 0, 0);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        myHistoricalData = new HistoricalData[MAX_HISTORICAL_DATA_ENTRIES];

        mChart = (CombinedChart) findViewById(R.id.combinedChart);
//        mChart.setMaxVisibleValueCount(40);  // not sure why
        mChart.setHighlightPerDragEnabled(true);
//        mChart.setVisibleXRange(0.0f, 20.0f);
//        mChart.setMaxVisibleValueCount(20);
//        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getDescription().setEnabled(false);

        mChart.setDrawBorders(true);
//        mChart.setBorderColor(getResources().getColor(R.color.colorLightGray));

        mChart.getAxisRight().setDrawGridLines(false);
        mChart.getAxisRight().setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setTextColor(red);
        mChart.getAxisRight().setDrawLabels(true);

        mChart.getAxisLeft().setTextColor(green);

        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);


        setupSocket();

        mRxHandler = new Handler();
        mRxRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    myNetworkRxHandler = new SecondActivity.NetworkRxHandler();
                    myNetworkRxHandler.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (m_hist_index>=0) {
                    setData(m_hist_index);
                    m_hist_index = -1;
                }
                mRxHandler.postDelayed(mRxRunnable, 1000L);
            }
        };

        mTxHandler = new Handler();
        mTxRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    myNetworkTxHandler = new SecondActivity.NetworkTxHandler();
                    myNetworkTxHandler.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mTxRunnable.run();
        mRxRunnable.run();



    }

    protected void setData(int hist_source_index)
    {
//        Log.w("SolarBars.Second.rx", String.format("setData(%d)",hist_source_index));

        ArrayList<Entry> max_mA_entries = new ArrayList<>();
        ArrayList<Entry> min_mA_entries = new ArrayList<>();
        ArrayList<Entry> avg_mA_entries = new ArrayList<>();
        ArrayList<Entry> max_v_entries = new ArrayList<>();
        ArrayList<Entry> min_v_entries = new ArrayList<>();
        ArrayList<Entry> avg_v_entries = new ArrayList<>();

        for(int index=0; index<myHistoricalData[hist_source_index].m_time.length; index++) {

            int mA_max = myHistoricalData[hist_source_index].m_mA_max[index];
            int mA_min = myHistoricalData[hist_source_index].m_mA_min[index];
            int mA_avg = myHistoricalData[hist_source_index].m_mA_avg[index];
            float v_max = (float)myHistoricalData[hist_source_index].m_v_max[index];
            float v_min = (float)myHistoricalData[hist_source_index].m_v_min[index];
            float v_avg = (float)myHistoricalData[hist_source_index].m_v_avg[index];
            float time = (float)myHistoricalData[hist_source_index].m_time[index]/60/60;

            max_mA_entries.add(new Entry(time, mA_max ));
            min_mA_entries.add(new Entry(time, mA_min ));
            avg_mA_entries.add(new Entry(time, mA_avg ));
            max_v_entries.add(new Entry(time, v_max ));
            min_v_entries.add(new Entry(time, v_min ));
            avg_v_entries.add(new Entry(time, v_avg ));
        }

        LineDataSet max_mA_set = new LineDataSet(max_mA_entries, String.format("%s %s max", m_source_names[currentSourceIndex], "mA"));
        LineDataSet min_mA_set = new LineDataSet(min_mA_entries, String.format("%s %s min", m_source_names[currentSourceIndex], "mA"));
        LineDataSet avg_mA_set = new LineDataSet(avg_mA_entries, String.format("%s %s avg", m_source_names[currentSourceIndex], "mA"));
        LineDataSet max_v_set = new LineDataSet(max_v_entries, String.format("%s %s max", m_source_names[currentSourceIndex], "Volts"));
        LineDataSet min_v_set = new LineDataSet(min_v_entries, String.format("%s %s min", m_source_names[currentSourceIndex], "Volts"));
        LineDataSet avg_v_set = new LineDataSet(avg_v_entries, String.format("%s %s avg", m_source_names[currentSourceIndex], "Volts"));

        max_mA_set.setAxisDependency(YAxis.AxisDependency.LEFT);
        min_mA_set.setAxisDependency(YAxis.AxisDependency.LEFT);
        avg_mA_set.setAxisDependency(YAxis.AxisDependency.LEFT);
        max_v_set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        min_v_set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        avg_v_set.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineData max_line_data = new LineData();

//        CandleDataSet dataset = new CandleDataSet(entries, String.format("%s %s", m_source_names[currentSourceIndex], currentParm));
        max_mA_set.setColor(pale_green);
        min_mA_set.setColor(pale_green);
        avg_mA_set.setColor(green);
        max_v_set.setColor(pale_red);
        min_v_set.setColor(pale_red);
        avg_v_set.setColor(red);

        max_mA_set.setCircleColor(pale_green);
        min_mA_set.setCircleColor(pale_green);
        avg_mA_set.setCircleColor(green);
        max_v_set.setCircleColor(pale_red);
        min_v_set.setCircleColor(pale_red);
        avg_v_set.setCircleColor(red);

        max_mA_set.setDrawCircles(false);
        min_mA_set.setDrawCircles(false);
//        avg_mA_set.setDrawCircles(false);
        max_v_set.setDrawCircles(false);
        min_v_set.setDrawCircles(false);
//        avg_v_set.setDrawCircles(false);

        avg_mA_set.setLineWidth(2.5f);
        avg_v_set.setLineWidth(2.5f);



        max_line_data.addDataSet(max_mA_set);
        max_line_data.addDataSet(min_mA_set);
        max_line_data.addDataSet(avg_mA_set);
        max_line_data.addDataSet(max_v_set);
        max_line_data.addDataSet(min_v_set);
        max_line_data.addDataSet(avg_v_set);

        TextView myAwesomeTextView = (TextView)findViewById(R.id.history_text_view_id);
        myAwesomeTextView.setText(myHistoricalData[hist_source_index].m_date);

        CombinedData data = new CombinedData();
        data.setData(max_line_data);
//        data.setData(min_line_data);
//        data.setData(avg_line_data);

//        CandleData data = new CandleData(dataset);

        mChart.setData(data);
        mChart.invalidate();
    }

    public void handleRightButton(View view)
    {
        currentDayIndex--;
        if (currentDayIndex <0)
        {
            currentDayIndex = 0;
        }
//        Log.w("SolarBars.Second.rx", String.format("currentDayIndex=%d",currentDayIndex));

        mTxRunnable.run();
    }

    public void handleLeftButton(View view)
    {
        currentDayIndex++;
//        Log.w("SolarBars.Second.rx", String.format("currentDayIndex=%d",currentDayIndex));
        mTxRunnable.run();
    }

    public void handleUpButton(View view)
    {
        currentSourceIndex--;
        if (currentSourceIndex < 0)
        {
            currentSourceIndex = m_source_names.length-1;
        }
//        Log.w("SolarBars.Second.rx", String.format("currentDayIndex=%d",currentDayIndex));
        mTxRunnable.run();
    }

    public void handleDownButton(View view)
    {
        currentSourceIndex++;
        if (currentSourceIndex >= m_source_names.length)
        {
            currentSourceIndex = 0;
        }
//        Log.w("SolarBars.Second.rx", String.format("currentDayIndex=%d",currentDayIndex));
        mTxRunnable.run();
    }

    private class NetworkTxHandler extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String subMessage = String.format("getdata %d", currentDayIndex);
            InetAddress destAddr;
            try {
                destAddr = InetAddress.getByName("192.168.86.44");
                mTxDatagramPacket = new DatagramPacket(subMessage.getBytes(), 0, subMessage.length(), destAddr, 29551);
//                mTxDatagramPacket = new DatagramPacket(subMessage.getBytes(), subMessage.length(), destAddr, 0);
                Log.w("SolarBars.Second.tx", String.format("sending %s", subMessage));
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
            byte[] rxbuf = new byte[65535];
            mRxDatagramPacket = new DatagramPacket(rxbuf, rxbuf.length);

//            InetAddress destAddr;
            for(int index=0; index<20; index++)
            {
                int hist_index;
                try {
                    mDatagramSocket.setSoTimeout(20);
                    mDatagramSocket.receive(mRxDatagramPacket);

                    String rxMessage = new String(mRxDatagramPacket.getData());

                    Log.w("SolarBars.Second.rx", rxMessage);
                    HistoricalData temp_entry = new HistoricalData(rxMessage);

                    boolean found = false;
                    for(hist_index=0; hist_index<myHistoricalData_count; hist_index++)
                    {
                        if( temp_entry.m_sourceName.equals(myHistoricalData[hist_index].m_sourceName) )
                        {
                            found = true;
                            break;
                        }
                    }
                    if(found == true) {
                        myHistoricalData[hist_index] = temp_entry;
//                        Log.w("SolarBars.Second.rx", String.format("storing entry at index %d", hist_index));

                    }
                    else {
                        if(myHistoricalData_count < MAX_HISTORICAL_DATA_ENTRIES) {
//                            Log.w("SolarBars.Second.rx", String.format("storing new entry at index %d", myHistoricalData_count));
                            myHistoricalData[myHistoricalData_count] = temp_entry;
                            myHistoricalData_count += 1;
                        }
                    }

                    found = false;
                    for(hist_index=0; hist_index<myHistoricalData_count; hist_index++)
                    {
                        if( myHistoricalData[hist_index].m_sourceName.equals(m_source_names[currentSourceIndex]) )
                        {
                            found = true;
                            m_hist_index = hist_index;
//                            Log.w("SolarBars.Second.rx", String.format("found %s at m_hist_index %d",m_source_names[currentSourceIndex], m_hist_index));
                            break;
                        }
                    }
                    if(found == true) {
//                        setData(m_hist_index);
                    }


                } catch (Exception e) {
//                    e.printStackTrace();
                }

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



}