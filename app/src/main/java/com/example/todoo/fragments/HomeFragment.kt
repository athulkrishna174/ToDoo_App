package com.example.todoo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoo.R
import com.example.todoo.databinding.FragmentHomeBinding
import com.example.todoo.utils.adapter.ToDooAdapter
import com.example.todoo.utils.model.ToDooData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment : Fragment(), AddToDooPopUpFragment.DialogNextButtonListener,
    ToDooAdapter.ToDooAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: AddToDooPopUpFragment? = null
    private lateinit var adapter: ToDooAdapter
    private lateinit var mutableList: MutableList<ToDooData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        homeEvents()
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance("https://todooapp-5c859-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Tasks").child(auth.currentUser!!.uid)

        binding.toDooRecyclerView.setHasFixedSize(true)
        binding.toDooRecyclerView.layoutManager = LinearLayoutManager(context)

        mutableList = mutableListOf()
        adapter = ToDooAdapter(mutableList)
        adapter.setListener(this)
        binding.toDooRecyclerView.adapter =adapter
    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mutableList.clear()
                for (taskSnapshot in snapshot.children){
                    val todoTask =taskSnapshot.key?.let {
                        ToDooData(it, taskSnapshot.value.toString())
                    }
                    if(todoTask != null){
                        mutableList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun homeEvents(){
        binding.addButton.setOnClickListener{
            if (popUpFragment != null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddToDooPopUpFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(childFragmentManager, AddToDooPopUpFragment.TAG)
        }

        binding.logoutButton.setOnClickListener{
            if(auth.currentUser != null)
                auth.signOut()
            navController.navigate(R.id.action_homeFragment_to_signInFragment)
        }
    }

    override fun onSaveTask(todoText: String, todoEt: TextInputEditText) {
        databaseRef.push().setValue(todoText).addOnSuccessListener {
            Toast.makeText(context, "Task added Successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
        todoEt.text = null
        popUpFragment!!.dismiss()
    }

    override fun onUpdateTask(toDooData: ToDooData, todoEt: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDooData.taskId] = toDooData.task
        databaseRef.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            todoEt.text = null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskButtonClicked(toDooData: ToDooData) {
        databaseRef.child(toDooData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskButtonClicked(toDooData: ToDooData) {
        if (popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

        popUpFragment = AddToDooPopUpFragment.newInstance(toDooData.taskId, toDooData.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, AddToDooPopUpFragment.TAG)
    }

}