package youten.redo.musuroom.kanjisyo.json;

import android.content.res.Resources;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import youten.redo.musuroom.kanjisyo.R;

/**
 * 艦じしょ Dictionary
 */
public class KJDic {
    public List<String> groups = new ArrayList<String>();

    public List<List<KJItem>> dic = new ArrayList<List<KJItem>>();

    public List<String> search(String keyword) {
        List<String> ret = new ArrayList<String>();
        if (keyword == null) {
            return ret;
        }
        for (List<KJItem> group : dic) {
            for (KJItem item : group) {
                if (item.yomi.length > 0) {
                    for (String yomi1 : item.yomi) {
                        if (yomi1.indexOf(keyword) != -1) {
                            ret.add(item.kanji);
                            break;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * load dic.txt(json)
     *
     * @param resources Context#getResources();
     * @return 艦じしょ
     */
    public static final KJDic load(Resources resources) {
        if (resources == null) {
            return new KJDic(); // blank
        }
        InputStream is = resources.openRawResource(R.raw.dic);
        Gson gson = new Gson();
        KJDic ret = new KJDic();
        try {
            ret = (KJDic) gson.fromJson(new InputStreamReader(is, "UTF-8"),
                    KJDic.class);
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        return ret;
    }
}
