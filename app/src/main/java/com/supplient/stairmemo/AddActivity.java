package com.supplient.stairmemo;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.supplient.stairmemo.DefaultOptions.maxMeanings;
import static com.supplient.stairmemo.DefaultOptions.minPriority;

public class AddActivity extends AppCompatActivity {

    protected Menu menu;

    protected int priority;

    // Init Functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Init local vars
        priority = DefaultOptions.defaultPriority;

        // Init floating action button
        FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.fab_save);
        saveFab.setOnClickListener(new OnSaveFabClickListener());

        // Add Empty Row for edit and del
        // The Last one cannot be deleted.
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout_add);
        layout.addView(this.GetOneRow());

        // Add newMeaningButton for add
        Button newMeaningButton = new Button(this);
        newMeaningButton.setOnClickListener(new OnNewMeaningButtonClickListener());
        newMeaningButton.setText("Add");
        layout.addView(newMeaningButton);
    }

    // For Switch Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Init vars for convenience
        this.menu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_add_activity, menu);

        // Init menu's actions and info
        UpdateShower();

        return super.onCreateOptionsMenu(menu);
    }

    // Options Menu's logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.add_bar_priority_up:
                if(priority < DefaultOptions.maxPriority)
                    priority++;
                UpdateShower();
                return true;
            case R.id.add_bar_priority_down:
                if(priority > minPriority)
                    priority--;
                UpdateShower();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void UpdateShower() {
        RelativeLayout layout = (RelativeLayout) menu.findItem(R.id.add_bar_priority_shower).
                getActionView().findViewById(R.id.priority_shower_layout);
        TextView textView = (TextView) layout.findViewById(R.id.priority_shower);
        textView.setText(String.valueOf(priority));
    }

    // Resolve Back Pressed
    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        setResult(MemoFragment.RESULT_CANCEL, data);

        super.onBackPressed();
    }

    // View Plugins
    protected LinearLayout GetOneRow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.layout_meaning, null);

        Button button = (Button) layout.getChildAt(1);
        button.setOnClickListener(new OnDelButtonClicked());
        // TODO: 将硬编码的改成 @string/xxx 的
        button.setText("Rem");

        return layout;
    }

    protected LinearLayout GetOneRow(String text) {
        LinearLayout layout = GetOneRow();

        TextView textView = (TextView) layout.getChildAt(0);
        textView.setText(text);

        return layout;
    }

    // ActivityResult Plugins
    protected Intent GetResultIntent() {
        ArrayList<String> meanings = new ArrayList<String>();
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout_add);
        for(int i=0;i<layout.getChildCount()-1;i++)
        {
            LinearLayout meaningLayout = (LinearLayout) layout.getChildAt(i);
            EditText text = (EditText) meaningLayout.getChildAt(0);
            meanings.add(text.getText().toString());
        }

        Intent intent = new Intent();
        intent.putStringArrayListExtra(MemoFragment.DATA_MEANINGS, meanings);
        intent.putExtra(MemoFragment.DATA_PRIORITY, priority);

        return intent;
    }

    // Private Class
    // OnClickListener
    protected class OnSaveFabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = GetResultIntent();

            setResult(MemoFragment.RESULT_OK, intent);
            finish();
        }
    }

    protected class OnNewMeaningButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout_add);
            if(layout.getChildCount() == 1 + maxMeanings)
                // TODO:弹出提示框，指出数目已满
                return;
            layout.addView(GetOneRow(), layout.getChildCount()-1);
        }
    }

    private class OnDelButtonClicked implements View.OnClickListener{
        @Override
        public void onClick(View view){
            LinearLayout bigLayout = (LinearLayout) findViewById(R.id.linearLayout_add);
            if(bigLayout.getChildCount()<=2)
                return;

            bigLayout.removeView((View) view.getParent());
        }
    }

}
