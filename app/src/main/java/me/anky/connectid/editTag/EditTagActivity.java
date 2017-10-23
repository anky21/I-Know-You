package me.anky.connectid.editTag;

import android.app.Service;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.anky.connectid.R;
import me.anky.connectid.Utilities;
import me.anky.connectid.data.ConnectionTag;
import me.anky.connectid.edit.EditActivity;
import me.anky.connectid.root.ConnectidApplication;

public class EditTagActivity extends AppCompatActivity implements EditTagActivityMVP.View, View.OnKeyListener {

    public List<ConnectionTag> allTags = new ArrayList<>();
    public Map<String, List<String>> allTagsHashmap = new HashMap<>();
    List<String> connectionTags = new ArrayList<>();
    String oldTags = "black,cool,white,US";

    final static int TAG_BASE_NUMBER = 1000;

    int mDatabaseId = -1;


    @BindView(R.id.all_tags)
    LinearLayout allTagsLinear;

    @BindView(R.id.search_tags_listview)
    ListView searchTagsLv;

    @BindView(R.id.add_tag_et)
    EditText addTagEt;

    @BindView(R.id.selected_tags)
    RelativeLayout selectedTagRl;

    String input = "";

    @Inject
    EditTagActivityPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);

        ButterKnife.bind(this);

        ((ConnectidApplication) getApplication()).getApplicationComponent().inject(this);

//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//
//        Log.v("testing", "screenWidth is " + screenWidth);

        Intent intent = getIntent();
        if (intent.hasExtra("data_id")){
            mDatabaseId = intent.getIntExtra("data_id", -1);
        }

        addTagEt.setOnKeyListener(this);
        addTagEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                Log.v("testing", charSequence + "");
                if (before == 0 && count == 1 && charSequence.charAt(start) == ',') {

//                    Log.v("testing", "hit comma");
                    addTagEt.getText().replace(start, start + 1, "");
                    input = addTagEt.getText().toString().trim();
                    presenter.createNewTag(input, connectionTags, allTags);
                    addTagEt.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = editable.toString().trim();
                if (input.contains(",")) {
                    addTagEt.getText().clear();
                    String modifiedInput = input.split(",")[0].trim();
                    input = modifiedInput;
                    presenter.createNewTag(input, connectionTags, allTags);

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
        presenter.loadTags();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_save:
                Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                if (mDatabaseId != -1){
                    presenter.updateConnectionTags(mDatabaseId, connectionTags);
                    presenter.updateTagTable(oldTags, allTags, connectionTags, mDatabaseId);
                }
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayAllTags(List<ConnectionTag> allTags) {
        // Clear all views
        allTagsLinear.removeAllViews();

        this.allTags = allTags;

        searchTagsLv.setVisibility(View.GONE);
        for (ConnectionTag tag : allTags) {
            String tagString = tag.getTag();

//            Log.v("testing", "tag is " + tag.getTag());
            boolean isSelectedTag = false;

            if (connectionTags != null && connectionTags.size() > 0) {
                for (String selectedTag : connectionTags) {
                    if (tag.getTag().equalsIgnoreCase(selectedTag)) {
                        isSelectedTag = true;
                        break;
                    }
                }
            }

            TextView tagTv = new TextView(this);
            tagTv.setText(tag.getTag());
            tagTv.setTextSize(14);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 2, 8, 2);
            tagTv.setLayoutParams(params);

            if(isSelectedTag){
                tagTv.setTextColor(ContextCompat.getColor(EditTagActivity.this, R.color.colorAccent));
                tagTv.setBackgroundResource(R.drawable.round_bg_blue);
            }else {
                tagTv.setBackgroundResource(R.drawable.round_bg_gray);
            }

            allTagsLinear.addView(tagTv);
        }
    }

    @Override
    public void displayNoTags() {
    }

    @Override
    public void displayError() {

    }

    @Override
    public void displayConnectionTags() {
        selectedTagRl.removeAllViews();
        int containerWidth = selectedTagRl.getMeasuredWidth() - 16;
        int i = 0;

        int count = 0;
        int currentWidth = 0;
        boolean isNewLine = false;
        boolean isFirstLine = true;

//        Log.v("testing", "container width is " + containerWidth);
        if (connectionTags.size() > 0) {
            for (final String tag : connectionTags) {
                //ToDo: Check if the new tag is already in "All Tags"
                if (allTags != null && allTags.size() > 0) {
                    for (ConnectionTag allTagsItem : allTags) {
                        if (allTagsItem.getTag().equalsIgnoreCase(tag)) {
                            // Refresh allTags
                            displayAllTags(allTags);
                        }
                    }
                }

//                Log.v("testing", "selected tag is " + tag);
                TextView tagTv = new TextView(this);
                tagTv.setId(TAG_BASE_NUMBER + i);
                tagTv.setText(tag);
                tagTv.setTextSize(14);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 4, 8, 4);
                tagTv.setLayoutParams(params);
                tagTv.setBackgroundResource(R.drawable.round_bg_blue);
                tagTv.setTextColor(ContextCompat.getColor(EditTagActivity.this, R.color.colorAccent));
                tagTv.setMaxLines(1);
                tagTv.measure(0, 0);

                tagTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedTagRl.removeView(view);
                        connectionTags.remove(tag);

                        // ToDo: Check if the tag is already in "All Tags"
                        if (allTags != null && allTags.size() > 0) {
                            for (ConnectionTag allTagsItem : allTags) {
                                if (allTagsItem.getTag().equalsIgnoreCase(tag)) {
                                    // Refresh allTags
                                    displayAllTags(allTags);
                                }
                            }
                        }
                        // Refresh the screen
                        displayConnectionTags();
                    }
                });

                int width = tagTv.getMeasuredWidth();
//                Log.v("testing", "width is " + tagTv.getMeasuredWidth());

                if (currentWidth + width <= containerWidth) {
                    currentWidth += width + 16;
                    isNewLine = false;
                    count++;
                } else {
                    currentWidth = width + 16;
                    isNewLine = true;
                    isFirstLine = false;
                    count = 1;
                }

                // Add TextView to the screen
                if (i == 0) {
                    params.addRule(RelativeLayout.ALIGN_START);
                    tagTv.setLayoutParams(params);
                    selectedTagRl.addView(tagTv);
                } else if (isNewLine) {
                    params.addRule(RelativeLayout.ALIGN_LEFT);
                    params.addRule(RelativeLayout.BELOW, TAG_BASE_NUMBER - 1 + i);
                    tagTv.setLayoutParams(params);
                    selectedTagRl.addView(tagTv);
                } else if (isFirstLine) {
                    params.addRule(RelativeLayout.RIGHT_OF, TAG_BASE_NUMBER - 1 + i);
                    tagTv.setLayoutParams(params);
                    selectedTagRl.addView(tagTv);
                } else {
                    params.addRule(RelativeLayout.RIGHT_OF, TAG_BASE_NUMBER - 1 + i);
                    params.addRule(RelativeLayout.BELOW, TAG_BASE_NUMBER - count + i);
                    tagTv.setLayoutParams(params);
                    selectedTagRl.addView(tagTv);
                }

                i++;
            }
        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent event) {
        if (i == KeyEvent.KEYCODE_COMMA || i == KeyEvent.KEYCODE_ENTER) {
//            Log.v("testing", "pressed key is " + i);
            input = addTagEt.getText().toString().trim();
            presenter.createNewTag(input, connectionTags, allTags);

            addTagEt.setText("");
            addTagEt.setFocusableInTouchMode(true);
            addTagEt.requestFocus();
            return true;
        }

        return false;
    }
}
