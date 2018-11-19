package com.example.mike.solarbars;

import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

public class HistoricalData
{
    String m_date = null;
    String m_sourceName = null;
    int m_sourceIndex = -1;
    int m_sourceCount = -1;

    double [] m_v_avg = null;
    double [] m_v_min = null;
    double [] m_v_max = null;
    int [] m_mA_avg = null;
    int [] m_mA_min = null;
    int [] m_mA_max = null;
    int [] m_time = null;
    int [] m_mASec = null;

    int m_count = 0;

    HistoricalData(String json_data)
    {
        int length;
        JSONArray tempJSONArray;

        try {
            JSONObject overall = new JSONObject(json_data);

            m_date       = overall.getString("date");
            m_sourceName = overall.getString("sourceName");
            m_sourceIndex = overall.getInt("sourceIndex");
            m_sourceCount = overall.getInt("sourceCount");

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("mA_avg");
            length = tempJSONArray.length();
            m_mA_avg = new int[length];
            for(int index=0; index<length; index++)
            {
                m_mA_avg[index] = tempJSONArray.getInt(index);
            }

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("mA_min");
            length = tempJSONArray.length();
            m_mA_min = new int[length];
            for(int index=0; index<length; index++)
            {
                m_mA_min[index] = tempJSONArray.getInt(index);
            }

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("mA_max");
            length = tempJSONArray.length();
            m_mA_max = new int[length];
            for(int index=0; index<length; index++)
            {
                m_mA_max[index] = tempJSONArray.getInt(index);
            }

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("time");
            length = tempJSONArray.length();
            m_time = new int[length];
            for(int index=0; index<length; index++)
            {
                m_time[index] = tempJSONArray.getInt(index);
//                System.out.println(String.format("[%d]=%d", index, m_time[index]));
            }

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("v_avg");
            length = tempJSONArray.length();
            m_v_avg = new double[length];
            for(int index=0; index<length; index++)
            {
                m_v_avg[index] = tempJSONArray.getDouble(index);
            }

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("v_min");
            length = tempJSONArray.length();
            m_v_min = new double[length];
            for(int index=0; index<length; index++)
            {
                m_v_min[index] = tempJSONArray.getDouble(index);
            }

            tempJSONArray = overall.getJSONObject("sourceData").getJSONArray("v_max");
            length = tempJSONArray.length();
            m_v_max = new double[length];
            for(int index=0; index<length; index++)
            {
                m_v_max[index] = tempJSONArray.getDouble(index);
            }




        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
