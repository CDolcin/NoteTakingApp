package com.example.notetakingapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoteListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyMessage: View
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var searchEditText: EditText
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_note_list,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewNotes)
        emptyMessage = view.findViewById(R.id.tvEmptyMessage)
        fabAddNote = view.findViewById(R.id.fabAddNote)
        searchEditText = view.findViewById(R.id.etSearchNotes)

        adapter = NoteAdapter(
            NoteRepository.notes,

            onNoteClick = { note ->
                openEditNote(note.id)
            },

            onDeleteClick = { note ->
                showDeleteConfirmation(note)
            }
        )

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerView.adapter = adapter

        fabAddNote.setOnClickListener {
            openAddNote()
        }

        searchEditText.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    adapter.filter(s?.toString() ?: "")
                    updateEmptyMessage()
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        updateEmptyMessage()
    }

    override fun onResume() {
        super.onResume()

        if (::adapter.isInitialized) {
            adapter.updateNotes(NoteRepository.notes)
            updateEmptyMessage()
        }
    }

    private fun openAddNote() {

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                AddEditNoteFragment.newInstance()
            )
            .addToBackStack(null)
            .commit()
    }

    private fun openEditNote(noteId: Int) {

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                AddEditNoteFragment.newInstance(noteId)
            )
            .addToBackStack(null)
            .commit()
    }

    private fun showDeleteConfirmation(note: Note) {

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_note))
            .setMessage(
                getString(R.string.delete_confirmation)
            )
            .setPositiveButton(
                getString(R.string.delete)
            ) { _, _ ->

                NoteRepository.deleteNote(
                    requireContext(),
                    note.id
                )

                adapter.updateNotes(NoteRepository.notes)
                updateEmptyMessage()
            }
            .setNegativeButton(
                getString(R.string.cancel),
                null
            )
            .show()
    }

    private fun updateEmptyMessage() {

        if (NoteRepository.notes.isEmpty()) {

            emptyMessage.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        } else {

            emptyMessage.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}