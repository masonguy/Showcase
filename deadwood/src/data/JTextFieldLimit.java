package data;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


//THIS WAS USED FROM AN ONLINE TUTORIAL, ORACLE JAVA DOCS POINTED ME TOWARDS THIS, SAID EXTEND A DOCUMENT TO LIMIT A TEXT FIELD
public class JTextFieldLimit extends PlainDocument {
    private int textLimit;

    public JTextFieldLimit(int limit) {
        super();
        textLimit = limit;
    }

    public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
        if (str == null) {
            return;
        }

        if ((getLength() + str.length()) <= textLimit) {
            super.insertString(offset, str, attr);
        }
    }
}