
package youten.redo.musuroom.kanjisyo;

import java.util.ArrayList;
import java.util.List;

import youten.redo.musuroom.kanjisyo.json.KJDic;
import youten.redo.musuroom.kanjisyo.json.KJItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "Kanjisyo:Main";
    private static final String ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";
    private static final String REPLACE_KEY = "replace_key";

    private Button mUpButton;
    private TextView mTitleTextView;
    private KJDic mDic;
    private boolean mIsFromSimeji;
    private List<String> mSelectItemList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        // check whether startActivity from simeji
        String action = getIntent().getAction();
        if ((action != null) && ACTION_INTERCEPT.equals(action)) {
            mIsFromSimeji = true;
        } else {
            mIsFromSimeji = false;
        }

        // Up Button
        mUpButton = (Button) findViewById(R.id.main_up);
        mUpButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                resetListView();
            }
        });

        // title TextView
        mTitleTextView = (TextView) findViewById(R.id.main_title);

        // load Dictionary
        mDic = KJDic.load(getResources());

        // 検索結果モード
        if (mIsFromSimeji) {
            String keyword = getIntent().getStringExtra(REPLACE_KEY);
            if ((keyword != null) && (keyword.length() > 0)) {
                List<String> result = mDic.search(keyword);
                if (result.size() > 0) {
                    initSearchResultListView(keyword, result);
                    return;
                }
            }
        }

        // 通常モード
        // ListView
        resetListView();
    }

    /**
     * 通常モード
     */
    private void resetListView() {
        mSelectItemList.clear();
        mSelectItemList.addAll(mDic.groups);
        mTitleTextView.setText(R.string.app_name);
        mUpButton.setVisibility(View.INVISIBLE);
        mUpButton.setEnabled(false);
        ListView mainListView = (ListView) findViewById(R.id.main_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mSelectItemList);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mUpButton.isEnabled()) {
                    finishSimeji(mSelectItemList.get(position));
                } else {
                    @SuppressWarnings("unchecked")
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                    adapter.clear();
                    if (mDic.dic.size() > position) {
                        for (KJItem item : mDic.dic.get(position)) {
                            if (item.group) {
                                String title = mTitleTextView.getText().toString();
                                mTitleTextView.setText(title + " " + item.kanji);
                            } else {
                                adapter.add(item.kanji);
                            }
                        }
                    }

                    mUpButton.setVisibility(View.VISIBLE);
                    mUpButton.setEnabled(true);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 検索結果モード
     */
    private void initSearchResultListView(String keyword, List<String> result) {
        mSelectItemList.clear();
        mSelectItemList.addAll(result);
        String appName = getResources().getString(R.string.app_name);
        String searchResult = getResources().getString(R.string.search_result);
        mTitleTextView.setText(appName + " " + searchResult + keyword);
        mUpButton.setVisibility(View.GONE);
        mUpButton.setEnabled(false);
        ListView mainListView = (ListView) findViewById(R.id.main_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mSelectItemList);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                finishSimeji(mSelectItemList.get(position));
            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void finishSimeji(String afterReplace) {
        if (mIsFromSimeji) {
            // return extra
            Intent data = new Intent();
            data.putExtra(REPLACE_KEY, afterReplace);
            setResult(RESULT_OK, data);
        } else {
            // copy to Clipboard
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ClipData.Item item = new ClipData.Item(afterReplace);
                String[] mimeType = new String[1];
                mimeType[0] = ClipDescription.MIMETYPE_TEXT_PLAIN;
                ClipData cd = new ClipData(new ClipDescription("text_data", mimeType), item);
                android.content.ClipboardManager cm = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(cd);
            } else {
                android.text.ClipboardManager cm = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(afterReplace);
            }
            Toast.makeText(this, getResources().getString(R.string.prefix_clip) + afterReplace,
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
