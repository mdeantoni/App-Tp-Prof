package com.example.matias.tprof.detail;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matias.tprof.R;
import com.example.matias.tprof.data.QuotesContract;
import com.example.matias.tprof.task.CreateCommentTask;
import com.example.matias.tprof.task.RefreshCommentsTask;
import com.melnykov.fab.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener  {

    public final String LOG_TAG = CommentsFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommentsAdapter commentsAdaptert;
    private String tickerSymbol;
    private int oldestIdentifier;

    private static final int COMMENTS_LOADER = 1;

    private static final String[] COMMENTS_COLUMNS = {
            QuotesContract.CommentsEntry.TABLE_NAME + "." + QuotesContract.CommentsEntry._ID,
            QuotesContract.CommentsEntry.COLUMN_SYMBOL,
            QuotesContract.CommentsEntry.COLUMN_DATE,
            QuotesContract.CommentsEntry.COLUMN_USERNAME,
            QuotesContract.CommentsEntry.COLUMN_COMMENT,
            QuotesContract.CommentsEntry.COLUMN_IDENTIFIER,
    };



    static final int COL_ID = 0;
    static final int COL_SYMBOL = 1;
    static final int COL_DATE = 2;
    static final int COL_USERNAME = 3;
    static final int COL_COMMENET = 4;
    static final int COL_IDENTIFIER = 5;


    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        RefreshCommentsTask task = new RefreshCommentsTask(getActivity(),(SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh));
        task.execute(tickerSymbol);

        getLoaderManager().initLoader(COMMENTS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            tickerSymbol = arguments.getString(StockDetailFragment.SYMBOL);
        }


        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        ArrayList<Comment> comments = new ArrayList<Comment>();
        comments.add(new Comment("10/12 13:45", "Alguien", "Esto es un comentario de prueba corto"));
        comments.add(new Comment("10/12 13:45", "Otro Alguien", "Esto es un comentario de prueba corto"));
        comments.add(new Comment("10/12 13:45", "Prueba", "Esto es un comentario de prueba mas largo, deberia ocupar dos renglones como minimo , asi que lo relleno un poco"));
        comments.add(new Comment("10/12 13:45", "Alguien", "Esto es un comentario de prueba mas largo, deberia ocupar dos renglones como minimo , asi que lo relleno un poco"));
        comments.add(new Comment("10/12 13:45", "Alguien", "Esto es un comentario de prueba corto"));
        comments.add(new Comment("10/12 02:45", "otro", "Esto es un comentario de prueba mas largo, deberia ocupar dos renglones como minimo , asi que lo relleno un poco"));
        comments.add(new Comment("10/12 13:45", "Otro Alguien", "Esto es un comentario de prueba mucho mas largo, deberia ocupar tres renglones como minimo , asi que lo relleno un poco mucho" +
                "con el contenido que se me ocurre en el momento basicamente nada, prueba prueba prueba prueba prueba ahhh holaaaaaaaaaa, tengo hambre, creo que voy a pedir empanadas."));
        comments.add(new Comment("10/12 12:45", "Matias", "Esto es un comentario de prueba corto"));

        commentsAdaptert = new CommentsAdapter(getActivity(), null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_comments);
        listView.setAdapter(commentsAdaptert);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.attachToListView(listView);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Nuevo comentario:");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                input.setSingleLine(false);
                input.setMaxLines(10);

                builder.setView(input);
                builder.setPositiveButton("ACEPTAR", null);
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String textinput = input.getText().toString();
                                if (textinput.isEmpty()) {
                                    input.setError("Un comentario no puede estar vacio.");
                                } else if (textinput.length() > 400) {
                                    input.setError("El comentario no puede superar los 400 caracteres.");
                                } else {
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date date = new Date();

                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    String username = preferences.getString(getString(R.string.pref_username_key), getString(R.string.pref_default_username));

                                    CreateCommentTask task = new CreateCommentTask(getActivity(),(SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh));
                                    task.execute(tickerSymbol, textinput, username, dateFormat.format(date), Integer.toString(oldestIdentifier));

                                    Toast toast = Toast.makeText(getActivity(), "Comentario agregado", Toast.LENGTH_SHORT);
                                    toast.show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "Comments Loader created.");

        return new CursorLoader(
                getActivity(),
                QuotesContract.CommentsEntry.CONTENT_URI,
                COMMENTS_COLUMNS,
                QuotesContract.CommentsEntry.COLUMN_SYMBOL + " = ?",
                new String[]{this.tickerSymbol},
                QuotesContract.CommentsEntry.COLUMN_DATE + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "Comments Loader load finished.");
        if(cursor.moveToFirst()){
            oldestIdentifier = cursor.getInt(CommentsFragment.COL_IDENTIFIER);
        }

        commentsAdaptert.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {
        RefreshCommentsTask task = new RefreshCommentsTask(getActivity(),(SwipeRefreshLayout) getActivity().findViewById(R.id.swiperefresh));
        task.execute(tickerSymbol);
    }
}
