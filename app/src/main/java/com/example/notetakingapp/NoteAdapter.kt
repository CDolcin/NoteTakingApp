package com.example.notetakingapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private var notes: List<Note>,
    private val onNoteClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    // Keep the complete list of notes
    private var allNotes: List<Note> = notes.toList()

    class NoteViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val title: TextView =
            itemView.findViewById(R.id.tvNoteTitle)

        val snippet: TextView =
            itemView.findViewById(R.id.tvNoteContent)

        val deleteButton: TextView =
            itemView.findViewById(R.id.tvDeleteNote)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoteViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_note,
                parent,
                false
            )

        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        position: Int
    ) {

        val note = notes[position]

        holder.title.text = note.title
        holder.snippet.text = note.content

        holder.itemView.setOnClickListener {
            onNoteClick(note)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(note)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    // Update the complete list when notes are added/deleted
    fun updateNotes(newNotes: List<Note>) {

        allNotes = newNotes.toList()
        notes = newNotes.toList()

        notifyDataSetChanged()
    }

    // Search/filter notes
    fun filter(query: String) {

        val searchText = query.trim()

        notes = if (searchText.isEmpty()) {

            // Show all notes when search is empty
            allNotes

        } else {

            // Search both title and content
            allNotes.filter { note ->

                note.title.contains(
                    searchText,
                    ignoreCase = true
                ) ||

                        note.content.contains(
                            searchText,
                            ignoreCase = true
                        )
            }
        }

        notifyDataSetChanged()
    }
}