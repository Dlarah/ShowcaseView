package com.espian.showcaseview.drawing;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseView.ConfigOptions.TextPosition;
import com.espian.showcaseview.utils.ShowcaseAreaCalculator;
import com.github.espiandev.showcaseview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;

/**
 * Draws the text as required by the ShowcaseView
 */
public class TextDrawerImpl implements TextDrawer {
	
	private static final int PADDING = 24;
	private static final int ACTIONBAR_PADDING = 66;

    private final TextPaint mPaintTitle;
    private final TextPaint mPaintDetail;
    private CharSequence mTitle, mDetails;
    private float mDensityScale;
    private ShowcaseAreaCalculator mCalculator;
    private float[] mBestTextPosition = new float[3];
    private DynamicLayout mDynamicTitleLayout;
    private DynamicLayout mDynamicDetailLayout;
    private TextAppearanceSpan mTitleSpan;
    private TextAppearanceSpan mDetailSpan;
    private ShowcaseView showcaseView;

    public TextDrawerImpl(float densityScale, ShowcaseAreaCalculator calculator) {
        mDensityScale = densityScale;
        mCalculator = calculator;

        mPaintTitle = new TextPaint();
        mPaintTitle.setAntiAlias(true);

        mPaintDetail = new TextPaint();
        mPaintDetail.setAntiAlias(true);   
    }
    
    @Override
    public void setTitleStyling(Context context, int styleId) {
        mTitleSpan = new TextAppearanceSpan(context, styleId);
    }

    @Override
    public void setDetailStyling(Context context, int styleId) {
        mDetailSpan = new TextAppearanceSpan(context, styleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title != null) {
            SpannableString ssbTitle = new SpannableString(title);
            ssbTitle.setSpan(mTitleSpan, 0, ssbTitle.length(), 0);
            mTitle = ssbTitle;
        }        
    }
    
    @Override
    public void setDetails(CharSequence details) {
        if (details != null) {
            SpannableString ssbDetail = new SpannableString(details);
            ssbDetail.setSpan(mDetailSpan, 0, ssbDetail.length(), 0);
            mDetails = ssbDetail;
        }
    }
    
    public boolean shouldDrawText() {
        return !TextUtils.isEmpty(mTitle) || !TextUtils.isEmpty(mDetails);
    }
    
    public float[] getBestTextPosition() {//returns offset of X and Y, the width of text field (dotn't forget text field has no left padding) 
        return mBestTextPosition;
    }

    @Override
    public void draw(Canvas canvas, boolean hasPositionChanged, int BackgroundColor) {
        if (shouldDrawText()) {
        	
            float[] textPosition = getBestTextPosition();
            
            if (!TextUtils.isEmpty(mTitle)) { 
            	if (hasPositionChanged) {
                    mDynamicTitleLayout = new DynamicLayout(mTitle, mPaintTitle, (int) textPosition[2],
                            Layout.Alignment.ALIGN_NORMAL, 1.0f, 1.0f, false);
            	}
            }

            if (!TextUtils.isEmpty(mDetails)) {
            	if (hasPositionChanged) {
            		mDynamicDetailLayout = new DynamicLayout(mDetails, mPaintDetail, (int) textPosition[2],
                            Layout.Alignment.ALIGN_NORMAL, 1.2f, 1.0f, false);
            	}                   
            }           

        	Paint background = new Paint();
        	background.setStyle(Style.FILL);
        	background.setColor(Color.YELLOW);

            float[] rect_size = getTextAreaSize();

        	Path mainpath;
        	mainpath = new Path();
        	RectF oval1 = new RectF(0, 0, rect_size[0]-3, rect_size[1]-3);
        	oval1 = new RectF(rect_size[0]-13,0,rect_size[0]-3, 12);
        	mainpath.moveTo(6, 0);
        	mainpath.lineTo(rect_size[0]-13, 0);
        	mainpath.arcTo(oval1, 270, 90);
        	mainpath.lineTo(rect_size[0]-3, rect_size[1]-13);
        	oval1 = new RectF(rect_size[0]-13,rect_size[1]-13,rect_size[0]-3,rect_size[1]-3);
        	mainpath.arcTo(oval1, 0, 90);
        	mainpath.lineTo(rect_size[0]-13, rect_size[1]-3);
        	oval1 = new RectF(0,rect_size[1]-13,12,rect_size[1]-3);
        	mainpath.arcTo(oval1, 90, 90);
        	mainpath.lineTo(0, 12);
        	oval1 = new RectF(0,0,12,12);
        	mainpath.arcTo(oval1, 180, 90);
        	mainpath.close();
        	
        	
            canvas.save();
            canvas.translate(textPosition[0] - PADDING * mDensityScale, textPosition[1]);
            canvas.drawPath(mainpath, background);
            background.setStyle(Style.STROKE);
            background.setColor(Color.MAGENTA);
            canvas.drawPath(mainpath, background);
            canvas.restore();
            
        	if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mDetails)) {
                canvas.save();
                canvas.translate(textPosition[0], textPosition[1]);
                mDynamicTitleLayout.draw(canvas);
                canvas.restore();
                canvas.save();
                canvas.translate(textPosition[0], textPosition[1] + mDynamicTitleLayout.getHeight());
                mDynamicDetailLayout.draw(canvas);
                canvas.restore();
        	} else if (TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mDetails)) {
                canvas.save();
                canvas.translate(textPosition[0], textPosition[1]);
                mDynamicDetailLayout.draw(canvas);
                canvas.restore();
        	} else if (!TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(mDetails)) {
                canvas.save();
 //               canvas.drawColor(BackgroundColor);
                canvas.translate(textPosition[0], textPosition[1]); //+ mDynamicTitleLayout.getHeight());
                mDynamicTitleLayout.draw(canvas);
                canvas.restore();
        	}
            
        }
    }



    /**
     * Calculates the best place to position text
     *
     * @param canvasW width of the screen
     * @param canvasH height of the screen
     */
    @Override
