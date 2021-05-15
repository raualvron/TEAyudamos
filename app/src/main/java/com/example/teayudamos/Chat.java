package com.example.teayudamos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.teayudamos.model.Messages;
import com.example.teayudamos.services.Constants;
import com.example.teayudamos.services.SharePref;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Chat extends AppCompatActivity {

    private LinearLayoutManager mLayoutManager;
    private RecyclerView rvChat;
    private EditText edtMessage;
    private ImageButton imbSend;
    FirestoreRecyclerAdapter<Messages, ChatViewHolder> adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView txtAlumn;
    SharePref sharePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        edtMessage = findViewById(R.id.edtMessage);
        imbSend = findViewById(R.id.imbSend);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(false);
        mLayoutManager.setStackFromEnd(true);

        txtAlumn = findViewById(R.id.user);
        txtAlumn.setText("Chats de " + getAlumnName());

        rvChat.setHasFixedSize(true);
        rvChat.setLayoutManager(mLayoutManager);

        FirestoreRecyclerOptions<Messages> options = new FirestoreRecyclerOptions.Builder<Messages>()
                .setQuery(db.collection("messages").orderBy("datetime"), Messages.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<Messages, ChatViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Messages model) {
                holder.setList(model.getMessage(), model.getDateTime(), model.getType(), getApplicationContext());
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_message, parent,false);
                return new ChatViewHolder(view);
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mLayoutManager.smoothScrollToPosition(rvChat,null,adapter.getItemCount());
            }
        });

        rvChat.setAdapter(adapter);
        adapter.startListening();

        imbSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message =edtMessage.getText().toString().trim();
                SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-mm-dd hh:mm", Locale.getDefault());
                String datetime = simpleDate.format(new Date());
                if (TextUtils.isEmpty(message)) {

                } else {
                    HashMap<String,Object> dataMessage = new HashMap<>();
                    dataMessage.put("datetime", datetime);
                    dataMessage.put("message",message);

                    // Add a new document with a generated ID
                    db.collection("messages")
                            .add(dataMessage)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("Chat Activity", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    edtMessage.setText("");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Chat Activity", "Error adding document", e);
                                }
                            });
                }
            }
        });
    }

    private String getAlumnName() {
        sharePref = new SharePref(getBaseContext());
        return sharePref.getSharedPrefString(Constants.ALUMN);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ConstraintLayout clMessage;
        TextView txtMessage;
        TextView txtDate;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            clMessage = mView.findViewById(R.id.clMessage);
            txtMessage = mView.findViewById(R.id.txtMessage);
            txtDate = mView.findViewById(R.id.txtDate);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public void setList(String message, String date, String type, Context context) {

            if (TextUtils.isEmpty(type)) {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(clMessage);
                constraintSet.setHorizontalBias(R.id.txtMessage,1.0f);
                constraintSet.setHorizontalBias(R.id.txtDate,1.0f);

                constraintSet.applyTo(clMessage);
                txtMessage.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.sender,context.getTheme()));
                txtMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                txtMessage.setText(message);
                txtMessage.setTextColor(getResources().getColor(R.color.white));

                txtDate.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                txtDate.setText(date);
                txtDate.setTextColor(getResources().getColor(R.color.black));

            } else {
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(clMessage);
                constraintSet.setHorizontalBias(R.id.txtMessage,0.0f);
                constraintSet.setHorizontalBias(R.id.txtDate,0.0f);
                constraintSet.applyTo(clMessage);
                txtMessage.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.receiver,context.getTheme()));
                txtMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                txtMessage.setText(message);
                txtMessage.setTextColor(getResources().getColor(R.color.black));

                txtDate.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                txtDate.setText(date);
                txtDate.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
}