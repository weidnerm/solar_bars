package com.example.mike.solarbars;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class CustomXAxisRenderer extends XAxisRenderer {
    public CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        String line[] = formattedLabel.split("\n");
        Utils.drawXAxisValue(c, line[0], x,                                    y, mAxisLabelPaint, anchor, angleDegrees);
        Utils.drawXAxisValue(c, line[1], x, y + mAxisLabelPaint.getTextSize(), mAxisLabelPaint, anchor, angleDegrees);
        Utils.drawXAxisValue(c, line[2], x, y + mAxisLabelPaint.getTextSize()*2, mAxisLabelPaint, anchor, angleDegrees);
        Utils.drawXAxisValue(c, line[3], x, y + mAxisLabelPaint.getTextSize()*3, mAxisLabelPaint, anchor, angleDegrees);
    }
}
