package com.supplient.stairmemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public class MemoBook extends ArrayList<Word> {

    private File filesDir;
    private ArrayList<Word> orderedList;

    final public static String fileName = "memobook.txt";

    private static MemoBook instance;
    static MemoBook GetInstance() {
        return instance;
    }

    static MemoBook GetInstance(File filesDir) {
        if(instance == null)
            instance = new MemoBook(filesDir);
        return instance;
    }

    private MemoBook(File filesDir) {
        orderedList = null;

        // Open file or Create File
        this.filesDir = filesDir;
        File file = new File(filesDir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        catch(IOException exception)
        {
            Log.e("Error", "MemoBook: createNewFile failed!", exception);
            return;
        }

        // Read File if there are something in it.
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));

            String tempString;
            ArrayList<String> tempList = new ArrayList<>();
            while((tempString = in.readLine()) != null)
            {
                if(tempString.length() < 1)
                {
                    tempString = "";
                    for(String str: tempList) {
                        tempString += str + "\n";
                    }

                    Word word = new Word(tempString);
                    this.add(word);
                    tempList.clear();
                    continue;
                }

                tempList.add(tempString);
            }
        } catch (IOException e) {
            Log.e("Error", "MemoBook: happened when reading memobook!", e);
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void UpdateBook() {
        // Open File or Create New File
        File file = new File(filesDir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        }
        catch(IOException exception)
        {
            Log.e("Error", "MemoBook: createNewFile failed!", exception);
            return;
        }

        // Write File
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file));

            Iterator<Word> iterator = this.iterator();
            while(iterator.hasNext())
            {
                Word word = iterator.next();
                out.write(word.ToString());
            }
        } catch (IOException e) {
            Log.e("Error", "MemoBook: happened when reading memobook!", e);
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public boolean add(Word word) {
        if(!super.add(word))
            return false;
        // TODO: Make this more smart... Insert sort can be better...
        Collections.sort(this, new Comparator<Word>() {
            @Override
            public int compare(Word wa, Word wb) {
                return wa.GetMeanings().get(0).compareTo(wb.GetMeanings().get(0));
            }
        });
        UpdateBook();
        orderedList = null;
        return true;
    }

    @Override
    public Word remove(int index) {
        Word word = super.remove(index);

        UpdateBook();
        orderedList = null;
        return word;
    }

    public Word GetLowestOrderWord() {
        if(orderedList == null) {
            orderedList = GetOrderedList();
        }

        if(orderedList.size() == 0)
            return null;

        Word res = orderedList.get(0);
        res.AddReciteTime();
        orderedList = null;
        return res;
    }

    private ArrayList<Word> GetOrderedList() {
        ArrayList<Word> list = new ArrayList<>(this);
        Collections.sort(list, new Comparator<Word>() {
            @Override
            public int compare(Word wa, Word wb) {
                return (int)(wa.GetOrder() - wb.GetOrder());
            }
        });
        return list;
    }

}