//    public void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView) {
    public void calculateTextPosition(int canvasW, int canvasH, ShowcaseView showcaseView) {
    	
    	this.showcaseView = showcaseView;
    	
    	Rect showcase = showcaseView.hasShowcaseView() ?
    			mCalculator.getShowcaseRect() :
    			new Rect();

    	int[] areas = new int[4]; //left, top, right, bottom
    	areas[0] = showcase.left * canvasH;
    	areas[1] = showcase.top * canvasW;
    	areas[2] = (canvasW - showcase.right) * canvasH;
    	areas[3] = (canvasH - showcase.bottom) * canvasW;
    	
    	int largest;
    	
    	switch (showcaseView.getConfigOptions().position) {
		case BOTTOM:
    		mBestTextPosition[0] = 2 * PADDING * mDensityScale;
    		mBestTextPosition[1] = showcase.bottom + PADDING * mDensityScale ;
    		mBestTextPosition[2] = canvasW - 4 * PADDING * mDensityScale;
			break;
		case LEFT:
    		mBestTextPosition[0] = 2 * PADDING * mDensityScale;
    		mBestTextPosition[1] = (PADDING + ACTIONBAR_PADDING) * mDensityScale;
    		mBestTextPosition[2] = showcase.left - 4 * PADDING * mDensityScale;
			break;
		case NOT_SPECIFIED:
			largest = 0;
	    	for(int i = 1; i < areas.length; i++) {
	    		if(areas[i] > areas[largest])
	    			largest = i;
	    	}
	    	switch(largest) {
	    	case 0://left
	    		mBestTextPosition[0] = 2 * PADDING * mDensityScale;
	    		mBestTextPosition[1] = (PADDING + ACTIONBAR_PADDING) * mDensityScale;
	  //  		mBestTextPosition[2] = showcase.left - 2 * PADDING * mDensityScale;
	    		mBestTextPosition[2] = (int) showcaseView.getClingRect().left - 4 * PADDING * mDensityScale;
	    		break;
	    	case 1://top
	    		mBestTextPosition[0] = 2 * PADDING * mDensityScale;
	    		mBestTextPosition[1] = (PADDING + ACTIONBAR_PADDING) * mDensityScale;
	    		mBestTextPosition[2] = canvasW - 4 * PADDING * mDensityScale;
	    		break;
	    	case 2://right
	 //   		mBestTextPosition[0] = showcase.right + 2 * PADDING * mDensityScale;
	    		mBestTextPosition[0] = (int) showcaseView.getClingRect().right + 2 * PADDING * mDensityScale;
	    		mBestTextPosition[1] = (PADDING + ACTIONBAR_PADDING) * mDensityScale;
	 //   		mBestTextPosition[2] = (canvasW - showcase.right) - 2 * PADDING * mDensityScale;
	    		mBestTextPosition[2] = (canvasW - (int) showcaseView.getClingRect().right) - 4 * PADDING * mDensityScale;
	    		break;
	    	case 3:// bottom  		
	    		mBestTextPosition[0] = 2 * PADDING * mDensityScale;
	 //   		mBestTextPosition[1] = showcase.bottom + PADDING * mDensityScale;
	    		mBestTextPosition[1] = (int) showcaseView.getClingRect().bottom + PADDING * mDensityScale;
	    		mBestTextPosition[2] = canvasW - 4 * PADDING * mDensityScale;
	    		break;
	    	}
			break;
		case RIGHT:
			mBestTextPosition[0] = showcase.right + 2 * PADDING * mDensityScale;
    		mBestTextPosition[1] = (PADDING + ACTIONBAR_PADDING) * mDensityScale;
    		mBestTextPosition[2] = (canvasW - showcase.right) - 4 * PADDING * mDensityScale;
			break;
		case TOP:
    		mBestTextPosition[0] = 2 * PADDING * mDensityScale;
    		mBestTextPosition[1] = (PADDING + ACTIONBAR_PADDING) * mDensityScale;
    		mBestTextPosition[2] = canvasW - 4 * PADDING * mDensityScale;
			break;
		default:
			break;
    	}

/*    	if(showcaseView.getConfigOptions().centerText) {
	    	// Center text vertically or horizontally
	    	switch(largest) {
	    	case 0:
	    	case 2:
	    		mBestTextPosition[1] += canvasH / 4;
	    		break;
	    	case 1:
	    	case 3:
	    		mBestTextPosition[2] /= 2;
	    		mBestTextPosition[0] += canvasW / 4;
	    		break;
	    	} 
    	} else {
    		// As text is not centered add actionbar padding if the text is left or right
	    	switch(largest) {
	    		case 0:
	    		case 2:
	    			mBestTextPosition[1] += ACTIONBAR_PADDING * mDensityScale;
	    			break;
	    	}
    	}*/
    }

    @Override
    public float[] getTextAreaSize() {
    	if (TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mDetails)) {
    		return new float[] {mDynamicDetailLayout.getWidth() + 2 * PADDING * mDensityScale, 
    				            mDynamicDetailLayout.getHeight()};
    	} else if (!TextUtils.isEmpty(mTitle) && TextUtils.isEmpty(mDetails)) {
    		return new float[] {mDynamicTitleLayout.getWidth() + 2 * PADDING * mDensityScale, 
    				            mDynamicTitleLayout.getHeight()};
    	} else if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mDetails)) {
    		float max_width = (mDynamicTitleLayout.getWidth() >= mDynamicDetailLayout.getWidth())?
    				           mDynamicTitleLayout.getWidth() :
    				           mDynamicDetailLayout.getWidth();   
        	return new float[] {max_width + 2 * PADDING * mDensityScale, 
        			            mDynamicTitleLayout.getHeight() + mDynamicDetailLayout.getHeight()};
    	} else {
        	return new float[] {0, 0};
    	}
/*    	if (mDynamicTitleLayout == null && mDynamicDetailLayout != null) {
    		return new float[] {mDynamicDetailLayout.getWidth() + 10, mDynamicDetailLayout.getHeight()};
    	} else if (mDynamicTitleLayout != null && mDynamicDetailLayout == null) {
    		return new float[] {mDynamicTitleLayout.getWidth() + 10, mDynamicTitleLayout.getHeight()};
    	} else if (mDynamicTitleLayout != null && mDynamicDetailLayout != null) {
    		float max_width = 0;
    		max_width = (mDynamicTitleLayout.getWidth() >= mDynamicDetailLayout.getWidth())?
    				mDynamicTitleLayout.getWidth() : mDynamicDetailLayout.getWidth();
        	return new float[] {max_width + 10, mDynamicTitleLayout.getHeight() + mDynamicDetailLayout.getHeight()};
    	} else {
        	return new float[] {0, 0};
    	}*/

    }
}
