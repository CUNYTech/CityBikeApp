package com.cunycodes.bikearound;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jody on 8/29/2018.
 *
 * Taken from code.tutplus.com/tutorials/how-to-use-fontawesome-in-an-android-app-cms-24167
 */

public class FontManager {

    public static final String ROOT = "fonts/",
    FONTAWESOME = ROOT + "FontAwesome.otf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

    public static void markAsIconContainer(View v, Typeface typeface) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                markAsIconContainer(child, typeface);
            }
        } else if (v instanceof TextView){
            ((TextView) v).setTypeface(typeface);
        }
    }
}
