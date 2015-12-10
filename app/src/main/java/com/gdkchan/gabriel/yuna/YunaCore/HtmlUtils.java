package com.gdkchan.gabriel.yuna.YunaCore;

import java.util.*;

/**
 * Created by gabriel on 06/10/2015.
 */
public class HtmlUtils {
    public static String GetTagContent(String HTML, String TagName, String Parameter) {
        if (HTML == null) return null;

        int ContentIndex = -1;
        int StartIndex = 0;
        int OpenTags = 0;
        for (;;)
        {
            int PositionOpen = HTML.indexOf("<" + TagName, StartIndex);
            int PositionClose = HTML.indexOf("</" + TagName, StartIndex);
            int Position = Math.min(PositionOpen, PositionClose);
            if (Position == -1 && PositionClose > -1) Position = PositionClose; else if (Position == -1) break;
            int TagEnd = HTML.indexOf(">", Position + TagName.length() + 1);
            StartIndex = TagEnd + 1;
            if (Position == PositionOpen) {
                if (!(HTML.substring(TagEnd - 1, TagEnd) == "/")) {
                    if (ContentIndex == -1) {
                        if (Parameter != null) {
                            int ParametersStart = Position + TagName.length() + 2;
                            if (ParametersStart < TagEnd) {
                                String Parameters = HTML.substring(ParametersStart, TagEnd);
                                if (Parameters.contains(Parameter)) ContentIndex = StartIndex;
                            }
                        }
                        else
                            ContentIndex = StartIndex;
                    }
                    if (ContentIndex > -1) OpenTags++;
                }
            }
            else
                if (ContentIndex > -1) OpenTags--;

            if (ContentIndex > -1 && OpenTags == 0) return HTML.substring(ContentIndex, Position);
        }

        return null;
    }

    public static String GetTagContent(String HTML, String TagName) {
        return GetTagContent(HTML, TagName, null);
    }

    public static String[] GetTagsContent(String HTML, String TagName, String Parameter) {
        if (HTML == null) return null;

        List<String> Output = new ArrayList<String>();

        int ContentIndex = -1;
        int StartIndex = 0;
        int OpenTags = 0;
        for (;;) {
            if (StartIndex >= HTML.length()) break;

            int PositionOpen = HTML.indexOf("<" + TagName, StartIndex);
            int PositionClose = HTML.indexOf("</" + TagName, StartIndex);
            int Position = Math.min(PositionOpen, PositionClose);
            if (Position == -1 && PositionClose > -1) Position = PositionClose; else if (Position == -1) break;
            int TagEnd = HTML.indexOf(">", Position + TagName.length() + 1);
            StartIndex = TagEnd + 1;
            if (Position == PositionOpen) {
                if (!(HTML.substring(TagEnd - 1, TagEnd) == "/")) {
                    if (ContentIndex == -1) {
                        if (Parameter != null) {
                            int ParametersStart = Position + TagName.length() + 2;
                            if (ParametersStart < TagEnd) {
                                String Parameters = HTML.substring(ParametersStart, TagEnd);
                                if (Parameters.contains(Parameter)) ContentIndex = StartIndex;
                            }
                        }
                        else
                            ContentIndex = StartIndex;
                    }
                    if (ContentIndex > -1) OpenTags++;
                }
            }
            else
                if (ContentIndex > -1) OpenTags--;

            if (ContentIndex > -1 && OpenTags == 0) {
                Output.add(HTML.substring(ContentIndex, Position));
                ContentIndex = -1;
            }
        }

        String[] StrArr = new String[Output.size()];
        StrArr = Output.toArray(StrArr);
        return StrArr;
    }

    public static String[] GetTagsContent(String HTML, String TagName) {
        return GetTagsContent(HTML, TagName, null);
    }

    public static String GetTagParameterContent(String HTML, String TagName, String Parameter, String ExtraParameters) {
        if (HTML == null) return null;

        int StartIndex = 0;
        for (;;) {
            if (StartIndex >= HTML.length()) break;

            int Position = HTML.indexOf("<" + TagName, StartIndex);
            if (Position == -1) break;
            int TagEnd = HTML.indexOf(">", Position + TagName.length() + 1);
            StartIndex = TagEnd + 1;

            int ParametersStart = Position + TagName.length() + 2;
            if (ParametersStart < TagEnd) {
                String Parameters = HTML.substring(ParametersStart, TagEnd);
                if (Parameters.contains(Parameter) && (ExtraParameters == null || Parameters.contains(ExtraParameters))) {
                    int ParameterIndex = HTML.indexOf(Parameter, Position + TagName.length() + 1);
                    int ValStart = HTML.indexOf("=\"", ParameterIndex + Parameter.length());
                    if (ValStart > -1) {
                        ValStart += 2;
                        int ValEnd = HTML.indexOf("\"", ValStart);
                        if (ValEnd > -1) return HTML.substring(ValStart, ValEnd);
                    }
                }
            }
        }

        return null;
    }

    public static String GetTagParameterContent(String HTML, String TagName, String Parameter) {
        return GetTagParameterContent(HTML, TagName, Parameter, null);
    }
}
