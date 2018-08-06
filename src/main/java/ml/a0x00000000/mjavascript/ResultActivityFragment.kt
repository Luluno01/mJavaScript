package ml.a0x00000000.mjavascript

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ml.a0x00000000.mjavascript.highlightingdefinitions.definitions.NoHighlightingDefinition

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class ResultActivityFragment: EditActivityFragment() {
    companion object {
        const val TAG = "ResultActivityFragment"
    }

    lateinit var oldKeyListener: KeyListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {
        val view = super.onCreateView(inflater, container, state)
        editor.loadHighlightingDefinition(NoHighlightingDefinition())
        oldKeyListener = editor.keyListener
        setReadOnly()
        return view
    }

    fun setReadOnly() {
        editor.keyListener = null
        editor.setTextIsSelectable(true)
    }
}
