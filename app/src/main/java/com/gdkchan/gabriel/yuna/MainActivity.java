package com.gdkchan.gabriel.yuna;

import com.gdkchan.gabriel.yuna.YunaCore.*;
import com.gdkchan.gabriel.yuna.YunaCore.Youtube.*;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int CurrentPage;
    int PreviousItemCount;
    boolean SearchLoading;
    String SearchQuery;
    SearchListAdapter SearchAdapter; //Busca

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            CurrentPage = savedInstanceState.getInt("CurrentPage");
            PreviousItemCount = savedInstanceState.getInt("PreviousItemCount");
            SearchQuery = savedInstanceState.getString("SearchQuery");

            boolean HasItems = savedInstanceState.getBoolean("HasItems");
            if (HasItems) {
                ListView SearchList = (ListView) findViewById(R.id.searchList);
                SearchAdapter = new SearchListAdapter(this, new ArrayList<SearchItem>());
                SearchList.setAdapter(SearchAdapter);
                SearchAdapter.AddItems((SearchItem[]) savedInstanceState.getParcelableArray("SearchItems"));
                int Top = savedInstanceState.getInt("SearchScrollPos");
                int Offset = savedInstanceState.getInt("SearchScrollOfs");
                SearchList.setSelectionFromTop(Top, Offset);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("CurrentPage", CurrentPage);
        savedInstanceState.putInt("PreviousItemCount", PreviousItemCount);
        savedInstanceState.putString("SearchQuery", SearchQuery);

        boolean HasItems = PreviousItemCount > 0;
        savedInstanceState.putBoolean("HasItems", HasItems);
        if (HasItems) {
            ListView SearchList = (ListView) findViewById(R.id.searchList);
            savedInstanceState.putInt("SearchScrollPos", SearchList.getFirstVisiblePosition());
            View FirstItem = SearchList.getChildAt(0);
            savedInstanceState.putInt("SearchScrollOfs", FirstItem != null ? FirstItem.getTop() : 0);
            savedInstanceState.putParcelableArray("SearchItems", SearchAdapter.GetItems());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final Activity Context = this;
        MenuItem AboutItem = menu.findItem(R.id.action_about);
        AboutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder Dlg = new AlertDialog.Builder(Context);
                Dlg.setMessage("Feito por Gabriel Alencar.");
                Dlg.setTitle("Sobre");
                Dlg.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) { }
                        });
                Dlg.setCancelable(true);
                Dlg.create().show();
                return true;
            }
        });

        MenuItem SearchItem = menu.findItem(R.id.search);
        final SearchView Search = (SearchView) MenuItemCompat.getActionView(SearchItem);
        if (SearchQuery != null) Search.setQuery(SearchQuery, false);
        final ListView SearchList = (ListView) findViewById(R.id.searchList);

        SearchView.OnQueryTextListener SearchListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                CurrentPage = 1;
                PreviousItemCount = 0;
                SearchLoading = true;
                SearchQuery = query;
                SearchAdapter = new SearchListAdapter(Context, new ArrayList<SearchItem>());
                SearchList.setAdapter(SearchAdapter);
                new Search.GetResults(SearchAdapter, 1).execute(query);
                Toast.makeText(getApplicationContext(), getString(R.string.searching), Toast.LENGTH_LONG).show();
                return false;
            }
        };
        Search.setOnQueryTextListener(SearchListener);

        AbsListView.OnScrollListener ScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > 0) {
                    if (SearchLoading) {
                        if (totalItemCount > PreviousItemCount) {
                            SearchLoading = false;
                            PreviousItemCount = totalItemCount;
                            CurrentPage++;
                        }
                    } else {
                        if ((totalItemCount - visibleItemCount) * 0.9f <= firstVisibleItem + visibleItemCount) {
                            SearchLoading = true;
                            new Search.GetResults(SearchAdapter, CurrentPage).execute(SearchQuery);
                            Toast.makeText(getApplicationContext(), getString(R.string.searching_more), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        };

        SearchList.setOnScrollListener(ScrollListener);
        SearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                SearchItem Item = (SearchItem) SearchList.getItemAtPosition(position);
                Intent PlayerIntent = new Intent(getApplicationContext(), PlayerActivity.class);
                PlayerIntent.putExtra("VideoTitle", Item.Title);
                PlayerIntent.putExtra("VideoURL", Item.URL);
                startActivity(PlayerIntent);
            }
        });

        return true;
    }
}
