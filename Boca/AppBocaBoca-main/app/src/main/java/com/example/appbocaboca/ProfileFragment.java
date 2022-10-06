package com.example.appbocaboca;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {


    //FIREBASEAUTH
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Componentes views xml
    ImageView avatar, coverIv;
    TextView nameT, emailT, telefoneT;
    FloatingActionButton fab;

    //progress dialog
    ProgressDialog pd;
    // permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    //ARRAYS OF PERMISSIONS TO BE REQUESTED
    String cameraPermissions[];
    String storagePermissions[];


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //iniciar Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        //init arrays of permissions
        cameraPermissions = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
       storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Iniciar as views
        avatar = view.findViewById(R.id.avataricon);
        coverIv = view.findViewById(R.id.coverIv);
        emailT = view.findViewById(R.id.emailTv);
        telefoneT = view.findViewById(R.id.telefoneTv);
        nameT = view.findViewById(R.id.nameTv);
        fab = view.findViewById(R.id.fab);

        //initial progress dialog
        pd = new ProgressDialog(getActivity());



        /*Nos temos que conseguir informações do usuario logado.
         * Conseguimos isso usando o email ou o uid, aqui nos usaremos o email
         * Usando a query orderByChild nos vamos mostrar os detalhes de um nó o
         * qual a chave chamada email tem valor igual ao email logado.
         * Vai procurar todos os nós , onde a chave combina combina vai pegar os detalhes*/

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //checar até obter os dados necessários
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    String name = "" + ds.child("nome").getValue();
                    String email = "" + ds.child("email").getValue();
                    String telefone = "" + ds.child("telefone").getValue();
                    String imagem = "" + ds.child("imagem").getValue();
                    String cover = "" + ds.child("cover").getValue();
                    //set data

                    nameT.setText("name");
                    emailT.setText("email");
                    telefoneT.setText("telefone");
                    try {
                        //Se a imagem for recebida entao mudar
                        Picasso.get().load(imagem).into(avatar);

                    } catch (Exception e) {

                        //Se tiver uma exception enquanto conseguir a  imagem entao definir uma imagem default
                        Picasso.get().load(R.drawable.ic_default_image_white).into(avatar);

                    }
                    try {
                        //Se a imagem for recebida entao mudar
                        Picasso.get().load(cover).into(coverIv);

                    } catch (Exception e) {

                        //Se tiver uma exception enquanto conseguir a  imagem entao definir uma imagem default

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // fab button click "Botao flutuante nao mexa nessa caralha"
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });

        return view;
    }

    private boolean checkStoragePermission(){
       boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
       ==(PackageManager.PERMISSION_GRANTED);
       return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(),storagePermissions,STORAGE_REQUEST_CODE);
    }
    private boolean checkcCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(),cameraPermissions,CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        String options[] = {"Editar foto do Perfil", "Editar foto da capa", "Editar Nome", "Editar Telefone"};
        //Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Escolher ação");
        // set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //handle dialog item clicls
            if (which == 0){
            // Edit profile clicked
            pd.setMessage("Atualizar foto do perfil");
                showImagePicDialog();
            }
            else if (which == 1){
            // Edit Cover Clicked
                pd.setMessage("Atualizar foto da capa");
            }
            else if (which == 2){
                //Edit name Clicked
                pd.setMessage("Atualizar Nome");
            }
            else if (which == 3){
                //Edit phone clicked
                pd.setMessage("Atualizar Número do Telefone");
            }

            }
        });
        // create and show dialog
        builder.create().show();

    }

    private void showImagePicDialog() {
        //  show dialog containing options camera and gallery to pick the image
        String options[] = {"Camera", "Galeria",};
        //Alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Escolha a Imagem");
        // set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //handle dialog item clicls
                if (which == 0){
                    // Camera Clicked

                }
                else if (which == 1){
                    // Gallery Clicked

                }

            }
        });
        // create and show dialog
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
      switch (requestCode){
          case CAMERA_REQUEST_CODE:{
              if (grantResults.length>0){
                  boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                  boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                  if (cameraAccepted && writeAccepted){
                      pickFromCamera();
                  }

              }
              else{

          }
          case STORAGE_REQUEST_CODE:{

          }
      }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}