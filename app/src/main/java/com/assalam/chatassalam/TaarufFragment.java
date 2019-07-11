package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaarufFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaarufFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaarufFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SharedPreferences sharedPreferences;
    private ImageView ivGambar;
    private TextView tvNama;
    private LinearLayout llChat;
    private LinearLayout llSearch;
    private LinearLayout llProfil;

    private OnFragmentInteractionListener mListener;

    public TaarufFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaarufFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaarufFragment newInstance(String param1, String param2) {
        TaarufFragment fragment = new TaarufFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_taaruf, container, false);

        ivGambar = view.findViewById(R.id.gambar);
        tvNama = view.findViewById(R.id.nama);
        llChat = view.findViewById(R.id.chat);
        llSearch = view.findViewById(R.id.search);
        llProfil = view.findViewById(R.id.profil);

        sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);

        Picasso.get().load("https://www.assalam.id/assets/panel/images/" + sharedPreferences.getString("gambar", "")).into(ivGambar);
        tvNama.setText(sharedPreferences.getString("nama", ""));

        llChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChatHeaderTaarufActivity.class);
                startActivity(intent);
            }
        });

        llSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        llProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserDetailTaarufActivity.class);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                intent.putExtra("id_user", sharedPreferences.getString("id_user", ""));
                intent.putExtra("nama", sharedPreferences.getString("nama", ""));
                intent.putExtra("username", sharedPreferences.getString("username", ""));
                intent.putExtra("gambar", sharedPreferences.getString("gambar", ""));
                intent.putExtra("jenis_kelamin", sharedPreferences.getString("jenis_kelamin", ""));
                intent.putExtra("no_hp", sharedPreferences.getString("no_hp", ""));
                intent.putExtra("tanggal_lahir", sharedPreferences.getString("tanggal_lahir", ""));
                intent.putExtra("status_taaruf", sharedPreferences.getString("status_taaruf", ""));
                startActivity(intent);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
