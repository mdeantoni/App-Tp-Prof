package com.example.matias.tprof.detail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matias.tprof.R;

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

        // Inflate the layout for this fragment
        return rootView;
    }

}
