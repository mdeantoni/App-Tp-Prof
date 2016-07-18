package com.example.matias.tprof.detail;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.matias.tprof.R;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    private CommentsAdapter commentsAdaptert;

    public CommentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);


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

        commentsAdaptert = new CommentsAdapter(getActivity(), comments);
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
                input.setSingleLine(false);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
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

}
