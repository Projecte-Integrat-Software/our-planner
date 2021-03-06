package com.example.our_planner.ui.calendar.comments;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.our_planner.LocaleLanguage;
import com.example.our_planner.R;
import com.example.our_planner.model.Comment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewComments;
    private EditText message;
    private FloatingActionButton sendBtn;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        message = findViewById(R.id.message);
        sendBtn = findViewById(R.id.sendBtn);

        CommentsViewModel viewModel = new ViewModelProvider(this).get(CommentsViewModel.class);
        viewModel.setIdEvent(getIntent().getExtras().getString("idEvent"));
        MutableLiveData<ArrayList<Comment>> comments = viewModel.getComments();
        AdapterComments adapterComments = new AdapterComments(comments.getValue());
        recyclerViewComments.setAdapter(adapterComments);

        comments.observe(this, commentsNew -> adapterComments.notifyDataSetChanged());

        sendBtn.setOnClickListener(view -> {
            String messageToPost = message.getText().toString();
            if (!messageToPost.equals("")){
                viewModel.postComment(messageToPost);
                message.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Resources r = LocaleLanguage.getLocale(this).getResources();
        if (item.getItemId() == R.id.help) {
            alert.setMessage(r.getString(R.string.help_comments));
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeLanguage();
    }

    private void changeLanguage() {
        Resources r = LocaleLanguage.getLocale(this).getResources();

        setTitle(r.getString(R.string.comments));
        message.setHint(r.getString(R.string.message));
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(r.getString(R.string.help_comments));
        builder.setNeutralButton(r.getString(R.string.close), (dialogInterface, i) -> dialogInterface.cancel());
        alert = builder.create();
        alert.setTitle(r.getString(R.string.help));
    }
}
