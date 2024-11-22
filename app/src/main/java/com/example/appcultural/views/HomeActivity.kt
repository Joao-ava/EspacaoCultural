package com.example.appcultural.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.appcultural.R
import com.example.appcultural.adapters.ArtListAdapter
import com.example.appcultural.data.FirebaseArtsRepository
import com.example.appcultural.databinding.ActivityHomeBinding
import com.example.appcultural.entities.Art
import kotlinx.coroutines.launch

class HomeActivity: Fragment() {
    private lateinit var binding: ActivityHomeBinding
    private val repository = FirebaseArtsRepository()
    private var data: MutableList<Art> = mutableListOf()
    private lateinit var adapter: ArtListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityHomeBinding.inflate(inflater, container, false)
        return binding.main
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ArtListAdapter(requireContext(), data);
        val viewManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        viewManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.recycleView.layoutManager = viewManager
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                data.clear()
                data.addAll(repository.list())
                adapter.notifyDataSetChanged()
            } catch (err: Exception) {
                println("err")
                println(err)
            }
        }
        binding.recycleView.adapter = adapter

        binding.filterActionButton.setOnClickListener {
            showFilterPopup()
        }
    }

    private fun showFilterPopup() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_dialog_art_filter, null)

        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Filtrar")
        dialogBuilder.setPositiveButton("Filtrar") { dialog, _ ->
            val titleField = dialogView.findViewById<EditText>(R.id.art_title_field)
            val artistField = dialogView.findViewById<EditText>(R.id.art_artist_field)
            val genderField = dialogView.findViewById<EditText>(R.id.art_genders_field)
            lifecycleScope.launch {
                data.clear()
                val content = repository.list(
                    titleField.text.toString(),
                    artistField.text.toString(),
                    genderField.text.toString()
                )
                data.addAll(content)
                adapter.notifyDataSetChanged()
            }
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }
}