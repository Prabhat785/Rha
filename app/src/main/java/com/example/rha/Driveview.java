package com.example.rha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class Driveview extends AppCompatActivity {

    private RecyclerView memberlist;
    private DatabaseReference memref;
    private Button endbtn;
    private Button cancelbtn;
    private FirebaseAuth mAuth;
   // private String smiles;
    String PostKey;
    public static String hostid;

    private DatabaseReference Driveref,userref,userref2;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driveview);
        memberlist = (RecyclerView) findViewById(R.id.memberslist) ;
        memberlist.setHasFixedSize(true);
        endbtn=findViewById(R.id.enddrive);
        PostKey = getIntent().getExtras().get("Postkey").toString();
        Driveref= FirebaseDatabase.getInstance().getReference().child("Drives").child(PostKey);
        mAuth=FirebaseAuth.getInstance();
        cancelbtn=findViewById(R.id.canceldrive);
        memref = FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        memberlist.setLayoutManager(linearLayoutManager);
        Displaymembers();
        Driveref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Status"))
                    endbtn.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void btn_dialog(View view) {
        final EditText mSmilesText;;
        final AlertDialog.Builder alert= new AlertDialog.Builder(Driveview.this);
        View view1=getLayoutInflater().inflate(R.layout.custom_dialog,null);
        mSmilesText=(EditText)view1.findViewById(R.id.smiles);
        final Button mcancelbtn=view1.findViewById(R.id.cancel);
        final Button mokbtn=view1.findViewById(R.id.ok);
        alert.setView(view1);

        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        mokbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String smiles=mSmilesText.getText().toString();
                HashMap hashMap=new HashMap();
                String Status="ended";
                hashMap.put("Status",Status);
                hashMap.put("Smiles",smiles);
               // Toast.makeText(Driveview.this,"Smiles"+smiles,Toast.LENGTH_SHORT).show();
                Driveref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        //Toast.makeText(Driveview.this,"Drive ended",Toast.LENGTH_SHORT).show();
                    }
                });
                Driveref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("uid"))
                        {
                            hostid = dataSnapshot.child("uid").getValue().toString();
                           // Toast.makeText(Driveview.this, "hostid  " + hostid, Toast.LENGTH_SHORT).show();
                            updatedrives(hostid);
                            updatesmiles(Integer.parseInt(smiles));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(Driveview.this,MainActivity.class);
                startActivity(intent);
            }
        });
        mcancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    alertDialog.show();

    }
    private void updatesmiles( final int smiles){

        memref=FirebaseDatabase.getInstance().getReference().child("Members").child(PostKey);
       memref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String uid=snapshot.getKey();
                            Toast.makeText(Driveview.this,"Your Id is"+uid,Toast.LENGTH_SHORT).show();
                            userref = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                           // userref2 = FirebaseDatabase.getInstance().getReference().child("User").child(uid).child("Smiles");
                            userref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild("Smiles")){
                                        String drives=dataSnapshot.child("Smiles").getValue().toString();
                                        int x =Integer.parseInt(drives)+smiles;
                                        HashMap hashMap=new HashMap();
                                        hashMap.put("Smiles",String.valueOf(x));
                                        userref.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    //Toast.makeText(Driveview.this, "Congratulations you have earned"+smiles+"smiles", Toast.LENGTH_SHORT).show();
                                                    endbtn.setVisibility(View.INVISIBLE);

                                                }
                                                else
                                                    Toast.makeText(Driveview.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
    private void updatedrives( String hostid1){

        //Toast.makeText(Driveview.this,"Your Id is"+hostid1,Toast.LENGTH_SHORT).show();
        userref = FirebaseDatabase.getInstance().getReference().child("User").child(hostid);
        userref2 = FirebaseDatabase.getInstance().getReference().child("User").child(hostid).child("drives");
        userref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("drives")){
                    String drives=dataSnapshot.child("drives").getValue().toString();
                    int x =Integer.parseInt(drives)+1;
                    userref2.setValue(String.valueOf(x)).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(Driveview.this, "Congratulations on your have earned a smile", Toast.LENGTH_SHORT).show();
                                endbtn.setVisibility(View.INVISIBLE);

                            }
                            else
                                Toast.makeText(Driveview.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Toast.makeText(Driveview.this, "Congratulations on your first drive"+x[0], Toast.LENGTH_SHORT).show();

    }
    private void Displaymembers()
    {
        FirebaseRecyclerAdapter<Memberlist , MembersviewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Memberlist, MembersviewHolder>
                (
                        Memberlist.class,
                        R.layout.memberslist,
                        MembersviewHolder.class,
                        memref
                ) {
            @Override
            protected void populateViewHolder(MembersviewHolder membersviewHolder, Memberlist memberlist, int i)
            {
                membersviewHolder.setProfile1(getApplicationContext(),memberlist.getProfile1());
                membersviewHolder.setName1(memberlist.getName1());
                membersviewHolder.setUsername1(memberlist.getUsername1());
                membersviewHolder.setAddress1(memberlist.getAddress1());
                membersviewHolder.setPhoneno1(memberlist.getPhoneno1());
            }
        };
        memberlist.setAdapter(firebaseRecyclerAdapter);
    }



    public  static class MembersviewHolder extends RecyclerView.ViewHolder
    {
        View  mview;
        public MembersviewHolder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
        }
        public void setName1(String name) {
            TextView nm = mview.findViewById(R.id.member_name);
            nm.setText("NAME : "+name);
        }
        public void setUsername1(String username) {
            TextView un = mview.findViewById(R.id.memberuser_name);
            un.setText(username);
        }
        public void setAddress1(String address) {
            TextView add = mview.findViewById(R.id.member_adress);
            add.setText("ADDRESS : "+address);
        }
        public void setPhoneno1(String phoneno) {
            TextView ph = mview.findViewById(R.id.member_phoneno);
            ph.setText("PHONENO : "+phoneno);
        }
        public void setProfile1(Context applicationContext, String profile) {
            CircularImageView pi = mview.findViewById(R.id.memberprofile_image);
            Picasso.get().load(profile).into(pi);
        }

    }
    public void onBackPressed() {
        Intent startMain = new Intent(Driveview.this,MainActivity.class);
        startActivity(startMain);
        finish();

    }

}