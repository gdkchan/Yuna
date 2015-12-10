package com.gdkchan.gabriel.yuna;

import com.gdkchan.gabriel.yuna.YunaCore.SearchItem;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.*;
import android.widget.*;

import java.util.List;

/**
 * Created by gabriel on 07/10/2015.
 */
public class SearchListAdapter extends ArrayAdapter<SearchItem>  {
    Activity Context;
    List<SearchItem> Items;

    public SearchListAdapter(Activity Context, List<SearchItem> Items) {
        super(Context, R.layout.search_list_item, Items);

        this.Context = Context;
        this.Items = Items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater Inflater = Context.getLayoutInflater();
            convertView = Inflater.inflate(R.layout.search_list_item, null, true);
        }

        ImageView VideoThumb = (ImageView) convertView.findViewById(R.id.thumbnail);
        TextView VideoThumbText = (TextView) convertView.findViewById(R.id.thumbText);
        TextView VideoTitle = (TextView) convertView.findViewById(R.id.title);
        TextView VideoDescription = (TextView) convertView.findViewById(R.id.description);
        TextView VideoAuthor = (TextView) convertView.findViewById(R.id.author);

        SearchItem Item = Items.get(position);
        if (Item.Thumbnail != null) VideoThumb.setImageBitmap(Item.Thumbnail);
        VideoThumbText.setText(Item.Duration);
        VideoTitle.setText(TextFromHtml(Item.Title));
        VideoDescription.setText(TextFromHtml(Item.Description));
        VideoAuthor.setText(TextFromHtml(Item.Author));

        return convertView;
    }

    private String TextFromHtml(String Text) {
        if (Text != null)
            return Html.fromHtml(Text).toString();
        else
            return "";
    }

    public void AddItems(List<SearchItem> Items) {
        this.Items.addAll(Items);
        this.notifyDataSetChanged();
    }

    public void AddItems(SearchItem[] Items) {
        for (int i = 0; i < Items.length; i++) this.Items.add(Items[i]);
        this.notifyDataSetChanged();
    }

    public SearchItem[] GetItems() {
        SearchItem[] Output;
        Output = new SearchItem[Items.size()];
        Output = Items.toArray(Output);
        return Output;
    }
}
