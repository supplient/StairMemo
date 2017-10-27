package com.supplient.stairmemo;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReciteFragment extends Fragment {

    // Vars for Convenience
    private View view;
    private LinearLayout layout;
    private MemoBook memoBook;

    // Vars for States
    private Word nowWord;
    private int nowMeaningIndex;


    public ReciteFragment() {
        // Required empty public constructor
    }

    // Init Functions
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_recite, container, false);

        // Init Vars for Convenience
        layout = (LinearLayout) view.findViewById(R.id.layout_recite);
        memoBook = MemoBook.GetInstance();

        // Init Vars for States
        nowWord = memoBook.GetLowestOrderWord();
        nowMeaningIndex = 0;

        // Init Views
        layout.setClickable(true);
        layout.setOnClickListener(new OnLayoutClickListener());
        addNowMeaningView();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.recite_menu, menu);
    }

    // View Plugins
    private void addNowMeaningView() {
        if(nowWord == null)
            // 当memoBook为空时
            return;
        layout.addView(GetMeaningView(nowWord.GetMeanings().get(nowMeaningIndex)));
    }

    private View GetMeaningView(String s) {
        TextView textView = new TextView(getActivity());
        textView.setText(s);
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(MATCH_PARENT, 0, 1);
        textView.setLayoutParams(layoutParams);

        return textView;
    }

    // Logic Parts
    private boolean IterateWord() {
        // return true if word is changed.

        if(nowWord == null)
            // 当memoBook为空时
            return false;

        nowMeaningIndex++;
        if(nowMeaningIndex == nowWord.GetMeanings().size())
        {
            nowWord = memoBook.GetLowestOrderWord();
            nowMeaningIndex = 0;
            return true;
        }
        else
            return false;
    }

    // Private Class
    // OnClickListener
    private class OnLayoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if(IterateWord())
                layout.removeAllViews();
            addNowMeaningView();
        }
    }

}
