package com.example.todolist.fragment.add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.example.todolist.R
import com.example.todolist.ToDoDatabase
import com.example.todolist.data.models.Priority
import com.example.todolist.data.models.ToDoData
import com.example.todolist.data.viewmodel.ToDoViewModel
import com.example.todolist.databinding.FragmentAddBinding
import com.example.todolist.databinding.FragmentListBinding
import com.example.todolist.fragment.SharedViewModel


class AddFragment : Fragment() {

    private val mSharedViewModel:SharedViewModel by viewModels()

    private val mToDoViewModel: ToDoViewModel by viewModels()

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(inflater, container, false)


        setHasOptionsMenu(true)

        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener




        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.menu_add){

            insertDataToDb()

        }

        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {

        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle,mDescription)

        if(validation) {

        // Insert Data to Database
            val newData = ToDoData(0,mTitle,mSharedViewModel.parsePriority(mPriority),mDescription)

            mToDoViewModel.insertData(newData)

            Toast.makeText(requireContext(),"Successfully added!",Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(),"Please fill out all fields",Toast.LENGTH_SHORT).show()

        }
    }



}