package com.espian.showcaseview.drawing;

import com.espian.showcaseview.utils.ShowcaseAreaCalculator;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface ClingDrawer extends ShowcaseAreaCalculator {


    void drawShowcase(Canvas canvas, float x, float y, float[] scaleXYMultiplier, float radius);

    int getShowcaseWidth();

    int getShowcaseHeight();

}
