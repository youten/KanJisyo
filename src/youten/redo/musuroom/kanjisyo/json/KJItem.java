
package youten.redo.musuroom.kanjisyo.json;

import com.google.gson.annotations.SerializedName;

public class KJItem {
    @SerializedName("group")
    public boolean group = false;

    @SerializedName("kanji")
    public String kanji = "";

    @SerializedName("yomi")
    public String yomi = "";

    @SerializedName("desc")
    public String desc = "";
}