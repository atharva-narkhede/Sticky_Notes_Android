package com.atharva.stickynotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final String PREFS_NAME = "NotePrefs";
    private static final String KEY_NOTE_COUNT = "NoteCount";
    private LinearLayout notescontainer;
    private List<Note> NoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        notescontainer=findViewById(R.id.notecontainer);
        Button savebutton=findViewById(R.id.savebutton);

        NoteList = new ArrayList<>();
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        LoadNotesfromPreferences();
        displayNotes();
    }

    private void displayNotes() {
        for(Note note: NoteList){
            createNoteview(note);
        }
    }

    private void LoadNotesfromPreferences() {
        SharedPreferences sharedPreferences=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        int noteCount=sharedPreferences.getInt(KEY_NOTE_COUNT,0);
        for(int i=0;i<noteCount;i++){
            String title=sharedPreferences.getString("note_title_"+i,"");
            String content=sharedPreferences.getString("note_content_"+i,"");
            Note note =new Note();
            note.setTitle(title);
            note.setContent(content);
            NoteList.add(note);

        }
    }

    private void saveNote(){
        EditText titletext=findViewById(R.id.titlenote);
        EditText contenttext=findViewById(R.id.contentnote);
        String title=titletext.getText().toString();
        String content= contenttext.getText().toString();
        if(!title.isEmpty() && !content.isEmpty()){
            Note note = new Note();
            note.setTitle(title);
            note.setContent(content);

            NoteList.add(note);
            saveNotestoPreferences();
            createNoteview(note);
            clearInputFields();
            Toast.makeText(MainActivity.this,"Saved Successfully",Toast.LENGTH_SHORT).show();

        }
    }

    private void clearInputFields() {
        EditText titleedittext=findViewById(R.id.titlenote);
        EditText contentedittext=findViewById(R.id.contentnote);
        titleedittext.getText().clear();
        contentedittext.getText().clear();
    }

    private void createNoteview(final Note note){
        View noteView=getLayoutInflater().inflate(R.layout.note_item,null);
        TextView titletextview=noteView.findViewById(R.id.titletextview);
        TextView contenttextview=noteView.findViewById(R.id.contenttextview);
        titletextview.setText(note.getTitle());
        contenttextview.setText(note.getContent());
        noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedata(note);

            }

        });
        //noteView.click
        noteView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialog(note);
                return true;
            }
        });
        notescontainer.addView(noteView);


    }
    public void sharedata(final Note note){
        Intent i=new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,note.getTitle()+"\n\n"+note.getContent());
        startActivity(Intent.createChooser(i,"Choose a Platform"));


    }
    private void showDialog(final Note note) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Please Select");
        builder.setMessage("Do you want to Edit / Delete");
        builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editnote(note);
            }
        });

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNoteandRefresh(note);
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

    private void editnote(final Note note) {
        EditText titleedtxt=findViewById(R.id.titlenote);
        EditText contentedtxt=findViewById(R.id.contentnote);
        titleedtxt.getText().clear();
        contentedtxt.getText().clear();
        titleedtxt.setText(note.getTitle());
        contentedtxt.setText(note.getContent());
        deleteNoteandRefresh(note);
    }

    private void deleteNoteandRefresh(Note note) {
        NoteList.remove(note);
        saveNotestoPreferences();
        refreshNotesView();

    }

    private void refreshNotesView() {
        notescontainer.removeAllViews();
        displayNotes();
    }

    private void saveNotestoPreferences(){
        SharedPreferences sharedPreferences=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putInt(KEY_NOTE_COUNT,NoteList.size());
        for(int i=0;i<NoteList.size();i++){
            Note note= NoteList.get(i);
            editor.putString("note_title_"+i,note.getTitle());
            editor.putString("note_content_"+i,note.getContent());

        }
        editor.apply();

    }

}