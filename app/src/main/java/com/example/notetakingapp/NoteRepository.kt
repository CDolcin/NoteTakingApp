package com.example.notetakingapp
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object NoteRepository {

    private const val PREF_NAME = "note_preferences"
    private const val KEY_NOTES = "notes"

    val notes = mutableListOf<Note>()

    private var nextId = 1

    fun initialize(context: Context) {
        if (notes.isEmpty()) {
            loadNotes(context)
        }
    }

    fun addNote(
        context: Context,
        title: String,
        content: String
    ) {
        val note = Note(
            id = nextId++,
            title = title,
            content = content
        )

        notes.add(note)
        saveNotes(context)
    }

    fun updateNote(
        context: Context,
        id: Int,
        title: String,
        content: String
    ) {
        val note = notes.find { it.id == id }

        note?.let {
            it.title = title
            it.content = content
        }

        saveNotes(context)
    }

    fun deleteNote(
        context: Context,
        id: Int
    ) {
        notes.removeAll { it.id == id }

        saveNotes(context)
    }

    fun getNoteById(id: Int): Note? {
        return notes.find { it.id == id }
    }

    private fun saveNotes(context: Context) {

        val sharedPreferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val jsonArray = JSONArray()

        for (note in notes) {

            val jsonObject = JSONObject()

            jsonObject.put("id", note.id)
            jsonObject.put("title", note.title)
            jsonObject.put("content", note.content)

            jsonArray.put(jsonObject)
        }

        sharedPreferences.edit()
            .putString(KEY_NOTES, jsonArray.toString())
            .apply()
    }

    private fun loadNotes(context: Context) {

        val sharedPreferences = context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

        val jsonString = sharedPreferences.getString(
            KEY_NOTES,
            null
        )

        if (jsonString == null) {
            return
        }

        try {

            val jsonArray = JSONArray(jsonString)

            notes.clear()

            var largestId = 0

            for (i in 0 until jsonArray.length()) {

                val jsonObject = jsonArray.getJSONObject(i)

                val id = jsonObject.getInt("id")
                val title = jsonObject.getString("title")
                val content = jsonObject.getString("content")

                notes.add(
                    Note(
                        id = id,
                        title = title,
                        content = content
                    )
                )

                if (id > largestId) {
                    largestId = id
                }
            }

            nextId = largestId + 1

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}