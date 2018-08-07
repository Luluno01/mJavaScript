package ml.a0x00000000.mjavascript

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
//import ml.a0x00000000.mjavascript.editor.HighlightingDefinition

import ml.a0x00000000.mjavascript.editor.IridiumHighlightingEditorJ
//import ml.a0x00000000.mjavascript.highlightingdefinitions.definitions.GenericHighlightingDefinition

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditorFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class EditActivityFragment: Fragment() {
    companion object {
        const val TAG = "EditActivityFragment"
    }

    private var imm: InputMethodManager? = null
    lateinit var editor: IridiumHighlightingEditorJ
        private set
    private var yOffset: Int = 0

    val isModified: Boolean
        get() = editor.isModified()

    var text: String
        get() = editor.cleanText
        set(text) {
            clearError()
            editor.setTextHighlighted(text)
        }

    val isCodeVisible: Boolean
        get() = editor.visibility == View.VISIBLE

//    private var _highlightingDefinition: HighlightingDefinition = GenericHighlightingDefinition()
//    var highlightingDefinition: HighlightingDefinition
//        set(def) {
//            _highlightingDefinition = def
//            editor.loadHighlightingDefinition(def)
//        }
//        get() = _highlightingDefinition

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View? {

        if(null == activity) return null

        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view: View = inflater.inflate(R.layout.fragment_edit, container, false)
        editor = view.findViewById(R.id.editor)
//        editor.loadHighlightingDefinition(_highlightingDefinition)

        try {
            editor.setOnTextChangedListener(activity as IridiumHighlightingEditorJ.OnTextChangedListener)
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement ShaderEditor.OnTextChangedListener")
        }

        return view
    }

    //    override fun onResume() {
//        super.onResume()
//        //updateToPreferences();
//    }

    fun hasErrorLine(): Boolean {
        return editor.hasErrorLine()
    }

    fun clearError() {
        editor.setErrorLine(0)
    }

    fun updateHighlighting() {
        editor.updateHighlighting()
    }

//    fun showError(infoLog:String) {
//        val activity = activity ?: return
//
//        /*
//        InfoLog.parse(infoLog);
//        codeEditor.setErrorLine(InfoLog.getErrorLine());
//        updateHighlighting();
//
//        Toast errorToast = Toast.makeText(
//                activity,
//                InfoLog.getMessage(),
//                Toast.LENGTH_SHORT);
//
//        errorToast.setGravity(
//                Gravity.TOP | Gravity.CENTER_HORIZONTAL,
//                0,
//                getYOffset(activity));
//        errorToast.show();
//        */
//    }

    fun insertTab() {
        editor.insertTab()
    }

    fun addUniform(name:String) {
        editor.addUniform(name)
    }

    fun toggleCode(): Boolean {
        val visible = isCodeVisible

        editor.visibility = if(visible) View.GONE else View.VISIBLE

        if (visible) imm!!.hideSoftInputFromWindow(editor.windowToken,0)

        return visible
    }

    /*
    private void updateToPreferences() {
        Preferences preferences =
                ShaderEditorApplication.preferences;

        codeEditor.setUpdateDelay(
                preferences.getUpdateDelay());

        codeEditor.setTextSize(
                android.util.TypedValue.COMPLEX_UNIT_SP,
                preferences.getTextSize());

        codeEditor.setTabWidth(
                preferences.getTabWidth());
    }
    */

    private fun getYOffset(activity: Activity): Int {
        if(yOffset == 0) {
            val dp = resources.displayMetrics.density
            try {
                val actionBar = (activity as AppCompatActivity).supportActionBar
                if (actionBar != null) yOffset = actionBar.height
            } catch (e: ClassCastException) {
                yOffset = Math.round(48f * dp)
            }
            yOffset += Math.round(16f * dp)
        }

        return yOffset
    }
}
