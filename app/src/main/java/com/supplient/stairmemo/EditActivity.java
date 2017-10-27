package com.supplient.stairmemo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class EditActivity extends AddActivity {

    private int editingIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get meanings and priority
        Intent intent = getIntent();
        editingIndex = intent.getIntExtra(MemoFragment.DATA_WORD_INDEX, -1);
        ArrayList<String> metaMeanings = intent.getStringArrayListExtra(MemoFragment.DATA_MEANINGS);
        this.priority = intent.getIntExtra(MemoFragment.DATA_PRIORITY, DefaultOptions.defaultPriority);

        if(editingIndex < 0)
            throw new ArrayIndexOutOfBoundsException();

        // Fill view with above data
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout_add);
        layout.removeViewAt(0);
        for(int i = 0; i< metaMeanings.size(); i++)
        {
            LinearLayout meaningLayout = GetOneRow(metaMeanings.get(i));
            layout.addView(meaningLayout, layout.getChildCount()-1);
        }
    }

    @Override
    protected Intent GetResultIntent() {
        Intent intent = super.GetResultIntent();
        intent.putExtra(MemoFragment.DATA_WORD_INDEX, editingIndex);

        return intent;
    }
}
