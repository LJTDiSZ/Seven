package com.jcc.seven;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by juyuan on 12/23/2015.
 */
public class TranslateTask implements Runnable {
    private static final String TAG = "TranslateTask";
    private final Translate translate;
    private final String original, from, to;

    TranslateTask(Translate translate, String original, String from, String to){
        this.translate = translate;
        this.original = original;
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        String trans = doTranslateBaidu(original, from, to);
        translate.setTranslated(trans);

        String retrans = doTranslateBaidu(trans, to, from);
        translate.setRetranslated(retrans);
    }

    private String doTranslate(String original, String from, String to){
        String result = translate.getResources().getString(R.string.translation_error);
        HttpURLConnection con = null;
        Log.d(TAG, "doTranslate(" + original + ", " + from + ", " + to + ")");

        try{
            //check if task has be interrupted
            if (Thread.interrupted())
                throw new InterruptedException();

            //build RESTful query for Google API
            String q = URLEncoder.encode(original, "UTF-8");
            URL url = new URL("https://ajax.googleapis.com/ajax/services/language/translate?v=1.0" + "&q=" + q + "&langpair=" + from + "%7C" + to);
            Log.d(TAG, "doTranslate(" + url.toString() + ")");

            con = (HttpURLConnection)url.openConnection();
            con.setReadTimeout(10000);//10 seconds
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.addRequestProperty("Referer", "http://www.pragprog.com/titles/eband3/hello-android");
            con.setDoInput(true);
            con.connect();

            if (Thread.interrupted())
                throw new InterruptedException();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String payload = reader.readLine();
            reader.close();

            JSONObject jsonObject = new JSONObject(payload);
            result = jsonObject.getJSONObject("responseData").getString("translatedText")
                    .replace("&#39;", "'")
                    .replace("&amp;", "&");

            if (Thread.interrupted())
                throw new InterruptedException();

        } catch (IOException e){
            Log.e(TAG, "IOException", e);
        } catch (JSONException e){
            Log.e(TAG, "JSONException", e);
        } catch (InterruptedException e){
            Log.d(TAG, "InterruptedException", e);
            result = translate.getResources().getString(R.string.translation_interrupted);
        } finally {
            if (con != null)
                con.disconnect();
        }

        Log.d(TAG, "    -> returned " + result);
        return result;
    }

    private String doTranslateBaidu(String original, String from, String to){
        String result = translate.getResources().getString(R.string.translation_error);
        HttpURLConnection con = null;
        Log.d(TAG, "doTranslateBaidu(" + original + ", " + from  + ", " + to + ")");

        try{
            //check if task has be interrupted
            if (Thread.interrupted())
                throw new InterruptedException();

            String appID = "20151227000008306";
            String appKey = "fRUyVXKNykEGLT9qo6iS";
            int salt = new Random().nextInt(100000000);
            String sign = appID + original + salt + appKey;
            Log.d(TAG, "salt=" + salt + ", sign=" + sign);
            //sign = MD5.getMD5x32(URLEncoder.encode(sign, "UTF-8"));
            sign = MD5.getMD5x32(sign);

            //build RESTful query for Google API
            String q = URLEncoder.encode(original, "UTF-8");
            URL url = new URL("http://api.fanyi.baidu.com/api/trans/vip/translate?q=" + q
                    + "&from=" + from + "&to=" + to
                    + "&appid=" + appID + "&salt=" + salt
                    + "&sign=" + sign
            );
            Log.d(TAG, "doTranslateBaidu(" + url.toString() + ")");

            con = (HttpURLConnection)url.openConnection();
            con.setReadTimeout(10000);//10 seconds
            con.setConnectTimeout(15000);
            con.setRequestMethod("GET");
            con.addRequestProperty("Referer", "http://www.pragprog.com/titles/eband3/hello-android");
            con.setDoInput(true);
            con.connect();

            if (Thread.interrupted())
                throw new InterruptedException();

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String payload = reader.readLine();
            reader.close();

            Log.d(TAG, "payload=" + payload);
            //{"from":"en","to":"zh","trans_result":[{"src":"apple","dst":"\u82f9\u679c"}]}

            JSONObject jsonObject = new JSONObject(payload);
            result = jsonObject.getJSONArray("trans_result").getJSONObject(0).getString("dst")
                    .replace("&#39;", "'")
                    .replace("&amp;", "&");

            if (Thread.interrupted())
                throw new InterruptedException();

        } catch (IOException e){
            Log.e(TAG, "IOException", e);
        } catch (JSONException e){
            Log.e(TAG, "JSONException", e);
        } catch (InterruptedException e){
            Log.d(TAG, "InterruptedException", e);
            result = translate.getResources().getString(R.string.translation_interrupted);
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
        } finally {
            if (con != null)
                con.disconnect();
        }

        Log.d(TAG, "    -> doTranslateBaidu returned " + result);
        return result;
    }
}

class MD5 {

    public static byte[] getMD5(String val) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();
            return m;
        }catch (Exception e){
            System.out.println("NoSuchAlgorithmException caught!");
            return null;
        }
    }
    private static String getString(byte[] b){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i ++){
            sb.append(b[i]);
        }
        return sb.toString();
    }
    public static String getMD5x32(String val) {
        byte[] b = getMD5(val);

        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }

        return buf.toString();
    }
}
