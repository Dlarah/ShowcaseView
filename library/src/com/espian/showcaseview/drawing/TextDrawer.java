package com.espian.showcaseview.drawing;

import com.espian.showcaseview.ShowcaseView;

import android.content.Context;
import android.graphics.Canvas;

/**
 * Created by curraa01 on 13/10/2013.
 */
public interface TextDrawer {

    void draw(Canvas canvas, boolean hasPositionChanged, int backColor);

    void setDetails(CharSequence details);

    void setTitle(CharSequence title);

    void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView);

    void setTitleStyling(Context context, int styleId);

    void setDetailStyling(Context context, int styleId);
    
    public float[] getTextAreaSize();
    
    public float[] getBestTextPosition();
}
