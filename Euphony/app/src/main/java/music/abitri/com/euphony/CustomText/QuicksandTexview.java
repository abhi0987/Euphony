package music.abitri.com.euphony.CustomText;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by abhis on 2/21/2017.
 */

public class QuicksandTexview extends TextView {

    public static Typeface FONT_NAME;

    public QuicksandTexview(Context context) {
        super(context);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "Quicksand-Bold.otf");

        this.setTypeface(FONT_NAME);
    }

    public QuicksandTexview(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "Quicksand-Bold.otf");

        this.setTypeface(FONT_NAME);
    }

    public QuicksandTexview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(FONT_NAME == null)
            FONT_NAME = Typeface.createFromAsset(context.getAssets(), "Quicksand-Bold.otf");

        this.setTypeface(FONT_NAME);
    }
}
