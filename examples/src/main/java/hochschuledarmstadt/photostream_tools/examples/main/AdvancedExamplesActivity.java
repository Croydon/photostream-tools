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

package hochschuledarmstadt.photostream_tools.examples.main;


import android.os.Bundle;
import android.support.annotation.Nullable;

import hochschuledarmstadt.photostream_tools.examples.advanced_examples.fullscreen.FullscreenActivityViewPager;
import hochschuledarmstadt.photostream_tools.examples.advanced_examples.viewpager.ViewPagerFragmentActivity;
import hochschuledarmstadt.photostream_tools.examples.advanced_examples.viewpager.ViewPagerWithoutFragmentsActivity;

/**
 * Menüanzeige für erweiterte Beispiele
 */
public class AdvancedExamplesActivity extends MenuActivity {

    private static final String MENU_VIEW_PAGER = "Photos anzeigen in einem ViewPager Widget (mit Fragmente)";
    private static final String MENU_VIEW_PAGER_LAYOUT = "Photos anzeigen in einem ViewPager Widget (ohne Fragmente)";
    private static final String MENU_VIEW_PAGER_FULLSCREEN_LAYOUT = "Viewpager Vollbild";

    private static final MenuItem[] menu = new MenuItem[]{
            new MenuItem(MENU_VIEW_PAGER, ViewPagerFragmentActivity.class),
            new MenuItem(MENU_VIEW_PAGER_LAYOUT, ViewPagerWithoutFragmentsActivity.class),
            new MenuItem(MENU_VIEW_PAGER_FULLSCREEN_LAYOUT, FullscreenActivityViewPager.class),
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize(menu);
    }
}
