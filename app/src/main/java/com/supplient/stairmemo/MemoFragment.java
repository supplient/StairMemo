package com.supplient.stairmemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.supplient.stairmemo.DefaultOptions.defaultPriority;
import static com.supplient.stairmemo.DefaultOptions.maxMeanings;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemoFragment extends Fragment {
    // TODO: 将MemoFragment显示的那一部分美化掉（可以拖到最后（打死
    // TODO: 把getChildAt改成用id的
    // TODO: 研究Order怎么算
    // TODO: 美化界面，优化UI
    // TODO: 着手制作文档

    // Vars for alterable Options Parameters
    public int meaningPadding = 20;


    // Vars for convenience
    private View view;
    private LinearLayout memoList;
    private MemoBook memoBook;

    // Vars for Mode control
    private boolean multiMode;

    // Constant ENUMS
    // Request Code
    static final public int ADD_WORD = 128;
    static final public int EDIT_WORD = 129;
    // Result Code
    static final public int RESULT_OK = 1;
    static final public int RESULT_CANCEL = 2;
    // Data Name
    static final public String DATA_WORD_INDEX = "com.supplient.stairmemo.data_word_index";
    static final public String DATA_MEANINGS = "com.supplient.stairmemo.data_meanings";
    static final public String DATA_PRIORITY = "com.supplient.stairmemo.data_priority";

    // Useless but Necessary Constructor
    public MemoFragment() {
        // Required empty public constructor
    }

    // Create Fragment's view and Options' Menu
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Init vars for convenience
        view = inflater.inflate(R.layout.fragment_memo, container, false);
        memoList = (LinearLayout)view.findViewById(R.id.list_memo);
        memoBook = MemoBook.GetInstance(getActivity().getFilesDir());

        // Init floating action button
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start AddActivity
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivityForResult(intent, ADD_WORD);
            }
        });

        // Fill Data Views
        InitList(memoList);

        // Init modes
        multiMode = false;

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.memo_menu, menu);
    }

    // Options' Menu Logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId())
        {
            case R.id.del_memo_menu:
                OnDelClicked();
                return true;
            default:
                return false;
        }
    }

    private void OnDelClicked() {
        // Only work in multiMode
        if(!multiMode)
            return;

        for(int i=0;i<memoList.getChildCount();i++)
        {
            LinearLayout wordLayout =(LinearLayout) memoList.getChildAt(i);
            CheckBox checkbox =(CheckBox) wordLayout.getChildAt(1);
            if(checkbox.isChecked())
            {
                memoList.removeView(wordLayout);
                memoBook.remove(i);
                i--;
            }
        }

        CloseMultiMode();
    }

    // Resolve Activity's Results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case ADD_WORD:
                onAddResult(requestCode, resultCode, data);
                break;
            case EDIT_WORD:
                onEditResult(requestCode, resultCode, data);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAddResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCEL)
            return;

        ArrayList<String> list = data.getStringArrayListExtra(DATA_MEANINGS);
        int priority = data.getIntExtra(DATA_PRIORITY, defaultPriority);

        AddWord(list, priority, true);
    }

    private void onEditResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_CANCEL)
            return;

        int index = data.getIntExtra(DATA_WORD_INDEX, -1);
        ArrayList<String> list = data.getStringArrayListExtra(DATA_MEANINGS);
        int priority = data.getIntExtra(DATA_PRIORITY, defaultPriority);

        if(index<0)
            throw new ArrayIndexOutOfBoundsException();

        memoBook.remove(index);
        AddWord(list, priority, true);
    }

    // Resolve Back Pressed
    public boolean onBackPressed(){
        if(!multiMode)
            return false;
        CloseMultiMode();
        return true;
    }

    // View Plugins
    private LinearLayout GetWordLayout(Word word, int index) {
        /* View Tree
        wordLayout
            wordMeaningLayout(here id)
                priorityView
                meaningView
                meaningView
                ...
            checkBox
         */
        // wordLayout: Top Row Layout
        LinearLayout wordLayout = new LinearLayout(getActivity());
        wordLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        wordLayout.setLayoutParams(layoutParams);

        // wordMeaningLayout: Left Sub Layout
        LinearLayout wordMeaningLayout = new LinearLayout(getActivity());
        wordMeaningLayout.setId(index);
        wordMeaningLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams meaningLayoutParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2);
        wordMeaningLayout.setLayoutParams(meaningLayoutParams);
        wordMeaningLayout.setGravity(Gravity.START);

        wordMeaningLayout.setClickable(true);
        wordMeaningLayout.setOnClickListener(new OnWordMeaningLayoutClickListener());
        wordMeaningLayout.setOnLongClickListener(new OnWordMeaningLayoutLongClickListener());

        LinearLayout.LayoutParams meaningViewParams
                = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 2);
        List<String> meanings = word.GetMeanings();
        for(int i=0;i<meanings.size();i++)
        {
            TextView meaningView = new TextView(getActivity());
            meaningView.setLayoutParams(meaningViewParams);
            meaningView.setText(meanings.get(i));
            //meaningView.setGravity(Gravity.FILL_HORIZONTAL);
            meaningView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            wordMeaningLayout.addView(meaningView);
        }

        wordLayout.addView(wordMeaningLayout);

        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setId(maxMeanings+index);
        LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1);
        wordMeaningLayout.setLayoutParams(checkBoxParams);
        checkBox.setGravity(Gravity.END);
        checkBox.setVisibility(View.GONE);
        wordLayout.addView(checkBox);

        return wordLayout;
    }

    // Private Class
    // OnClickListener
    private class OnWordMeaningLayoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view){
            LinearLayout wordMeaningLayout = (LinearLayout) view;
            int index = wordMeaningLayout.getId();
            Word word = memoBook.get(index);

            int priority = word.GetPriority();
            ArrayList<String> meanings = word.GetMeanings();

            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra(DATA_WORD_INDEX, index);
            intent.putStringArrayListExtra(DATA_MEANINGS, meanings);
            intent.putExtra(DATA_PRIORITY, priority);
            startActivityForResult(intent, EDIT_WORD);
        }
    }

    private class OnWordMeaningLayoutLongClickListener implements  View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
            if(!multiMode)
            {
                OpenMultiMode();
                return true;
            }

            return false;
        }
    }

    // Control MultiMode
    private void OpenMultiMode() {
        if(multiMode)
            return;
        for(int i=0;i<memoList.getChildCount();i++)
        {
            LinearLayout wordLayout = (LinearLayout)memoList.getChildAt(i);
            CheckBox checkBox = (CheckBox) wordLayout.getChildAt(1);
            checkBox.setVisibility(View.VISIBLE);
        }
        multiMode = true;
    }

    private void CloseMultiMode() {
        if(!multiMode)
            return;
        for(int i=0;i<memoList.getChildCount();i++)
        {
            LinearLayout wordLayout = (LinearLayout)memoList.getChildAt(i);
            CheckBox checkBox = (CheckBox) wordLayout.getChildAt(1);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
        }
        multiMode = false;
    }

    // Control memoList -- Fill a LinearLayout with memoBook's data
    private void InitList(LinearLayout list) {
        Iterator<Word> iterator = memoBook.iterator();
        // index: Build link with memoList's item and memoBook's item.
        //          They have the same id(index).
        int index=0;
        while(iterator.hasNext())
        {
            Word word = iterator.next();
            list.addView(GetWordLayout(word, index));
            index++;
        }
    }

    private void UpdateList(LinearLayout list){
        // TODO: Make this more smart...

        list.removeAllViews();

        InitList(list);
    }

    // MemoBook Actions
    private void AddWord(ArrayList<String> meanings, int priority, boolean update) {
        Word word = new Word(meanings, priority);
        memoBook.add(word);
        if(update)
            UpdateList(memoList);
    }

}
