package com.assalam.chatassalam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.assalam.chatassalam.api.ApiClient;
import com.assalam.chatassalam.api.ApiInterface;
import com.assalam.chatassalam.model.Chat;
import com.assalam.chatassalam.model.ChatHeader;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.Group;
import com.daasuu.bl.ArrowDirection;
import com.daasuu.bl.BubbleLayout;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FloatingActionButton fab;
    private RecyclerView lvListView;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter adapter;
    private ApiInterface apiInterface;
    private List<ChatHeader> listChatHeader;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);

        fab = view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), SelectContactActivity.class);
                startActivity(intent);
            }
        });

        lvListView = view.findViewById(R.id.list_view);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Now set the layout manager and the adapter to the RecyclerView
        lvListView.setLayoutManager(mLayoutManager);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        String idUser = sharedPreferences.getString("id_user", "");


        Query query=FirebaseDatabase.getInstance().getReference().child("chat_header").child(idUser).orderByChild("waktu");
        FirebaseRecyclerOptions<ChatHeader> options =
                new FirebaseRecyclerOptions.Builder<ChatHeader>()
                        .setQuery(query, ChatHeader.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<ChatHeader, ChatFragment.ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                Toast.makeText(getActivity(), "create", Toast.LENGTH_SHORT).show();
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_chat_header, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(ChatFragment.ViewHolder holder, final int position, ChatHeader model) {
//                Toast.makeText(getActivity(), "bind", Toast.LENGTH_SHORT).show();
                LinearLayout llInti = holder.llInti;
                final TextView tvNama = holder.tvNama;
                final CircleImageView civGambar = holder.civGambar;
                final TextView tvPesanTerakhir = holder.tvPesanTerakhir;

                if (model.getHeader() != null) {
                    tvNama.setText(model.getNama());
                    tvPesanTerakhir.setText(model.getPesan_terakhir());
                    String status = "";
                    String id = "";
                    int i = 0;
                    for (; i < model.getHeader().length(); i++) {
                        if (model.getHeader().charAt(i) == '_') {
                            i++;
                            break;
                        }
                        status += model.getHeader().charAt(i);
                    }

                    for (; i < model.getHeader().length(); i++) {
                        if (model.getHeader().charAt(i) == '_') {
                            break;
                        }
                        id += model.getHeader().charAt(i);
                    }

                    final String finalId = id;
                    final String finalStatus = status;

                    if (status.equals("personal")) {
                        Map<String, String> param = new HashMap<>();
                        param.put("id_user", id);

                        Call<Contact> call = apiInterface.getUser(param);
                        call.enqueue(new Callback<Contact>() {
                            @Override
                            public void onResponse(Call<Contact> call, Response<Contact> response) {
                                Picasso.get().load("https://www.assalam.id/assets/panel/images/" + response.body().getGambar())
                                        .into(civGambar);
                            }

                            @Override
                            public void onFailure(Call<Contact> call, Throwable t) {

                            }
                        });
                    }
                    else {
                        Map<String, String> param = new HashMap<>();
                        param.put("id_group", id);

                        Call<Group> call = apiInterface.getGroup(param);
                        call.enqueue(new Callback<Group>() {
                            @Override
                            public void onResponse(Call<Group> call, Response<Group> response) {
                                Picasso.get().load("https://www.assalam.id/assets/panel/images/" + response.body().getGambar())
                                        .into(civGambar);
                            }

                            @Override
                            public void onFailure(Call<Group> call, Throwable t) {

                            }
                        });
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final Intent intent = new Intent(v.getContext(), ChatActivity.class);
                            if (finalStatus.equals("personal")) {
                                intent.putExtra("id", finalId);
                                intent.putExtra("nama", tvNama.getText().toString());
                                intent.putExtra("status", finalStatus);
                                v.getContext().startActivity(intent);
                            }
                            else {
                                Map<String, String> param = new HashMap<>();
                                param.put("id_group", finalId);

                                Call<List<Contact>> call = apiInterface.groupMember(param);
                                call.enqueue(new Callback<List<Contact>>() {
                                    @Override
                                    public void onResponse(Call<List<Contact>> call, Response<List<Contact>> response) {
                                        ArrayList<String> listId = new ArrayList<>();
                                        for (Contact contact : response.body()) {
                                            listId.add(contact.getIdUser());
                                        }

                                        intent.putStringArrayListExtra("id", listId);
                                        intent.putExtra("id_group", finalId);
                                        intent.putExtra("nama", tvNama.getText().toString());
                                        intent.putExtra("status", finalStatus);
                                        v.getContext().startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(Call<List<Contact>> call, Throwable t) {

                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    llInti.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        adapter.startListening();
        lvListView.setAdapter(adapter);

        return view;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout llInti;
        public CircleImageView civGambar;
        public TextView tvNama;
        public TextView tvPesanTerakhir;

        public ViewHolder(View itemView) {
            super(itemView);
            llInti = itemView.findViewById(R.id.inti);
            civGambar = itemView.findViewById(R.id.gambar);
            tvNama = itemView.findViewById(R.id.nama);
            tvPesanTerakhir = itemView.findViewById(R.id.pesan_terakhir);
        }
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
