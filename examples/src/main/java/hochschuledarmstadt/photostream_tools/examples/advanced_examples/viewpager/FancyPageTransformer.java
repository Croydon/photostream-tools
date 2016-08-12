/*
 * The MIT License
 *
 * Copyright (c) 2016 Andreas Schattney
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hochschuledarmstadt.photostream_tools.examples.advanced_examples.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import hochschuledarmstadt.photostream_tools.examples.R;

public class FancyPageTransformer implements ViewPager.PageTransformer {

    /**
     * Konstanten für die Animation zum schnellen Modifizieren
     */
    private static final float SCALE_FACTOR_SLIDE = 0.85f;
    private static final double MIN_ALPHA_SLIDE = 0.0f;
    private static final double MAX_ALPHA_SLIDE = 1.0f;
    public static final double ALPHA_VELOCITY = 0.75;

    @Override
    public void transformPage(View page, float position) {
        float scale;
        double alpha;
        float translationX;
        if (position > 0 && position < 1) {
            scale = Math.abs(Math.abs(position) - 1) * (1.0f - SCALE_FACTOR_SLIDE) + SCALE_FACTOR_SLIDE;
            alpha = Math.max(MIN_ALPHA_SLIDE, 1 - Math.abs(position));
            int pageWidth = page.getWidth();
            float translateValue = position * -pageWidth;
            if (translateValue > -pageWidth) {
                translationX = translateValue;
            } else {
                translationX = 0;
            }
            ImageView imageView = (ImageView) page.findViewById(R.id.imageView);
            imageView.setScaleX(scale);
            imageView.setScaleY(scale);
            imageView.setAlpha((float) alpha);
        } else {
            alpha = Math.min(MAX_ALPHA_SLIDE, 1 - (Math.abs(position) * ALPHA_VELOCITY));
            scale = 1;
            translationX = 0;
        }
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setTranslationX(translationX);
        page.setAlpha((float) alpha);
    }
}
