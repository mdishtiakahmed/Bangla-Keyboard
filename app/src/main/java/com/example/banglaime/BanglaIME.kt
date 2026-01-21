package com.example.banglaime

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class BanglaIME : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    private var keyboardView: KeyboardView? = null
    private var qwertyKeyboard: Keyboard? = null
    private var banglaKeyboard: Keyboard? = null
    private var isCaps = false
    private var isBangla = true
    
    private val phoneticEngine = PhoneticEngine()
    private val composing = StringBuilder()

    override fun onCreateInputView(): View {
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as KeyboardView
        qwertyKeyboard = Keyboard(this, R.xml.qwerty)
        banglaKeyboard = Keyboard(this, R.xml.qwerty)
        
        keyboardView?.keyboard = banglaKeyboard
        keyboardView?.setOnKeyboardActionListener(this)
        
        return keyboardView!!
    }
    
    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        composing.setLength(0)
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray?) {
        val ic = currentInputConnection ?: return
        
        when (primaryCode) {
            Keyboard.KEYCODE_DELETE -> handleDelete(ic)
            Keyboard.KEYCODE_SHIFT -> handleShift()
            Keyboard.KEYCODE_DONE -> handleDone(ic)
            -101 -> switchLanguage() // Custom code for globe
            else -> {
                val code = primaryCode.toChar()
                if (isBangla) {
                    handleBanglaInput(code, ic)
                } else {
                    handleEnglishInput(code, ic)
                }
            }
        }
    }

    private fun handleBanglaInput(code: Char, ic: InputConnection) {
        if (Character.isLetter(code)) {
            composing.append(code)
            val banglaText = phoneticEngine.convert(composing.toString())
            ic.setComposingText(banglaText, 1)
        } else {
            if (composing.isNotEmpty()) {
                 val banglaText = phoneticEngine.convert(composing.toString())
                 ic.commitText(banglaText, 1)
                 composing.setLength(0)
            }
            ic.commitText(code.toString(), 1)
        }
    }
    
    private fun handleEnglishInput(code: Char, ic: InputConnection) {
        var char = code
        if (isCaps && Character.isLetter(char)) {
            char = Character.toUpperCase(char)
        }
        ic.commitText(char.toString(), 1)
    }

    private fun handleDelete(ic: InputConnection) {
        if (composing.isNotEmpty()) {
            composing.deleteCharAt(composing.length - 1)
            if (composing.isEmpty()) {
                ic.commitText("", 1)
            } else {
                val banglaText = phoneticEngine.convert(composing.toString())
                ic.setComposingText(banglaText, 1)
            }
        } else {
            ic.deleteSurroundingText(1, 0)
        }
    }

    private fun handleShift() {
        isCaps = !isCaps
        keyboardView?.isShifted = isCaps
        keyboardView?.invalidateAllKeys()
    }
    
    private fun handleDone(ic: InputConnection) {
        ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER))
    }
    
    private fun switchLanguage() {
        isBangla = !isBangla
        // In a real app, swap the keyboard layout object here if the layouts differ
        // keyboardView?.keyboard = if (isBangla) banglaKeyboard else qwertyKeyboard
    }

    override fun onPress(primaryCode: Int) {}
    override fun onRelease(primaryCode: Int) {}
    override fun onText(text: CharSequence?) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}
