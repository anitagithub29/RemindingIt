package com.anaremind.remindingit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NotesChange extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_change);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        Button confirmButton = (Button) findViewById(R.id.confirm);


        mRowId = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString(DbAdapter.KEY_TITLE);
            String body = extras.getString(DbAdapter.KEY_BODY);
            mRowId = extras.getLong(DbAdapter.KEY_ROWID);

            if (title != null) {
                mTitleText.setText(title);
            }
            if (body != null) {
                mBodyText.setText(body);
            }
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (!mTitleText.getText().toString().equals("") && !mBodyText.getText().toString().equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString(DbAdapter.KEY_TITLE, mTitleText.getText().toString());
                    bundle.putString(DbAdapter.KEY_BODY, mBodyText.getText().toString());
                    if (mRowId != null) {
                        bundle.putLong(DbAdapter.KEY_ROWID, mRowId);
                    }

                    Intent mIntent = new Intent();
                    mIntent.putExtras(bundle);
                    setResult(RESULT_OK, mIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),"Necesitas incluir datos en titulo y cuerpo de la nota", Toast.LENGTH_LONG).show();
                }

            }

        });
    }
}
