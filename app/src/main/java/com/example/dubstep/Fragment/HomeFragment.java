package com.example.dubstep.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.dubstep.CartMainActivity;
import com.example.dubstep.FoodItemActivity;
import com.example.dubstep.Interface.ItemClickListener;
import com.example.dubstep.LoginActivity;
import com.example.dubstep.MainActivity;
import com.example.dubstep.Model.FoodClass;
import com.example.dubstep.Model.FoodItem;
import com.example.dubstep.R;
import com.example.dubstep.ViewHolder.FoodClassViewHolder;
import com.example.dubstep.ViewHolder.FoodItemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

public class HomeFragment extends Fragment {

// TODO: 1. manage this fragment lifecycle properly
//          showing loading dialog on each call rather than just once when initialised
//       2. Insert ImageView in each element in recycler view for item image
    FirebaseAuth firebaseAuth;
    private DatabaseReference userref;
    private DatabaseReference foodref;
    private DatabaseReference cartref;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton mCartButton;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        firebaseAuth = FirebaseAuth.getInstance();


        userref = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getCurrentUser().getUid());



        foodref = FirebaseDatabase.getInstance().getReference().child("food_menu");


        cartref = FirebaseDatabase.getInstance().getReference("Cart");
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        loaderOnFoodMenuChange();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        mCartButton = view.findViewById(R.id.cart_btn);
        recyclerView = view.findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);

        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartMainActivity.class);
                startActivity(intent);
            }
        });

    }


    private void loaderOnFoodMenuChange() {
        foodref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<FoodClass> options = new FirebaseRecyclerOptions.Builder<FoodClass>().setQuery(foodref, FoodClass.class).build();
        final RandomColors rc = new RandomColors();
        final FirebaseRecyclerAdapter<FoodClass, FoodClassViewHolder> adapter =
                new FirebaseRecyclerAdapter<FoodClass, FoodClassViewHolder>(options) {

                    private ItemClickListener listener;

                    @Override
                    protected void onBindViewHolder(@NonNull final FoodClassViewHolder holder, final int position, @NonNull final FoodClass model) {

                        holder.foodClassTextView.setText(model.getBase_name());
                        holder.foodClassCardView.setBackgroundTintList(ColorStateList.valueOf(rc.getColor()));
                        holder.foodClassCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                take to that particular class
                                Intent intent = new Intent(getContext(), FoodItemActivity.class);
                                intent.putExtra("base_name",model.getBase_name());
                                intent.putExtra("index",String.valueOf(position));
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FoodClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_class_layout, parent, false);
                        FoodClassViewHolder holder = new FoodClassViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}

class RandomColors {
    private Stack<Integer> recycle, colors;

    public RandomColors() {
        colors = new Stack<>();
        recycle =new Stack<>();
        recycle.addAll(Arrays.asList(
                0xfff44336,0xffe91e63,0xff9c27b0,0xff673ab7,
                0xff3f51b5,0xff2196f3,0xff03a9f4,0xff00bcd4,
                0xff009688,0xff4caf50,0xff8bc34a,0xffcddc39,
                0xffffeb3b,0xffffc107,0xffff9800,0xffff5722,
                0xff795548,0xff9e9e9e,0xff607d8b,0xff333333
                )
        );
    }

    public int getColor() {
        if (colors.size()==0) {
            while(!recycle.isEmpty())
                colors.push(recycle.pop());
            Collections.shuffle(colors);
        }
        Integer c= colors.pop();
        recycle.push(c);
        return c;
    }
}

