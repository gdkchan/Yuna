package com.gdkchan.gabriel.yuna.YunaCore.Youtube;

import com.gdkchan.gabriel.yuna.YunaCore.*;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.VideoView;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gabriel on 08/10/2015.
 */
public class VideoStream {
    public static class GetStreams extends AsyncTask<String, Void, List<StreamInfo>> {
        VideoView Player;
        StreamInfo Stream;

        public GetStreams(VideoView Player, StreamInfo Stream) {
            this.Player = Player;
            this.Stream = Stream;
        }

        protected List<StreamInfo> doInBackground(String... URLs) {
            List<StreamInfo> Output = new ArrayList<StreamInfo>();

            String URL = URLs[0];
            String HTML = null;
            try {
                HTML = HttpUtils.Get(URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String PlayerURL = HtmlUtils.GetTagParameterContent(HTML, "script", "src", "player/base");

            if (PlayerURL.startsWith("//"))
                PlayerURL = "https:" + PlayerURL;
            else
                PlayerURL = "https://www.youtube.com" + PlayerURL;

            //Deixa apenas a parte com os links dos vídeos
            Matcher DataBlockMatch = Pattern.compile("(?s)\"url_encoded_fmt_stream_map\":(?:\\s+)?\"(.+?)\"").matcher(HTML);
            boolean UseAPI = !DataBlockMatch.find();
            HTML = DataBlockMatch.group(1);

            if (UseAPI)
            {
                //Não achou nenhuma URL, o vídeo deve ter proteção de idade...
                //Nesse caso, podemos usar este outro método
                //OBS: Não está funcionando p/ videos VEVO c/ proteção de idade, a assinatura está saindo errada
                URL = String.format("https://www.youtube.com/get_video_info?video_id=%1$s&el=player_embedded&eurl=https://youtube.googleapis.com/v/%1$s&asv=3&sts=1588", Utils.IdFromUrl(URL));
                try {
                    HTML = HttpUtils.Get(URL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                HTML = HTML.substring(HTML.indexOf("&url_encoded_fmt_stream_map=") + 28);
            }

            //Decodifica URLs
            try {
                HTML = URLDecoder.decode(HTML, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Extração de URLs e assinaturas
            String Delimiter = UseAPI ? "&" : "\\\\u0026";
            Matcher Streams = Pattern.compile(String.format("(?:^|%1$s|,)url=(.+?)(?:$|%1$s|,)", Delimiter)).matcher(HTML);
            Matcher ITags = Pattern.compile(String.format("(?:^|%1$s|,)itag=(.+?)(?:$|%1$s|,)", Delimiter)).matcher(HTML);
            Matcher Signatures = Pattern.compile(String.format("(?:^|%1$s|,)s=(.+?)(?:$|%1$s|,)", Delimiter)).matcher(HTML);

            //Extrai e monta as URLs
            String PlayerCode = null;
            while (Streams.find()) {
                String StreamURL = Streams.group(1);
                if (Signatures.find())
                {
                    if (PlayerCode == null) {
                        try {
                            PlayerCode = HttpUtils.Get(PlayerURL);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    StreamURL += "&signature=" + SignatureDecipher(Signatures.group(1), PlayerCode);
                }

                StreamInfo.StreamQuality Quality = StreamInfo.StreamQuality.Unknown;
                if (ITags.find()) {
                    switch (Integer.parseInt(ITags.group(1))) {
                        case 33: case 136: Quality = StreamInfo.StreamQuality.q240p; break;
                        case 35: case 44: case 83: Quality = StreamInfo.StreamQuality.q480p; break;
                        case 22: case 84: Quality = StreamInfo.StreamQuality.q720p; break;
                        case 37: case 85: Quality = StreamInfo.StreamQuality.q1080p; break;
                        case 38: Quality = StreamInfo.StreamQuality.q4k; break;
                    }
                }

                Output.add(new StreamInfo(StreamURL, Quality));
            };

            return Output;
        }

        protected void onPostExecute(List<StreamInfo> Result) {
            Stream.URL = Result.get(0).URL;
            Player.setVideoURI(Uri.parse(Stream.URL));
            Player.start();
        }
    }

    private static String SignatureDecipher(String Signature, String Code) {
        Matcher FunctionNameMatch = Pattern.compile("\\(\"signature\",([\\w\\$]+)\\(\\w+\\)\\);").matcher(Code);
        FunctionNameMatch.find();
        String FunctionName = FunctionNameMatch.group(1);
        FunctionName = FunctionName.replace("$", "\\$");

        Matcher FunctionMatch = Pattern.compile(String.format("(?s)%s=function\\((\\w+)\\)\\{(.+?)\\}", FunctionName)).matcher(Code);
        FunctionMatch.find();
        String Var = FunctionMatch.group(1);
        String FunctionCode = FunctionMatch.group(2);
        String[] Lines = FunctionCode.split(";");

        Matcher ReverseFunc = Pattern.compile(String.format("(\\w{2}):function\\(%1$s\\)\\{%1$s\\.reverse\\(\\)\\}", Var)).matcher(Code);
        Matcher SpliceFunc = Pattern.compile(String.format("(\\w{2}):function\\(%1$s,\\w\\)\\{%1$s\\.splice\\(0,\\w\\)\\}", Var)).matcher(Code);
        Matcher SwapFunc = Pattern.compile(String.format("(\\w{2}):function\\(%1$s,\\w\\)\\{var \\w=%1$s\\[0\\](.+?)\\}", Var)).matcher(Code);

        ReverseFunc.find();
        SpliceFunc.find();
        SwapFunc.find();

        for (int i = 0; i < Lines.length; i++)
        {
            String Line = Lines[i].trim();
            if (Line.contains(".split") || Line.contains(".join")) continue;

            //Comandos: Split*, Reverse, Slice, Swap, Join*
            //* Apenas necessário no código JavaScript
            if (Line.contains(ReverseFunc.group(1))) Signature = Reverse(Signature); //Reverse
            else if (Line.contains(SpliceFunc.group(1))) { //Slice
                Matcher SpliceVal = Pattern.compile(String.format("\\(%s,(\\d+)\\)", Var)).matcher(Line);
                SpliceVal.find();
                Signature = Slice(Signature, Integer.parseInt(SpliceVal.group(1)));
            }
            else if (Line.contains(SwapFunc.group(1))) { //Swap
                Matcher SwapVal = Pattern.compile(String.format("\\(%s,(\\d+)\\)", Var)).matcher(Line);
                SwapVal.find();
                Signature = Swap(Signature, Integer.parseInt(SwapVal.group(1)));
            }
        }

        return Signature;
    }

    private static String Reverse(String Input) {
        return new StringBuilder(Input).reverse().toString();
    }

    private static String Slice(String Input, int Length) {
        return Input.substring(Length);
    }

    private static String Swap(String Input, int Position) {
        StringBuilder Str = new StringBuilder(Input);
        Position = Position % Str.length();
        char SwapChar = Str.charAt(Position);
        Str.setCharAt(Position, Str.charAt(0));
        Str.setCharAt(0, SwapChar);
        return Str.toString();
    }
}
