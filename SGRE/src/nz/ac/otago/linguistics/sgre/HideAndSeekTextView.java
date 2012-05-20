package nz.ac.otago.linguistics.sgre;

import java.util.Vector;

import nz.ac.otago.linguistics.sgre.HideAndSeekTextView.OnCharChangeListener;
import nz.ac.otago.linguistics.sgre.HideAndSeekTextView.OnWordChangeListener;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
	private float[] wordPositions;
	private long lastTimeWordChanged;
	private OnWordChangeListener onWordChangeListener;
	private Vector<String> words;

	private int currchar;
	private float[] charPositions;
	private long lastTimeCharChanged;
	private OnCharChangeListener onCharChangeListener;

	private BufferType buffType;
	private float sentenceRightXPos;
	private float sentenceLeftXPos;
	private RelativeLayout parentLayout;

	public HideAndSeekTextView(Context context) {
		super(context);
	}

	public HideAndSeekTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public HideAndSeekTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		lastTimeWordChanged = System.currentTimeMillis();
		lastTimeCharChanged = System.currentTimeMillis();
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
		// wordPositions = getWordPositions(getTextLeftXCoord(getX()));
		// charPositions = getCharPositions(getTextLeftXCoord(getX()));
		// sentenceRightXPos = getTextLeftXCoord(getX()) +
		// getPaint().measureText((String) text);
		// sentenceLeftXPos = getTextLeftXCoord(getX());
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		wordPositions = getWordPositions(getTextLeftXCoord(getX()));
		charPositions = getCharPositions(getTextLeftXCoord(getX()));
		sentenceRightXPos = getTextLeftXCoord(getX()) + getPaint().measureText((String) getText());
		sentenceLeftXPos = getTextLeftXCoord(getX());

		// LayoutParams sbvLayoutParams = seekBar.getLayoutParams();
		// int margin = (int)
		// TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0,
		// getResources().getDisplayMetrics());
		// int width = getTextWidth() + 2 * margin;
		// sbvLayoutParams.width = width;
		// seekBar.setLayoutParams(sbvLayoutParams);
		// seekBar.setMax(width);
		// parentLayout.addView(seekBar);
	}

	/**
	 * Update the view with a new finger position. Reveals the word the finger
	 * is pointing too.
	 * 
	 * @param x
	 *            X coordinate of the finger.
	 */
	public void giveFingerPosition(int x) {
		// Find the word the finger is pointing to. Note that the space after
		// the word is included in this case.
		if (x > sentenceRightXPos || x < sentenceLeftXPos) {
			currword = -1;
			currchar = -1;
		} else {
			for (int i = 0; i < wordPositions.length; i++) {
				// Looking through word positions from left to right.
				if (x < wordPositions[i]) {
					// Our word must be the one before this.
					int newCurrword = (i - 1) / 2;
					if (currword != newCurrword) {
						long t = System.currentTimeMillis();
						currword = newCurrword;
						onWordChangeListener.OnWordChange(currword, getWord(), t, t - lastTimeWordChanged);
						lastTimeWordChanged = t;
					}
					break;
				}
			}
			for (int i = 0; i < charPositions.length; i++) {
				if (x < charPositions[i]) {
					currchar = i;
					int newCurrchar = (i - 1) / 2;
					if (currchar != newCurrchar) {
						long t = System.currentTimeMillis();
						currchar = newCurrchar;
						onWordChangeListener.OnWordChange(currchar, getChar(), t, t - lastTimeCharChanged);
						lastTimeCharChanged = t;
					}
					break;
				}
			}
		}
		super.setText(getMangledSentence(), buffType);
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

	public String getChar() {
		if (currchar >= 0) {
			return ((String) getText()).substring(currchar, currchar + 1);
		}
		return "";
	}

	public int getWordIndex() {
		return currword;
	}

	public int getCharIndex() {
		return currchar;
	}

	public int getTextWidth() {
		return (int) getPaint().measureText((String) getText());
	}

	/**
	 * Get the coord of the left edge of the actual drawn text relative.
	 * 
	 * @param offset
	 *            If 0, the coord is relative to the textView.
	 * @return Some offset of the actual drawn text.
	 */
	public float getTextLeftXCoord(final float offset) {
		CharSequence text = getText();
		final Paint p = getPaint();
		float coord = offset;
		// Adjust offset for text alignment.
		switch (getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) {
		case Gravity.CENTER_HORIZONTAL:
			// Adjust coord from the view's center.
			float measureText = p.measureText((String) text);
			int width = getWidth();
			coord += getWidth() / 2 - p.measureText((String) text) / 2;
			break;

		case Gravity.RIGHT:
			// Adjust coord from the right edge of the view.
			coord += getWidth() - p.measureText((String) text);
			break;

		default:
			// Already got left edge.
			break;
		}
		return coord;
	}

	/**
	 * Get the x coord of the right edge of the actual drawn text.
	 * 
	 * @param offset
	 *            If 0, the coord is relative to the textView.
	 * @return Some offset of the actual drawn text.
	 */
	public float getTextRightXCoord(final float offset) {
		float coord = offset;
		CharSequence text = getText();
		final Paint p = getPaint();
		// Adjust offset for text alignment.
		switch (getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK) {
		case Gravity.CENTER_HORIZONTAL:
			coord += getWidth() / 2 + p.measureText((String) text) / 2;
			break;

		case Gravity.LEFT:
			coord += p.measureText((String) text);
			break;

		default:
			// Already got left edge.
			break;
		}
		return coord;
	}

	/**
	 * Get the x-positions of a string if it was to be drawn by this TextView.
	 * 
	 * @param text
	 *            CharSequence to measure.
	 * @param offset
	 *            If 0, positions returned are relative to the left edge of the
	 *            actual drawn text rather than the screen, or text view.
	 * @return Character positions.
	 */
	public float[] getCharPositions(float offset) {
		final CharSequence text = getText();
		final Paint p = getPaint();
		final float[] positions = new float[text.length()];
		// Measure each character, accumulate values to get the left position of
		// each character.
		for (int i = 0; i < text.length(); i++) {
			// Add character position.
			positions[i] = offset;
			offset += p.measureText(text, i, i + 1);
		}
		return positions;
	}

	/**
	 * Get the x-positions of each word in a string.
	 * 
	 * @param text
	 *            CharSequence to measure.
	 * @param offset
	 *            Distance from left edge of screen. If 0, positions returned
	 *            are relative to the left edge of the actual drawn text rather
	 *            than the screen, or text view.
	 * @return Word positions. Even indices are words, the odd being the space
	 *         positions dividing the words.
	 */
	public float[] getWordPositions(float offset) {
		final CharSequence text = getText();
		final Paint p = getPaint();
		final String[] words = TextUtils.split((String) text, " ");
		final float[] positions = new float[2 * words.length];
		final float spaceWidth = p.measureText(" ");
		// Measure each word, accumulate values to get the left position.
		for (int i = 0; i < words.length; i++) {
			// Add word position.
			positions[2 * i] = offset;
			offset += p.measureText(words[i]);
			// Add space between words.
			positions[2 * i + 1] = offset;
			offset += spaceWidth;
		}
		return positions;
	}

	public void addParentLayout(View v) {
		parentLayout = (RelativeLayout) v;
	}

	public void setOnWordChangeListener(OnWordChangeListener listener) {
		onWordChangeListener = listener;
	}

	public void setOnCharChangeListener(OnCharChangeListener listener) {
		onCharChangeListener = listener;
	}

	public interface OnWordChangeListener {
		public void OnWordChange(int wordIndex, String word, long eventTime, long timeSinceLastWordChange);
	}

	public interface OnCharChangeListener {
		public void OnCharChange(int charIndex, String character, long eventTime, long timeSinceLastCharChange);
	}
}
