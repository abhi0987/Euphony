package music.abitri.com.euphony.CustomText;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Abhishek on 5/2/2016.
 */
public class custom_text_view extends TextView {
    public static Typeface FONT_NAME;

    public custom_text_view(Context context) {
        super(context);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "urban.otf");

        this.setTypeface(FONT_NAME);
    }

    public custom_text_view(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "urban.otf");

        this.setTypeface(FONT_NAME);
    }

    public custom_text_view(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "urban.otf");

        this.setTypeface(FONT_NAME);
    }
}
