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

package hochschuledarmstadt.photostream_tools.examples;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import hochschuledarmstadt.photostream_tools.PhotoStreamActivity;
import hochschuledarmstadt.photostream_tools.adapter.DividerItemDecoration;
import hochschuledarmstadt.photostream_tools.examples.comment.CommentActivity;
import hochschuledarmstadt.photostream_tools.examples.plugin.AlertDialogPluginActivity;
import hochschuledarmstadt.photostream_tools.examples.plugin.ContextualActionBarPluginActivity;
import hochschuledarmstadt.photostream_tools.examples.notification.NotificationActivity;
import hochschuledarmstadt.photostream_tools.examples.photo.PhotoActivity;
import hochschuledarmstadt.photostream_tools.examples.search.SearchActivity;
import hochschuledarmstadt.photostream_tools.examples.upload.PhotoUploadActivity;
import hochschuledarmstadt.photostream_tools.examples.viewpager.ViewPagerActivity;

/**
 * Nur ein Auswahlmenü für die Anzeige der einzelnen Beispielactivities.
 */
public class MenuActivity extends AppCompatActivity {

    private static final String MENU_PHOTOS = "Photos anzeigen";
    private static final String MENU_COMMENTS = "Kommentare anzeigen";
    private static final String MENU_UPLOAD_PHOTO = "Neues Photo hochladen";
    private static final String MENU_SEARCH = "Suche nach Photos anhand Beschreibung";
    private static final String MENU_VIEW_PAGER = "Photos anzeigen in einem ViewPager Widget";
    private static final String MENU_CAB_EXTENSION = "Beispiel für Contextual ActionBar Plugin";
    private static final String MENU_DIALOG_EXTENSION = "Beispiel für AlertDialog Plugin";
    private static final String MENU_BROADCAST_RECEIVER = "Broadcast Receiver Beispiel";

    private static final MenuItem[] menu = new MenuItem[]{
            new MenuItem(MENU_PHOTOS, PhotoActivity.class),
            new MenuItem(MENU_CAB_EXTENSION, ContextualActionBarPluginActivity.class),
            new MenuItem(MENU_DIALOG_EXTENSION, AlertDialogPluginActivity.class),
            new MenuItem(MENU_COMMENTS, CommentActivity.class),
            new MenuItem(MENU_UPLOAD_PHOTO, PhotoUploadActivity.class),
            new MenuItem(MENU_SEARCH, SearchActivity.class),
            new MenuItem(MENU_VIEW_PAGER, ViewPagerActivity.class),
            new MenuItem(MENU_BROADCAST_RECEIVER, NotificationActivity.class)
    };

    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        menuAdapter = new MenuAdapter(this, Arrays.asList(menu), new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                final MenuItem menuItem = menuAdapter.getItemAtPosition(position);
                Class<? extends PhotoStreamActivity> clazz = menuItem.getActivityClass();
                Intent intent = new Intent(MenuActivity.this, clazz);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(menuAdapter);
    }

    private static class MenuItem {

        private final Class<? extends PhotoStreamActivity> activityClass;
        private final String title;

        MenuItem(String title, Class<? extends PhotoStreamActivity> activityClass){
            this.title = title;
            this.activityClass = activityClass;
        }

        public String getTitle() {
            return title;
        }

        public Class<? extends PhotoStreamActivity> getActivityClass() {
            return activityClass;
        }
    }

    private static class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder>{

        private final List<MenuItem> menu;
        private final Context context;
        private final OnItemClickListener itemClickListener;

        public MenuAdapter(Context context, List<MenuItem> menu, OnItemClickListener itemClickListener){
            this.context = context;
            this.menu = menu;
            this.itemClickListener = itemClickListener;
        }

        @Override
        public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MenuViewHolder(LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false), itemClickListener);
        }

        @Override
        public void onBindViewHolder(MenuViewHolder holder, int position) {
            holder.textView.setText(menu.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return menu.size();
        }

        public MenuItem getItemAtPosition(int position) {
            return menu.get(position);
        }

        static class MenuViewHolder extends RecyclerView.ViewHolder{
            public TextView textView;
            public MenuViewHolder(View itemView, final OnItemClickListener itemClickListener) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.textView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }

        public interface OnItemClickListener{
            void onItemClick(int position);
        }

    }

}
