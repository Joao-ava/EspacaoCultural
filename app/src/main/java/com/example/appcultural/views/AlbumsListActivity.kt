package com.example.appcultural.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import com.example.appcultural.R
import com.example.appcultural.adapters.AlbumListAdapter
import com.example.appcultural.data.FirebaseAlbumsRepository
import com.example.appcultural.databinding.ActivityAlbumsListBinding
import com.example.appcultural.entities.Album
import kotlinx.coroutines.launch

class AlbumsListActivity : Fragment() {
    private lateinit var binding: ActivityAlbumsListBinding
    private lateinit var albumAdapter: AlbumListAdapter
    private val albumsRepository = FirebaseAlbumsRepository()
    private var originalAlbumList = mutableListOf<Album>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityAlbumsListBinding.inflate(inflater, container, false)
        return binding.main
    }

    override fun onResume() {
        super.onResume()
        loadFromFirestore()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        albumAdapter = AlbumListAdapter(requireContext(), mutableListOf())
        binding.recycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recycleView.adapter = albumAdapter

        setupSearchView()
        loadFromFirestore()

        binding.addAlbumButton.setOnClickListener {
            showAddPopup()
        }
    }

    private fun setupSearchView() {
        val searchView = binding.constraintLayout.findViewById<SearchView>(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = if (!newText.isNullOrEmpty()) {
                    originalAlbumList.filter { album ->
                        album.name.contains(newText, ignoreCase = true)
                    }
                } else {
                    originalAlbumList
                }
                albumAdapter.updateList(filteredList)
                return true
            }
        })
    }

    private fun loadFromFirestore() {
        lifecycleScope.launch {
            try {
                val albums = albumsRepository.fetchAll()
                originalAlbumList.clear()
                originalAlbumList.addAll(albums)
                albumAdapter.updateList(originalAlbumList)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro ao carregar álbuns: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddPopup() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.activity_dialog_add_album, null)
        dialogBuilder.setView(dialogView)

        val albumNameInput = dialogView.findViewById<EditText>(R.id.edit_album_name)

        dialogBuilder.setTitle("Adicionar Novo Álbum")
        dialogBuilder.setPositiveButton("Adicionar") { dialog, _ ->
            val albumName = albumNameInput.text.toString()
            if (albumName.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val newAlbum = albumsRepository.create(albumName)
                        originalAlbumList.add(newAlbum)
                        albumAdapter.addItem(newAlbum)
                        Toast.makeText(requireContext(), "Álbum criado com sucesso!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Erro ao criar álbum: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
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
