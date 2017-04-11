package music.abitri.com.euphony.CustomText;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhis on 3/20/2016.
 */
public class myTextcustomView extends TextView {

    public static Typeface FONT_NAME;

    public myTextcustomView(Context context) {
        super(context);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "Quicksand-Regular.otf");

        this.setTypeface(FONT_NAME);
    }

    public myTextcustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "Quicksand-Regular.otf");

        this.setTypeface(FONT_NAME);
    }

    public myTextcustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "Quicksand-Regular.otf");

        this.setTypeface(FONT_NAME);
    }
}
