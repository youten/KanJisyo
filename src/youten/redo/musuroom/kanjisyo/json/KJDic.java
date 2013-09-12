package youten.redo.musuroom.kanjisyo.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import youten.redo.musuroom.kanjisyo.R;

import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * 艦じしょ<br>
 * "指令", "駆逐艦", "軽巡洋艦", "重雷装巡洋艦", "重巡洋艦", "航空巡洋艦", "戦艦", "航空戦艦", "水上機母艦", "軽空母",
 * "正規空母", "潜水艦"
 */
public class KJDic {
	@SerializedName("groups")
	public List<String> groups = new ArrayList<String>();

	@SerializedName("dic")
	public List<List<KJItem>> dic = new ArrayList<List<KJItem>>();

	public List<String> search(String keyword) {
		List<String> ret = new ArrayList<String>();
		if (keyword != null) {
			for (List<KJItem> group : dic) {
				for (KJItem item : group) {
					if ((item.yomi.indexOf(keyword) != -1)
							|| (item.yomi2.indexOf(keyword) != -1)) {
						ret.add(item.kanji);
					}
				}
			}
		}

		return ret;
	}

	/**
	 * load dic.txt(json)
	 * 
	 * @param resources
	 *            Context#getResources();
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
