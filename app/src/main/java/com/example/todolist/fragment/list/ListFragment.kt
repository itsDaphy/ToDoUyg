package com.example.todolist.fragment.list

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todolist.R
import com.example.todolist.data.models.ToDoData
import com.example.todolist.data.viewmodel.ToDoViewModel
import com.example.todolist.databinding.FragmentListBinding
import com.example.todolist.fragment.SharedViewModel
import com.example.todolist.fragment.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mToDoViewModel : ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val adapter : ListAdapter by lazy { ListAdapter() }

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        swipeToDelete(binding.recyclerView)


        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)

        })

        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer {

            showEmptyDatabaseView(it)

        })

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        binding.listLayout.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_updateFragment)
        }

        setHasOptionsMenu(true)



        return binding.root
    }

    private fun showEmptyDatabaseView(emptyDatabase: Boolean) {

        if(emptyDatabase) {

            binding?.noDataImageView?.visibility = View.VISIBLE
            binding?.noDataTextView?.visibility = View.VISIBLE

        } else {

            binding?.noDataImageView?.visibility = View.INVISIBLE
            binding?.noDataTextView?.visibility = View.INVISIBLE

        }

    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                // Delete Item
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                // Restore Deleted Item
              restoreDeletedData(viewHolder.itemView, deletedItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(
            view, "Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu,menu)

        val search:MenuItem = menu.findItem(R.id.menu_search)
        val searchView: SearchView = search.actionView as SearchView
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

       when (item.itemId) {
           R.id.menu_delete_all-> confirmRemoval()
           R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(this, Observer { adapter.setData(it) })
           R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(this, Observer { adapter.setData(it) })

       }
        return super.onOptionsItemSelected(item)
    }
    private fun confirmRemoval() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(
                requireContext(),
                "Successfully Removed Everything!",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun searchThroughDatabase(query: String) {

        var searchQuery = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                Log.d("ListFragment", "searchThroughDatabase")
                adapter.setData(it)
            }
        })


    }
    override fun onQueryTextSubmit(query: String?): Boolean {

        if(query != null) {
            searchThroughDatabase(query)
        }
        return true
    }



    override fun onQueryTextChange(query: String?): Boolean {

        if(query != null) {
            searchThroughDatabase(query)
        }
        return true

    }

}

