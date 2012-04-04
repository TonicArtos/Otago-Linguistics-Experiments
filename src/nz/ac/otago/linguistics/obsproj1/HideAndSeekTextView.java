package nz.ac.otago.linguistics.obsproj1;

import java.util.Vector;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * A TextView that hides/obfuscates the text by some method. Each word can be
 * displayed (exclusively so) in turn using the step method.
 * 
 * @author Tonic Artos
 * 
 */
public class HideAndSeekTextView extends TextView {
	private int currword;
	private Vector<String> words;
	private BufferType buffType;

	public HideAndSeekTextView(Context context) {
		super(context);
	}

	public HideAndSeekTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HideAndSeekTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Override to store the given sentence and pass through its mangled
	 * representation to the base type.
	 */
	@Override
	public void setText(CharSequence text, BufferType type) {
		buffType = type;
		currword = -1;
		words = new Vector<String>();
		// strip empty strings
		for (String word : TextUtils.split(text.toString(), " ")) {
			if (!TextUtils.isEmpty(word)) {
				words.add(word);
			}
		}
		super.setText(getMangledSentence(), type);
	}

	/**
	 * Step through each word of the sentence. Loops to the beginning if all
	 * words has been stepped through.
	 * 
	 * @return true if stepping has finished entire sequence of words.
	 *         Otherwise, false.
	 */
	public boolean step() {
		boolean isFinished = false;
		currword += 1;
		if (currword == words.size()) {
			currword = -1;
			isFinished = true;
		}
		// update representation
		super.setText(getMangledSentence(), buffType);
		return isFinished;
	}

	/**
	 * Gets a mangled representation of the set text. This representation
	 * changes depending on the current step state.
	 * 
	 * @return mangled representation.
	 */
	private CharSequence getMangledSentence() {
		String sentence = "";
		for (int i = 0; i < words.size(); i++) {
			if (i == currword) {
				sentence += words.get(i);
			} else {
				// TODO: Make the char replacement an attribute that can be set
				// in the layout xml.
				sentence += words.get(i).replaceAll(getResources().getString(R.string.char_pattern), getResources().getString(R.string.replace_char));
			}

			if (i < words.size() - 1) {
				sentence += " ";
			}
		}
		return sentence;
	}

	public String getWord() {
		if (currword >= 0) {
			return words.get(currword);
		}
		return "";
	}

	public int getWordIndex() {
		return currword;
	}
}
