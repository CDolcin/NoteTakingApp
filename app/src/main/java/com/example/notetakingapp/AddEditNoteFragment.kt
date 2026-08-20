package com.example.notetakingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddEditNoteFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var formTitle: TextView

    private var noteId: Int? = null

    companion object {

        private const val ARG_NOTE_ID = "note_id"

        fun newInstance(noteId: Int? = null): AddEditNoteFragment {

            val fragment = AddEditNoteFragment()

            if (noteId != null) {

                val bundle = Bundle()

                bundle.putInt(ARG_NOTE_ID, noteId)

                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noteId = arguments?.getInt(ARG_NOTE_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_add_edit_note,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(view, savedInstanceState)

        formTitle = view.findViewById(R.id.tvFormTitle)
        titleEditText = view.findViewById(R.id.etNoteTitle)
        contentEditText = view.findViewById(R.id.etNoteContent)
        saveButton = view.findViewById(R.id.btnSave)
        cancelButton = view.findViewById(R.id.btnCancel)

        loadNoteIfEditing()

        saveButton.setOnClickListener {
            saveNote()
        }

        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadNoteIfEditing() {

        if (noteId != null) {

            val note = NoteRepository.getNoteById(noteId!!)

            if (note != null) {

                formTitle.text =
                    getString(R.string.edit_note)

                titleEditText.setText(note.title)
                contentEditText.setText(note.content)
            }

        } else {

            formTitle.text =
                getString(R.string.add_note)
        }
    }

    private fun saveNote() {

        val title =
            titleEditText.text.toString().trim()

        val content =
            contentEditText.text.toString().trim()

        if (title.isEmpty()) {

            titleEditText.error =
                getString(R.string.title_required)

            titleEditText.requestFocus()

            return
        }

        if (content.isEmpty()) {

            contentEditText.error =
                getString(R.string.content_required)

            contentEditText.requestFocus()

            return
        }

        if (noteId == null) {

            NoteRepository.addNote(
                requireContext(),
                title,
                content
            )

            Toast.makeText(
                requireContext(),
                getString(R.string.note_saved),
                Toast.LENGTH_SHORT
            ).show()

        } else {

            NoteRepository.updateNote(
                requireContext(),
                noteId!!,
                title,
                content
            )

            Toast.makeText(
                requireContext(),
                getString(R.string.note_updated),
                Toast.LENGTH_SHORT
            ).show()
        }

        parentFragmentManager.popBackStack()
    }
}