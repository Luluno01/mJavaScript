package ml.a0x00000000.mjavascript.helpers

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import ml.a0x00000000.mjavascript.R

class AlertDialog constructor(context: Context): AlertDialog(context) {
    private var _editText: EditText? = null
    var editText: EditText?
    get() = _editText
    set(value) {
        _editText = value?: EditText(context)
        val layout = LinearLayout(context)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val margin = context.resources.getDimensionPixelSize(R.dimen.filename_edit_text_margin)
        layoutParams.setMargins(margin, 0, margin, 0)
        _editText!!.layoutParams = layoutParams
        layout.addView(_editText)
        setView(layout)
    }

    init {
        super.setOnDismissListener { _ -> hideSoftKeyboard() }
        super.setOnCancelListener { _ -> hideSoftKeyboard() }
    }

    private var noHideListener: ((DialogInterface, Int) -> Unit)? = null

    override fun show() {
        super.show()
        _editText!!.isFocusable = true
        _editText!!.isFocusableInTouchMode = true
        _editText!!.requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        if(null != noHideListener) {
            getButton(DialogInterface.BUTTON_POSITIVE)
                    .setOnClickListener { _ ->
                        noHideListener?.invoke(this, -1)
                    }
        }
    }

    fun hideSoftKeyboard() {
        if(null != _editText) (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(_editText!!.windowToken, 0)
    }

    fun setPositiveButton(@StringRes textId: Int, noHide: Boolean?, listener: (DialogInterface, Int) -> Unit) {
        setPositiveButton(context.getText(textId), null, listener)
    }

    fun setPositiveButton(positiveButtonText: CharSequence, noHide: Boolean?, listener: (DialogInterface, Int) -> Unit) {
        noHideListener = listener
        val fakeListener: ((DialogInterface, Int) -> Unit)? = null
        setButton(DialogInterface.BUTTON_POSITIVE, positiveButtonText, fakeListener)
    }

    fun setPositiveButton(@StringRes textId: Int, listener: (DialogInterface, Int) -> Unit) {
        setPositiveButton(context.getText(textId), listener)
    }

    fun setPositiveButton(positiveButtonText: CharSequence, listener: (DialogInterface, Int) -> Unit) {
        setButton(DialogInterface.BUTTON_POSITIVE, positiveButtonText) { dialog: DialogInterface, which: Int ->
            listener(dialog, which)
            hideSoftKeyboard()
        }
    }

    fun setNegativeButton(@StringRes textId: Int, listener: (DialogInterface, Int) -> Unit) {
        setNegativeButton(context.getText(textId), listener)
    }

    fun setNegativeButton(negativeButtonText: CharSequence, listener: (DialogInterface, Int) -> Unit) {
        setButton(DialogInterface.BUTTON_NEGATIVE, negativeButtonText) { dialog, which ->
            listener(dialog, which)
            hideSoftKeyboard()
        }
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener { dialog: DialogInterface ->
            listener?.onDismiss(dialog)
            hideSoftKeyboard()
        }
    }
}