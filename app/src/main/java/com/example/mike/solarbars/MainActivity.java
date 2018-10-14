package com.example.mike.solarbars;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BarChart mChart;
    private BarChart mChart2;
    private BarChart mChart3;

    ArrayList<String> xLabel1;
    ArrayList<String> xLabel2;
    ArrayList<String> xLabel3;

    Handler mHandler;
    Runnable mRunnable;

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

        mHandler = new Handler();
        mRunnable = new Runnable(){
            @Override
            public void run() {
                setData(8);
                mHandler.postDelayed(mRunnable, 1000L);
            }
        };
        mRunnable.run();
    }

    public void setData(int count){
        ArrayList<BarEntry> yValues = new ArrayList<>();
        xLabel1 = new ArrayList<>();

        for(int i=0; i<count; i++){
            float val1 = (float)(Math.random()*60)+ 20;
            float val2 = 100-val1;

            yValues.add(new BarEntry(i, new float[]{val1, val2}));

            xLabel1.add("Batt "+(i+1)+"\n14.100 V\n75 mA\n1.056 W");
        }

        BarDataSet set1;

        set1 = new BarDataSet(yValues, "Battery State");
        set1.setDrawIcons(false); // not sure why
//        set1.setStackLabels(new String[]{"", ""});
        set1.setColors(new int[] {Color.GREEN, R.color.grey1,Color.RED, R.color.grey1});
//        set1.setDrawValues(false);
        set1.setValueTextSize(10.0f);


        BarData data = new BarData(set1);
//        data.setValueFormatter(new MyValueFormatter());

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
