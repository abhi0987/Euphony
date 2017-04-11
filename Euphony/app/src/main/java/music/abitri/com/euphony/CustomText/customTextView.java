package music.abitri.com.euphony.CustomText;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhis on 3/15/2016.
 */
public class customTextView extends TextView {

    public static Typeface FONT_NAME;

    public customTextView(Context context) {
        super(context);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "OpenSans_Regular.ttf");

        this.setTypeface(FONT_NAME);
    }

    public customTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "OpenSans_Regular.ttf");

        this.setTypeface(FONT_NAME);
    }

    public customTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "OpenSans_Regular.ttf");

        this.setTypeface(FONT_NAME);
    }


}
