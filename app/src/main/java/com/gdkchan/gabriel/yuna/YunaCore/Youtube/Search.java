package com.gdkchan.gabriel.yuna.YunaCore.Youtube;

import com.gdkchan.gabriel.yuna.SearchListAdapter;
import com.gdkchan.gabriel.yuna.YunaCore.*;

import android.os.AsyncTask;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by gabriel on 06/10/2015.
 */
public class Search
{
    public static class GetResults extends AsyncTask<String, Void, List<SearchItem>> {
        SearchListAdapter Adapter;
        int Page;

        public GetResults(SearchListAdapter Adapter, int Page) {
            this.Adapter = Adapter;
            this.Page = Page;
        }

        protected List<SearchItem> doInBackground(String... Query) {
            String HTML = null;
            try {
                String SearchURL = String.format("https://www.youtube.com/results?search_query=%s&page=%s", URLEncoder.encode(Query[0], "UTF-8"), Page);
                HTML = HttpUtils.Get(SearchURL);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String SearchBlock = HtmlUtils.GetTagContent(HTML, "ol", "item-section");
            String[] SearchResultBlocks = HtmlUtils.GetTagsContent(SearchBlock, "li");

            List<SearchItem> Output = new ArrayList<SearchItem>();
            if (SearchResultBlocks != null) {
                for (String SearchResult : SearchResultBlocks) {
                    if (SearchResult.contains("yt-lockup-content")) {
                        String URL = "https://www.youtube.com" + HtmlUtils.GetTagParameterContent(SearchResult, "a", "href");
                        String Title = HtmlUtils.GetTagContent(HtmlUtils.GetTagContent(SearchResult, "h3", "yt-lockup-title"), "a");
                        String Description = HtmlUtils.GetTagContent(SearchResult, "div", "yt-lockup-description");
                        String Uploader = HtmlUtils.GetTagContent(HtmlUtils.GetTagContent(SearchResult, "div", "yt-lockup-byline"), "a");
                        String Duration = HtmlUtils.GetTagContent(SearchResult, "span", "video-time");
                        String ThumbURL = HtmlUtils.GetTagParameterContent(SearchResult, "img", "data-thumb");
                        if (ThumbURL == null) ThumbURL = HtmlUtils.GetTagParameterContent(SearchResult, "img", "src");
                        if (!ThumbURL.startsWith("http")) ThumbURL = "https:" + ThumbURL;

                        Output.add(new SearchItem(Title, Description, Uploader, Duration, URL, ThumbURL));
                    }
                }
            }

            return Output;
        }

        protected void onPostExecute(List<SearchItem> Result) {
            Adapter.AddItems(Result);
        }
    }
}